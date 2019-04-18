package com.example.amazon.dynamodb.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.example.amazon.dynamodb.admin.dto.DynamdbTableForm;
import com.example.amazon.dynamodb.admin.dto.DynamodbInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/dynamodb")
public class DynamodbController {

	@Autowired
	AmazonDynamoDB db;

	@GetMapping("")
	public String list(@ModelAttribute("info") DynamodbInfo info) {
		ListTablesResult result = db.listTables();

		info.setListTable(result);

		return "dynamodb/table-list";
	}

	@GetMapping("{tableName}")
	public String info(
			WebRequest request,
			@PathVariable(required = true) String tableName,
			@ModelAttribute("info") DynamodbInfo info) {

		DescribeTableResult describeTable = db.describeTable(new DescribeTableRequest(tableName));
		ScanResult scan = db.scan(new ScanRequest(tableName));
		List<String> attributeNames = removeAttributeNames(scan.getItems(), describeTable.getTable().getKeySchema());

		setKeyElements(info, describeTable.getTable().getKeySchema());
		info.setTableName(tableName);
		info.setScan(scan);
		info.setDescribeTable(describeTable);
		info.setAttributeNames(attributeNames);

		return "dynamodb/table-info";
	}

	@GetMapping({
			"{tableName}/data/{hashKey}/",
			"{tableName}/data/{hashKey}/{rangeKey}"
	})
	public String data(
			WebRequest request,
			HttpServletRequest servletRequest,
			@PathVariable(required = true) String tableName,
			@PathVariable(required = true) String hashKey,
			@PathVariable(required = false) String rangeKey,
			@ModelAttribute("info") DynamodbInfo info) {

		info.setServletPath(servletRequest.getServletPath());
		info.setTableName(tableName);

		DescribeTableResult describeTable = db.describeTable(new DescribeTableRequest(tableName));

		Map<String, AttributeValue> keyInfo = createKeys(describeTable.getTable(), hashKey, rangeKey);
		GetItemResult getItem = db.getItem(new GetItemRequest(tableName, keyInfo));

		setKeyElements(info, describeTable.getTable().getKeySchema());
		info.setDescribeTable(describeTable);
		info.setGetItem(getItem);
		info.setKeyAttributeValues(keyInfo);
		info.setAttributeValues(
				getItem.getItem()
						.entrySet()
						.stream()
						.filter(item -> {
							for (KeySchemaElement key : describeTable.getTable().getKeySchema()) {
								return !Objects.equals(key.getAttributeName(), item.getKey());
							}
							return true;
						})
						.sorted((a, b) -> StringUtils.compare(a.getKey(), b.getKey()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new)));
		return "dynamodb/data-info";
	}

	@PostMapping({
			"{tableName}/data/{hashKey}/",
			"{tableName}/data/{hashKey}/{rangeKey}"
	})
	public String dataPost(
			WebRequest request,
			HttpServletRequest servletRequest,
			@ModelAttribute("form") DynamdbTableForm form,
			@PathVariable(required = true) String tableName,
			@PathVariable(required = true) String hashKey,
			@PathVariable(required = false) String rangeKey,
			@ModelAttribute("info") DynamodbInfo info) {

		log.debug("form -> {}", form);

		Map<String, AttributeValue> item = new HashMap<>();

		for (Entry<String, String> entry : form.getTypes().entrySet()) {

			String attributeName = entry.getKey();
			String attributeType = entry.getValue().toUpperCase();
			String attributeValue = form.getValues().get(attributeName);

			AttributeValue value = new AttributeValue();
			switch (attributeType) {

			default:
			case "S":
				value.setS(attributeValue);
				break;

			case "N":
				value.setN(attributeValue);
				break;
			}
			item.put(attributeName, value);
		}

		db.putItem(new PutItemRequest(tableName, item));

		return "redirect:" + servletRequest.getServletPath();
	}

	private Map<String, AttributeValue> createKeys(TableDescription tableDescription, String hashKey, String rangeKey) {

		Map<String, AttributeValue> attributeValues = new LinkedHashMap<>();
		Map<String, AttributeDefinition> attributeDefinitions = new LinkedHashMap<>();
		List<KeySchemaElement> elements = tableDescription.getKeySchema();
		List<AttributeDefinition> definitions = tableDescription.getAttributeDefinitions();

		attributeDefinitions = definitions.stream()
				.collect(Collectors.toMap(AttributeDefinition::getAttributeName, item -> item));

		for (KeySchemaElement element : elements) {
			String attributeName = element.getAttributeName();
			String attributeType = attributeDefinitions.get(element.getAttributeName()).getAttributeType();

			ScalarAttributeType type = ScalarAttributeType.fromValue(attributeType);

			AttributeValue value = new AttributeValue();

			String data = "";
			switch (element.getKeyType()) {

			case "HASH":
				data = hashKey;
				break;

			case "RANGE":
				data = rangeKey;
				break;
			}

			switch (type) {
			case S:
				value.setS(data);
				break;

			case N:
				value.setN(data);
				break;

			default:
				continue;
			}
			attributeValues.put(attributeName, value);
		}
		log.info("{}", attributeValues);
		return attributeValues;
	}

	private List<String> removeAttributeNames(List<Map<String, AttributeValue>> items, List<KeySchemaElement> key) {
		List<String> attributeNames = new ArrayList<>();
		for (Map<String, AttributeValue> item : items) {
			attributeNames.addAll(item.keySet());
		}
		attributeNames = attributeNames.stream().distinct().sorted().collect(Collectors.toList());
		for (KeySchemaElement keySchemaElement : key) {
			attributeNames.remove(keySchemaElement.getAttributeName());
		}
		return attributeNames;
	}

	private void setKeyElements(DynamodbInfo info, List<KeySchemaElement> key) {
		info.setHashKeyName(key.get(0).getAttributeName());
		if (key.size() == 2) {
			info.setRangeKeyName(key.get(1).getAttributeName());
		}
	}
}
