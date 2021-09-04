package com.example.starter.MobileDevice.DeviceMsg;

import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.time.LocalDateTime;
import java.util.Map;


public class DeviceMetaInfo {
    public CoordinatePoint.Longitude longitude;
    public CoordinatePoint.Latitude latitude;
    public int battery;
    public int deviceId;

    public DeviceMetaInfo(CoordinatePoint.Longitude longitude, CoordinatePoint.Latitude latitude, int battery,int deviceId) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.battery = battery;
        this.deviceId = deviceId;
    }
    //lat long bat
    public Tuple getTuple(){
        return Tuple.of(latitude.getDegree()+"."+latitude.getMinutes()+latitude.getMark(),
                longitude.getDegree()+"."+longitude.getMinutes()+longitude.getMark(),
                battery,
                deviceId,
                LocalDateTime.now()
                );
    }

    public Future<Void> insert(MySQLPool pool){
      Map<String,Object> param = Map.of("lat", latitude.getDegree()+"."+latitude.getMinutes()+latitude.getMark(),"long", longitude.getDegree()+"."+longitude.getMinutes()+longitude.getMark(),"bat",battery,"device_id",deviceId,"time",LocalDateTime.now());
      return pool.getConnection()
        //language=sql
        .compose(sqlConnection -> SqlTemplate.forQuery(sqlConnection, "INSERT INTO metainfo (lat, `long`, bat, device_id, time) VALUES (#{lat}, #{long},#{bat},#{device_id}, #{time})").execute(param).onComplete(ar -> sqlConnection.close()))
        .flatMap(rs -> Future.succeededFuture());

    }

}
