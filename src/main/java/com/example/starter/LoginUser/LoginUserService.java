package com.example.starter.LoginUser;

import com.example.starter.LoginUser.Entity.LoginUserPO;
import com.example.starter.LoginUser.Entity.LoginUserVO;
import com.example.starter.LoginUser.Entity.Roles;
import com.example.starter.Station.Station;
import com.example.starter.Station.StationMapper;
import com.example.starter.Util.Pair;
import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLPool;

import java.util.List;
import java.util.UUID;


public class LoginUserService {

    private LoginUserMapper loginUserMapper;
    private StationMapper stationMapper;


    public LoginUserService(MySQLPool mySQLClient, StationMapper stationMapper) {
    this.loginUserMapper = new LoginUserMapper(mySQLClient);
    this.stationMapper = stationMapper;
    }

    public Future<LoginUserPO> loginIn(LoginUserVO loginUserVO){
        return loginUserMapper.selectLoginUser(loginUserVO.getUsername(), loginUserVO.getPassword())
          .compose(po -> po.getPassword().equals(loginUserVO.getPassword())?Future.succeededFuture(po):Future.failedFuture("密码无效"));
    }

    public Future<Pair<Station, List<Integer>>> getStationInfo(Integer userId){
      return stationMapper.getStationInfoByUserId(userId);
    }
    public Future<LoginUserPO> register(LoginUserVO loginUserVO){
        if (loginUserVO.getNickname() == null || loginUserVO.getNickname().isBlank()){
            loginUserVO.setNickname(UUID.randomUUID().toString());
        }
        loginUserVO.setRole(Roles.USER.role);
        LoginUserPO userPO = new LoginUserPO();
        userPO.setPassword(loginUserVO.getPassword());
        userPO.setUsername(loginUserVO.getUsername());
        return loginUserMapper.insertLoginUser(userPO)
                .map(v -> userPO);
    }

    public Future<Void> modifyPassword(String password, Integer userId) {
        return loginUserMapper.updatePasswordByUserId(password, userId);
    }

    public Future<Void> updateRoleByUserId(String role, Integer userId) {
        return Roles.allRoles.containsKey(role) ? loginUserMapper.updateRoleByUserId(role, userId) : Future.failedFuture("无效的role");
    }

    public Future<Void> updateNickName(String nickname, Integer userId) {
        return loginUserMapper.updateNickName(nickname, userId);
    }


}
