package com.rv.admin.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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

public final class InventoryReportExporter {

    private InventoryReportExporter() {
    }

    public static String toCsv(List<InventoryReportView> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Product ID,Product Name,Category,Stock,Active\n");
        for (InventoryReportView row : data) {
            sb.append(safe(row.getProductId()))
              .append(',')
              .append(escape(row.getProductName()))
              .append(',')
              .append(escape(row.getCategoryName()))
              .append(',')
              .append(safe(row.getStock()))
              .append(',')
              .append(row.getActive() != null && row.getActive() ? "YES" : "NO")
              .append('\n');
        }
        return sb.toString();
    }

    public static byte[] toExcel(List<InventoryReportView> data) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inventory");
            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            String[] headers = {"Product ID", "Product Name", "Category", "Stock", "Active"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }

            for (InventoryReportView rowData : data) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(rowData.getProductId() != null ? rowData.getProductId() : 0);
                row.createCell(col++).setCellValue(rowData.getProductName() != null ? rowData.getProductName() : "");
                row.createCell(col++).setCellValue(rowData.getCategoryName() != null ? rowData.getCategoryName() : "");
                row.createCell(col++).setCellValue(rowData.getStock() != null ? rowData.getStock() : 0);
                row.createCell(col++).setCellValue(rowData.getActive() != null && rowData.getActive() ? "YES" : "NO");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate Excel inventory report", e);
        }
    }

    public static byte[] toPdf(List<InventoryReportView> data) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true)) {
                float margin = 40;
                float y = page.getMediaBox().getHeight() - margin;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, y);
                contentStream.showText("Inventory Report");
                contentStream.endText();

                y -= 24;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                writeLine(contentStream, margin, y, String.format("%-10s %-30s %-20s %-8s %-8s",
                        "ID", "Name", "Category", "Stock", "Active"));

                y -= 16;
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                for (InventoryReportView row : data) {
                    if (y < margin) {
                        break; // simple single-page output
                    }
                    String line = String.format("%-10s %-30s %-20s %-8s %-8s",
                            safe(row.getProductId()),
                            truncate(row.getProductName(), 30),
                            truncate(row.getCategoryName(), 20),
                            safe(row.getStock()),
                            row.getActive() != null && row.getActive() ? "YES" : "NO");
                    writeLine(contentStream, margin, y, line);
                    y -= 14;
                }
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate PDF inventory report", e);
        }
    }

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