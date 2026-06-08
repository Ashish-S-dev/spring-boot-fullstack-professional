package com.example.demo.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SiteService {

	@Autowired
	private SiteRepository siteRepository;
	
	public Site findBySiteId(Long siteId) {
		
		Site siteObj = siteRepository.findById(siteId).orElse(new Site());
		
		return siteObj;
		
	}
	
}
