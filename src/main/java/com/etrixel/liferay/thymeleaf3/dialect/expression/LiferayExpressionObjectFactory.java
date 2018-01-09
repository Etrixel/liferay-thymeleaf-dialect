package com.etrixel.liferay.thymeleaf3.dialect.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class LiferayExpressionObjectFactory implements IExpressionObjectFactory {

    private static final String PREFERENCES_EXPRESSION_OBJECT_NAME = "portletPreferences";

    private static final Set<String> ALL_EXPRESSION_OBJECT_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<String>(Arrays.asList(new String[] {PREFERENCES_EXPRESSION_OBJECT_NAME})));

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        HttpServletRequest request = ((IWebContext) context).getRequest();
        PortletRequest portletRequest = (PortletRequest) request.getAttribute("javax.portlet.request");

        if (PREFERENCES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return portletRequest.getPreferences();
        }

        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return true;
    }

}
