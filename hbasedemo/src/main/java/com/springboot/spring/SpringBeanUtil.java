package com.springboot.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        synchronized (SpringBeanUtil.class) {
            while (applicationContext == null) {
                try {
                    SpringBeanUtil.class.wait(60000);
                    if (applicationContext == null) {
                        log.warn("Have been waiting for ApplicationContext to be set for 1 minute", new Exception());
                    }
                } catch (InterruptedException ex) {
                    log.debug("getApplicationContext, wait interrupted");
                }
            }
            return applicationContext;
        }
    }

    public static <T> T getBean(Class<?> beanType) {
        return getApplicationContext().getBean((Class<T>) beanType);
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (SpringBeanUtil.class) {
            SpringBeanUtil.applicationContext = applicationContext;
            SpringBeanUtil.class.notifyAll();
        }
    }
}