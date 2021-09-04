package com.example.starter.MobileDevice.DeviceMsg;



import com.example.starter.Util.Pair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DeviceCmd {
    public static class UpwardCMD{
        /**
         *
         * @param deviceMsg
         * @return left为经度 right为纬度
         */
        public static Pair<CoordinatePoint.Longitude,CoordinatePoint.Latitude> getCoordinate(DeviceMsg deviceMsg){
            if (deviceMsg.params.get("long") == null){
                return null;
            }
            return Pair.of(new CoordinatePoint.Longitude(deviceMsg.params.get("long")),new CoordinatePoint.Latitude(deviceMsg.params.get("lati")));
        }

        /**
         *
         * @param deviceMsg
         * @return 获取电量，格式为dd 单位为百分比
         */
        public static int getBattery(DeviceMsg deviceMsg){
            return Integer.parseInt(deviceMsg.params.get("bat"));
        }

        /**
         *
         * @param deviceMsg
         * @return false为发生了问题
         */
        public static Pair<Boolean,String> getDone(DeviceMsg deviceMsg){
            String doneCmd = deviceMsg.params.get("done");
            return Pair.of(doneCmd.equals("ERROR"),doneCmd);
        }

        public static String getValidCode(DeviceMsg deviceMsg){
            return deviceMsg.params.get("validCode");
        }

        public static String getStatus(DeviceMsg deviceMsg){
            return deviceMsg.params.get("status");
        }
    }

    public static class DownCMD{

        public static void go(DeviceMsg deviceMsg, List<Integer> targets){
            deviceMsg.params.put("go", targets.stream().map(Objects::toString).collect(Collectors.joining("%")));
        }

        public static void open(DeviceMsg deviceMsg, int cabinet){
            deviceMsg.params.put("open", String.valueOf(cabinet));
        }

        public static void check(DeviceMsg deviceMsg, int cabinet){
            deviceMsg.params.put("check", String.valueOf(cabinet));
        }

        public static void stop(DeviceMsg deviceMsg, boolean isStill){
            deviceMsg.params.put("stop", isStill ? "0": "1");
        }

        public static void validResult(DeviceMsg deviceMsg, boolean isSuccess){
            deviceMsg.params.put("validResult", isSuccess ? "01":"00");
        }
    }
}
