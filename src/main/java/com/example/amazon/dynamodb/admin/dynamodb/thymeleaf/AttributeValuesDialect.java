package com.example.amazon.dynamodb.admin.dynamodb.thymeleaf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class AttributeValuesDialect implements IExpressionObjectDialect {

	@Override
	public String getName() {
		return "attributeValues";
	}

	@Override
	public IExpressionObjectFactory getExpressionObjectFactory() {
		return new IExpressionObjectFactory() {

			@Override
			public boolean isCacheable(String expressionObjectName) {
				return false;
			}

			@Override
			public Set<String> getAllExpressionObjectNames() {
				return new HashSet<>(Arrays.asList(AttributeValuesDialect.this.getName()));
			}

			@Override
			public Object buildObject(IExpressionContext context, String expressionObjectName) {
				if (expressionObjectName.equals(AttributeValuesDialect.this.getName())) {
					return new AttributeValues();
				}
				return null;
			}
		};
	}

}
