package com.example.registerbeantest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author wangkang
 * @date 2020-12-28
 * @since -
 */
@Configuration
public class BeanConfig {
    /**
     * 用户库QueryService
     * @return
     */
    @Bean
    public QueryService userQueryService() {
        QueryService userQueryService = new QueryService("userQueryService");
        return userQueryService;
    }

    /**
     * 订单库QueryService
     * @return
     */
    @Bean
    public QueryService orderQueryService() {
        QueryService orderQueryService = new QueryService("orderQueryService");
        return orderQueryService;
    }


    /**
     * 默认的DbTemplate， 也是初始化注入到QueryService里的
     * @return
     */
    @Primary
    @Bean
    public DbTemplate defaultDbTemplate() {
        DbTemplate dbTemplate = new DbTemplate();
        dbTemplate.setDbName("default-db").setUserName("admin");
        return dbTemplate;
    }

    /**
     * DynamicQueryServiceHandler  更换QueryService中的DbTemplate引用
     * @return
     */
    @Bean
    public DynamicQueryServiceHandler dynamicQueryServiceHandler() {
        DynamicQueryServiceHandler dynamicQueryServiceHandler = new DynamicQueryServiceHandler();
        return dynamicQueryServiceHandler;
    }

}
