package com.example.starter.LoginUser;

import com.example.starter.LoginUser.Entity.LoginUserPO;
import com.example.starter.LoginUser.Entity.LoginUserVO;
import com.example.starter.LoginUser.Entity.Roles;
import com.example.starter.RouteHandler.AccessBaseSessionHandler;
import com.example.starter.RouteHandler.ValidationJsonHandler;
import com.example.starter.Station.Station;
import com.example.starter.Station.StationController;
import com.example.starter.Util.Pair;
import com.example.starter.Util.ResponseEntity;
import com.example.starter.Util.ValidationGroup;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;


public class UserController{
    public static String userKeyInSession = "user";
    private static final String PREFIX = "/User";
    public static final String stationPairKeyInSession = "station";
    private LoginUserService loginUserService;

  public UserController(LoginUserService loginUserService) {
    this.loginUserService = loginUserService;
  }


  private void loginIn(Router router){
        router.post(PREFIX+"/loginIn")
                .handler(BodyHandler.create())
                .handler(ValidationJsonHandler.create(LoginUserVO.class,new Class[]{ValidationGroup.selectGroup.class}))
                .handler(rc-> {
                    LoginUserVO loginUserVO = rc.get(ValidationJsonHandler.VALUE_KEY);
                    loginUserService.loginIn(loginUserVO)
                            .onSuccess(user -> {
                                Session session = rc.session();
                                session.put(userKeyInSession,user);
                                if (Roles.STATION.role.equals(user.getRole())){
                                  loginUserService.getStationInfo(user.getUserId())
                                    .onSuccess(p -> {
                                      session.put(stationPairKeyInSession,p);
                                      rc.response().end(ResponseEntity.success(new LoginUserVO(user)).toJson());
                                    })
                                    .onFailure(rc::fail);
                                }else {
                                  rc.response().end(ResponseEntity.success(new LoginUserVO(user)).toJson());
                                }
                            })
                            .onFailure(rc::fail);
                });
    }


    private void loginOut(Router router){
        router.delete(PREFIX+"/loginOut")
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.USER))
                .handler(rc -> {
                    final Integer stationId = rc.session().<Pair<Station, List<Integer>>>get(stationPairKeyInSession).left.getStationId();
                    if (stationId != null){
                      ServerWebSocket ws = StationController.stationWSConnections.get(stationId);
                      if (ws != null){
                        ws.close();
                        StationController.stationWSConnections.remove(stationId);
                      }
                    }
                    rc.session().destroy();
                    rc.response().end(ResponseEntity.success("????????????").toJson());
                });
    }

    private void nowUser(Router router){
        router.get(PREFIX+"/now")
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.USER))
                .handler(rc -> {
                    LoginUserPO user = rc.session().get(userKeyInSession);
                    rc.response().end(ResponseEntity.success(new LoginUserVO(user)).toJson());
                });
    }

    private void register(Router router){
        router.post(PREFIX+"/register")
                .handler(BodyHandler.create())
                .handler(ValidationJsonHandler.create(LoginUserVO.class,new Class[]{ValidationGroup.insertGroup.class}))
                .handler(rc -> {
                    LoginUserVO register = rc.get(ValidationJsonHandler.VALUE_KEY);
                    loginUserService.register(register)
                            .onSuccess(user -> {
                                rc.session().put(userKeyInSession,user);
                                rc.response().end(ResponseEntity.success("????????????").toJson());
                            })
                            .onFailure(rc::fail);
                });
    }

    private void modifyPassword(Router router){
        router.put(PREFIX+"/password")
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.USER))
                .handler(BodyHandler.create())
                .handler(rc -> {
                    JsonObject json = rc.getBodyAsJson();
                    String oldPassword = json.getString("oldPassword");
                    String newPassword = json.getString("newPassword");
                    LoginUserPO user = rc.session().get(userKeyInSession);

                    //??????user?????????????????????oldPassword??????????????????false??????
                    //oldpassword????????????newPassword??????????????????false;
                    //??????equals???null??????
                    if (user.getPassword().equals(oldPassword) && !oldPassword.equals(newPassword)){
                        loginUserService.modifyPassword(newPassword, user.getUserId())
                                .onSuccess(v -> {
                                    user.setPassword(newPassword);
                                    rc.response().end(ResponseEntity.success("????????????").toJson());
                                })
                                .onFailure(rc::fail);
                    }else {
                        rc.fail(500, new NoStackTraceThrowable("????????????"));
                    }
                });
    }

    //????????????????????????????????????session???????????????????????????
    private void modifyRole(Router router){
        router.put(PREFIX+"/role")
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.ADMIN))
                .handler(BodyHandler.create())
                .handler(rc -> {
                    JsonObject body = rc.getBodyAsJson();
                    Integer userId = body.getInteger("userId");
                    String role = body.getString("role");
                    loginUserService.updateRoleByUserId(role,userId)
                            .onSuccess(v -> {
                                rc.response().end(ResponseEntity.success("??????").toJson());
                            })
                            .onFailure(rc::fail);
                });
    }
}
