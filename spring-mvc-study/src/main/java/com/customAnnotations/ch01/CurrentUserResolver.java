package com.customAnnotations.ch01;

import lombok.Setter;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentUserResolver implements HandlerMethodArgumentResolver {

    @Setter
    private UserDao userDao;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
       return parameter.getParameterType().isAssignableFrom(UserInfo.class) && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        CurrentUser currentUser = methodParameter.getParameterAnnotation(CurrentUser.class);
        String sid = nativeWebRequest.getParameter("sid");
        if(currentUser.require()){
            if(StringUtils.isEmpty(sid)){
                throw new Exception("必要的sid缺失，请先登录！");
            }
        }
        //TODO 通过登录的sid,获取对应的UserInfo对象。 userDao.getUser(sid);
        return new UserInfo(1L,"zhangsan",System.currentTimeMillis());
    }
}
