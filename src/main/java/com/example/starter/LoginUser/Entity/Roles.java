package com.example.starter.LoginUser.Entity;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Roles {
  ADMIN("admin", 3),
  USER("user", 2),
  STATION("station",1),
  NOT_LOGIN("notLogin", 0);
  public String role;
  public int power;
  public static Map<String, Roles> allRoles = Arrays.stream(Roles.values()).collect(Collectors.toUnmodifiableMap(role -> role.role, Function.identity()));

  Roles(String role, int power) {
    this.role = role;
    this.power = power;
  }
}
