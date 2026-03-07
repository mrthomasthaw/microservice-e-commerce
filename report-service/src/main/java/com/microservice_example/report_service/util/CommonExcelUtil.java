package com.microservice_example.report_service.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonExcelUtil {

    public static void writeToExcel(List<String> columnHeaders, List<Object[]> rows, OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Report");

        createHeaderRow(sheet.createRow(0), columnHeaders);

        createRows(sheet, rows);

        workbook.write(outputStream);
        workbook.close();
    }

    private static void createRows(Sheet sheet, List<Object[]> rows) {
        int rowIndex = 0;
        for(var record : rows) {
            Row row = sheet.createRow(++rowIndex);
            int j = 0;
            row.createCell(j).setCellValue(rowIndex);
            for(var col : record) {
                j++;
                row.createCell(j).setCellValue(col.toString());
            }
        }
    }

    private static void createHeaderRow(Row row, List<String> columnHeaders) {

        var i = new AtomicInteger(0);
        columnHeaders.forEach(col -> {
            row.createCell(i.getAndIncrement()).setCellValue(col);
        });
    }
}
