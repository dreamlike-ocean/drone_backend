package com.example.starter.Util;

import com.example.starter.MobileDevice.DeviceMsg.DeviceMsg;
import io.netty.channel.Channel;
import io.netty.util.concurrent.GenericFutureListener;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.mysqlclient.SslMode;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class OperatorUtil {



    public static<T> void defaultHandleRes(T t,Integer status,RoutingContext rc){
        rc.end(ResponseEntity.success(t, status).toJson());
    }

    public static <T> void defaultHandleRes(T t,RoutingContext rc){
        defaultHandleRes(t,200,rc);
    }

    public static String[] getNowMethodParams(){
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        String className = element.getClassName();
        try {
            for (Method method : Class.forName(className).getDeclaredMethods()) {
                if (method.getName().equals(element.getMethodName())){
                    return Arrays.stream(method.getParameters()).map(Parameter::getName).toArray(String[]::new);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  获取当前类下面的所有参数为
     * @see  Router 的方法，并设置为可访问
     * @return 获取获取当前类下面的所有参数为io.vertx.ext.web.Router的方法
     */
    public static List<Method> getThisClassRouterParamMethod(){
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        String className = element.getClassName();
        try {
            Class<?> nowClass = Class.forName(className);
            return Arrays.stream(nowClass.getDeclaredMethods())
                    .filter(method -> {
                        Parameter[] parameters = method.getParameters();
                        return !method.getName().equals("route") && parameters.length == 1 && parameters[0].getType().equals(Router.class);
                    })
                    .peek(m -> m.setAccessible(true))
                    .collect(Collectors.toList());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    /**
     * 纯数字
     * @param length
     * @return
     */
    public static String randomString(int length){
        //30-39
        final Random random = new Random(System.currentTimeMillis());
        return IntStream.generate(() -> random.nextInt(10))
                .limit(length)
                .collect(StringBuilder::new,StringBuilder::append,StringBuilder::append).toString();
    }


    public static MySQLPool createMysqlPool(){
      return createMysqlPool(8);
    }
  public static MySQLPool createMysqlPool(int poolsize){
    final Vertx vertx = Vertx.currentContext().owner();
    MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions();
    mySQLConnectOptions.setHost("localhost")
      .setCachePreparedStatements(true)
      .setPreparedStatementCacheMaxSize(256)
      .setPreparedStatementCacheSqlLimit(2048)
      .setPort(3306)
      .setUser("root")
      .setPassword("12345678")
      .setDatabase("iheart")
      .setCharset("utf8mb4")
      .setUseAffectedRows(true)
      .setSslMode(SslMode.of("disabled"))
      .setTracingPolicy(TracingPolicy.PROPAGATE)
      .setCharacterEncoding("UTF-8");
    final PoolOptions poolOptions = new PoolOptions().setMaxSize(poolsize).setMaxWaitQueueSize(-1);
    return MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);
  }

    //转换future类型
    public static Future<Void> writeAndFlushDeviceMsg(DeviceMsg deviceMsg, Channel channel){
      Promise<Void> promise = Promise.promise();
      channel.writeAndFlush(deviceMsg)
        .addListener(future -> {
          if (future.isSuccess()) {
            promise.complete();
          }else {
            promise.fail(future.cause());
          }
        });
      return promise.future();
    }




}
