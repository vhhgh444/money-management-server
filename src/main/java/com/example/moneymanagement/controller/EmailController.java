package com.example.moneymanagement.controller;

import com.example.moneymanagement.entity.ProfileEntity;
import com.example.moneymanagement.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final IncomeService incomeService;
    private final ProfileService profileService;
    private final ExpenseService expenseService;
    private final ExcelService excelService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void>emailIncomeExcel() throws IOException , MessagingException {
        ProfileEntity profile=profileService.getCurrentProfile();
        ByteArrayOutputStream boos=new ByteArrayOutputStream();
        excelService.writeIncomeToExcel(boos,incomeService.getCurrentMonthsIncomesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail(),"Your Income Excel Report","Please find attached your income report",boos.toByteArray(),"income.xlsx");
        return ResponseEntity.ok(null);

    }


    @GetMapping("/expense-excel")
    public ResponseEntity<Void>emailExpenseExcel() throws IOException , MessagingException {
        ProfileEntity profile=profileService.getCurrentProfile();
        ByteArrayOutputStream boos=new ByteArrayOutputStream();
        excelService.writeExpenseToExcel(boos,expenseService.getCurrentMonthsExpensesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail(),"Your Expense Excel Report","Please find attached your expense report",boos.toByteArray(),"expense.xlsx");
        return ResponseEntity.ok(null);

    }
}
