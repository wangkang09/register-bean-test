package com.example.registerbeantest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author wangkang
 * @date 2020-12-28
 * @since -
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class QueryService {
    @Autowired(required = false)
    DbTemplate defaultDbTemplate;

    private String name;

    public QueryService(String name) {
        this.name = name;
    }

    private static QueryService instance;

    public static QueryService instance() {
        return instance;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        instance = this;
        System.out.println("QueryService 初始化完成, name=" + name + ",dbTemplate： " + defaultDbTemplate.toString());
    }
}
