package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) { // 判断我们拦截的是不是method，因为那个注解是只有在方法上生效
            HandlerMethod handlerMethod = (HandlerMethod) handler; // 如果拦截到的是方法，那么它其实是一个HandlerMethod对象，我们强转一下
            Method method = handlerMethod.getMethod(); // 获取拦截到的method对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class); // 尝试从这个method上取LoginRequired注解
            if(loginRequired != null && hostHolder.getUser() == null) { // 当前方法需要登陆，但是user又为空, 即没登陆
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
