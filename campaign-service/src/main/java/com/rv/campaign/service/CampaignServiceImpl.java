package com.rv.campaign.service;

import com.rv.campaign.dto.CampaignRequestDto;
import com.rv.campaign.dto.CampaignResponseDto;
import com.rv.campaign.dto.PriceCalculationRequestDto;
import com.rv.campaign.dto.PriceCalculationResponseDto;
import com.rv.campaign.entity.Campaign;
import com.rv.campaign.exception.CampaignNotFoundException;
import com.rv.campaign.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final WebClient webClient; // reserved for future product validation calls

    @Override
    public CampaignResponseDto createCampaign(CampaignRequestDto requestDto) {
        Campaign campaign = new Campaign();
        campaign.setName(requestDto.getName());
        campaign.setDescription(requestDto.getDescription());
        campaign.setDiscountType(requestDto.getDiscountType());
        campaign.setDiscountValue(requestDto.getDiscountValue());
        campaign.setProductId(requestDto.getProductId());
        campaign.setCategoryId(requestDto.getCategoryId());
        campaign.setCouponCode(requestDto.getCouponCode());
        campaign.setStartAt(requestDto.getStartAt());
        campaign.setEndAt(requestDto.getEndAt());
        campaign.setActive(Boolean.TRUE.equals(requestDto.getActive()));

        Campaign saved = campaignRepository.save(campaign);
        return mapToDto(saved);
    }

    @Override
    public List<CampaignResponseDto> getActiveCampaigns(Long productId, Integer categoryId) {
        LocalDateTime now = LocalDateTime.now();
        return campaignRepository.findActiveCampaigns(now, productId, categoryId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PriceCalculationResponseDto calculatePrice(PriceCalculationRequestDto requestDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Campaign> active = campaignRepository.findActiveCampaigns(now, requestDto.getProductId(), requestDto.getCategoryId());

        if (active.isEmpty() && requestDto.getCouponCode() != null) {
            Campaign byCoupon = campaignRepository.findByCouponCodeAndActiveTrue(requestDto.getCouponCode())
                    .orElseThrow(() -> new CampaignNotFoundException("No active campaign for coupon"));
            active = List.of(byCoupon);
        }

        if (active.isEmpty()) {
            PriceCalculationResponseDto resp = new PriceCalculationResponseDto();
            resp.setOriginalPrice(requestDto.getOriginalPrice());
            resp.setDiscountedPrice(requestDto.getOriginalPrice());
            return resp;
        }

        Campaign campaign = active.get(0);
        double discounted = requestDto.getOriginalPrice();
        switch (campaign.getDiscountType()) {
            case PERCENTAGE -> discounted = discounted - (discounted * (campaign.getDiscountValue() / 100.0));
            case FIXED -> discounted = discounted - campaign.getDiscountValue();
        }
        if (discounted < 0) discounted = 0.0;

        PriceCalculationResponseDto resp = new PriceCalculationResponseDto();
        resp.setOriginalPrice(requestDto.getOriginalPrice());
        resp.setDiscountedPrice(discounted);
        resp.setCampaignName(campaign.getName());
        resp.setCouponCode(campaign.getCouponCode());
        return resp;
    }

    private CampaignResponseDto mapToDto(Campaign campaign) {
        CampaignResponseDto dto = new CampaignResponseDto();
        dto.setId(campaign.getId());
        dto.setName(campaign.getName());
        dto.setDescription(campaign.getDescription());
        dto.setDiscountType(campaign.getDiscountType());
        dto.setDiscountValue(campaign.getDiscountValue());
        dto.setProductId(campaign.getProductId());
        dto.setCategoryId(campaign.getCategoryId());
        dto.setCouponCode(campaign.getCouponCode());
        dto.setStartAt(campaign.getStartAt());
        dto.setEndAt(campaign.getEndAt());
        dto.setActive(campaign.getActive());
        return dto;
    }
}
