package com.example.starter;


import com.example.starter.Verticle.HttpVerticle;
import com.example.starter.Verticle.DeviceVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;


public class Main {



  public static void main(String[] args) throws Exception{
    var v = new VertxOptions()
      .setInternalBlockingPoolSize(5).setWorkerPoolSize(2);
    final Vertx vertx = Vertx.vertx(v);
    vertx.deployVerticle(new HttpVerticle());
    vertx.deployVerticle(new DeviceVerticle());
  }

}

