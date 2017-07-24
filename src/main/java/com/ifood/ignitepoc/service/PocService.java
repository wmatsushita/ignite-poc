package com.ifood.ignitepoc.service;

import com.ifood.ignitepoc.model.Merchant;
import com.ifood.ignitepoc.model.MerchantAvailability;
import com.ifood.ignitepoc.model.MerchantCountResult;
import com.ifood.ignitepoc.model.MerchantStatus;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.cache.Cache.Entry;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by wagner-matsushita on 20/07/17.
 */
@Service
public class PocService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(PocService.class);

    public static final int NUMBER_OF_RESTAURANTS = 200000;
    public static final int KEEP_ALIVE_PAUSE = 5;
    public static final int OFFLINE_THREASHOLD = 1000 * 60 * 2;
    public static final int DETAIL_SIZE = 5;
    private Ignite ignite;
    private IgniteCache<Long, Merchant> merchantCache;

    @Autowired
    public PocService(Ignite ignite, IgniteCache<Long, Merchant> merchantCache) {
        this.ignite = ignite;
        this.merchantCache = merchantCache;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // Loads all the mock merchants in the ignite cache
        Map<Long, Merchant> allMerchants = new HashMap<>();

        for (long i = 0; i < NUMBER_OF_RESTAURANTS; i++) {
            Merchant merchant = new Merchant();
            merchant.setId(i);

            MerchantAvailability availability = new MerchantAvailability();
            availability.setMerchantId(i);
            if (i % 2 != 0) {
                // Restaurantes impares funcionam de manhã
                availability.setOpeningTime(LocalTime.parse("08:00"));
                availability.setClosingTime(LocalTime.parse("12:00"));
            } else {
                // Restaurantes pares funcionam de tarde
                availability.setOpeningTime(LocalTime.parse("13:00"));
                availability.setClosingTime(LocalTime.parse("18:00"));
            }

            merchant.addAvailability(availability);

            allMerchants.put(i, merchant);
        }

        merchantCache.putAll(allMerchants);

        logger.info("Finished populating Merchant Cache");

        // Start keep alive thread
        startKeepAlives();

    }

    public void startKeepAlives() {

        Thread t = new Thread(() -> {
            while (true) {
                Long merchantId = (long) (Math.random() * NUMBER_OF_RESTAURANTS);
                Merchant merchant = merchantCache.get(merchantId);
                merchant.setLastKeepAlive(new Date());

                merchantCache.put(merchantId, merchant);

                try {
                    Thread.currentThread().sleep(KEEP_ALIVE_PAUSE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        t.start();
    }

    public MerchantCountResult countRestaurants(MerchantStatus desiredStatus, LocalTime desiredTime) {
        long start = System.currentTimeMillis();

        ComputeTask<String, MerchantCountResult> task = new ComputeTaskAdapter<String, MerchantCountResult>() {

            @Override
            public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> subgrid, String arg)
                    throws IgniteException {
                Map<ComputeJob, ClusterNode> map = new HashMap<>(subgrid.size());

                for (ClusterNode node : subgrid) {
                    map.put(new ComputeJobAdapter() {
                        @Override
                        public Object execute() {

                            // Load all local merchants have the desired status at the desired time (tweakable now)
                            ScanQuery<Long, Merchant> qry = new ScanQuery<Long, Merchant>((merchantId, merchant) -> {
                                boolean shouldBeOpen = false;
                                MerchantStatus currentStatus = MerchantStatus.ONLINE;

                                // Decides if restaurant should be open
                                for (MerchantAvailability availability : merchant.getAvailabilityList()) {
                                    if (desiredTime.isAfter(availability.getOpeningTime()) && desiredTime.isBefore(availability.getClosingTime())) {
                                        shouldBeOpen = true;
                                        break;
                                    }
                                }

                                if (shouldBeOpen) {
                                    if (merchant.getLastKeepAlive() == null ||
                                            merchant.getLastKeepAlive().getTime() + OFFLINE_THREASHOLD < System.currentTimeMillis()) {
                                        currentStatus = MerchantStatus.OFFLINE;
                                    }
                                    return desiredStatus.equals(currentStatus);
                                }

                                return false;
                            });

                            qry.setLocal(true);

                            try (QueryCursor<Entry<Long, Merchant>> cursor = merchantCache.query(qry)) {

                                Stream<Entry<Long, Merchant>> stream =
                                        StreamSupport.stream(cursor.spliterator(), false);

                                MerchantCountResult merchantCountResult = new MerchantCountResult();
                                merchantCountResult.setMerchantStatus(desiredStatus);
                                merchantCountResult.setMerchantCount(stream.peek(entry -> {
                                    if (merchantCountResult.getMerchants().size() < DETAIL_SIZE) {
                                        merchantCountResult.getMerchants().add(entry.getValue());
                                    }
                                }).count());
                                return merchantCountResult;
                            }
                        }
                    }, node);
                }
                return map;
            }

            @Override
            public MerchantCountResult reduce(List<ComputeJobResult> results) throws IgniteException {

                MerchantCountResult merchantCountResult = new MerchantCountResult();
                results.stream().map(r -> (MerchantCountResult) r.getData()).forEach(mcr -> {
                    merchantCountResult.setMerchantStatus(mcr.getMerchantStatus());
                    merchantCountResult.setMerchantCount(merchantCountResult.getMerchantCount() + mcr.getMerchantCount());
                    merchantCountResult.getMerchants().addAll(mcr.getMerchants());
                });

                return merchantCountResult;
            }
        };

        IgniteCompute compute = ignite.compute().withNoFailover();
        MerchantCountResult result = compute.execute(task, null);

        logger.debug(String.format("Processing time: %d seconds", (System.currentTimeMillis() - start) / 1000));

        return result;
    }
}
