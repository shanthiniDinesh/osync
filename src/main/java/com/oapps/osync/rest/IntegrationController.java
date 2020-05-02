package com.oapps.osync.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.oapps.osync.repository.FieldMapRepository;
import com.oapps.osync.repository.IntegrationPropsRepository;

@RestController
public class IntegrationController {
	@Autowired
	IntegrationPropsRepository intPropsRepo;
	
	@Autowired
	FieldMapRepository fieldMapRepo;
}
