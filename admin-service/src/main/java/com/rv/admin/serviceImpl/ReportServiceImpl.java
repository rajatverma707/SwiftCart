package com.rv.admin.serviceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.rv.admin.client.AuthUserResponse;
import com.rv.admin.client.OrderListResponse;
import com.rv.admin.client.OrderSummary;
import com.rv.admin.client.UserSummary;
import com.rv.admin.report.CampaignReport;
import com.rv.admin.report.CampaignReportItem;
import com.rv.admin.report.InventoryReportView;
import com.rv.admin.report.SalesReport;
import com.rv.admin.report.UserActivityReport;
import com.rv.admin.repository.ProductReportRepository;
import com.rv.admin.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    public ProductReportRepository reportRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${campaign.service.url}")
    private String campaignServiceUrl;

    @Override
    public List<InventoryReportView> getInventoryReport(Integer categoryId,
                                                        Boolean active,
                                                        Integer minStock,
                                                        Integer maxStock,
                                                        LocalDateTime fromDate,
                                                        LocalDateTime toDate) {
        return reportRepository.getInventoryReport(categoryId, active, minStock, maxStock, fromDate, toDate);
    }

    @Override
    public SalesReport getSalesReport(String customerEmail, LocalDateTime fromDate, LocalDateTime toDate) {
        List<OrderSummary> orders = fetchCustomerOrders(customerEmail);

        List<OrderSummary> filtered = orders.stream()
                .filter(o -> isWithinRange(o.getDateCreated(), fromDate, toDate))
                .collect(Collectors.toList());

        double totalRevenue = filtered.stream()
                .map(OrderSummary::getTotalPrice)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        int totalQuantity = filtered.stream()
                .map(OrderSummary::getTotalQuantity)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        SalesReport report = new SalesReport();
        report.setCustomerEmail(customerEmail);
        report.setTotalOrders(filtered.size());
        report.setTotalRevenue(totalRevenue);
        report.setTotalQuantity(totalQuantity);
        report.setFromDate(fromDate);
        report.setToDate(toDate);
        return report;
    }

    @Override
    public UserActivityReport getUserActivityReport(String customerEmail, LocalDateTime fromDate, LocalDateTime toDate) {
        UserSummary user = fetchUserByEmail(customerEmail);
        List<OrderSummary> orders = fetchCustomerOrders(customerEmail);

        List<OrderSummary> filtered = orders.stream()
                .filter(o -> isWithinRange(o.getDateCreated(), fromDate, toDate))
                .collect(Collectors.toList());

        LocalDateTime lastOrderDate = filtered.stream()
                .map(OrderSummary::getDateCreated)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        UserActivityReport report = new UserActivityReport();
        if (user != null) {
            report.setUserId(user.getUserId());
            report.setName(user.getName());
            report.setEmail(user.getEmail());
            report.setRoleName(user.getRoleName());
            report.setCreatedDate(user.getCreatedDate());
            report.setUpdatedDate(user.getUpdatedDate());
        } else {
            report.setEmail(customerEmail);
        }

        report.setTotalOrders(filtered.size());
        report.setLastOrderDate(lastOrderDate);
        return report;
    }

    @Override
    public CampaignReport getCampaignReport(Long productId, Integer categoryId) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(campaignServiceUrl + "/active");

        if (productId != null) {
            builder.queryParam("productId", productId);
        }
        if (categoryId != null) {
            builder.queryParam("categoryId", categoryId);
        }

        CampaignReport report = new CampaignReport();
        try {
            CampaignReportItem[] response = restTemplate.getForObject(builder.toUriString(), CampaignReportItem[].class);
            List<CampaignReportItem> items = response != null ? List.of(response) : Collections.emptyList();
            report.setCampaigns(items);
            report.setTotalActive(items.stream().filter(c -> Boolean.TRUE.equals(c.getActive())).count());
        } catch (RestClientException ex) {
            report.setCampaigns(Collections.emptyList());
            report.setTotalActive(0);
        }

        return report;
    }

    private List<OrderSummary> fetchCustomerOrders(String email) {
        if (email == null || email.isBlank()) {
            return Collections.emptyList();
        }

        String url = orderServiceUrl + "/customer-orders/" + email;
        try {
            OrderListResponse response = restTemplate.getForObject(url, OrderListResponse.class);
            if (response != null && response.getStatus() != null && response.getStatus() == 200 && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("Order service returned non-OK status for email {}: status={}, msg={}",
                        email,
                        response != null ? response.getStatus() : null,
                        response != null ? response.getMsg() : null);
            }
        } catch (RestClientException ex) {
            log.error("Failed to fetch customer orders from {}: {}", url, ex.getMessage());
        }
        return Collections.emptyList();
    }

    private UserSummary fetchUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String url = UriComponentsBuilder.fromHttpUrl(userServiceUrl + "/email")
                .queryParam("email", email)
                .toUriString();
        try {
            AuthUserResponse response = restTemplate.getForObject(url, AuthUserResponse.class);
            if (response != null && response.getStatusCode() != null && response.getStatusCode() == 200) {
                return response.getData();
            } else {
                log.warn("Auth service returned non-OK status for email {}: statusCode={}, message={}",
                        email,
                        response != null ? response.getStatusCode() : null,
                        response != null ? response.getMessage() : null);
            }
        } catch (RestClientException ex) {
            log.error("Failed to fetch user details from {}: {}", url, ex.getMessage());
        }
        return null;
    }

    private boolean isWithinRange(LocalDateTime value, LocalDateTime from, LocalDateTime to) {
        if (value == null) {
            return false;
        }
        if (from != null && value.isBefore(from)) {
            return false;
        }
        if (to != null && value.isAfter(to)) {
            return false;
        }
        return true;
    }
}
