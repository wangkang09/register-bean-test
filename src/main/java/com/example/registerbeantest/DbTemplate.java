package com.example.registerbeantest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author wangkang
 * @date 2020-12-28
 * @since -
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DbTemplate {
    private String dbName;
    private String userName;

}
