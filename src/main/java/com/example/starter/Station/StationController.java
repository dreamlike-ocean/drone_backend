package com.example.starter.Station;

import com.example.starter.LoginUser.Entity.LoginUserPO;
import com.example.starter.LoginUser.Entity.Roles;
import com.example.starter.LoginUser.UserController;
import com.example.starter.RouteHandler.AccessBaseSessionHandler;
import com.example.starter.RouteHandler.ValidationJsonHandler;
import com.example.starter.Util.Pair;
import com.example.starter.Util.ResponseEntity;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class StationController  {

    private static final String PREFIX = "/Station";
    public static Pattern allDigital = Pattern.compile("^[1-9]\\d*$");
    public static ConcurrentHashMap<Integer,ServerWebSocket> stationWSConnections = new ConcurrentHashMap<>();
    private StationMapper stationMapper;
    private StationMessageService stationMessageService;
    private MySQLPool mySQLPool;


  public StationController(StationMapper stationMapper, StationMessageService stationMessageService, MySQLPool mySQLPool) {
    this.stationMapper = stationMapper;
    this.stationMessageService = stationMessageService;
    this.mySQLPool = mySQLPool;
  }


  /**
   *
   *  ws接入
   */
    public void pushSystem(Router router){
        router.route()
                .path(PREFIX+"/push")
                .handler(AccessBaseSessionHandler.createOnlyMode(Roles.STATION))
                .handler(rc -> {
                  final Integer stationId = rc.session().<Pair<Station, List<Integer>>>get(UserController.stationPairKeyInSession).left.getStationId();
                  Future<ServerWebSocket> webSocketFuture = rc.request().toWebSocket()
                      .onSuccess(wsc -> {
                        stationWSConnections.put(stationId,wsc);
                        wsc.textMessageHandler(s -> handlerPushRequest(s,stationId,wsc));
                      });
                });
    }
    private void handlerPushRequest(String message,Integer stationId,ServerWebSocket wsc){
      stationMessageService.dispatch(message, stationId,wsc);
    }


    public void addStation(Router router){
        router.post(PREFIX)
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.USER))
                .handler(BodyHandler.create(false))
                .handler(ValidationJsonHandler.create(Station.class))
                .handler(rc -> {
                   StringJoiner stringJoiner = new StringJoiner("\n");
                    Station station = rc.<Station>get(ValidationJsonHandler.VALUE_KEY);
                    if (!(station.getLatMark().equals('N')||station.getLatMark().equals('S'))){
                        stringJoiner.add("纬度符号有误");
                    }
                    if (!(station.getLongMark().equals('E')||station.getLongMark().equals('W'))){
                        stringJoiner.add("经度符号有误");
                    }
                })
                .handler(rc -> {
                    Station station = rc.<Station>get(ValidationJsonHandler.VALUE_KEY);
                    station.setUserId(rc.session().<LoginUserPO>get(UserController.userKeyInSession).getUserId());
                    stationMapper.insertStation(station)
                            .onFailure(rc::fail)
                            .onSuccess(v -> rc.response().end(ResponseEntity.success("新增成功").toJson()));
                });
    }

    public void passStation(Router router){
        router.put(PREFIX+"/admin/:stationId")
                .handler(AccessBaseSessionHandler.createLeastMode(Roles.ADMIN))
                .handler(rc -> {
                    String id = rc.pathParam("stationId");
                    if (!allDigital.matcher(id).matches()) {
                        rc.fail(new NoStackTraceThrowable("无效的stationId"));
                        return;
                    }
                    stationMapper.updateStationPass(Integer.valueOf(id),true)
                      .compose(v -> mySQLPool.getConnection())
                      .compose(sqlConnection -> SqlTemplate.forUpdate(sqlConnection,"UPDATE login_user SET role = #{role} WHERE user_id = (SELECT user_id FROM station WHERE station_id = #{stationId})")
                        .execute(Map.of("role",Roles.STATION,"stationId",Integer.valueOf(id)))
                        .onComplete(ar -> sqlConnection.close()))
                            .onSuccess(v -> rc.response().end(ResponseEntity.success("成功").toJson()))
                            .onFailure(rc::fail);
                });
    }

    public void getDevice(Router router){
      router.get(PREFIX+"/device")
        .handler(AccessBaseSessionHandler.createOnlyMode(Roles.STATION))
        .handler(rc -> {
          var deviceId = rc.session().<Pair<Station,List<Integer>>>get(UserController.stationPairKeyInSession).right;
          rc.json(ResponseEntity.success(deviceId));
        });
    }

    public void getSpecificStation(Router router){
        router.get(PREFIX+"/admin/:stationId")
                .handler(rc -> {
                    String id = rc.pathParam("stationId");
                    if (!allDigital.matcher(id).matches()) {
                        rc.fail(new NoStackTraceThrowable("无效的stationId"));
                        return;
                    }
                    Integer stationId = Integer.valueOf(id);
                    stationMapper.getStationById(stationId)
                            .onSuccess(s -> rc.json(s.getPass() ? ResponseEntity.success(s) : ResponseEntity.failure("无效的stationId")))
                            .onFailure(rc::fail);
                });
    }
    public void getAdminSpecificStation(Router router){
    router.get(PREFIX+"/:stationId")
      .handler(AccessBaseSessionHandler.createOnlyMode(Roles.ADMIN))
      .handler(rc -> {
        String id = rc.pathParam("stationId");
        if (!allDigital.matcher(id).matches()) {
          rc.fail(new NoStackTraceThrowable("无效的stationId"));
          return;
        }
        Integer stationId = Integer.valueOf(id);
        stationMapper.getStationById(stationId)
          .onSuccess(s -> rc.json(ResponseEntity.success(s)))
          .onFailure(rc::fail);
      });
  }

    public void getBatchStations(Router router){
        router.get(PREFIX+"/:current/:size")
                .handler(rc -> {
                    String currentS = rc.pathParam("current");
                    String sizeS = rc.pathParam("size");
                    if (!allDigital.matcher(currentS).matches() || !allDigital.matcher(sizeS).matches()) {
                        rc.fail(new NoStackTraceThrowable("无效的参数"));
                        return;
                    }
                    int current = Math.max(Integer.parseInt(currentS), 1);
                    int size = Math.max(Integer.parseInt(sizeS), 1);
                    stationMapper.getStations((current - 1) * size, size)
                      .map(l -> l.stream().filter(Station::getPass).collect(Collectors.toList()))
                      .map(ResponseEntity::success)
                      .onSuccess(rc::json).onFailure(rc::fail);

                });
    }
  public void getAdminBatchStations(Router router){
    router.get(PREFIX+"/admin/:all/:current/:size")
      .handler(AccessBaseSessionHandler.createOnlyMode(Roles.ADMIN))
      .handler(rc -> {
        String currentS = rc.pathParam("current");
        String sizeS = rc.pathParam("size");
        boolean isAll = Integer.parseInt(rc.pathParam("all")) == 1;
        if (!allDigital.matcher(currentS).matches() || !allDigital.matcher(sizeS).matches()) {
          rc.fail(new NoStackTraceThrowable("无效的参数"));
          return;
        }
        int current = Math.max(Integer.parseInt(currentS), 1);
        int size = Math.max(Integer.parseInt(sizeS), 1);
        stationMapper.getStations((current - 1) * size, size,isAll)
          .map(l -> l.stream().filter(Station::getPass).collect(Collectors.toList()))
          .map(ResponseEntity::success)
          .onSuccess(rc::json).onFailure(rc::fail);

      });
  }


    /**
     * todo 老板说不着急，不写了 。
     * 作者注：实现参考SpringBoot版的E-transfer
    <a href="https://gitee.com/dreamlikeocean/E-transfer/blob/master/src/main/java/com/yiban/demo/Station/Service/ServiceImp/StationServiceImp.java"></a>
    */
    public void getNearStation(Router router){

    }
}
