package com.ifood.ignitepoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wagner-matsushita on 20/07/17.
 */
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long Id;
    private String name;
    private List<MerchantAvailability> availabilityList;
    private Date lastKeepAlive;

    public Merchant() {
        this.availabilityList = new ArrayList();
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MerchantAvailability> getAvailabilityList() {
        return availabilityList;
    }

    public void setAvailabilityList(List<MerchantAvailability> availabilityList) {
        this.availabilityList = availabilityList;
    }

    public void addAvailability(MerchantAvailability... availabilities) {
        for (MerchantAvailability a : availabilities) {
            availabilityList.add(a);
        }
    }

    public Date getLastKeepAlive() {
        return lastKeepAlive;
    }

    public void setLastKeepAlive(Date lastKeepAlive) {
        this.lastKeepAlive = lastKeepAlive;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Merchant merchant = (Merchant) o;

        if (Id != null ? !Id.equals(merchant.Id) : merchant.Id != null)
            return false;
        if (name != null ? !name.equals(merchant.name) : merchant.name != null)
            return false;
        return availabilityList != null ?
                availabilityList.equals(merchant.availabilityList) :
                merchant.availabilityList == null;
    }

    @Override
    public int hashCode() {
        int result = Id != null ? Id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (availabilityList != null ? availabilityList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Merchant{" + "Id=" + Id + ", name='" + name + '\'' + ", availabilityList=" + availabilityList + '}';
    }

}
