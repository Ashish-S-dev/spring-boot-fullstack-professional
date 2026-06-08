package com.example.demo.overtimeEntry;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import com.example.demo.attendanceLog.AttendanceLog;
import com.example.demo.worker.Worker;

@Entity
@Table(
        name = "overtime_entries",
        indexes = {
                @Index(name = "idx_overtime_worker", columnList = "worker_id"),
                @Index(name = "idx_overtime_date", columnList = "entry_date"),
                @Index(name = "idx_overtime_status", columnList = "status")
        }
)

public class OvertimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "worker_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_overtime_worker")
    )
    private Worker worker;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "attendance_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_overtime_attendance")
    )
    private AttendanceLog attendance;

    @Column(name = "entry_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double overtimeHours;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal overtimeRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status = SettlementStatus.PENDING;
    
    
    

	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
	}




	public Worker getWorker() {
		return worker;
	}




	public void setWorker(Worker worker) {
		this.worker = worker;
	}




	public AttendanceLog getAttendance() {
		return attendance;
	}




	public void setAttendance(AttendanceLog attendance) {
		this.attendance = attendance;
	}




	public LocalDate getDate() {
		return date;
	}




	public void setDate(LocalDate date) {
		this.date = date;
	}




	public Double getOvertimeHours() {
		return overtimeHours;
	}




	public void setOvertimeHours(Double overtimeHours) {
		this.overtimeHours = overtimeHours;
	}




	public BigDecimal getOvertimeRate() {
		return overtimeRate;
	}




	public void setOvertimeRate(BigDecimal overtimeRate) {
		this.overtimeRate = overtimeRate;
	}




	public BigDecimal getAmount() {
		return amount;
	}




	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}




	public SettlementStatus getStatus() {
		return status;
	}




	public void setStatus(SettlementStatus status) {
		this.status = status;
	}


	public OvertimeEntry() {
		// TODO Auto-generated method stub

	}

	public OvertimeEntry(Worker worker, AttendanceLog attendance, LocalDate date, Double overtimeHours,
			BigDecimal overtimeRate, BigDecimal amount, SettlementStatus status) {
		super();
		this.worker = worker;
		this.attendance = attendance;
		this.date = date;
		this.overtimeHours = overtimeHours;
		this.overtimeRate = overtimeRate;
		this.amount = amount;
		this.status = status;
	}




	@Override
	public String toString() {
		return "OvertimeEntry [id=" + id + ", worker=" + worker + ", attendance=" + attendance + ", date=" + date
				+ ", overtimeHours=" + overtimeHours + ", overtimeRate=" + overtimeRate + ", amount=" + amount
				+ ", status=" + status + "]";
	}
    
    
    
}