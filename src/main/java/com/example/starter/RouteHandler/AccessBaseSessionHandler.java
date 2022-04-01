package com.example.starter.RouteHandler;

import com.example.starter.LoginUser.Entity.LoginUserPO;
import com.example.starter.LoginUser.Entity.Roles;
import com.example.starter.LoginUser.UserController;
import io.vertx.core.Handler;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.ext.web.RoutingContext;



/**
 * 其实这个设计和Vertx很多设计是一致的
 * 其create方法会每次创建一个新对象，具体为什么参照：
 * java.lang.invoke.InnerClassLambdaMetafactory#buildCallSite()
 * 注意：以上结论都是基于于hotspot环境对lambda的实现
 * @author 孙晨曦
 */
public class AccessBaseSessionHandler {
    /**
     *
     * @param role 当前接口最低权限
     * @return 可以用于route的handler
     */
    public static Handler<RoutingContext> createLeastMode(final Roles role) {
        return rc -> {
            if (getNowRole(rc).power < role.power){
                rc.fail(new NoStackTraceThrowable("权限不足"));
                return;
            }
            rc.next();
        };
    }

    public static Handler<RoutingContext> createOnlyMode(final Roles role) {
        return rc -> {
            if (!(getNowRole(rc) == role)) {
                rc.fail(new NoStackTraceThrowable("权限不足"));
                return;
            }
            rc.next();
        };
    }

    private static Roles getNowRole(RoutingContext rc) {
        //首先先获取当前权限，如果没登录那就是NOT_LOGIN,登陆后在获取到当前的权限 这就是第一个三目的意思
        //第一个三目的值为一个Role即当前的role，将其与此时接口的权限power相比，然后
        LoginUserPO loginUserPO = rc.session().get(UserController.userKeyInSession);
        return loginUserPO == null ? Roles.NOT_LOGIN : Roles.allRoles.getOrDefault(loginUserPO.getRole(), Roles.NOT_LOGIN);
    }


}
