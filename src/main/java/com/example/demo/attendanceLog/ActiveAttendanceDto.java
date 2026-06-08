package com.example.demo.attendanceLog;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ActiveAttendanceDto implements Serializable{
	
	private Long attendanceId;
    private String workerName;
    private String siteName;
    private LocalDateTime clockIn;
	public Long getAttendanceId() {
		return attendanceId;
	}
	public void setAttendanceId(Long attendanceId) {
		this.attendanceId = attendanceId;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public LocalDateTime getClockIn() {
		return clockIn;
	}
	public void setClockIn(LocalDateTime clockIn) {
		this.clockIn = clockIn;
	}
    
    
	
}
