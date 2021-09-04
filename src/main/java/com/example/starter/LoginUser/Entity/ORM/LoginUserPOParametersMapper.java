package com.example.starter.LoginUser.Entity.ORM;


import com.example.starter.LoginUser.Entity.LoginUserPO;

/**
 * Mapper for {@link LoginUserPO}.
 * NOTE: This class has been automatically generated from the {@link LoginUserPO} original class using Vert.x codegen.
 *
 */

public interface LoginUserPOParametersMapper extends io.vertx.sqlclient.templates.TupleMapper<LoginUserPO> {

  LoginUserPOParametersMapper INSTANCE = new LoginUserPOParametersMapper() {};

  default io.vertx.sqlclient.Tuple map(java.util.function.Function<Integer, String> mapping, int size, LoginUserPO params) {
    java.util.Map<String, Object> args = map(params);
    Object[] array = new Object[size];
    for (int i = 0;i < array.length;i++) {
      String column = mapping.apply(i);
      array[i] = args.get(column);
    }
    return io.vertx.sqlclient.Tuple.wrap(array);
  }

  default java.util.Map<String, Object> map(LoginUserPO obj) {
    java.util.Map<String, Object> params = new java.util.HashMap<>();
    params.put("nickname", obj.getNickname());
    params.put("password", obj.getPassword());
    params.put("phone_number", obj.getPhoneNumber());
    params.put("role", obj.getRole());
    params.put("user_id", obj.getUserId());
    params.put("username", obj.getUsername());
    return params;
  }
}
