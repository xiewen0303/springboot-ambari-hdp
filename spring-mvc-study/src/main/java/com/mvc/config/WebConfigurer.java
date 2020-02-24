package com.mvc.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.customAnnotations.ch01.CurrentUserResolver;
import com.customAnnotations.ch01.UserDao;
import com.mvc.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Resource
    private UserDao userDao;

    @Resource
    private LoginInterceptor loginInterceptor;

    // 这个方法是用来配置静态资源的，比如html，js，css，等等
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*.html");
    }

    // 这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns("/**") 表示拦截所有的请求，
        // excludePathPatterns("/login", "/register") 表示除了登陆与注册之外，因为登陆注册不需要登陆也可以访问
        // registry.addInterceptor(encryInterceptor).addPathPatterns("/**").excludePathPatterns("/login", "/register");
        // addPathPatterns 用来设置拦截路径，excludePathPatterns 用来设置白名单，也就是不需要触发这个拦截器的路径
//        registry.addInterceptor(encryInterceptor).addPathPatterns("/jupiter/**");
//        registry.addInterceptor(new PrivilegeInteceptor()).addPathPatterns("/**").excludePathPatterns("/jupiter");

        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }

    // param-------------- {"addChannel":"app","addProduct":"India","devicePlatform":"android","marketChannel":"google_play","mobile":"1861708643","password":"33333","productVersion":"1.0.0"}
    //key-------------- uxc4REON2aCMgd66

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter jsonConverter = new FastJsonHttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
        jsonConverter.setFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteDateUseDateFormat);
        converters.add(jsonConverter);
    }

//    国际化
//    @Bean
//    public LocaleResolver localeResolver() {
//        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
//        //设置默认区域
//
//        String language = ConfigManager.getValue("locale");
//        if(StringUtils.isBlank(language)) {
//            localeResolver.setDefaultLocale(Locale.ENGLISH);
//        } else {
//            localeResolver.setDefaultLocale(new Locale(language));
//        }
//        return localeResolver;
//    }


//    @Nullable
//    @Override
//    public Validator getValidator() {
//        return validator();
//    }

//    @Bean
//    public Validator validator() {
//        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
//        validator.setValidationMessageSource(messageSource);
//        return validator;
//    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:start/login/page");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        //super.addViewControllers(registry);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        CurrentUserResolver currentUserMethodArgumentResolver = new CurrentUserResolver();
        currentUserMethodArgumentResolver.setUserDao(userDao);
        argumentResolvers.add(currentUserMethodArgumentResolver);
    }

}
