package com.example.demo.attendanceLog;

import com.example.demo.site.Site;
import com.example.demo.worker.Worker;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;


@Entity
@Table(
        name = "attendance_logs",
        indexes = {
                @Index(name = "idx_attendance_worker", columnList = "worker_id"),
                @Index(name = "idx_attendance_site", columnList = "site_id"),
                @Index(name = "idx_attendance_clockin", columnList = "clockIn")
        }
)
public class AttendanceLog implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "worker_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_attendance_worker")
    )
    private Worker worker;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "site_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_attendance_site")
    )
    private Site site;

    @Column(nullable = false)
    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    @Column(nullable = false)
    private Double totalHours = 0.0;

    @Column(nullable = false)
    private Double overtimeHours = 0.0;

    @Column(nullable = false)
    private Boolean flagged = false;

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

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public LocalDateTime getClockIn() {
		return clockIn;
	}

	public void setClockIn(LocalDateTime clockIn) {
		this.clockIn = clockIn;
	}

	public LocalDateTime getClockOut() {
		return clockOut;
	}

	public void setClockOut(LocalDateTime clockOut) {
		this.clockOut = clockOut;
	}

	public Double getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(Double totalHours) {
		this.totalHours = totalHours;
	}

	public Double getOvertimeHours() {
		return overtimeHours;
	}

	public void setOvertimeHours(Double overtimeHours) {
		this.overtimeHours = overtimeHours;
	}

	public Boolean getFlagged() {
		return flagged;
	}

	public void setFlagged(Boolean flagged) {
		this.flagged = flagged;
	}

	@Override
	public String toString() {
		return "AttendanceLog [id=" + id + ", worker=" + worker + ", site=" + site + ", clockIn=" + clockIn
				+ ", clockOut=" + clockOut + ", totalHours=" + totalHours + ", overtimeHours=" + overtimeHours
				+ ", flagged=" + flagged + "]";
	}

	public AttendanceLog(Worker worker, Site site, LocalDateTime clockIn, LocalDateTime clockOut, Double totalHours,
			Double overtimeHours, Boolean flagged) {
		super();
		this.worker = worker;
		this.site = site;
		this.clockIn = clockIn;
		this.clockOut = clockOut;
		this.totalHours = totalHours;
		this.overtimeHours = overtimeHours;
		this.flagged = flagged;
	}
    
    public AttendanceLog() {}
}