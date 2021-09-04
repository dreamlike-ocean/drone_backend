package com.example.starter.Order.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVO {
    @Null(message = "订单号必须为空")
    private Long orderId;

    @NotBlank(message = "收件人不可为空")
    private String receiverName;
    @NotBlank(message = "收件手机号不可为空")
    private String phone;
    @NotBlank(message = "收件地址不可为空")
    private Integer deliveryAddress;
    @NotBlank(message = "包裹位置不能为空")
    private String packagePos;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Null(message = "启动时间必须为空")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Null(message = "开启运输时间必须为空")
    private LocalDateTime deliveryStartTime;

    @NotNull( message = "站点信息不能为空")
    private Integer stationId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Null(message = "结束运输时间必须为空")
    private LocalDateTime endTime;
    @Null(message = "设备必须为空")
    private Integer deliveryDeviceId;

    @Null(message = "状态码必须为空")
    private Integer status;

  public OrderVO(OrderPO orderPO) {
    this.receiverName = orderPO.getReceiverName();
    this.phone = orderPO.getPhone();
    this.deliveryAddress = orderPO.getDeliveryAddress();
    this.packagePos = orderPO.getPackagePos();
    this.startTime = orderPO.getStartTime();
    this.deliveryStartTime = orderPO.getDeliveryStartTime();
    this.stationId = orderPO.getStationId();
    this.endTime = orderPO.getEndTime();
    this.deliveryDeviceId = orderPO.getDeliveryDeviceId();
    this.status = orderPO.getStatus();
  }
}
