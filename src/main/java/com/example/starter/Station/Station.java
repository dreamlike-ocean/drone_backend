package com.example.starter.Station;

import io.vertx.core.json.JsonObject;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
public class Station {

    private Integer stationId;
    @NotBlank(message = "站点名称不能为空")
    private String stationName;
    @DecimalMax(value= "180",message = "经度超过180")
    @DecimalMin(value = "0",message = "经度低于0")
    private BigDecimal longitudes;
    private Character longMark;
    @DecimalMax(value = "90",message = "纬度超过90")
    @DecimalMin(value = "0",message = "纬度低于0")
    private BigDecimal latitudes;
    private Character latMark;
    private Integer userId;
    private Boolean pass;
    public Station(JsonObject jsonObject){

    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public BigDecimal getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(BigDecimal longitudes) {
        this.longitudes = longitudes;
    }

    public Character getLongMark() {
        return longMark;
    }

    public void setLongMark(Character longMark) {
        this.longMark = longMark;
    }

    public BigDecimal getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(BigDecimal latitudes) {
        this.latitudes = latitudes;
    }

    public Character getLatMark() {
        return latMark;
    }

    public void setLatMark(Character latMark) {
        this.latMark = latMark;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getPass() {
        return pass;
    }

    public void setPass(Boolean pass) {
        this.pass = pass;
    }
}
