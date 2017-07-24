package com.ifood.ignitepoc.model;

import java.io.Serializable;
import java.time.LocalTime;

public class MerchantAvailability implements Serializable {

    private static final long serialVersionUID = 7602123449307596764L;

    private Long merchantId;
    private String dayOfWeek;
    private LocalTime openingTime;
    private LocalTime closingTime;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    @Override
    public String toString() {
        return "MerchantAvailability" +
            "[ merchantId=" + merchantId +
            ", dayOfWeek=" + dayOfWeek +
            ", openingTime=" + openingTime +
            ", closingTime=" + closingTime + " ]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dayOfWeek == null) ? 0 : dayOfWeek.hashCode());
        result = prime * result + ((merchantId == null) ? 0 : merchantId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MerchantAvailability other = (MerchantAvailability) obj;
        if (dayOfWeek == null) {
            if (other.dayOfWeek != null)
                return false;
        } else if (!dayOfWeek.equals(other.dayOfWeek))
            return false;
        if (merchantId == null) {
            if (other.merchantId != null)
                return false;
        } else if (!merchantId.equals(other.merchantId))
            return false;
        return true;
    }
}
