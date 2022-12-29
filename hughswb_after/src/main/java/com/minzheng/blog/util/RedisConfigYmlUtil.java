package com.minzheng.blog.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "redis")
@Component
@Data
public class RedisConfigYmlUtil {
    private String host;
    private Integer port;
    private String password;
}
