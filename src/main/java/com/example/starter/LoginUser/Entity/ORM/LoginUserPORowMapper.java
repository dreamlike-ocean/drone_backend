package com.example.starter.LoginUser.Entity.ORM;


import com.example.starter.LoginUser.Entity.LoginUserPO;

/**
 * Mapper for {@link LoginUserPO}.
 * NOTE: This class has been automatically generated from the {@link LoginUserPO} original class using Vert.x codegen.
 */

public interface LoginUserPORowMapper extends io.vertx.sqlclient.templates.RowMapper<LoginUserPO> {


  LoginUserPORowMapper INSTANCE = new LoginUserPORowMapper() { };


  java.util.stream.Collector<io.vertx.sqlclient.Row, ?, java.util.List<LoginUserPO>> COLLECTOR = java.util.stream.Collectors.mapping(INSTANCE::map, java.util.stream.Collectors.toList());

  default LoginUserPO map(io.vertx.sqlclient.Row row) {
    LoginUserPO obj = new LoginUserPO();
    Object val;
    int idx;
    if ((idx = row.getColumnIndex("nickname")) != -1 && (val = row.getString(idx)) != null) {
      obj.setNickname((String)val);
    }
    if ((idx = row.getColumnIndex("password")) != -1 && (val = row.getString(idx)) != null) {
      obj.setPassword((String)val);
    }
    if ((idx = row.getColumnIndex("phone_number")) != -1 && (val = row.getString(idx)) != null) {
      obj.setPhoneNumber((String)val);
    }
    if ((idx = row.getColumnIndex("role")) != -1 && (val = row.getString(idx)) != null) {
      obj.setRole((String)val);
    }
    if ((idx = row.getColumnIndex("user_id")) != -1 && (val = row.getInteger(idx)) != null) {
      obj.setUserId((Integer)val);
    }
    if ((idx = row.getColumnIndex("username")) != -1 && (val = row.getString(idx)) != null) {
      obj.setUsername((String)val);
    }
    return obj;
  }
}
