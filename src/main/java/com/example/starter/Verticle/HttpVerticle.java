package com.example.starter.Verticle;

import com.example.starter.LoginUser.LoginUserService;
import com.example.starter.LoginUser.UserController;
import com.example.starter.Order.Mapper.OrderMapper;
import com.example.starter.Order.OrderController;
import com.example.starter.Order.OrderService;
import com.example.starter.Station.StationController;
import com.example.starter.Station.StationMapper;
import com.example.starter.Station.StationMessageService;
import com.example.starter.Util.ResponseEntity;
import com.example.starter.Util.OperatorUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;
import lombok.extern.java.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Log
public class HttpVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MySQLPool mysqlPool = OperatorUtil.createMysqlPool();
    final OrderService orderService = new OrderService(new OrderMapper(mysqlPool));
    OrderController controller = new OrderController(orderService);
    StationMapper stationMapper = new StationMapper(mysqlPool);
    StationController stationController = new StationController(stationMapper, new StationMessageService(orderService), mysqlPool);
    UserController userController = new UserController(new LoginUserService(mysqlPool, stationMapper));
    Router router = Router.router(vertx);
    invokeAllRouterMethod(controller, router);
    invokeAllRouterMethod(stationController, router);
    invokeAllRouterMethod(userController, router);
    router.errorHandler(500, rc -> rc.json(ResponseEntity.failure(rc.failure().getMessage())));
    router.get("/test")
      .handler(rc -> rc.end("hello"));
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(80)
      .onSuccess(httpserver -> {
        System.out.printf("http在端口:%d启动\n",httpserver.actualPort());
        startPromise.complete();
      })
      .onFailure(t -> {
        System.out.println("启动失败\n");
        startPromise.fail(t);
      });
  }

  private void invokeAllRouterMethod(Object controller,Router router){
    Method[] routerMethod = controller.getClass().getDeclaredMethods();
    for (Method method : routerMethod) {
      Parameter[] parameters = method.getParameters();
      if (parameters.length == 1 && parameters[0].getType().equals(Router.class)){
        method.setAccessible(true);
        try {
          method.invoke(controller, router);
        } catch (IllegalAccessException|InvocationTargetException e) {
          System.out.printf("%s 路由激活失败",method.toString());
        }
      }
    }
  }


}
