package com.example.demo.overtimeEntry;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface OvertimeRepository extends JpaRepository<OvertimeEntry, Long>{
	
	List<OvertimeEntry> findByWorkerIdAndDateBetween
	(
            Long workerId,
            LocalDate startDate,
            LocalDate endDate
    );
	
	@Modifying
    @Query("""
           UPDATE OvertimeEntry o
           SET o.status = :status
           WHERE o.worker.id = :workerId
           AND o.date BETWEEN :startDate AND :endDate
           """)
    int settleOvertimeEntries(
            Long workerId,
            LocalDate startDate,
            LocalDate endDate,
            SettlementStatus status
    );
	
	@Query("""
		       SELECT COALESCE(SUM(o.overtimeHours),0)
		       FROM OvertimeEntry o
		       WHERE o.worker.id = :workerId
		       AND YEAR(o.date) = :year
		       AND MONTH(o.date) = :month
		       """)
		Double getMonthlyOvertimeHours(
		        @Param("workerId") Long workerId,
		        @Param("year") int year,
		        @Param("month") int month);
}
