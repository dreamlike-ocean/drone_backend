package com.example.starter.MobileDevice.Server;

import com.example.starter.MobileDevice.DeviceMsg.DeviceMsg;
import com.example.starter.Order.Entity.OrderPO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.HashMap;

@ChannelHandler.Sharable
public class DeviceInitHandler extends SimpleChannelInboundHandler<DeviceMsg> {
  public static final AttributeKey<Integer> DEVICE_ID = AttributeKey.<Integer>valueOf("deviceId");
  public static final AttributeKey<HashMap<String, OrderPO>> DEVICE_MESSAGE_CODE = AttributeKey.valueOf("deliveryOrders");

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DeviceMsg msg) throws Exception {
    int deviceId = msg.DeviceId;
    Channel deviceConnection = ctx.channel();
    Attribute<Integer> deviceIdAttribute = deviceConnection.attr(DEVICE_ID);
    deviceIdAttribute.set(deviceId);
    DeviceMsgHandler.DEVICE_CONNECTIONS.put(deviceId, ctx.channel());
    deviceConnection.attr(DEVICE_MESSAGE_CODE).set(new HashMap<>());
    ctx.pipeline().remove(this);
    ctx.fireChannelRead(msg);
  }
}
