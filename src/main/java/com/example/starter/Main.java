package com.example.starter;


import com.example.starter.Verticle.HttpVerticle;
import com.example.starter.Verticle.DeviceVerticle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.net.impl.VertxEventLoopGroup;


public class Main {

  public static void main(String[] args) throws Exception{
    var v = new VertxOptions()
      .setInternalBlockingPoolSize(1).setWorkerPoolSize(2);
    final Vertx vertx = Vertx.vertx(v);
    vertx.deployVerticle(new HttpVerticle());
    vertx.deployVerticle(new DeviceVerticle());



  }

}

