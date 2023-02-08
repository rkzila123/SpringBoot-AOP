package com.rohit.aop.advice;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
public class LoggingAdvice {

	Logger log = LoggerFactory.getLogger(LoggingAdvice.class);
	
	private AuditLogInfo info = new AuditLogInfo();
	
	@Pointcut(value="execution(* com.rohit.aop.*.*.*(..) )")
	public void myPointcut() {
		
	}
	
	@Around("myPointcut()")
	public Object applicationLogger(ProceedingJoinPoint pjp) throws Throwable {
		ObjectMapper mapper = new ObjectMapper();
		String methodName = pjp.getSignature().getName();
		String className = pjp.getTarget().getClass().toString();
		Object[] array = pjp.getArgs();
		log.info("method invoked " + className + " : " + methodName + "()" + "arguments : "
				+ mapper.writeValueAsString(array));
		Object object = pjp.proceed();
		log.info(className + " : " + methodName + "()" + "Response : "
				+ mapper.writeValueAsString(object));
		return object;
	}
	
	
	@Around("myPointcut()")
	public Object aroundAll(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getName();
		log.info("Entered: {} method of {}", methodName, className);
		Object result = proceedingJoinPoint.proceed();
		log.info("Exited: {} method of {}", methodName, className);
		return result;
	}
	
	@AfterThrowing(pointcut = "myPointcut()", throwing = "error")
	public void afterThrowing(JoinPoint joinPoint, Throwable error) {
		String jsonRequestString = null;
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();

		for (Object input : joinPoint.getArgs()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonRequestString = mapper.writeValueAsString(input);
			} catch (JsonProcessingException e) {
				log.error("***Unable to parse request***");
			}
		}
		info.setUrl(request.getRequestURL().toString());
		info.setRequest(jsonRequestString);
		info.setResponse(null);
		StringBuilder errorMessage = new StringBuilder("Exception-->");
		errorMessage.append(error).append(" from -- ").append(joinPoint.getSignature()).append("||Class-->")
				.append(error.getStackTrace()[0].getClassName()).append("||Method-->")
				.append(error.getStackTrace()[0].getMethodName()).append(">>>Line--")
				.append(error.getStackTrace()[0].getLineNumber());
		info.setErrorMsg(errorMessage.toString());
		filelog();
	}
	
	private void filelog() {
		log.info(
				"Request Response log in aspect:  \n Request: {}  | \n Response : {} | \n Error: {} |  \n URI: {}",
				info.getRequest(),
				info.getResponse(), info.getErrorMsg(), info.getUrl());
	}

}
