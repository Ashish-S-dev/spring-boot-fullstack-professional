package com.example.demo.attendanceLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {

	
	public Optional<AttendanceLog> findByWorkerId(Long workerId);
	
	public Optional<AttendanceLog> findByWorkerIdAndClockOutIsNull(Long workerId);
	
	public List<AttendanceLog> findAllByClockOutIsNull();

	public Page<AttendanceLog> findByWorkerIdAndClockInBetween(
	            Long workerId,
	            LocalDateTime from,
	            LocalDateTime to,
	            Pageable pageable
	    );
}
