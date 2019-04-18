package com.example.amazon.dynamodb.admin.dto;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DynamodbInfo {

	String servletPath;

	String tableName;

	String hashKeyName;

	String rangeKeyName;

	ListTablesResult listTable;

	DescribeTableResult describeTable;

	ScanResult scan;
	List<String> attributeNames;

	GetItemResult getItem;
	Map<String, AttributeValue> keyAttributeValues;
	Map<String, AttributeValue> AttributeValues;

}
