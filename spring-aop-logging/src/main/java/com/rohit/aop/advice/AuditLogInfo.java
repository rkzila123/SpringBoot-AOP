package com.rohit.aop.advice;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.Data;



@Component
@Data
public class AuditLogInfo {
	
	
	private String errorMsg;
	private String type;
	
	private String url;
	@JsonRawValue
	private String request;
	@JsonRawValue
	private String response;
	

}