package com.leyou.gateway.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
@Component
@Slf4j
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProp;

    @Autowired
    private FilterProperties filterProp;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        // 获取白名单
        List<String> allowPaths = filterProp.getAllowPaths();

        // 获取上下文
        RequestContext context = RequestContext.getCurrentContext();

        // 获取request
        HttpServletRequest request = context.getRequest();

        // 获取请求路径
        String url = request.getRequestURL().toString();

        // 判断是否在白名单
        for (String path : allowPaths) {
            if (StringUtils.contains(url, path)) {
                // 如果在白名单，不进行过滤
                return false;
            }
        }

        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext context = RequestContext.getCurrentContext();

        // 获取request
        HttpServletRequest request = context.getRequest();

        // 获取token
        String token = CookieUtils.getCookieValue(request, jwtProp.getCookieName());
        if (StringUtils.isBlank(token)) {
            // 如果token为空，设置不转发
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        // 校验
        try {
            JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey());
        } catch (Exception e) {
            // 校验出现异常，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(403);
            log.error("[登录拦截] 非法访问，未登录，地址：{}", request.getRemoteHost());
        }

        return null;
    }
}
