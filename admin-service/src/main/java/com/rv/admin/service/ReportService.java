package com.rv.admin.service;

import java.time.LocalDateTime;
import java.util.List;

import com.rv.admin.report.CampaignReport;
import com.rv.admin.report.InventoryReportView;
import com.rv.admin.report.SalesReport;
import com.rv.admin.report.UserActivityReport;

public interface ReportService {

    List<InventoryReportView> getInventoryReport(Integer categoryId,
                                                 Boolean active,
                                                 Integer minStock,
                                                 Integer maxStock,
                                                 LocalDateTime fromDate,
                                                 LocalDateTime toDate);

    SalesReport getSalesReport(String customerEmail,
                               LocalDateTime fromDate,
                               LocalDateTime toDate);

    UserActivityReport getUserActivityReport(String customerEmail,
                                             LocalDateTime fromDate,
                                             LocalDateTime toDate);

    CampaignReport getCampaignReport(Long productId,
                                     Integer categoryId);
}
