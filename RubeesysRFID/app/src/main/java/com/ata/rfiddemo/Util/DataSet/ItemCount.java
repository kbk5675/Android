package com.ata.rfiddemo.Util.DataSet;

public class ItemCount {

    private String companyPrefix;
    private String itemReference;
    private Long count;

    public ItemCount(String companyPrefix, String itemReference, Long count) {
        this.companyPrefix = companyPrefix;
        this.itemReference = itemReference;
        this.count = count;
    }

    public String getCompanyPrefix() {
        return companyPrefix;
    }

    public void setCompanyPrefix(String companyPrefix) {
        this.companyPrefix = companyPrefix;
    }

    public String getItemReference() {
        return itemReference;
    }

    public void setItemReference(String itemReference) {
        this.itemReference = itemReference;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
