package com.ifood.ignitepoc.configuration;

import com.ifood.ignitepoc.model.Merchant;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.expiry.EternalExpiryPolicy;

/**
 * Created by wagner-matsushita on 13/07/17.
 */
@Configuration
public class IgniteCacheConfiguration{

    public static final String MERCHANT_CACHE_NAME = "merchant-cache";

    @Bean
    public Ignite getIgnite() {
        IgniteConfiguration config = new IgniteConfiguration();

        CacheConfiguration cacheConfiguration = new CacheConfiguration(MERCHANT_CACHE_NAME);
        cacheConfiguration.setExpiryPolicyFactory(EternalExpiryPolicy.factoryOf());
        cacheConfiguration.setBackups(1);
        config.setCacheConfiguration(cacheConfiguration);

        return Ignition.start(config);
    }

    @Bean
    public IgniteCache<Long, Merchant> getMerchantCache(Ignite ignite) {
        return ignite.getOrCreateCache(MERCHANT_CACHE_NAME);

    }

}

