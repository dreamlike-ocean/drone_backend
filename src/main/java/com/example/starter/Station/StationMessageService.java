package com.example.starter.Station;

import com.example.starter.MobileDevice.DeviceMsg.DeviceCmd;
import com.example.starter.MobileDevice.DeviceMsg.DeviceMsg;
import com.example.starter.MobileDevice.Server.DeviceMsgHandler;
import com.example.starter.Order.Entity.OrderPO;
import com.example.starter.Order.Entity.OrderStatus;
import com.example.starter.Order.OrderService;
import com.example.starter.Util.OperatorUtil;
import io.netty.channel.Channel;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.example.starter.Station.StationMsg.isValid;


public class StationMessageService {
  //deviceId -> orders
    public static ConcurrentHashMap<Integer, List<OrderPO>> deliveryOrders = new ConcurrentHashMap<>();

    private OrderService orderService;

    public StationMessageService(OrderService orderService) {
    this.orderService = orderService;
    }

    public void dispatch(String stationMsg, Integer stationId, ServerWebSocket serverWebSocket){
      final JsonObject msgJson = new JsonObject(stationMsg);
      msgJson.put("stationId", stationId);
      final Integer msgType = msgJson.getInteger("msgType");
      var sequence = msgJson.getInteger("sequence");
      if ((msgType == null || isValid(msgType)) || sequence == null){
        serverWebSocket.writeTextMessage(new StationMsg.ErrorMsg("格式有误",sequence).transToJson());
        return;
      }
      // station R_A R_B
      // R_A R_B
      Future<?> res;
      switch (msgType){
        case StationMsg.ACCEPT:{
          final StationMsg.AcceptMsg acceptMsg = msgJson.mapTo(StationMsg.AcceptMsg.class);
          res = acceptMsg.validate() ? handlerAcceptMsg(acceptMsg): Future.failedFuture("格式有误");
          break;
        }
        case StationMsg.REJECT:{
          final StationMsg.RejectMsg rejectMsg = msgJson.mapTo(StationMsg.RejectMsg.class);
          res = rejectMsg.validate() ? handlerRejectMsg(rejectMsg) : Future.failedFuture("格式有误");
          break;
        }
        case StationMsg.DELIVERY:{
          final StationMsg.DeliveryMsg deliveryMsg = msgJson.mapTo(StationMsg.DeliveryMsg.class);
          res = deliveryMsg.validate() ? handlerDeliveryMsg(deliveryMsg) : Future.failedFuture("格式有误");
          break;
        }
        case StationMsg.STOP_DEVICE:{
          StationMsg.StopMsg stopMsg = msgJson.mapTo(StationMsg.StopMsg.class);
          res = stopMsg.validate() ? handlerStop(stopMsg) : Future.failedFuture("格式有误");
          break;
        }
        default:
          res = Future.failedFuture("格式有误");
      }
      res.onSuccess(o -> serverWebSocket.writeTextMessage(new StationMsg.SuccessMsg(sequence).transToJson()))
        .onFailure(t -> serverWebSocket.writeTextMessage(new StationMsg.ErrorMsg(t.getMessage(),sequence).transToJson()));

    }

    public Future<Integer> handlerAcceptMsg(StationMsg.AcceptMsg acceptMsg){
      return orderService.updateOrderStatusBatch(acceptMsg.getOrderIds(), OrderStatus.accepted);
    }
    public Future<Void> handlerRejectMsg(StationMsg.RejectMsg rejectMsg){
      return orderService.cancelOrderByStation(rejectMsg.getOrderId(), rejectMsg.getReason(), rejectMsg.getStationId());
    }

    public Future<Void> handlerDeliveryMsg(StationMsg.DeliveryMsg deliveryMsg){
      if (DeviceMsgHandler.DEVICE_CONNECTIONS.get(deliveryMsg.getDeliveryDeviceId()) == null){
        return Future.failedFuture("当前设备未上线");
      }
      ArrayList<OrderPO> orders = new ArrayList<>();
      deliveryOrders.put(deliveryMsg.getDeliveryDeviceId(), orders);
      DeviceMsg deviceMsg = new DeviceMsg(deliveryMsg.getDeliveryDeviceId());
      //获取目的地
      return orderService.getOrderByOrderIds(deliveryMsg.getOrders().keySet())
        .map(l -> l.stream().peek(orders::add).map(OrderPO::getDeliveryAddress).collect(Collectors.toList()))
        .compose(addresses -> {
          //构造下行go指令
          DeviceCmd.DownCMD.go(deviceMsg, addresses);
          return OperatorUtil.writeAndFlushDeviceMsg(deviceMsg, DeviceMsgHandler.DEVICE_CONNECTIONS.get(deliveryMsg.getDeliveryDeviceId()));
        })
        //写出成功后落库
        .compose(v -> orderService.deliveryOrders(deliveryMsg.getOrders(), deliveryMsg.stationId, deliveryMsg.getDeliveryDeviceId()));

    }

    public Future<Void> handlerStop(StationMsg.StopMsg stopMsg){
      Channel channel = DeviceMsgHandler.DEVICE_CONNECTIONS.get(stopMsg.getDeviceId());
      DeviceMsg outMsg = DeviceMsg.createOutMsg();
      DeviceCmd.DownCMD.stop(outMsg, stopMsg.getIsStill());
      return OperatorUtil.writeAndFlushDeviceMsg(outMsg, channel);
    }

}
