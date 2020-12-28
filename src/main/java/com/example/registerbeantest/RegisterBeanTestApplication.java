package com.example.registerbeantest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootApplication
public class RegisterBeanTestApplication implements CommandLineRunner {

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(RegisterBeanTestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        GenericWebApplicationContextRegisterBean();
        DefaultListableBeanFactoryRegisterBean();
        GenericWebApplicationContextRegisterBeanReplaceDefaultListable();
    }

    private void GenericWebApplicationContextRegisterBeanReplaceDefaultListable() {
        GenericApplicationContext context = null;
        if (applicationContext instanceof GenericApplicationContext) {
            context = (GenericApplicationContext) applicationContext;
        }
        BeanDefinition definition = context.getBeanDefinition("defaultDbTemplate");


        DbTemplate fixPoint = new DbTemplate("fixPoint", "fixPoint");
        QueryService queryService = new QueryService("fixPoint");
        queryService.setDefaultDbTemplate(fixPoint);

        context.registerBean("fixPoint",DbTemplate.class,()->fixPoint);
        context.registerBean("userQueryService",QueryService.class,()->queryService);

        QueryService queryService1 = applicationContext.getBean("userQueryService",QueryService.class);
        //这里的 queryService1 中的 DbTemplate并没有替换成 fixPoint 说明，@Autowired 注解是在 类初始化之后生效的
        System.out.println(queryService1);

        //@Primary 注解是失效了的，只有在类上的注解才仍然有效
        context.registerBean("defaultDbTemplate",DbTemplate.class,()->fixPoint,bd -> bd.setPrimary(true));
        definition = context.getBeanDefinition("defaultDbTemplate");
        //这里的 queryService1 中的 DbTemplate 已经替换成了 myTemplate 了！
        queryService1 = applicationContext.getBean("userQueryService",QueryService.class);
        System.out.println(queryService1);
    }

    private void DefaultListableBeanFactoryRegisterBean() {
        // 获取DynamicQueryServiceHandler
        DynamicQueryServiceHandler dynamicQueryServiceHandler = applicationContext.getBean("dynamicQueryServiceHandler", DynamicQueryServiceHandler.class);

        // 初始化要替换的dbTemplate实例
        DbTemplate userDbTemplate = new DbTemplate("user-db", "userAdmin");
        DbTemplate orderDbTemplate = new DbTemplate("order-db", "orderAdmin");

        // 进行替换
        dynamicQueryServiceHandler.changeDbTemplate("userQueryService", "userDbTemplate", userDbTemplate);
        dynamicQueryServiceHandler.changeDbTemplate("orderQueryService", "orderDbTemplate", orderDbTemplate);

        // 打印更新之后的bean
        QueryService updatedUserQueryService = applicationContext.getBean("userQueryService", QueryService.class);
        QueryService updateOrderQueryService = applicationContext.getBean("orderQueryService", QueryService.class);
        System.out.println("updatedUserQueryService 更新完成, name=" + updatedUserQueryService.getName() + ",dbTemplate：" +
                updatedUserQueryService.getDefaultDbTemplate().toString());
        System.out.println("updateOrderQueryService 更新完成, name=" + updateOrderQueryService.getName() + ",dbTemplate：" +
                updateOrderQueryService.getDefaultDbTemplate().toString());
    }

    private void GenericWebApplicationContextRegisterBean() {
        // 获取DynamicQueryServiceHandler
        DynamicQueryServiceHandler dynamicQueryServiceHandler = applicationContext.getBean("dynamicQueryServiceHandler", DynamicQueryServiceHandler.class);
        MyTemplate myTemplate = dynamicQueryServiceHandler.myTemplate();
        System.out.println(myTemplate);
    }
}
