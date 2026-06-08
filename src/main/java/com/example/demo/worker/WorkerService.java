package com.example.demo.worker;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkerService {

	@Autowired
	private WorkerRepository workerRepository;
	
	public Worker findByWorkerId(Long workerId){
		
		Worker worker =  workerRepository.findById(workerId).orElse(new Worker());
		return worker;
		
	}
	
}
