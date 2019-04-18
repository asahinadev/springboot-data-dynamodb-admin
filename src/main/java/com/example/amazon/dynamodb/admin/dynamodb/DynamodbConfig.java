package com.example.amazon.dynamodb.admin.dynamodb;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.amazon.dynamodb.admin.dynamodb.thymeleaf.AttributeValuesDialect;

@Configuration
public class DynamodbConfig {

	@Autowired
	DynamodbProperty property;

	@Bean
	public AmazonDynamoDB dynamoDB() {
		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();

		if (StringUtils.isAnyEmpty(property.getEndPoint(), property.getRegion()) == false) {
			builder.setEndpointConfiguration(endpointConfiguration());
		} else if (StringUtils.isNotEmpty(property.getRegion())) {
			builder.setRegion(property.getRegion());
		} else if (StringUtils.isNotEmpty(property.getEndPoint())) {
			throw new IllegalStateException("region 未設定");
		}

		builder.setCredentials(provider());

		builder.setRequestHandlers(new DynamodbRequestHandler());

		return builder.build();
	}

	@Bean
	public DynamoDBMapper dbMapper() {
		return new DynamoDBMapper(dynamoDB());
	}

	@Bean
	public EndpointConfiguration endpointConfiguration() {
		return new EndpointConfiguration(property.getEndPoint(), property.getRegion());
	}

	@Bean
	public AWSCredentialsProvider provider() {
		if (StringUtils.isNoneEmpty(property.getProfile())) {
			return new ProfileCredentialsProvider(property.getProfile());
		}
		return new DefaultAWSCredentialsProviderChain();
	}

	@Bean
	public AttributeValuesDialect attributeValuesDialect() {
		return new AttributeValuesDialect();
	}
}
