package com.leyou.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "ly.filter")
@Data
public class FilterProperties {

    private List<String> allowPaths;
}
