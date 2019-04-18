package com.example.amazon.dynamodb.admin.dynamodb;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.Request;
import com.amazonaws.handlers.RequestHandler2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamodbRequestHandler extends RequestHandler2 {

	@Override
	public void beforeRequest(Request<?> request) {
		log.debug("request => {}", request);
		super.beforeRequest(request);
	}

	@Override
	public AmazonWebServiceRequest beforeExecution(AmazonWebServiceRequest request) {
		log.debug("request => {}", request);
		return super.beforeExecution(request);
	}

}
