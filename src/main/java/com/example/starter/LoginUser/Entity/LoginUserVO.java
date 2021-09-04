package com.example.starter.LoginUser.Entity;


import com.example.starter.Util.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = "password",allowGetters = false,allowSetters = true)
public class LoginUserVO {
    private Integer userId;
    @NotBlank(message = "用户名不能为空",groups = {ValidationGroup.selectGroup.class,ValidationGroup.insertGroup.class})
    private String username;
    @NotBlank(message = "密码不能为空",groups = {ValidationGroup.selectGroup.class,ValidationGroup.insertGroup.class})
    private String password;
    private String nickname;
    private String role;
    @Pattern(regexp = "^1[3|4|5|7|8][0-9]\\d{4,8}$",message = "手机号格式错误",groups = {ValidationGroup.insertGroup.class})
    private String phoneNumber;

  public LoginUserVO(LoginUserPO loginUserPO) {
    this.nickname = loginUserPO.getNickname();
    this.username = loginUserPO.getUsername();
    this.userId = loginUserPO.getUserId();
    this.phoneNumber = loginUserPO.getPhoneNumber();
    this.role = loginUserPO.getRole();
  }
}
