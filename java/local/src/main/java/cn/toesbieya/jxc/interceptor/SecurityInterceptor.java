package cn.toesbieya.jxc.interceptor;

import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.module.PermissionModule;
import cn.toesbieya.jxc.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getServletPath();
        String ip = IpUtil.getIp(request);
        UserVo user = SessionUtil.get(request);

        if (user == null) {
            WebUtil.responseJson(response, Result.requireLogin());
            ThreadUtil.clearAll();
            return false;
        }

        if (!PermissionModule.authority(user, url)) {
            WebUtil.responseJson(response, Result.noPermission());
            log.warn("权限拦截，访问路径：{}，用户：{}，IP:{}", url, user.getName(), ip);
            ThreadUtil.clearAll();
            return false;
        }

        ThreadUtil.setUser(user);

        return true;
    }
}
