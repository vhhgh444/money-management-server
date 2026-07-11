package com.example.moneymanagement.service;

import com.example.moneymanagement.DTO.ExpenseDTO;
import com.example.moneymanagement.entity.CategoryEntity;
import com.example.moneymanagement.entity.ExpenseEntity;
import com.example.moneymanagement.entity.ProfileEntity;
import com.example.moneymanagement.repository.CategoryRepository;
import com.example.moneymanagement.repository.ExpenseRepo;
import jdk.dynalink.linker.LinkerServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepo expenseRepo;
    private final ProfileService profileService;


    public ExpenseDTO addExpense(ExpenseDTO dto){
       ProfileEntity profile= profileService.getCurrentProfile();
      CategoryEntity category= categoryRepository.findById(dto.getCategoryId())
              .orElseThrow(()->new RuntimeException("Category not found"));
      ExpenseEntity newExpense=toEntity(dto,profile,category);
      newExpense=expenseRepo.save(newExpense);
      return toDTO(newExpense);
    }
    // Retrive all expenses for the current month or based on startDate and endDate
    public List<ExpenseDTO>getCurrentMonthsExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity>list=expenseRepo.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDTO).toList();
    }

    // Delete Expense by id for current user
    public void deleteExpense(Long expenseId){
       ProfileEntity profile= profileService.getCurrentProfile();
       ExpenseEntity entity=expenseRepo.findById(expenseId)
               .orElseThrow(()->new RuntimeException("Expense not found"));
       if(!entity.getProfile().getId().equals(profile.getId())){
           throw new RuntimeException("Unauthorized to delete this expense");
       }
       expenseRepo.delete(entity);
    }

    //Get latest 5 expenses for current user
    public List<ExpenseDTO>getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity>list=expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    // Get the total expenses for current user
    public BigDecimal getTotalExpenseForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal total=expenseRepo.findTotalExpenseByProfileId(profile.getId());
        return total !=null?total:BigDecimal.ZERO;
    }

    // Filter Expenses
    public List<ExpenseDTO>filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity>list=expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return list.stream().map(this::toDTO).toList();
    }

    //Notifications
    public List<ExpenseDTO>getExpensesForUserOnDate(Long profileId,LocalDate date){
       List<ExpenseEntity>list= expenseRepo.findByProfileIdAndDate(profileId,date);
       return list.stream().map(this::toDTO).toList();
    }

    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();

    }
    private ExpenseDTO toDTO(ExpenseEntity expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName()) // CategoryEntity must have getName()
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

}
