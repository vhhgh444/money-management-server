package com.example.moneymanagement.service;

import com.example.moneymanagement.DTO.ExpenseDTO;
import com.example.moneymanagement.DTO.IncomeDTO;
import com.example.moneymanagement.entity.CategoryEntity;
import com.example.moneymanagement.entity.ExpenseEntity;
import com.example.moneymanagement.entity.IncomeEntity;
import com.example.moneymanagement.entity.ProfileEntity;
import com.example.moneymanagement.repository.CategoryRepository;
import com.example.moneymanagement.repository.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepo incomeRepo;
    private final ProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO dto){
        ProfileEntity profile= profileService.getCurrentProfile();
        CategoryEntity category= categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));
        IncomeEntity newExpense=toEntity(dto,profile,category);
        newExpense=incomeRepo.save(newExpense);
        return toDTO(newExpense);
    }

    public List<IncomeDTO> getCurrentMonthsIncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity>list=incomeRepo.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteIncomes(Long incomeId){
        ProfileEntity profile= profileService.getCurrentProfile();
        IncomeEntity entity=incomeRepo.findById(incomeId)
                .orElseThrow(()->new RuntimeException("Income not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepo.delete(entity);
    }

    //Get latest 5 incomes for current user
    public List<IncomeDTO>getLatest5IncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity>list=incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    // Get the total incomes for current user
    public BigDecimal getTotalIncomeForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal total=incomeRepo.findTotalIncomeByProfileId(profile.getId());
        return total !=null?total:BigDecimal.ZERO;
    }

    // Filter Incomes
    public List<IncomeDTO>filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity>list=incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return list.stream().map(this::toDTO).toList();
    }

    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();

    }
    private IncomeDTO toDTO(IncomeEntity income) {
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory().getId())
                .categoryName(income.getCategory().getName()) // CategoryEntity must have getName()
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
//    public byte[] downloadIncomeExcel() {
//
//        List<IncomeDTO> incomes = getCurrentMonthsIncomesForCurrentUser();
//
//        try (Workbook workbook = new XSSFWorkbook();
//             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//
//            Sheet sheet = workbook.createSheet("Income Details");
//
//            // Header Row
//            Row header = sheet.createRow(0);
//            header.createCell(0).setCellValue("Name");
//            header.createCell(1).setCellValue("Category");
//            header.createCell(2).setCellValue("Amount");
//            header.createCell(3).setCellValue("Date");
//
//            // Data Rows
//            int rowNum = 1;
//
//            for (IncomeDTO income : incomes) {
//                Row row = sheet.createRow(rowNum++);
//
//                row.createCell(0).setCellValue(income.getName());
//                row.createCell(1).setCellValue(income.getCategoryName());
//                row.createCell(2).setCellValue(income.getAmount().doubleValue());
//                row.createCell(3).setCellValue(income.getDate().toString());
//            }
//
//            // Auto-size columns
//            for (int i = 0; i < 4; i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//            workbook.write(outputStream);
//
//            return outputStream.toByteArray();
//
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to generate Excel file", e);
//        }
//    }
}
