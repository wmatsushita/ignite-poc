package com.ifood.ignitepoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wagner-matsushita on 20/07/17.
 */
public class MerchantCountResult implements Serializable {

    private MerchantStatus merchantStatus;
    private long merchantCount;
    private List<Merchant> merchants;

    public MerchantCountResult() {
        merchants = new ArrayList<>();
        merchantCount = 0;
    }

    public MerchantStatus getMerchantStatus() {
        return merchantStatus;
    }

    public void setMerchantStatus(MerchantStatus merchantStatus) {
        this.merchantStatus = merchantStatus;
    }

    public Long getMerchantCount() {
        return merchantCount;
    }

    public void setMerchantCount(Long merchantCount) {
        this.merchantCount = merchantCount;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<Merchant> merchants) {
        this.merchants = merchants;
    }

}
