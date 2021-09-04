package com.example.starter.Order.Entity.ORM;


import com.example.starter.Order.Entity.OrderPO;

/**
 * Mapper for {@link OrderPO}.
 * NOTE: This class has been automatically generated from the {@link OrderPO} original class using Vert.x codegen.
 */

public interface OrderPOParametersMapper extends io.vertx.sqlclient.templates.TupleMapper<OrderPO> {

  OrderPOParametersMapper INSTANCE = new OrderPOParametersMapper() {};

  default io.vertx.sqlclient.Tuple map(java.util.function.Function<Integer, String> mapping, int size, OrderPO params) {
    java.util.Map<String, Object> args = map(params);
    Object[] array = new Object[size];
    for (int i = 0;i < array.length;i++) {
      String column = mapping.apply(i);
      array[i] = args.get(column);
    }
    return io.vertx.sqlclient.Tuple.wrap(array);
  }

  default java.util.Map<String, Object> map(OrderPO obj) {
    java.util.Map<String, Object> params = new java.util.HashMap<>();
    params.put("delivery_address", obj.getDeliveryAddress());
    params.put("delivery_device_id", obj.getDeliveryDeviceId());
    params.put("delivery_start_time", obj.getDeliveryStartTime());
    params.put("end_time", obj.getEndTime());
    params.put("order_id", obj.getOrderId());
    params.put("package_pos", obj.getPackagePos());
    params.put("phone", obj.getPhone());
    params.put("receiver_name", obj.getReceiverName());
    params.put("start_time", obj.getStartTime());
    params.put("station_id", obj.getStationId());
    params.put("status", obj.getStatus());
    params.put("user_id", obj.getUserId());
    params.put("package_pos_in_device", obj.getPackagePosInDevice());
    return params;
  }
}
