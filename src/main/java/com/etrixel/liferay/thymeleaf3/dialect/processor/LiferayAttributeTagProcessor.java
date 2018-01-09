package com.etrixel.liferay.thymeleaf3.dialect.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.templatemode.TemplateMode;

import com.etrixel.liferay.thymeleaf3.dialect.utils.LiferayPortletURLUtils;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;

public class LiferayAttributeTagProcessor extends AbstractAttributeTagProcessor {

    private final String attributeName;

    public LiferayAttributeTagProcessor(String attributeName, String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, attributeName, true, 1000, true);
        this.attributeName = attributeName;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
            String attributeValue, IElementTagStructureHandler structureHandler) {
        Map<String, Object> params = Collections.emptyMap();

        if (!isEmpty(attributeValue)) {
            AssignationSequence assignations =
                    new StandardExpressionParser().parseAssignationSequence(context, attributeValue, false);

            params = new HashMap<String, Object>(assignations.size() + 1, 1.0f);

            for (Assignation assignation : assignations) {
                params.put(assignation.getLeft().getStringRepresentation(), assignation.getRight().execute(context));
            }
        }

        LiferayPortletURL portletURL = LiferayPortletURLUtils.createURL(params, ((IWebContext) context).getRequest());
        structureHandler.setAttribute(this.attributeName, portletURL.toString());
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

}
