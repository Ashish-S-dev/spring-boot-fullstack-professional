package com.example.demo.attendanceLog;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.overtimeEntry.OvertimeEntry;
import com.example.demo.overtimeEntry.OvertimeRepository;
import com.example.demo.overtimeEntry.SettlementStatus;
import com.example.demo.site.Site;
import com.example.demo.site.SiteService;
import com.example.demo.student.exception.ConflictException;
import com.example.demo.student.exception.ResourceNotFoundException;
import com.example.demo.student.exception.ValidationException;
import com.example.demo.worker.Worker;
import com.example.demo.worker.WorkerService;

@Service
public class AttendanceLogService {

	@Autowired
	private AttendanceLogRepository attendanceLogRepository;

	@Autowired
	private WorkerService workerService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private OvertimeRepository overtimeRepository;
	
// Clock in
	@Transactional
	@CacheEvict(value = "activeAttendance", allEntries = true)
	public String clockIn(Long workerId, Long siteId) {

		// Check Worker Exist or not
		Worker workerObj = workerService.findByWorkerId(workerId);

		// check worker presence and active or not
		Boolean workerPresence = false;
		Boolean activeWorker = false;
		if (workerObj.getId() != null) {
			workerPresence = true;

			if (workerObj.isActive()) {
				activeWorker = true;
			} else {
//				return "WORKER_NOT_ACTIVE";
				throw new ResourceNotFoundException("Worker Not Active"); 	
			}
		} else {
			//return "WORKER_ABSENCE";
			throw new ResourceNotFoundException("Worker Not Found Exception");
		}

		// check Site active or not
		Site siteObj = siteService.findBySiteId(siteId);

		Boolean siteActive = false;
		if (siteObj.getId() != null && siteObj.isActive()) {

			siteActive = true;

		} else {

			siteActive = false;
			//return "SITE_NOT_ACTIVE";
			throw new ValidationException(
			        "Site is not active"
			);

		}

		// Double Checkin
		Boolean checkedIn = false;
		AttendanceLog attendanceObj = findByWorkerIdAndClockOutIsNull(workerId);
		if (attendanceObj.getId() == null) {
			checkedIn = true;
		} else {
			//return "ALREADY_CHECKED_IN";
			throw new ConflictException(
			        "Worker is already clocked in"
			);
		}

//		Final Condition
		if (workerPresence && activeWorker && siteActive && checkedIn) {

			AttendanceLog attendanceLog = new AttendanceLog();
			attendanceLog.setWorker(workerObj);
			attendanceLog.setSite(siteObj);
			attendanceLog.setClockIn(LocalDateTime.now());
			attendanceLog.setFlagged(false);

			if (saveAttendance(attendanceLog).getId() != null) {
				return "ATTENDANCE_UPDATED";
			}

		}

		return "RETRY";
	}

// Clock Out
	@Transactional
	@CacheEvict(value = "activeAttendance", allEntries = true)
	public String clockOut(Long workerId) {

	    // Validate Worker
	    Worker workerObj = workerService.findByWorkerId(workerId);

	    if (workerObj == null) {
	        throw new ResourceNotFoundException(
	                "Worker not found with id: " + workerId
	        );
	    }

	    // Find Active Attendance
	    AttendanceLog attendanceObj =
	            findByWorkerIdAndClockOutIsNull(workerId);

	    if (attendanceObj == null) {
	        throw new ConflictException(
	                "Worker is not currently clocked in"
	        );
	    }
	    if (attendanceObj.getClockIn() == null) {
	        throw new ConflictException(
	                "Attendance record is corrupted. Clock-in time is missing."
	        );
	    }

	    // Clock Out Time
	    LocalDateTime clockOutTime = LocalDateTime.now();

	    attendanceObj.setClockOut(clockOutTime);

	    // Calculate Total Hours
	    Duration duration =
	            Duration.between(
	                    attendanceObj.getClockIn(),
	                    clockOutTime
	            );

	    double totalHours =
	            duration.toMinutes() / 60.0;

	    attendanceObj.setTotalHours(totalHours);

	    // 16 Hour Flag Rule
	    attendanceObj.setFlagged(totalHours > 16);

	    // Calculate Overtime
	    double overtimeHours =
	            Math.max(0, totalHours - 8);

	    attendanceObj.setOvertimeHours(overtimeHours);

	    // Save Attendance
	    AttendanceLog savedAttendance =
	            attendanceLogRepository.save(attendanceObj);

	    // No Overtime
	    if (overtimeHours <= 0) {
	        return "CLOCK_OUT_SUCCESS";
	    }

	    // Current Month OT
	    YearMonth currentMonth = YearMonth.now();

	    Double monthlyOt =
	            overtimeRepository.getMonthlyOvertimeHours(
	                    workerId,
	                    currentMonth.getYear(),
	                    currentMonth.getMonthValue()
	            );

	    if (monthlyOt == null) {
	        monthlyOt = 0.0;
	    }

	    // Monthly Cap = 60 Hours
	    double remainingOtAllowed =
	            Math.max(0, 60 - monthlyOt);

	    double overtimeToSave =
	            Math.min(
	                    overtimeHours,
	                    remainingOtAllowed
	            );

	    // Attendance saved but cap reached
	    if (overtimeToSave <= 0) {
	        return "CLOCK_OUT_SUCCESS";
	    }

	    // Hourly Wage
	    double hourlyRate =
	            workerObj.getDailyWageRate()
	                    .doubleValue() / 8;

	    // OT Amount Calculation
	    double overtimeAmount =
	            calculateOvertimeAmount(
	                    overtimeToSave,
	                    hourlyRate
	            );

	    OvertimeEntry overtimeEntry =
	            new OvertimeEntry();

	    overtimeEntry.setWorker(workerObj);

	    overtimeEntry.setAttendance(
	            savedAttendance
	    );

	    overtimeEntry.setDate(
	            clockOutTime.toLocalDate()
	    );

	    overtimeEntry.setOvertimeHours(
	            overtimeToSave
	    );

	    if (overtimeToSave > 2) {

	        overtimeEntry.setOvertimeRate(
	                BigDecimal.valueOf(
	                        hourlyRate * 2
	                )
	        );

	    } else {

	        overtimeEntry.setOvertimeRate(
	                BigDecimal.valueOf(
	                        hourlyRate * 1.5
	                )
	        );
	    }

	    overtimeEntry.setAmount(
	            BigDecimal.valueOf(
	                    overtimeAmount
	            )
	    );

	    overtimeEntry.setStatus(
	            SettlementStatus.PENDING
	    );

	    overtimeRepository.save(
	            overtimeEntry
	    );

	    return "CLOCK_OUT_SUCCESS";
	}
	
	
	
//	@Transactional
//	@CacheEvict(value = "activeAttendance", allEntries = true)
//	public String clockOut(Long workerId) {
//
//		// Check Worker Exists
//		Worker workerObj = workerService.findByWorkerId(workerId);
//
//		if (workerObj.getId() == null) {
//			//return "WORKER_ABSENCE";
//			 throw new ResourceNotFoundException(
//			            "Worker not found with id: " + workerId
//			    );
//		}
//
//		// Find Active Attendance
//		AttendanceLog attendanceObj = findByWorkerIdAndClockOutIsNull(workerId);
//
//		if (attendanceObj.getId() == null) {
//			//return "NO_ACTIVE_ATTENDANCE";
//			if (attendanceObj.getId() == null) {
//			    throw new ConflictException(
//			            "Worker is not currently clocked in"
//			    );
//			}
//		}
//
//		// Set Clock Out Time
//		LocalDateTime clockOutTime = LocalDateTime.now();
//		attendanceObj.setClockOut(clockOutTime);
//
//		// Calculate Total Hours
//		Duration duration = Duration.between(attendanceObj.getClockIn(), clockOutTime);
//
//		double totalHours = duration.toMinutes() / 60.0;
//
//		attendanceObj.setTotalHours(totalHours);
//
//		// Calculate Overtime Hours
//		double overtimeHours = totalHours > 8 ? totalHours - 8 : 0;
//
//		attendanceObj.setOvertimeHours(overtimeHours);
//
//		// Save Attendance First
//		AttendanceLog savedAttendance = attendanceLogRepository.save(attendanceObj);
//
//		// Create Overtime Entry if OT Exists
//		if (overtimeHours > 0) {
//
//			double hourlyRate = workerObj.getDailyWageRate().doubleValue() / 8;
//
//			double overtimeAmount;
//
//			// First 2 hours -> 1.5x
//			// Remaining hours -> 2x
//			if (overtimeHours <= 2) {
//
//				overtimeAmount = overtimeHours * (hourlyRate * 1.5);
//
//			} else {
//
//				double firstTwoHoursAmount = 2 * (hourlyRate * 1.5);
//
//				double remainingHours = overtimeHours - 2;
//
//				double remainingAmount = remainingHours * (hourlyRate * 2);
//
//				overtimeAmount = firstTwoHoursAmount + remainingAmount;
//			}
//
//			OvertimeEntry overtimeEntry = new OvertimeEntry();
//
//			overtimeEntry.setWorker(workerObj);
//			overtimeEntry.setAttendance(savedAttendance);
//			overtimeEntry.setDate(LocalDate.now());
//			overtimeEntry.setOvertimeHours(overtimeHours);
//
//			// Highest rate applied
//			if (overtimeHours > 2) {
//				overtimeEntry.setOvertimeRate(BigDecimal.valueOf(hourlyRate * 2));
//			} else {
//				overtimeEntry.setOvertimeRate(BigDecimal.valueOf(hourlyRate * 1.5));
//			}
//
//			overtimeEntry.setAmount(BigDecimal.valueOf(overtimeAmount));
//
//			overtimeEntry.setStatus(SettlementStatus.PENDING);
//
//			overtimeRepository.save(overtimeEntry);
//		}
//
//		return "CLOCK_OUT_SUCCESS";
//	}
	
	
//  Calculate Overtime
	private double calculateOvertimeAmount(
	        double overtimeHours,
	        double hourlyRate) {

	    if (overtimeHours <= 0) {
	        return 0;
	    }

	    if (overtimeHours <= 2) {

	        return overtimeHours *
	                (hourlyRate * 1.5);
	    }

	    double firstTwoHoursAmount =
	            2 * (hourlyRate * 1.5);

	    double remainingHours =
	            overtimeHours - 2;

	    double remainingAmount =
	            remainingHours *
	                    (hourlyRate * 2);

	    return firstTwoHoursAmount +
	            remainingAmount;
	}
	
//Find worker attendence within specified range
	public Page<AttendanceLog> getAttendanceHistory(
	        Long workerId,
	        LocalDate from,
	        LocalDate to,
	        int page,
	        int size
	        ) {

	    Pageable pageable = PageRequest.of(page, size);

	    return attendanceLogRepository
	            .findByWorkerIdAndClockInBetween(
	                    workerId,
	                    from.atStartOfDay(),
	                    to.atTime(23, 59, 59),
	                    pageable
	            );
	}
	
//	Find all worker who clocked in 
	@Cacheable("activeAttendance")
	public Iterable<ActiveAttendanceDto> findAllClockedInWorker(){
		
		return attendanceLogRepository.findAllByClockOutIsNull()
				 .stream()
		            .map(log -> {
		                ActiveAttendanceDto dto = new ActiveAttendanceDto();

		                dto.setAttendanceId(log.getId());
		                dto.setWorkerName(log.getWorker().getName());
		                dto.setSiteName(log.getSite().getSiteName());
		                dto.setClockIn(log.getClockIn());

		                return dto;
		            })
		            .toList();
		
	}
	
//	Find by worker Id clock out is null
	public AttendanceLog findByWorkerIdAndClockOutIsNull(Long workerId) {

		return attendanceLogRepository.findByWorkerIdAndClockOutIsNull(workerId).orElse(new AttendanceLog());

	}

//	Find by worker Id
	public AttendanceLog findByWorkerId(Long workerId) {

		return attendanceLogRepository.findByWorkerId(workerId).orElse(new AttendanceLog());

	}

//  Save Attendance
	public AttendanceLog saveAttendance(AttendanceLog attendanceLog) {

		return attendanceLogRepository.save(attendanceLog);

	}

}
