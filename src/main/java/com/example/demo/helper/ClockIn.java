package com.example.demo.helper;

public class ClockIn {

	private Long workerId;
	
	private Long siteId;

	public Long getWorkerId() {
		return workerId;
	}

	public void setWorkerId(Long workerId) {
		this.workerId = workerId;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	@Override
	public String toString() {
		return "ClockIn [workerId=" + workerId + ", siteId=" + siteId + "]";
	}

	public ClockIn(Long workerId, Long siteId) {
		super();
		this.workerId = workerId;
		this.siteId = siteId;
	}
	
	public ClockIn() {}
	
}
