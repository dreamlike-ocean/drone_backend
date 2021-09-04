package com.example.starter.Order;


import com.example.starter.Order.Entity.OrderPO;
import com.example.starter.Order.Entity.OrderStatus;
import com.example.starter.Order.Entity.OrderVO;

import com.example.starter.Order.Mapper.OrderMapper;
import com.example.starter.Station.StationController;
import com.example.starter.Station.StationMsg;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.ServerWebSocket;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class OrderService {


  public OrderService(OrderMapper orderMapper) {
    this.orderMapper = orderMapper;
  }

  private OrderMapper orderMapper;


    /**
     *
     * @param orderVO 前端传入的order 会回写订单号
     * @param userId 此时的用户
     *
     * @return 是否成功 ，成功会直接给商家发信息
     */
    public Future<Void> addNewOrder(OrderVO orderVO, Integer userId) {
      final ServerWebSocket serverWebSocket = StationController.stationWSConnections.get(orderVO.getStationId());
      if (serverWebSocket == null){
        return Future.failedFuture("商家未上线");
      }
      orderVO.setStartTime(LocalDateTime.now());
      OrderPO orderPO = new OrderPO(orderVO);
      orderPO.setUserId(userId);
      return serverWebSocket.writeTextMessage(new StationMsg.NewOrderMsg(orderVO).transToJson())
        .compose(v -> orderMapper.insertOrder(orderPO))
        .flatMap(i -> i == 1 ? Future.<Void>succeededFuture() : Future.failedFuture("添加失败"))
        .onSuccess(v -> orderVO.setOrderId(orderPO.getOrderId()));
    }

    public Future<List<OrderPO>> getOrderByOrderIds(Collection<Long> orderIds) {
        return orderMapper.getOrderByIds(orderIds);
    }

    //用户
    public Future<Void> cancelOrderByUser(Long orderId,String reason,Integer userId){
      return orderMapper.getOrderByOrderId(orderId)
        //为当前用户且处于waiting状态则可以取消
        .flatMap(orderPO -> orderPO.getUserId().equals(userId) && OrderStatus.cancelAllowed(orderPO.getStatus()) ? Future.succeededFuture(orderPO) : Future.failedFuture("无效的订单号"))
        .compose(orderPO -> {
          final ServerWebSocket serverWebSocket = StationController.stationWSConnections.get(orderPO.getStationId());
          if (serverWebSocket == null){
            return Future.failedFuture("商家未上线");
          }
          return serverWebSocket.writeTextMessage(new StationMsg.RejectMsg(orderId,reason).transToJson());
        })
        .compose(orderPO -> orderMapper.cancelOrder(orderId, reason));
    }

    //商家
    public Future<Void> cancelOrderByStation(Long orderId,String reason,Integer stationId){
        return orderMapper.getOrderByOrderId(orderId)
        //为当前用户且处于waiting状态则可以取消
        .flatMap(orderPO -> OrderStatus.cancelAllowed(orderPO.getStatus()) && orderPO.getStationId().equals(stationId)? Future.succeededFuture(orderPO) : Future.failedFuture("无效的订单号"))
        .compose(orderPO -> orderMapper.cancelOrder(orderId, reason));
    }


    public Future<OrderVO> getUserOrder(Long orderId,Integer userId){
      return orderMapper.getOrderByOrderId(orderId)
        .flatMap(orderPO -> orderPO.getUserId().equals(userId) ? Future.succeededFuture(orderPO) : Future.failedFuture("无效的订单号"))
        .map(this::ToVO);
    }

    public Future<List<OrderVO>> getHistoryRecord(int current,int size,int userId){
        int offset = (Math.max(current - 1, 0)) * size  ;
        return orderMapper.getOrderByUserId(userId, size, offset, true)
                .map(l -> l.stream().map(this::ToVO).collect(Collectors.toList()));
    }

    public Future<Void> updateOrderStatus(Long orderId,Integer orderStatus){
      return orderMapper.updateOrderStatus(orderStatus,orderId)
        .flatMap(i -> i == 1 ? Future.succeededFuture() : Future.failedFuture("无效的订单号"));
    }
  public Future<Integer> updateOrderStatusBatch(List<Long> orderIds,Integer orderStatus){
    return orderMapper.updateOrderStatusBatch(orderIds,orderStatus);
  }
  public Future<Void> deliveryOrders(Map<Long,Integer> order,Integer stationId,Integer deviceId){
    return orderMapper.updateOrdersDelivery(order, stationId,deviceId);
  }


    private OrderVO ToVO(OrderPO orderPO){
        return new OrderVO(orderPO);
    }






}
