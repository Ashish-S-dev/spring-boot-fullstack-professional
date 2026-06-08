package com.example.demo.helper;

public class ClockOut {

	private Long workerId;

	public Long getWorkerId() {
		return workerId;
	}

	public void setWorkerId(Long workerId) {
		this.workerId = workerId;
	}

	@Override
	public String toString() {
		return "ClockOut [workerId=" + workerId + "]";
	}

	public ClockOut(Long workerId) {
		super();
		this.workerId = workerId;
	}

	public ClockOut() {}
	
	
	
}
