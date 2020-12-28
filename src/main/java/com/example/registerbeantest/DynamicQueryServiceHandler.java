package com.example.registerbeantest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Field;

/**
 * @author wangkang
 * @date 2020-12-28
 * @since -
 */
public class DynamicQueryServiceHandler implements ApplicationContextAware {

    private GenericApplicationContext applicationContext;

    public void changeDbTemplate(String queryServiceName, String dbTemplateBeanName, DbTemplate dbTemplate) {
        QueryService queryService = applicationContext.getBean(queryServiceName, QueryService.class);
        if (queryService == null) {
            return;
        }

        // 更新QueryService中的dbTemplate引用然后重新注册回去
        Class<?> beanType = applicationContext.getType(queryServiceName);
        if (beanType == null) {
            return;
        }

        Field[] declaredFields = beanType.getDeclaredFields();
        for (Field field : declaredFields) {
            // 从spring容器中拿到这个具体的bean对象
            Object bean = queryService;
            // 当前字段设置新的值
            try {
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (type == DbTemplate.class) {
                    field.set(bean, dbTemplate);
                }
                System.out.println("finished  " + type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 刷新容器中的bean,获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        // 刷新DbTemplate的bean定义
        BeanDefinitionBuilder dbTemplatebeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DbTemplate.class);
        dbTemplatebeanDefinitionBuilder.addPropertyValue("dbName", dbTemplate.getDbName());
        dbTemplatebeanDefinitionBuilder.addPropertyValue("userName", dbTemplate.getDbName());

        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(QueryService.class);
        // 设置属性defaultDbTemplate,此属性引用已经定义的bean,这里defaultDbTemplate已经被spring容器管理了.
        beanDefinitionBuilder.addPropertyReference("defaultDbTemplate", dbTemplateBeanName);
        // 刷新QueryService的DbTemplate引用
        beanDefinitionBuilder.addPropertyValue("name", queryServiceName);

        // 重新注册bean
        defaultListableBeanFactory.registerBeanDefinition(dbTemplateBeanName, dbTemplatebeanDefinitionBuilder.getRawBeanDefinition());
        defaultListableBeanFactory.registerBeanDefinition(queryServiceName, beanDefinitionBuilder.getRawBeanDefinition());
    }

    public MyTemplate myTemplate() {
        MyTemplate myTemplate = new MyTemplate();
        myTemplate.setDbName("myDb");
        applicationContext.registerBean("myTemplate", MyTemplate.class, () -> myTemplate);
        return applicationContext.getBean("myTemplate", MyTemplate.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof GenericApplicationContext) {
            this.applicationContext = (GenericApplicationContext) applicationContext;
        }
    }
}
