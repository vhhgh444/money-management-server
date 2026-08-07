package com.example.moneymanagement.controller;

import com.example.moneymanagement.service.ExcelService;
import com.example.moneymanagement.service.ExpenseService;
import com.example.moneymanagement.service.IncomeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final IncomeService incomeService;
    private final ExcelService excelService;
    private final ExpenseService expenseService;

    @GetMapping("/download/income")
//    public ResponseEntity<byte[]>downloadIncome(){
//        byte[] fileBytes=incomeService.downloadIncomeExcel();
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=income_details.xlsx")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(fileBytes);
//    }
    public void downloadIncomeExcel(HttpServletResponse response)throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=income.xlsx");
        excelService.writeIncomeToExcel(response.getOutputStream(),incomeService.getCurrentMonthsIncomesForCurrentUser());

    }
    @GetMapping("/download/expense")
    public void downloadExpenseExcel(HttpServletResponse response) throws IOException{
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=income.xlsx");
        excelService.writeExpenseToExcel(response.getOutputStream(),expenseService.getCurrentMonthsExpensesForCurrentUser());
    }
}
