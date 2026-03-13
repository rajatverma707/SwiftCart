package com.rv.admin.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility methods to export non-inventory admin reports (sales, user activity, campaigns)
 * to CSV, Excel, and PDF formats.
 */
public final class AdminReportExporter {

    private AdminReportExporter() {
    }

    // ---- Sales ----

    public static String salesToCsv(SalesReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer Email,Total Orders,Total Revenue,Total Quantity,From,To\n");
        sb.append(escape(report.getCustomerEmail()))
          .append(',')
          .append(safe(report.getTotalOrders()))
          .append(',')
          .append(safe(report.getTotalRevenue()))
          .append(',')
          .append(safe(report.getTotalQuantity()))
          .append(',')
          .append(safe(report.getFromDate()))
          .append(',')
          .append(safe(report.getToDate()))
          .append('\n');
        return sb.toString();
    }

    public static byte[] salesToExcel(SalesReport report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sales");
            Row header = sheet.createRow(0);
            String[] headers = {"Customer Email", "Total Orders", "Total Revenue", "Total Quantity", "From", "To"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }

            Row row = sheet.createRow(1);
            int col = 0;
            row.createCell(col++).setCellValue(report.getCustomerEmail() != null ? report.getCustomerEmail() : "");
            row.createCell(col++).setCellValue(report.getTotalOrders());
            row.createCell(col++).setCellValue(report.getTotalRevenue());
            row.createCell(col++).setCellValue(report.getTotalQuantity());
            row.createCell(col++).setCellValue(safe(report.getFromDate()));
            row.createCell(col++).setCellValue(safe(report.getToDate()));

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate Excel sales report", e);
        }
    }

    public static byte[] salesToPdf(SalesReport report) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true)) {
                float margin = 40;
                float y = page.getMediaBox().getHeight() - margin;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                writeLine(contentStream, margin, y, "Sales Report");
                y -= 24;

                contentStream.setFont(PDType1Font.HELVETICA, 11);
                writeLine(contentStream, margin, y, "Customer: " + safe(report.getCustomerEmail()));
                y -= 16;
                writeLine(contentStream, margin, y, "Total Orders: " + report.getTotalOrders());
                y -= 14;
                writeLine(contentStream, margin, y, "Total Revenue: " + report.getTotalRevenue());
                y -= 14;
                writeLine(contentStream, margin, y, "Total Quantity: " + report.getTotalQuantity());
                y -= 14;
                writeLine(contentStream, margin, y, "From: " + safe(report.getFromDate()) + "    To: " + safe(report.getToDate()));
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate PDF sales report", e);
        }
    }

    // ---- User activity ----

    public static String userActivityToCsv(UserActivityReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("User ID,Name,Email,Role,Created,Updated,Total Orders,Last Order Date\n");
        sb.append(safe(report.getUserId()))
          .append(',')
          .append(escape(report.getName()))
          .append(',')
          .append(escape(report.getEmail()))
          .append(',')
          .append(escape(report.getRoleName()))
          .append(',')
          .append(safe(report.getCreatedDate()))
          .append(',')
          .append(safe(report.getUpdatedDate()))
          .append(',')
          .append(report.getTotalOrders())
          .append(',')
          .append(safe(report.getLastOrderDate()))
          .append('\n');
        return sb.toString();
    }

    public static byte[] userActivityToExcel(UserActivityReport report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("User Activity");
            Row header = sheet.createRow(0);
            String[] headers = {"User ID", "Name", "Email", "Role", "Created", "Updated", "Total Orders", "Last Order Date"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }

            Row row = sheet.createRow(1);
            int col = 0;
            if (report.getUserId() != null) {
                row.createCell(col++).setCellValue(report.getUserId());
            } else {
                row.createCell(col++).setCellValue(0);
            }
            row.createCell(col++).setCellValue(report.getName() != null ? report.getName() : "");
            row.createCell(col++).setCellValue(report.getEmail() != null ? report.getEmail() : "");
            row.createCell(col++).setCellValue(report.getRoleName() != null ? report.getRoleName() : "");
            row.createCell(col++).setCellValue(safe(report.getCreatedDate()));
            row.createCell(col++).setCellValue(safe(report.getUpdatedDate()));
            row.createCell(col++).setCellValue(report.getTotalOrders());
            row.createCell(col++).setCellValue(safe(report.getLastOrderDate()));

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate Excel user activity report", e);
        }
    }

    public static byte[] userActivityToPdf(UserActivityReport report) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true)) {
                float margin = 40;
                float y = page.getMediaBox().getHeight() - margin;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                writeLine(contentStream, margin, y, "User Activity Report");
                y -= 24;

                contentStream.setFont(PDType1Font.HELVETICA, 11);
                writeLine(contentStream, margin, y, "User: " + safe(report.getName()) + " (" + safe(report.getEmail()) + ")");
                y -= 16;
                writeLine(contentStream, margin, y, "Role: " + safe(report.getRoleName()));
                y -= 14;
                writeLine(contentStream, margin, y, "Created: " + safe(report.getCreatedDate()) + "    Updated: " + safe(report.getUpdatedDate()));
                y -= 14;
                writeLine(contentStream, margin, y, "Total Orders: " + report.getTotalOrders());
                y -= 14;
                writeLine(contentStream, margin, y, "Last Order Date: " + safe(report.getLastOrderDate()));
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate PDF user activity report", e);
        }
    }

    // ---- Campaigns ----

    public static String campaignsToCsv(CampaignReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Description,Discount Type,Discount Value,Product ID,Category ID,Coupon Code,Start At,End At,Active\n");
        if (report.getCampaigns() != null) {
            for (CampaignReportItem c : report.getCampaigns()) {
                sb.append(safe(c.getId()))
                  .append(',')
                  .append(escape(c.getName()))
                  .append(',')
                  .append(escape(c.getDescription()))
                  .append(',')
                  .append(escape(c.getDiscountType()))
                  .append(',')
                  .append(safe(c.getDiscountValue()))
                  .append(',')
                  .append(safe(c.getProductId()))
                  .append(',')
                  .append(safe(c.getCategoryId()))
                  .append(',')
                  .append(escape(c.getCouponCode()))
                  .append(',')
                  .append(safe(c.getStartAt()))
                  .append(',')
                  .append(safe(c.getEndAt()))
                  .append(',')
                  .append(c.getActive() != null && c.getActive() ? "YES" : "NO")
                  .append('\n');
            }
        }
        return sb.toString();
    }

    public static byte[] campaignsToExcel(CampaignReport report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Campaigns");
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "Name", "Description", "Discount Type", "Discount Value", "Product ID", "Category ID", "Coupon Code", "Start At", "End At", "Active"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            if (report.getCampaigns() != null) {
                for (CampaignReportItem c : report.getCampaigns()) {
                    Row row = sheet.createRow(rowIdx++);
                    int col = 0;
                    row.createCell(col++).setCellValue(c.getId() != null ? c.getId() : 0L);
                    row.createCell(col++).setCellValue(c.getName() != null ? c.getName() : "");
                    row.createCell(col++).setCellValue(c.getDescription() != null ? c.getDescription() : "");
                    row.createCell(col++).setCellValue(c.getDiscountType() != null ? c.getDiscountType() : "");
                    row.createCell(col++).setCellValue(c.getDiscountValue() != null ? c.getDiscountValue() : 0);
                    row.createCell(col++).setCellValue(c.getProductId() != null ? c.getProductId() : 0L);
                    row.createCell(col++).setCellValue(c.getCategoryId() != null ? c.getCategoryId() : 0);
                    row.createCell(col++).setCellValue(c.getCouponCode() != null ? c.getCouponCode() : "");
                    row.createCell(col++).setCellValue(safe(c.getStartAt()));
                    row.createCell(col++).setCellValue(safe(c.getEndAt()));
                    row.createCell(col++).setCellValue(c.getActive() != null && c.getActive() ? "YES" : "NO");
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate Excel campaigns report", e);
        }
    }

    public static byte[] campaignsToPdf(CampaignReport report) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true)) {
                float margin = 30;
                float y = page.getMediaBox().getHeight() - margin;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                writeLine(contentStream, margin, y, "Campaigns Report (Active: " + report.getTotalActive() + ")");
                y -= 20;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
                writeLine(contentStream, margin, y, String.format("%-5s %-20s %-10s %-8s %-8s", "ID", "Name", "Discount", "Start", "Active"));
                y -= 14;
                contentStream.setFont(PDType1Font.HELVETICA, 9);

                if (report.getCampaigns() != null) {
                    for (CampaignReportItem c : report.getCampaigns()) {
                        if (y < margin) {
                            break; // single page
                        }
                        String line = String.format("%-5s %-20s %-10s %-8s %-8s",
                                safe(c.getId()),
                                truncate(c.getName(), 20),
                                truncate(c.getDiscountType(), 10),
                                c.getStartAt() != null ? c.getStartAt().toLocalDate() : "",
                                c.getActive() != null && c.getActive() ? "YES" : "NO");
                        writeLine(contentStream, margin, y, line);
                        y -= 12;
                    }
                }
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate PDF campaigns report", e);
        }
    }

    // ---- helpers ----

    private static void writeLine(PDPageContentStream contentStream, float x, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private static String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private static String truncate(String value, int max) {
        if (value == null || value.length() <= max) {
            return value == null ? "" : value;
        }
        return value.substring(0, max - 3) + "...";
    }
}
