package com.stripe.payment_service_provider.subscription.controller;

import com.stripe.payment_service_provider.subscription.dto.PlanDTO;
import com.stripe.payment_service_provider.subscription.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        List<PlanDTO> planDTOList = planService.getAllPlans();
        return ResponseEntity.ok(planDTOList);
    }
}
