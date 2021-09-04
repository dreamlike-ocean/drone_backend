package com.example.starter.Order.Mapper;

import com.example.starter.Order.Entity.ORM.OrderPOParametersMapper;
import com.example.starter.Order.Entity.ORM.OrderPORowMapper;
import com.example.starter.Order.Entity.OrderPO;
import com.example.starter.Order.Entity.OrderStatus;
import com.example.starter.Util.SQLConnectionWrapper;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class OrderMapper {

  private MySQLPool mySQLPool;

  public OrderMapper(MySQLPool mySQLPool) {
    this.mySQLPool = mySQLPool;
  }

  //主键回写
     public Future<Integer> insertOrder(OrderPO orderPO){
        return mySQLPool.getConnection()
                .flatMap(conn -> SqlTemplate.forUpdate(conn, "INSERT INTO `order` (receiver_name, phone, delivery_address, start_time, end_time, status, delivery_device_id, user_id, delivery_start_time, station_id, package_pos, package_pos_in_device) VALUE (#{receiver_name},#{phone}, #{delivery_address}, #{start_time}, #{end_time}, #{status},#{delivery_device_id},#{user_id}, #{delivery_start_time}, #{station_id}, #{package_pos}, #{package_pos_in_device})" )
                                .mapFrom(OrderPOParametersMapper.INSTANCE)
                                .execute(orderPO)
                                .onComplete(ar -> conn.close()))
                .map(sqlResult -> {
                    orderPO.setOrderId(sqlResult.property(MySQLClient.LAST_INSERTED_ID));
                    return sqlResult.rowCount();
                });
     }
    public Future<List<OrderPO>> getOrderByIds(Collection<Long> ids){
      String sql = "SELECT * FROM `order` WHERE order_id IN ";
      StringJoiner inJoiner = new StringJoiner(",", "(", ")");
      final HashMap<String, Object> param = new HashMap<>();
      String item,format="#{id%d}";
      for (int i = 0; i < ids.size(); i++) {
        item = String.format(format,i);
        param.put(item.substring(2,item.length()-1),i);
        inJoiner.add(item);
      }
      return mySQLPool.getConnection()
                .flatMap(conn -> SqlTemplate.forQuery(conn, sql+ inJoiner).collecting(OrderPORowMapper.COLLECTOR).execute(param).onComplete(ar -> conn.close()))
        .map(SqlResult::value);

    }

    public Future<OrderPO> getOrderByOrderId(Long orderId){
    return mySQLPool.getConnection()
      .compose(sc -> SqlTemplate.forQuery(sc,"SELECT * FROM `order` WHERE order_id=#{id}").mapTo(OrderPORowMapper.INSTANCE).execute(Map.of("id",orderId)).onComplete(ar -> sc.close()))
      .flatMap(rs -> rs.size() == 0 ? Future.failedFuture("无此单号"):Future.succeededFuture(rs.iterator().next()));
    }

    public Future<Integer> updateOrderStatus(Integer status, Long orderId, Integer userId){
        return mySQLPool.getConnection()
                .flatMap(c -> SqlTemplate.forUpdate(c, "UPDATE `order` SET status = #{status} WHERE order_id = #{orderId} AND user_id = #{userId}")
                        .execute(Map.of("status", status, "orderId", orderId,"userId",userId))
                        .onComplete(ar -> c.close())
                )
                .map(SqlResult::rowCount);
    }

    public Future<Integer> updateOrderStatus(Integer status, Long orderId){
        return mySQLPool.getConnection()
                .flatMap(c -> SqlTemplate.forUpdate(c, "UPDATE `order` SET status = #{status} WHERE order_id = #{orderId}")
                        .execute(Map.of("status", status, "orderId", orderId))
                        .onComplete(ar -> c.close())
                )
                .map(SqlResult::rowCount);
    }

    public Future<Integer> updateOrderStatusBatch(List<Long> orderIds,Integer status){
      StringJoiner inJoiner = new StringJoiner(",", "(", ")");
      final HashMap<String, Object> param = new HashMap<>();
      String item,format="#{id%d}";
      for (int i = 0; i < orderIds.size(); i++) {
        item = String.format(format,i);
        param.put(item.substring(2,item.length()-1),i);
        inJoiner.add(item);
      }
      String sql = "UPDATE `order` SET status = #{status} WHERE order_id IN "+inJoiner;
      param.put("status",status);
      return mySQLPool.getConnection()
        .compose(sqlConnection -> SqlTemplate.forUpdate(sqlConnection,sql).execute(param).onComplete(ar -> sqlConnection.close()))
        .map(SqlResult::rowCount);
    }

    public Future<Void> updateOrdersDelivery(Map<Long,Integer> orderIdAndPosInDevice,Integer stationId,Integer deviceId){
    //language=sql
       String sql = "UPDATE `order` SET status = #{status},package_pos_in_device=#{pos},delivery_device_id = #{deviceId},delivery_start_time = #{deliveryTime} WHERE user_id =  #{orderId} AND station_id = #{stationId}";
      LocalDateTime now = LocalDateTime.now();
      final List<Map<String, Object>> param = orderIdAndPosInDevice.entrySet().stream().map(e -> Map.of("status", OrderStatus.delivery, "pos", e.getValue(), "orderId",(Object)e.getKey(),"stationId",stationId,"deviceId",deviceId,"deliveryTime",now)).collect(Collectors.toList());
      return mySQLPool.getConnection()
        .compose(sqlConnection -> SqlTemplate.forUpdate(sqlConnection, sql).executeBatch(param).onComplete(ar -> sqlConnection.close()))
        .flatMap(ar -> Future.succeededFuture());
    }




  public Future<Integer> updateOrder(OrderPO orderPO){
        //language=sql
        String sql = "UPDATE `order` SET receiver_name = #{receiver_name},phone=#{phone},delivery_address=#{delivery_address},start_time=#{start_time},end_time = #{end_time},status = #{status},delivery_device_id = #{delivery_device_id},user_id = #{user_id},delivery_start_time = #{delivery_start_time},station_id = #{station_id},package_pos = #{package_pos},package_pos_in_device = #{package_pos_in_device}\n" +
                "WHERE order_id =#{order_id}";

        return mySQLPool.getConnection()
                .flatMap(c -> SqlTemplate.forUpdate(c, sql).mapFrom(OrderPOParametersMapper.INSTANCE).execute(orderPO).onComplete(ar -> c.close()).map(SqlResult::rowCount));
    }

    public Future<List<OrderPO>> getOrderByUserId(Integer userId,int limit,int offset,boolean isReverse){
        //language=sql
        String sql = "SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY order_id "+(isReverse?"DESC":"")+" LIMIT #{limit} OFFSET #{offset}";
        Map<String, Object> params = Map.of("userId", userId, "limit", limit, "offset", offset);
        return mySQLPool.getConnection()
                .flatMap(c -> SqlTemplate.forQuery(c, sql).mapTo(OrderPORowMapper.INSTANCE).execute(params).onComplete(ar -> c.close()))
                .map(rs -> {
                    ArrayList<OrderPO> list = new ArrayList<>();
                    rs.forEach(list::add);
                    return list;
                });
    }


    public Future<Void> deleteOrderById(Long orderId){
        return mySQLPool.getConnection()
                .flatMap(c -> SqlTemplate.forUpdate(c, "DELETE FROM `order` WHERE order_id = #{orderId}").execute(Map.of("orderID", orderId)).onComplete(ar -> c.close()))
                .flatMap(sr -> Future.succeededFuture());
    }

    public Future<Void> insertCancelOrder(Long orderId,String reason){
        return mySQLPool.getConnection()
                .flatMap(conn -> SqlTemplate.forUpdate(conn, "INSERT INTO cancel_order (order_id, reason) VALUE (#{orderId},#{reason})").execute(Map.of("orderId", orderId, "reason", reason)).onComplete(ar -> conn.close()))
                .flatMap(sqlResult -> sqlResult.rowCount() == 1 ? Future.succeededFuture() : Future.failedFuture("插入无效"));
    }

    public Future<Void> deleteCancelOrder(Long orderId){
        return mySQLPool.getConnection()
                .flatMap(conn -> SqlTemplate.forQuery(conn,"DELETE FROM cancel_order WHERE order_id = #{orderId}").execute(Map.of("orderId",orderId)).onComplete(ar -> conn.close()))
                .flatMap(rs -> Future.succeededFuture());
    }

    public Future<Integer> updateOrders(List<OrderPO> orders){
        String sql = "UPDATE `order` SET receiver_name = #{receiver_name},phone=#{phone},delivery_address=#{delivery_address},start_time=#{start_time},end_time = #{end_time},status = #{status},delivery_device_id = #{delivery_device_id},user_id = #{user_id},delivery_start_time = #{delivery_start_time},station_id = #{station_id},package_pos = #{package_pos},package_pos_in_device = #{package_pos_in_device} WHERE order_id =#{order_id}";
        return mySQLPool.getConnection()
                .flatMap(conn -> SqlTemplate.forUpdate(conn, sql).mapFrom(OrderPOParametersMapper.INSTANCE).executeBatch(orders).onComplete(ar -> conn.close()))
                .map(SqlResult::rowCount);
    }

    /**
     * 以事务的方式更新数据表中的订单状态和插入取消订单信息
     * @param orderId
     * @param reason
     * @return
     */
    public Future<Void> cancelOrder(Long orderId,String reason){
        Promise<Void> cancelPromise = Promise.promise();
        mySQLPool.getConnection()
                //获取连接后启动事务，获取事务失败后会关闭连接，获取成功会把事务和连接打个包封在一起传递下去
                .flatMap(sqlConnection -> sqlConnection.begin().map(transaction -> SQLConnectionWrapper.create(sqlConnection,null,transaction)).onFailure(t -> {
                    cancelPromise.fail(t);
                    sqlConnection.close();
                }))
                //获取事务成功
                .onSuccess(sw -> {
                    SqlConnection sqlConnection = sw.getSqlConnection();
                    //插入取消的订单信息
                    SqlTemplate.forUpdate(sqlConnection, "INSERT INTO cancel_order (order_id, reason) VALUE (#{orderId},#{reason})").execute(Map.of("orderId", orderId, "reason", reason))
                            .flatMap(voidSqlResult -> voidSqlResult.rowCount() == 1 ? Future.failedFuture("无效的订单号"):Future.succeededFuture())
                            //更新订单数据表中的状态
                            .flatMap(V -> SqlTemplate.forUpdate(sqlConnection, "UPDATE `order` SET status = #{status} WHERE order_id = #{orderId}").execute(Map.of("status", OrderStatus.cancel, "orderId", orderId)))
                            .flatMap(voidSqlResult -> voidSqlResult.rowCount() == 1 ? Future.failedFuture("无效的订单号"):Future.succeededFuture())
                            //以上都成功会提交事务并关闭连接
                            .onSuccess(o -> {
                                sw.getTransaction().commit().onFailure(cancelPromise::fail).onComplete(ar -> sqlConnection.close());
                            })
                            //反之回滚事务关闭连接
                            .onFailure(t -> sw.getTransaction().rollback(ar -> sqlConnection.close()));
                })
                .onFailure(cancelPromise::fail);
        return cancelPromise.future();
    }
}
