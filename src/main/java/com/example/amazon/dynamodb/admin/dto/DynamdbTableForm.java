package com.example.amazon.dynamodb.admin.dto;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DynamdbTableForm {

	Map<String, String> types;
	Map<String, String> values;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
