package com.example.starter.Verticle;

import com.example.starter.MobileDevice.Server.DeviceServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;


public class DeviceVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    DeviceServer.create(vertx)
      .listen(4399)
      .onSuccess(v -> startPromise.complete())
      .onFailure(startPromise::fail);
  }
}
