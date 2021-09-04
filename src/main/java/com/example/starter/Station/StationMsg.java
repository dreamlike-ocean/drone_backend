package com.example.starter.Station;

import com.example.starter.Order.Entity.OrderVO;
import io.vertx.core.json.Json;
import lombok.*;

import java.util.List;
import java.util.Map;

//希望来个密封类
@Data
public abstract class StationMsg {
    public static final int ACCEPT = 0;
    public static final int REJECT = 1;
    public static final int DELIVERY = 2;
    public static final int NEW_ORDER = 3;
    public static final int ERROR = 4;
    public static final int SUCCESS = 5;
    public static final int STOP_DEVICE = 6;

    private static final byte MAX_TYPE = 6;

    protected int msgType;
    protected Integer stationId;
    protected Integer sequence;

  public void setSequence(int sequence) {
    this.sequence = sequence;
  }

  public static boolean isValid(Integer type){
      return type>=0 && type <= MAX_TYPE;
    }

    /**
     *
     * @return 参数是否合法
     */
    protected abstract boolean validate();

    public String transToJson(){
      return Json.encode(this);
    }


    @Setter
    @Getter
    @ToString(callSuper = true)
    public static class AcceptMsg extends StationMsg{
      private List<Long> orderIds;

      public AcceptMsg() {
        msgType = ACCEPT;
      }

      @Override
      public boolean validate() {
        return orderIds != null && orderIds.size() > 0;
      }
    }

    @Setter
    @Getter
    @ToString
    @AllArgsConstructor
    public static class DeliveryMsg extends StationMsg{
     private Integer deliveryDeviceId;
     // orderId -> posInDevice
     private Map<Long,Integer> orders;

      public DeliveryMsg() {
        msgType = DELIVERY;
      }

      @Override
      //其实这里有个小问题 因为我没验证device和station关系 别问问就是赶时间
    public boolean validate() {
      return deliveryDeviceId != null && orders != null && orders.size() > 0;
    }
  }

    @Setter
    @Getter
    @ToString
    @AllArgsConstructor
    public static class RejectMsg extends StationMsg{
      private Long orderId;
      private String reason;

      public RejectMsg() {
        msgType = REJECT;
      }

      @Override
      public boolean validate() {
        return orderId != null && reason != null && !reason.isBlank();
      }
    }
     @Setter
     @Getter
     @ToString
     public static class NewOrderMsg extends StationMsg{
       private OrderVO orderVO;

       public NewOrderMsg(OrderVO orderVO) {
         msgType = NEW_ORDER;
         this.orderVO = orderVO;
       }

       @Override
       public boolean validate() {
         return orderVO != null;
       }
     }

  @Setter
  @Getter
  @ToString
  public static class ErrorMsg extends StationMsg{
    private String msg;

    public ErrorMsg(String msg,Integer sequence) {
      msgType = ERROR;
      this.msg = msg;
      super.sequence = sequence;
    }

    @Override
    public boolean validate() {
      return true;
    }
  }

  public static class SuccessMsg extends StationMsg{
    public SuccessMsg(Integer sequence) {
      msgType = SUCCESS;
      super.sequence = sequence;
    }

    @Override
    protected boolean validate() {
      return true;
    }
  }

  @Setter
  @Getter
  @ToString
  public static class StopMsg extends StationMsg{
    private Integer deviceId;
    private Boolean isStill;

    public StopMsg() {
      msgType = STOP_DEVICE;
    }

    @Override
    public boolean validate() {
      return deviceId != null && isStill != null;
    }
  }



}
