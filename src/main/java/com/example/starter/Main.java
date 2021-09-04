package com.example.starter;


import com.example.starter.Verticle.HttpVerticle;
import com.example.starter.Verticle.DeviceVerticle;
import io.vertx.core.Vertx;


public class Main {



  public static void main(String[] args) throws Exception{
    final Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new HttpVerticle());
    vertx.deployVerticle(new DeviceVerticle());
  }

}

