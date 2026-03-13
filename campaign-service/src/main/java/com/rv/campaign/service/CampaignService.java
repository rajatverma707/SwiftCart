package com.rv.campaign.service;

import com.rv.campaign.dto.CampaignRequestDto;
import com.rv.campaign.dto.CampaignResponseDto;
import com.rv.campaign.dto.PriceCalculationRequestDto;
import com.rv.campaign.dto.PriceCalculationResponseDto;

import java.util.List;

public interface CampaignService {

    CampaignResponseDto createCampaign(CampaignRequestDto requestDto);

    List<CampaignResponseDto> getActiveCampaigns(Long productId, Integer categoryId);

    PriceCalculationResponseDto calculatePrice(PriceCalculationRequestDto requestDto);
}
