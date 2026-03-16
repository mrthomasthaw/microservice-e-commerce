package com.microservice_example.report_service.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
public class CommonExcelUtil {

    public static void writeToExcel(List<String> columnHeaders, List<Object[]> rows, OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Report");

        createHeaderRow(sheet.createRow(0), columnHeaders);

        createRows(sheet, rows);

        workbook.write(outputStream);
        workbook.close();
    }

    public static void writeToExcel(List<String> columnHeaders, Stream<Object[]> rowStream, OutputStream outputStream) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {

            log.info("Exporting in batch...");
            Sheet sheet = workbook.createSheet("Report");

            // Header row
            Row header = sheet.createRow(0);
            for(int x = 0; x < columnHeaders.size(); x++) {
                header.createCell(x).setCellValue(columnHeaders.get(x));
            }

            int[] count = {1};
            int[] rowNum = {1};


            try(Stream<Object[]> rows = rowStream)
            {

                log.info("Fetching in batch...");

                rows.forEach(rowData -> {
                    log.info("Writing to excel " + count[0]);

                    Row row = sheet.createRow(rowNum[0]);
                    row.createCell(0).setCellValue(rowNum[0]);
                    int j = 1;
                    for(var column : rowData) {
                        row.createCell(j++).setCellValue(column.toString());
                    }

                    rowNum[0]++;
                    count[0]++;
                });

            }

            workbook.write(outputStream);
            outputStream.flush();

            log.info("Writing in batch...");
        }
    }

    private static void createRows(Sheet sheet, List<Object[]> rows) {
        int rowIndex = 0;

        for(var record : rows) {
            log.info("Writing to excel row " + rowIndex);
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
