package com.example.demo.overtimeEntry;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.student.exception.ConflictException;
import com.example.demo.student.exception.ResourceNotFoundException;
import com.example.demo.student.exception.ValidationException;
import com.example.demo.worker.Worker;
import com.example.demo.worker.WorkerService;

@Service
public class OvertimeService {

	@Autowired
	private OvertimeRepository overtimeRepository;
	
	@Autowired
	private WorkerService workerService;
	
	public OvertimeSummary getMonthlySummary(
	        Long workerId,
	        String month) {

	    YearMonth yearMonth =
	            YearMonth.parse(month);

	    LocalDate startDate =
	            yearMonth.atDay(1);

	    LocalDate endDate =
	            yearMonth.atEndOfMonth();

	    List<OvertimeEntry> overtimeEntries =
	            overtimeRepository
	                    .findByWorkerIdAndDateBetween(
	                            workerId,
	                            startDate,
	                            endDate);

	    OvertimeSummary summary =
	            new OvertimeSummary();
	    
	    summary.setWorkerId(workerId);
	    summary.setMonth(month);

	    // Total Hours
	    Double totalHours =
	            overtimeEntries.stream()
	                    .mapToDouble(
	                            OvertimeEntry::getOvertimeHours)
	                    .sum();

	    summary.setTotalOvertimeHours(totalHours);

	    // Total Amount
	    BigDecimal totalAmount =
	            overtimeEntries.stream()
	                    .map(OvertimeEntry::getAmount)
	                    .reduce(
	                            BigDecimal.ZERO,
	                            BigDecimal::add);

	    summary.setTotalPayoutAmount(totalAmount);

	    // Breakdown
	    List<OvertimeEntry> breakdown =
	            overtimeEntries.stream()
	                    .map(entry -> {

	                    	OvertimeEntry dto =
	                                new OvertimeEntry();

	                        dto.setDate(
	                                entry.getDate());

	                        dto.setOvertimeHours(
	                                entry.getOvertimeHours());

	                        dto.setOvertimeRate(
	                                entry.getOvertimeRate());

	                        dto.setAmount(
	                                entry.getAmount());

	                        dto.setStatus(
	                                entry.getStatus());

	                        return dto;

	                    }).collect(Collectors.toList());

	    summary.setBreakdown(breakdown);

	    // Settlement Status
	    boolean allSettled =
	            overtimeEntries.stream()
	                    .allMatch(entry ->
	                            entry.getStatus()
	                                    == SettlementStatus.SETTLED);

	    summary.setSettlementStatus(
	            allSettled
	                    ? SettlementStatus.SETTLED
	                    : SettlementStatus.PENDING);

	    return summary;
	}
	
	@Transactional
	public String settleOvertime(
	        Long workerId,
	        String month) {

	    YearMonth requestedMonth =
	            YearMonth.parse(month);

	    YearMonth currentMonth =
	            YearMonth.now();

	    // Cannot settle current month
	    if(requestedMonth.equals(currentMonth)) {
	        //return "CURRENT_MONTH_CANNOT_BE_SETTLED";
	    	if(requestedMonth.equals(currentMonth)) {
	    	    throw new ConflictException(
	    	            "Current month overtime cannot be settled"
	    	    );
	    	}
	    }

	    // Cannot settle future month
	    if(requestedMonth.isAfter(currentMonth)) {
	        //return "INVALID_MONTH";
	    	if(requestedMonth.isAfter(currentMonth)) {
	    	    throw new ValidationException(
	    	            "Future month is not allowed"
	    	    );
	    	}
	    }

	    Worker worker =
	            workerService.findByWorkerId(workerId);

	    if(worker.getId() == null) {
	        //return "WORKER_ABSENCE";
	    	if(worker.getId() == null) {
	    	    throw new ResourceNotFoundException(
	    	            "Worker not found with id: " + workerId
	    	    );
	    	}
	    }

	    LocalDate startDate =
	            requestedMonth.atDay(1);

	    LocalDate endDate =
	            requestedMonth.atEndOfMonth();

	    List<OvertimeEntry> entries =
	            overtimeRepository
	                    .findByWorkerIdAndDateBetween(
	                            workerId,
	                            startDate,
	                            endDate);

	    if(entries.isEmpty()) {
	        //return "NO_OVERTIME_FOUND";
	    	if(entries.isEmpty()) {
	    	    throw new ResourceNotFoundException(
	    	            "No overtime records found for month: " + month
	    	    );
	    	}
	    }

	    int updatedCount =
	            overtimeRepository
	                    .settleOvertimeEntries(
	                            workerId,
	                            startDate,
	                            endDate,
	                            SettlementStatus.SETTLED);

	    return updatedCount
	            + "OVERTIME_ENTRIES_SETTLED";
	}
	
}
