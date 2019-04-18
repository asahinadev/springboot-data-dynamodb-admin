package com.example.amazon.dynamodb.admin.dynamodb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "spring.amazonaws.dynamodb")
public class DynamodbProperty {

	private String profile;

	private String region;

	private String endPoint;

}
