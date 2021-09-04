package com.example.starter.MobileDevice.Server;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServerOptions;


//childEventLoop用这个VertxEventLoopGroup，多一个监听就把对应的evetloop加进入
//保留一个main main里面启动，再有就是加入VertxEventLoopGroup
public interface DeviceServer {

    public static DeviceServer create(Vertx vertx, NetServerOptions netServerOptions){
        return new DeviceServerImp(vertx,netServerOptions);
    }

    public static DeviceServer create(Vertx vertx){
        return new DeviceServerImp(vertx,new NetServerOptions());

    }

    Future<DeviceServer> listen(int port);
}
