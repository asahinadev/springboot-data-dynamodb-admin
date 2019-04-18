package com.example.amazon.dynamodb.admin.dynamodb.thymeleaf;

import java.util.List;
import java.util.Map;

import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttributeValues {

	public String format(final AttributeValue value) {
		return format(value, null);
	}

	public String format(final AttributeValue value, String nullValue) {

		log.debug("value : {} , default : {}", value, nullValue);

		if (value != null) {

			if (value.getS() != null)
				return value.getS();
			if (value.getN() != null)
				return value.getN();
			if (value.getB() != null)
				return String.valueOf(value.getB());
			if (value.getSS() != null)
				return String.valueOf(value.getSS());
			if (value.getNS() != null)
				return String.valueOf(value.getNS());
			if (value.getBS() != null)
				return String.valueOf(value.getBS());
			if (value.getM() != null)
				return String.valueOf(value.getM());
			if (value.getL() != null)
				return String.valueOf(value.getL());
			if (value.getNULL() != null)
				return nullValue;
			if (value.getBOOL() != null)
				return String.valueOf(value.getBOOL());
		}

		return nullValue;
	}

	public String type(final AttributeValue value) {

		log.debug("value : {}", value);

		if (value != null) {

			if (value.getS() != null)
				return "S";
			if (value.getN() != null)
				return "N";
			if (value.getB() != null)
				return "B";
			if (value.getSS() != null)
				return "SS";
			if (value.getNS() != null)
				return "NS";
			if (value.getBS() != null)
				return "MS";
			if (value.getM() != null)
				return "M";
			if (value.getL() != null)
				return "L";
			if (value.getNULL() != null)
				return "NULL";
			if (value.getBOOL() != null)
				return "BOOL";
		}
		return "NULL";
	}

	public String dataPath(String tableName, List<KeySchemaElement> key, Map<String, AttributeValue> values) {

		Object[] args = new String[key.size() + 1];
		args[0] = tableName;
		for (int i = 0; i < key.size(); i++) {
			args[i + 1] = format(values.get(key.get(i).getAttributeName()));
		}

		if (key.size() == 2) {
			return UriComponentsBuilder.fromPath("{tableName}/data/{hashKey}/{rangeKey}").build(args).toString();
		}
		return UriComponentsBuilder.fromPath("{tableName}/data/{hashKey}").build(args).toString();
	}
}
