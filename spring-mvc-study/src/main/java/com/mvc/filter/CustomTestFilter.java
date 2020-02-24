package com.mvc.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Order(1)
@Component
@WebFilter(filterName = "BusinessFilter", urlPatterns = {"/*"})
public class CustomTestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        log.debug("filter init...{}",servletContext.getContextPath());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest) servletRequest;
        //favicon 不请求 否则给人执行两次的错觉
        if(!httpServletRequest.getRequestURI().contains("favicon.ico")){
            log.debug("coming real doFilter");
            filterChain.doFilter(servletRequest,servletResponse);
        }
        log.debug("coming dofilter {}",httpServletRequest.getRequestURI());
    }

    @Override
    public void destroy() {
        log.debug("destroy...{}");
    }
}
