package com.rv.admin.serviceImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rv.admin.report.InventoryReportExporter;
import com.rv.admin.report.InventoryReportView;
import com.rv.admin.service.ReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {

    private final ReportService reportService;

    /**
     * Generate a daily inventory CSV report at 01:00 server time.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void generateDailyInventoryCsv() {
        List<InventoryReportView> data = reportService.getInventoryReport(null, null, null, null, null, null);
        String csv = InventoryReportExporter.toCsv(data);

        Path outputDir = Paths.get("reports");
        try {
            Files.createDirectories(outputDir);
            String filename = "inventory-report-" + LocalDate.now() + ".csv";
            Path file = outputDir.resolve(filename);
            Files.writeString(file, csv, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Generated daily inventory report at {}", file.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write daily inventory report", e);
        }
    }
}