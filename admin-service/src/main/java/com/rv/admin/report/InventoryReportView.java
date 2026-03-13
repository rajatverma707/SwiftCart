package com.rv.admin.report;

public interface InventoryReportView {

    Integer getProductId();
    String getProductName();
    String getCategoryName();
    Integer getStock();
    Boolean getActive();
}
