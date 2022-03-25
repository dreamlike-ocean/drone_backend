package com.example.starter.MobileDevice.Server;


import com.example.starter.MobileDevice.DeviceMsg.DeviceMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeviceInfoCodec extends ByteToMessageCodec<DeviceMsg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, DeviceMsg deviceMsg, ByteBuf byteBuf) throws Exception {
        var params = deviceMsg.params
                .entrySet()
                .stream()
                .map(e -> e.getKey()+"&"+e.getValue())
                .collect(Collectors.joining(","))
                .getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(4+params.length)
                .writeInt(channelHandlerContext.channel().attr(DeviceInitHandler.DEVICE_ID).get())
                .writeBytes(params);

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int deviceId = byteBuf.readInt();
        Map<String, String> params = Arrays.stream(((String) byteBuf.readCharSequence(byteBuf.readableBytes(), StandardCharsets.UTF_8)).split(","))
                .map(s -> s.split("&"))
                .collect(Collectors.toUnmodifiableMap(ss -> ss[0], ss -> ss[1]));
        list.add(new DeviceMsg(deviceId, params));
    }


    private List<String> merge(List<String> l1,List<String> l2){
        ArrayList<String> list = new ArrayList<>(l1);
        list.addAll(l2);
        return list;
    }
}
