package com.example.moneymanagement.controller;


import com.example.moneymanagement.DTO.IncomeDTO;
import com.example.moneymanagement.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO>addIncome(@RequestBody IncomeDTO dto){
        IncomeDTO saved=incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @GetMapping
    public ResponseEntity<List<IncomeDTO>>getIncomes(){
        List<IncomeDTO>expenses=incomeService.getCurrentMonthsIncomesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteIncome(@PathVariable Long id){
        incomeService.deleteIncomes(id);
        return ResponseEntity.noContent().build();
    }
}
