package com.rv.campaign.controller;

import com.rv.campaign.dto.CampaignRequestDto;
import com.rv.campaign.dto.CampaignResponseDto;
import com.rv.campaign.dto.PriceCalculationRequestDto;
import com.rv.campaign.dto.PriceCalculationResponseDto;
import com.rv.campaign.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping("/create")
    public ResponseEntity<CampaignResponseDto> create(@RequestBody CampaignRequestDto requestDto) {
        CampaignResponseDto created = campaignService.createCampaign(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/active")
    public List<CampaignResponseDto> active(@RequestParam(required = false) Long productId,
                                            @RequestParam(required = false) Integer categoryId) {
        return campaignService.getActiveCampaigns(productId, categoryId);
    }

    @PostMapping("/calculate-price")
    public PriceCalculationResponseDto calculate(@RequestBody PriceCalculationRequestDto requestDto) {
        return campaignService.calculatePrice(requestDto);
    }
}
