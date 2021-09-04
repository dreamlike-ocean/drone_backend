
package com.example.starter.Order.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderPO {
    private Long orderId;

    private String receiverName;
    private String phone;
    private Integer deliveryAddress;

    private LocalDateTime startTime;

    private LocalDateTime deliveryStartTime;


    private LocalDateTime endTime;

    private Integer deliveryDeviceId;

    private Integer stationId;

    private String packagePos;

    private Integer status;

    private Integer userId;
    private Integer packagePosInDevice;

  public OrderPO(OrderVO orderVO) {
    this.receiverName = orderVO.getReceiverName();
    this.phone = orderVO.getPhone();
    this.deliveryAddress = orderVO.getDeliveryAddress();
    this.packagePos = orderVO.getPackagePos();
    this.startTime = orderVO.getStartTime();
    this.deliveryStartTime = orderVO.getDeliveryStartTime();
    this.stationId = orderVO.getStationId();
    this.endTime = orderVO.getEndTime();
    this.deliveryDeviceId = orderVO.getDeliveryDeviceId();
    this.status = orderVO.getStatus();
  }

  public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getDeliveryStartTime() {
        return deliveryStartTime;
    }

    public void setDeliveryStartTime(LocalDateTime deliveryStartTime) {
        this.deliveryStartTime = deliveryStartTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDeliveryDeviceId() {
        return deliveryDeviceId;
    }

    public void setDeliveryDeviceId(Integer deliveryDeviceId) {
        this.deliveryDeviceId = deliveryDeviceId;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getPackagePos() {
        return packagePos;
    }

    public void setPackagePos(String packagePos) {
        this.packagePos = packagePos;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPackagePosInDevice() {
        return packagePosInDevice;
    }

    public void setPackagePosInDevice(Integer packagePosInDevice) {
        this.packagePosInDevice = packagePosInDevice;
    }

    public Integer getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Integer deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
