package com.etrixel.liferay.thymeleaf3.dialect.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.templatemode.TemplateMode;

import com.etrixel.liferay.thymeleaf3.dialect.utils.LiferayPortletURLUtils;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;

public class LiferayWithAttributeTagProcessor extends AbstractAttributeTagProcessor {

    public LiferayWithAttributeTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, "withUrl", true, 600, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
            String attributeValue, IElementTagStructureHandler structureHandler) {

        IEngineContext engineContext = null;
        if (context instanceof IEngineContext) {
            engineContext = (IEngineContext) context;
        }

        HttpServletRequest request = ((IWebContext) context).getRequest();

        AssignationSequence assignations =
                new StandardExpressionParser().parseAssignationSequence(context, attributeValue, true);

        for (Assignation assignation : assignations) {
            String varName = assignation.getLeft().getStringRepresentation();
            Map<String, Object> params = getParams(assignation.getRight(), context);

            LiferayPortletURL portletURL = LiferayPortletURLUtils.createURL(params, request);

            if (engineContext != null) {
                engineContext.setVariable(varName, portletURL.toString());
            } else {
                structureHandler.setLocalVariable(varName, portletURL.toString());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getParams(IStandardExpression IStandardExpression, ITemplateContext ITemplateContext) {
        if (IStandardExpression == null || isEmpty(IStandardExpression.getStringRepresentation())) {
            return Collections.emptyMap();
        }

        Object params = IStandardExpression.execute(ITemplateContext);

        if (params instanceof Map) {
            return new HashMap<>((Map<String, Object>) params);
        }
        return Collections.emptyMap();
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

}
