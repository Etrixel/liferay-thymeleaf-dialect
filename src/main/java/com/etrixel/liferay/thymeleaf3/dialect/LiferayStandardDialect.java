package com.etrixel.liferay.thymeleaf3.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.etrixel.liferay.thymeleaf3.dialect.expression.LiferayExpressionObjectFactory;
import com.etrixel.liferay.thymeleaf3.dialect.processor.LiferayAttributeTagProcessor;
import com.etrixel.liferay.thymeleaf3.dialect.processor.LiferayWithAttributeTagProcessor;

public class LiferayStandardDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    private IExpressionObjectFactory factory;

    public LiferayStandardDialect() {
        super("Liferay Dialect", "liferay", StandardDialect.PROCESSOR_PRECEDENCE);
        factory = new LiferayExpressionObjectFactory();
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new LiferayWithAttributeTagProcessor(dialectPrefix));
        processors.add(new LiferayAttributeTagProcessor("src", dialectPrefix));
        processors.add(new LiferayAttributeTagProcessor("href", dialectPrefix));
        processors.add(new LiferayAttributeTagProcessor("action", dialectPrefix));
        return processors;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return factory;
    }

}
