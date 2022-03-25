package com.example.starter.MobileDevice.Server;


import com.example.starter.MobileDevice.DeviceMsg.CoordinatePoint;
import com.example.starter.MobileDevice.DeviceMsg.DeviceCmd;
import com.example.starter.MobileDevice.DeviceMsg.DeviceMetaInfo;
import com.example.starter.MobileDevice.DeviceMsg.DeviceMsg;
import com.example.starter.Order.Entity.OrderPO;
import com.example.starter.Station.StationMessageService;
import com.example.starter.Util.OperatorUtil;
import com.example.starter.Util.Pair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class DeviceMsgHandler extends SimpleChannelInboundHandler<DeviceMsg> {
  //deviceId -> channel
    public static ConcurrentHashMap<Integer, Channel> DEVICE_CONNECTIONS = new ConcurrentHashMap<>();
    private MySQLPool mySQLPool;

  public DeviceMsgHandler(MySQLPool mySQLPool) {
    this.mySQLPool = mySQLPool;
  }

  @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeviceMsg msg) throws Exception {
      String status = DeviceCmd.UpwardCMD.getStatus(msg);
      if (status != null){
        handleStatusCMD(status,ctx);
        return;
      }

      String validCode = DeviceCmd.UpwardCMD.getValidCode(msg);
      if (validCode != null){
        OrderPO orderPO = getVaildCode(ctx.channel()).get(validCode);
        if (orderPO != null){
          Integer posInDevice = orderPO.getPackagePosInDevice();
          DeviceMsg openMsg = DeviceMsg.createOutMsg();
          DeviceCmd.DownCMD.open(openMsg,posInDevice);
          ctx.channel().writeAndFlush(openMsg);
        }
        DeviceMsg validResMsg = DeviceMsg.createOutMsg();
        DeviceCmd.DownCMD.validResult(validResMsg, orderPO != null);
        ctx.channel().writeAndFlush(validResMsg);
        return;
      }

      Pair<CoordinatePoint.Longitude, CoordinatePoint.Latitude> coordinate = DeviceCmd.UpwardCMD.getCoordinate(msg);
      if (coordinate != null){
        int battery = DeviceCmd.UpwardCMD.getBattery(msg);
        new DeviceMetaInfo(coordinate.left, coordinate.right, battery, getDeviceId(ctx.channel()))
          .insert(mySQLPool);

      }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      DEVICE_CONNECTIONS.remove(ctx.channel().attr(DeviceInitHandler.DEVICE_ID).get());
    }

    private void handleStatusCMD(String status, ChannelHandlerContext ctx){
      //路上发
      if (status.charAt(0) == '1'){
        Integer deviceId = ctx.channel().attr(DeviceInitHandler.DEVICE_ID).get();
        Integer nowAddress = Integer.valueOf(status.substring(1));
        StationMessageService.deliveryOrders.get(deviceId)
          .stream()
          .filter(orderPO -> orderPO.getDeliveryAddress().equals(nowAddress))
          .forEach(orderPO -> ctx.channel().attr(DeviceInitHandler.DEVICE_MESSAGE_CODE).get().put(OperatorUtil.randomString(6), orderPO));
        //todo 发短信

      }
    }

    private void handleMetaInfo(DeviceMetaInfo deviceMetaInfo){

    }

    private Map<String, OrderPO> getVaildCode(Channel channel){
      return channel.attr(DeviceInitHandler.DEVICE_MESSAGE_CODE).get();
    }
    private Integer getDeviceId(Channel channel){
      return channel.attr(DeviceInitHandler.DEVICE_ID).get();
    }


}
