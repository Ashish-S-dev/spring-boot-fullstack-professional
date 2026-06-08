package com.example.demo.overtimeEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/api/overtime")
public class OvertimeController {

	@Autowired
	private OvertimeService overtimeService;
	
	@GetMapping("/summary/{workerId}")
    public ResponseEntity<OvertimeSummary>
    getMonthlySummary(

            @PathVariable Long workerId,

            @RequestParam String month) {

        return ResponseEntity.ok(
                overtimeService
                        .getMonthlySummary(
                                workerId,
                                month));
    }
	

    @PostMapping("/settle/{workerId}")
    public ResponseEntity<String> settleOvertime(

            @PathVariable Long workerId,

            @RequestParam String month) {

        return ResponseEntity.ok(
                overtimeService
                        .settleOvertime(
                                workerId,
                                month));
    }
	
}
