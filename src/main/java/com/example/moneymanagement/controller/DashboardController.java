package com.example.moneymanagement.controller;

import com.example.moneymanagement.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashBoardService dashBoardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>>getDashboardData(){
        Map<String,Object>dashboardData=dashBoardService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }
}
