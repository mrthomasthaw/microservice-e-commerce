package com.microservice_example.report_service.util;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public enum ReportType {
    PRODUCT_SUMMARY(1, "Product Summary"), SHOP_SUMMARY(2, "Shop Summary"),
    DAILY_SALE_SUMMARY(3, "Daily Sale Summary"),
    DETAIL_REPORT(4, "Detail Report");

    int code;
    String desc;

    ReportType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Optional<ReportType> getByCode(Integer groupByType) {
        if(groupByType == null)
            return Optional.empty();

        return Arrays.stream(ReportType.values()).filter(rt -> rt.code == groupByType).findFirst();
    }

    public static List<ReportType> getAll() {
        return Arrays.asList(ReportType.values());
    }
}
