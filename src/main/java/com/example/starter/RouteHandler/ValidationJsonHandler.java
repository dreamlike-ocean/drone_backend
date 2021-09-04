package com.example.starter.RouteHandler;

import io.vertx.core.Handler;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.ext.web.RoutingContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

public class ValidationJsonHandler<T> implements Handler<RoutingContext> {
  public static String VALUE_KEY = "ValidationObject";
  private Class<T> target;
  private Class[] group;
  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  private ValidationJsonHandler(Class<T> target, Class[] group) {
    this.target = target;
    this.group = group;
  }
  public static<T> ValidationJsonHandler<T> create(Class<T> target){
    return new ValidationJsonHandler<>(target, null);
  }
  public static<T> ValidationJsonHandler<T> create(Class<T> target,Class[] group){
    return new ValidationJsonHandler<>(target, group);
  }



  @Override
  public void handle(RoutingContext rc) {
    final T t = rc.getBodyAsJson().mapTo(target);
    Set<ConstraintViolation<T>> error;
    if (group == null) {
      error = VALIDATOR.validate(t);
    }else {
      error = VALIDATOR.validate(t,group);
    }
    if (!error.isEmpty()){
      rc.fail(new NoStackTraceThrowable(error.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"))));
      return;
    }
    rc.put(VALUE_KEY, t);
    rc.next();

  }
}
