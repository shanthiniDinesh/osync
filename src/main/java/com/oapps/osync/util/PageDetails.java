package com.oapps.osync.util;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component


public class PageDetails {
	
	private Boolean authorization_page;
	private Boolean module_page;
	private Boolean field_page;
	private Boolean configuration_page;




}
