package com.example.demo.attendanceLog;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.helper.ClockIn;
import com.example.demo.helper.ClockOut;

@RestController
@RequestMapping(value="/api/attendance")
public class AttendanceLogController {

	@Autowired
	private AttendanceLogService attendenceService;
	
//	Clock in
	@PostMapping(value="/clock-in")
	public String clockIn(@RequestBody ClockIn clockInObj) {
		System.out.println(clockInObj.getWorkerId() +" " + clockInObj.getSiteId());
		return attendenceService.clockIn(clockInObj.getWorkerId(), clockInObj.getSiteId());
		
	}
	
//	Clock Out
	@PostMapping(value="/clock-out")
	public String clockOut(@RequestBody ClockOut clockOutObj) {
		
		return attendenceService.clockOut(clockOutObj.getWorkerId());
		
	}
	
//	Find all worker attendance within start date and end date
	@GetMapping("/log")
	public Page<AttendanceLog> getAttendanceHistory(

	        @RequestParam Long workerId,

	        @RequestParam
	        @DateTimeFormat(
	                iso = DateTimeFormat.ISO.DATE)
	        LocalDate from,

	        @RequestParam
	        @DateTimeFormat(
	                iso = DateTimeFormat.ISO.DATE)
	        LocalDate to,

	        @RequestParam(defaultValue = "0")
	        int page,

	        @RequestParam(defaultValue = "10")
	        int size
	) {

	    return attendenceService
	            .getAttendanceHistory(
	                    workerId,
	                    from,
	                    to,
	                    page,
	                    size
	            );
	}
	
//	Find all workers Currently clocked in across all cities
	@GetMapping(value="/active")
	public Iterable<ActiveAttendanceDto> allWorkers(){
		
		return attendenceService.findAllClockedInWorker();
		
	}
	
}
