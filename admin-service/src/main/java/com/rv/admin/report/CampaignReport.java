package com.rv.admin.report;

import java.util.List;

import lombok.Data;

@Data
public class CampaignReport {

    private long totalActive;
    private List<CampaignReportItem> campaigns;
}
