package com.example.starter.Order;


import com.example.starter.LoginUser.Entity.LoginUserPO;
import com.example.starter.LoginUser.Entity.Roles;
import com.example.starter.LoginUser.UserController;
import com.example.starter.Order.Entity.OrderVO;
import com.example.starter.RouteHandler.AccessBaseSessionHandler;
import com.example.starter.RouteHandler.ValidationJsonHandler;
import com.example.starter.Util.ResponseEntity;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;




public class OrderController{

    private OrderService orderService;
    private static final String prefix = "/Order";

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }


  private void addOrder(Router router){
        router.post(prefix)
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.USER))
                .handler(BodyHandler.create())
                .handler(ValidationJsonHandler.create(OrderVO.class))
                .handler(rc -> {
                    LoginUserPO loginUserPO = rc.session().get(UserController.userKeyInSession);
                    OrderVO orderVO = rc.get(ValidationJsonHandler.VALUE_KEY);
                    orderService.addNewOrder(orderVO,loginUserPO.getUserId())
                            .map(v -> ResponseEntity.success(orderVO.getOrderId(),200).toJson())
                            .onSuccess(rs -> rc.response().end(rs))
                            .onFailure(rc::fail);
                });
    }

    private void getHistoryOrders(Router router){
        router.get(prefix+"/:current/:size")
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.USER))
                .handler(rc -> {
                    HttpServerRequest request = rc.request();
                    String current = request.getParam("current");
                    String size = request.getParam("size");
                    orderService.getHistoryRecord(Integer.parseInt(current), Integer.parseInt(size),rc.session().<LoginUserPO>get(UserController.userKeyInSession).getUserId())
                            .map(ResponseEntity::success)
                            .onSuccess(l -> rc.response().end(l.toJson()))
                            .onFailure(rc::fail);
                });
    }

    private void cancelOrder(Router router){
        router.post(prefix+"/cancel")
                .consumes("application/json")
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.USER))
                .handler(BodyHandler.create())
                .handler(rc -> {
                    JsonObject body = rc.getBodyAsJson();
                    String reason = body.getString("reason");
                    Long orderId = body.getLong("orderId");
                    if (reason != null && orderId != null){
                        orderService.cancelOrderByUser(orderId,reason,rc.session().<LoginUserPO>get(UserController.userKeyInSession).getUserId())
                                .onSuccess(v -> rc.json(ResponseEntity.success("取消成功")))
                                .onFailure(rc::fail);
                    }else {
                        rc.fail(new NoStackTraceThrowable("无效的参数"));
                    }
                });
    }


    private void getOrder(Router router){
        router.get(prefix+"/:orderId")
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.USER))
                .handler(rc -> {
                    Integer userId = rc.session().<LoginUserPO>get(UserController.userKeyInSession).getUserId();
                    orderService.getUserOrder(Long.parseLong(rc.pathParam("orderId")), userId)
                      .onSuccess(orderVo -> rc.end(ResponseEntity.success(orderVo).toJson()))
                      .onFailure(rc::fail);
                });
    }





}
