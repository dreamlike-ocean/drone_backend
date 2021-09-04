package com.example.starter.LoginUser;


import com.example.starter.LoginUser.Entity.LoginUserPO;
import com.example.starter.LoginUser.Entity.ORM.LoginUserPOParametersMapper;
import com.example.starter.LoginUser.Entity.ORM.LoginUserPORowMapper;
import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Map;


public class LoginUserMapper {

  public LoginUserMapper(MySQLPool mySQLPool) {
    this.mySQLPool = mySQLPool;
  }

  private MySQLPool mySQLPool;

    /**
     *
     * @param loginUserPO 插入的po 会回写主键
     * @return 无
     */
    public Future<Void> insertLoginUser(LoginUserPO loginUserPO){
        return mySQLPool.getConnection()
                .flatMap(conn -> SqlTemplate.forUpdate(conn, "INSERT INTO login_user (username, password, nickname, phone_number) SELECT #{username},#{password},#{nickname},#{phoneNumber} FROM dual WHERE NOT EXISTS(SELECT username FROM login_user WHERE username = #{username})").mapFrom(LoginUserPOParametersMapper.INSTANCE).execute(loginUserPO).onComplete(ar -> conn.close()))
                .flatMap(sq -> {
                    if (sq.rowCount() == 1) {
                        loginUserPO.setUserId(sq.property(MySQLClient.LAST_INSERTED_ID).intValue());
                        return Future.succeededFuture();
                    }
                    return Future.failedFuture("已经存在的用户名");
                });
    }

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @return 如果不成功就是个 Future.failedFuture，反之可以取到
     */
    public Future<LoginUserPO> selectLoginUser(String username,String password){
        return mySQLPool.getConnection()
                .flatMap(conn -> SqlTemplate.forQuery(conn, "SELECT * FROM login_user WHERE username = #{username} AND password =#{password}").mapTo(LoginUserPORowMapper.INSTANCE).execute(Map.of("username", username, "password", password)).onComplete(ar -> conn.close()))
                .flatMap(rs -> rs.size() == 1 ? Future.succeededFuture(rs.iterator().next()) : Future.failedFuture("查无此人"));
    }

    public Future<Void> updatePasswordByUserId(String password,Integer userId){
        return mySQLPool.getConnection()
          .compose(sqlConnection -> SqlTemplate.forUpdate(sqlConnection, "UPDATE login_user SET password = #{password} WHERE user_id = #{userId}").execute(Map.of("password", password, "userId", userId))
            .onComplete(ar -> sqlConnection.close()))
          .compose(sr -> Future.succeededFuture());
    }

    public Future<Void> updateRoleByUserId(String role,Integer userId){

        return mySQLPool.getConnection()
          .compose(sqlConnection -> SqlTemplate.forUpdate(sqlConnection, "UPDATE login_user SET role = #{role} WHERE user_id = #{userId}").execute(Map.of("role",role, "userId", userId))
            .onComplete(ar -> sqlConnection.close()))
          .compose(sr -> Future.succeededFuture());
    }

    public Future<Void> updateNickName(String nickname,Integer userId){

        return mySQLPool.getConnection()
          .compose(sqlConnection -> SqlTemplate.forUpdate(sqlConnection, "UPDATE login_user SET nickname=#{nickname} WHERE user_id = #{userId}").execute(Map.of("nickname", nickname, "userId", userId))
            .onComplete(ar -> sqlConnection.close()))
          .compose(sr -> Future.succeededFuture());

    }




}
