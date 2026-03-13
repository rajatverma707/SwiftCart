package com.rv.admin.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rv.admin.report.AdminReportExporter;
import com.rv.admin.report.CampaignReport;
import com.rv.admin.report.InventoryReportExporter;
import com.rv.admin.report.InventoryReportView;
import com.rv.admin.report.SalesReport;
import com.rv.admin.report.UserActivityReport;
import com.rv.admin.service.ReportService;

@RestController
@RequestMapping("/admin/reports")
public class ReportController {
    // ExecutorService for async report generation
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Autowired
    public ReportService reportService;

    /**
     * Simulate async report generation (e.g., for large sales report)
     */
    @GetMapping("/sales/async")
    public ResponseEntity<String> generateSalesReportAsync(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
       
            executor.submit(() -> {
            LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
            LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
            SalesReport report = reportService.getSalesReport(email, fromDateTime, toDateTime);
            // Simulate processing (e.g., export, email, etc.)
            try {
                Thread.sleep(5000); // Simulate long-running task
                System.out.println("[Async] Sales report generated for: " + email);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return ResponseEntity.ok("Sales report generation started asynchronously!");
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryReportView>> inventoryReport(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<InventoryReportView> data = getInventoryReportData(categoryId, active, minStock, maxStock, fromDate, toDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesReport> salesReport(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        SalesReport report = reportService.getSalesReport(email, fromDateTime, toDateTime);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/user-activity")
    public ResponseEntity<UserActivityReport> userActivityReport(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        UserActivityReport report = reportService.getUserActivityReport(email, fromDateTime, toDateTime);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/campaigns")
    public ResponseEntity<CampaignReport> campaignReport(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer categoryId) {
        CampaignReport report = reportService.getCampaignReport(productId, categoryId);
        return ResponseEntity.ok(report);
    }

        @GetMapping("/sales/csv")
        public ResponseEntity<ByteArrayResource> downloadSalesCsv(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        SalesReport report = reportService.getSalesReport(email, fromDateTime, toDateTime);

        byte[] bytes = AdminReportExporter.salesToCsv(report).getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "sales-report-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("text/csv"))
            .contentLength(bytes.length)
            .body(resource);
        }

        @GetMapping("/sales/excel")
        public ResponseEntity<ByteArrayResource> downloadSalesExcel(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        SalesReport report = reportService.getSalesReport(email, fromDateTime, toDateTime);

        byte[] bytes = AdminReportExporter.salesToExcel(report);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "sales-report-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .contentLength(bytes.length)
            .body(resource);
        }

        @GetMapping("/sales/pdf")
        public ResponseEntity<ByteArrayResource> downloadSalesPdf(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        SalesReport report = reportService.getSalesReport(email, fromDateTime, toDateTime);

        byte[] bytes = AdminReportExporter.salesToPdf(report);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "sales-report-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(bytes.length)
            .body(resource);
        }

    @GetMapping("/user-activity/csv")
    public ResponseEntity<ByteArrayResource> downloadUserActivityCsv(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        UserActivityReport report = reportService.getUserActivityReport(email, fromDateTime, toDateTime);

        byte[] bytes = AdminReportExporter.userActivityToCsv(report).getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "user-activity-report-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/user-activity/excel")
    public ResponseEntity<ByteArrayResource> downloadUserActivityExcel(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        UserActivityReport report = reportService.getUserActivityReport(email, fromDateTime, toDateTime);

        byte[] bytes = AdminReportExporter.userActivityToExcel(report);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "user-activity-report-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/user-activity/pdf")
    public ResponseEntity<ByteArrayResource> downloadUserActivityPdf(
            @RequestParam String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        UserActivityReport report = reportService.getUserActivityReport(email, fromDateTime, toDateTime);

        byte[] bytes = AdminReportExporter.userActivityToPdf(report);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "user-activity-report-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bytes.length)
                .body(resource);
    }

        @GetMapping("/campaigns/csv")
        public ResponseEntity<ByteArrayResource> downloadCampaignsCsv(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer categoryId) {
        CampaignReport report = reportService.getCampaignReport(productId, categoryId);

        byte[] bytes = AdminReportExporter.campaignsToCsv(report).getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "campaigns-report-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("text/csv"))
            .contentLength(bytes.length)
            .body(resource);
        }

        @GetMapping("/campaigns/excel")
        public ResponseEntity<ByteArrayResource> downloadCampaignsExcel(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer categoryId) {
        CampaignReport report = reportService.getCampaignReport(productId, categoryId);

        byte[] bytes = AdminReportExporter.campaignsToExcel(report);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "campaigns-report-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .contentLength(bytes.length)
            .body(resource);
        }

        @GetMapping("/campaigns/pdf")
        public ResponseEntity<ByteArrayResource> downloadCampaignsPdf(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer categoryId) {
        CampaignReport report = reportService.getCampaignReport(productId, categoryId);

        byte[] bytes = AdminReportExporter.campaignsToPdf(report);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "campaigns-report-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(bytes.length)
            .body(resource);
        }

    @GetMapping("/inventory/csv")
    public ResponseEntity<ByteArrayResource> downloadInventoryCsv(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<InventoryReportView> data = getInventoryReportData(categoryId, active, minStock, maxStock, fromDate, toDate);
        byte[] bytes = InventoryReportExporter.toCsv(data).getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        String filename = "inventory-report-" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/inventory/excel")
    public ResponseEntity<ByteArrayResource> downloadInventoryExcel(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<InventoryReportView> data = getInventoryReportData(categoryId, active, minStock, maxStock, fromDate, toDate);
        byte[] bytes = InventoryReportExporter.toExcel(data);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        String filename = "inventory-report-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/inventory/pdf")
    public ResponseEntity<ByteArrayResource> downloadInventoryPdf(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<InventoryReportView> data = getInventoryReportData(categoryId, active, minStock, maxStock, fromDate, toDate);
        byte[] bytes = InventoryReportExporter.toPdf(data);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        String filename = "inventory-report-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bytes.length)
                .body(resource);
    }

    private List<InventoryReportView> getInventoryReportData(Integer categoryId,
                                                             Boolean active,
                                                             Integer minStock,
                                                             Integer maxStock,
                                                             LocalDate fromDate,
                                                             LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;
        return reportService.getInventoryReport(categoryId, active, minStock, maxStock, fromDateTime, toDateTime);
    }
    // Shutdown executor when controller is destroyed (optional, for completeness)
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        executor.shutdown();
    }
    }

