package com.example.starter.MobileDevice.Server;

import com.example.starter.Util.OperatorUtil;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.impl.NetSocketInternal;
import io.vertx.mysqlclient.MySQLPool;


/**
 * 能够纳入Vert.x管理，而且绝不会出问题的
 * 其原型来自于
 * @see io.vertx.mysqlclient.impl.MySQLSocketConnection#init()
 * @see io.vertx.mysqlclient.impl.MySQLConnectionFactory#doConnectInternal(Promise)
 * 出问题建议联系Vert.x作者
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @author 孙晨曦
 */
public class DeviceServerImp implements DeviceServer{

    private Vertx vertx;
    private NetServerOptions netServerOptions;
    private NetServer netServer;

    public DeviceServerImp(Vertx vertx, NetServerOptions netServerOptions) {
        this.vertx = vertx;
        this.netServerOptions = netServerOptions;
    }

    @Override
    public Future<DeviceServer> listen(int port){
      MySQLPool mysqlPool = OperatorUtil.createMysqlPool(1);
      return vertx.createNetServer(netServerOptions)
                .connectHandler(socket -> {
                    ChannelPipeline pipeline = ((NetSocketInternal) socket).channelHandlerContext().pipeline();
                    pipeline
                      .addLast(new LengthFieldBasedFrameDecoder(5*1024,0,4,0,4))
                      .addLast(new DeviceInitHandler())
                      .addLast(new DeviceInfoCodec())
                      .addLast(new DeviceMsgHandler(mysqlPool));
                })
                .listen(port)
                .map(netServer -> {
                    setNetServer(netServer);
                    return this;
                });
    }

    private DeviceServerImp setNetServer(NetServer netServer) {
        this.netServer = netServer;
        return this;
    }
}
