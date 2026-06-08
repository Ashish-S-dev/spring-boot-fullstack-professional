package com.example.demo.overtimeEntry;

import java.math.BigDecimal;
import java.util.List;

public class OvertimeSummary {
	
	private Long workerId;

    private String month;

    private Double totalOvertimeHours;

    private BigDecimal totalPayoutAmount;

    private SettlementStatus settlementStatus;

    private List<OvertimeEntry> breakdown;

	public Long getWorkerId() {
		return workerId;
	}

	public void setWorkerId(Long workerId) {
		this.workerId = workerId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Double getTotalOvertimeHours() {
		return totalOvertimeHours;
	}

	public void setTotalOvertimeHours(Double totalOvertimeHours) {
		this.totalOvertimeHours = totalOvertimeHours;
	}

	public BigDecimal getTotalPayoutAmount() {
		return totalPayoutAmount;
	}

	public void setTotalPayoutAmount(BigDecimal totalPayoutAmount) {
		this.totalPayoutAmount = totalPayoutAmount;
	}

	public SettlementStatus getSettlementStatus() {
		return settlementStatus;
	}

	public void setSettlementStatus(SettlementStatus settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	public List<OvertimeEntry> getBreakdown() {
		return breakdown;
	}

	public void setBreakdown(List<OvertimeEntry> breakdown) {
		this.breakdown = breakdown;
	}

	public OvertimeSummary(Long workerId, String month, Double totalOvertimeHours, BigDecimal totalPayoutAmount,
			SettlementStatus settlementStatus, List<OvertimeEntry> breakdown) {
		super();
		this.workerId = workerId;
		this.month = month;
		this.totalOvertimeHours = totalOvertimeHours;
		this.totalPayoutAmount = totalPayoutAmount;
		this.settlementStatus = settlementStatus;
		this.breakdown = breakdown;
	}
	public OvertimeSummary() {}
    
    
}
