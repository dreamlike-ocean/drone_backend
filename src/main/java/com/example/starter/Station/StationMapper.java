package com.example.starter.Station;

import com.example.starter.Util.Pair;
import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StationMapper {

    private MySQLPool mySQLPool;

    public StationMapper(MySQLPool mySQLPool) {
      this.mySQLPool = mySQLPool;
    }

    public Future<Void> insertStation(Station station){
      return mySQLPool.getConnection()
                .flatMap(sqlConnection -> SqlTemplate.forUpdate(sqlConnection, "INSERT INTO station (station_name, longitudes, latitudes, user_id) VALUE (#{station_name},#{longitudes},#{latitudes},#{user_id})").mapFrom(StationParametersMapper.INSTANCE).execute(station).onComplete(ar -> sqlConnection.close()))
                .flatMap(sr -> sr.rowCount() == 1 ? Future.succeededFuture() : Future.failedFuture("新增站点信息失败"));
    }

    public Future<Void> updateStationPass(Integer stationId,boolean isPass){
        return mySQLPool.getConnection()
                .flatMap(sqlConnection -> SqlTemplate.forUpdate(sqlConnection,"UPDATE station SET pass = #{pass} WHERE station_id = #{stationId}").execute(Map.of("stationId",stationId,"pass",isPass)).onComplete(ar ->  sqlConnection.close()))
                .flatMap(sqlResult -> sqlResult.rowCount() == 1 ? Future.succeededFuture() : Future.failedFuture("更新站点信息失败"));
    }

    public Future<Station> getStationByUserId(Integer userId){
        //language=sql
        String sql = "SELECT * FROM station WHERE user_id = #{userId}";

        return mySQLPool.getConnection()
                .flatMap(sqlConnection -> SqlTemplate.forQuery(sqlConnection, sql).mapTo(StationRowMapper.INSTANCE).execute(Map.of("userId", userId)).onComplete(ar -> sqlConnection.close()))
                .flatMap(rs -> rs.size() == 1 ? Future.succeededFuture(rs.iterator().next()) : Future.failedFuture("无"));
    }
    public Future<Pair<Station,List<Integer>>> getStationInfoByUserId(Integer userId){
      //language=sql
      String sql = "SELECT * FROM station JOIN device d using(station_id) WHERE user_id = #{userId}";
      return mySQLPool.getConnection()
        .compose(sqlConnection -> SqlTemplate.forQuery(sqlConnection, sql).execute(Map.of("userId", userId)).onComplete(v -> sqlConnection.close()))
        .compose(rs -> {
          if (rs.rowCount() < 1){
            return Future.failedFuture("无结果");
          }
          var it  = rs.iterator();
          final Row first = it.next();
          var station = StationRowMapper.INSTANCE.map(first);
          var deviceIds = new ArrayList<Integer>();
          deviceIds.add(first.getInteger("device_id"));
          while (it.hasNext()) {
            deviceIds.add(it.next().getInteger("device_id"));
          }
          return Future.succeededFuture(Pair.of(station,deviceIds));
        });

    }


    public Future<Station> getStationById(Integer stationId){
        //language=sql
        String sql = "SELECT * FROM station WHERE station_id = #{stationId}";
        return mySQLPool.getConnection()
                .flatMap(sqlConnection -> SqlTemplate.forQuery(sqlConnection,sql).collecting(StationRowMapper.COLLECTOR).execute(Map.of("stationId",stationId)).onComplete(ar -> sqlConnection.close()))
                .flatMap(sr -> sr.rowCount() != 0 ? Future.succeededFuture(sr.value().get(0)):Future.failedFuture("无"));

    }

    public Future<List<Station>> getStations(int offset,int limit){
        //language=sql
        String sql = "SELECT * FROM station LIMIT #{limit} OFFSET #{offset}";
        return mySQLPool.getConnection()
                .compose(sqlConnection -> SqlTemplate.forQuery(sqlConnection, sql).collecting(StationRowMapper.COLLECTOR).execute(Map.of("limit", limit, "offset", offset)).onComplete(ar -> sqlConnection.close()))
                .map(SqlResult::value);
    }

  public Future<List<Station>> getStations(int offset,int limit,boolean isAll){
    //language=sql
    String sql = "SELECT * FROM station "+(!isAll ? "pass = 0":"")+" LIMIT #{limit} OFFSET #{offset}";
    return mySQLPool.getConnection()
      .compose(sqlConnection -> SqlTemplate.forQuery(sqlConnection, sql).collecting(StationRowMapper.COLLECTOR).execute(Map.of("limit", limit, "offset", offset)).onComplete(ar -> sqlConnection.close()))
      .map(SqlResult::value);
  }






}
