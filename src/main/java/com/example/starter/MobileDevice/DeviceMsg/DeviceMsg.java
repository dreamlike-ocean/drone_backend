package com.example.starter.MobileDevice.DeviceMsg;

import java.util.HashMap;
import java.util.Map;


public class DeviceMsg {
  //写出时会自动赋值
    public int DeviceId;
    public Map<String, String> params;

    public DeviceMsg(int deviceId, Map<String, String> params) {
        DeviceId = deviceId;
        this.params = params;
    }
    private DeviceMsg(){

    }

    public DeviceMsg(int deviceId){
        this(deviceId,new HashMap<>());
    }

    public static DeviceMsg createOutMsg(){
      return new DeviceMsg();
    }


}
