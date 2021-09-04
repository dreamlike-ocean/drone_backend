package com.example.starter.Order.Entity.ORM;


import com.example.starter.Order.Entity.OrderPO;

/**
 * Mapper for {@link OrderPO}.
 * NOTE: This class has been automatically generated from the {@link OrderPO} original class using Vert.x codegen.
 */

public interface OrderPORowMapper extends io.vertx.sqlclient.templates.RowMapper<OrderPO> {


  OrderPORowMapper INSTANCE = new OrderPORowMapper() { };


  java.util.stream.Collector<io.vertx.sqlclient.Row, ?, java.util.List<OrderPO>> COLLECTOR = java.util.stream.Collectors.mapping(INSTANCE::map, java.util.stream.Collectors.toList());


  default OrderPO map(io.vertx.sqlclient.Row row) {
    OrderPO obj = new OrderPO();
    Object val;
    int idx;
    if ((idx = row.getColumnIndex("delivery_address")) != -1 && (val = row.getInteger(idx)) != null) {
      obj.setDeliveryAddress((Integer)val);
    }
    if ((idx = row.getColumnIndex("delivery_device_id")) != -1 && (val = row.getInteger(idx)) != null) {
      obj.setDeliveryDeviceId((Integer)val);
    }
    if ((idx = row.getColumnIndex("delivery_start_time")) != -1 && (val = row.getLocalDateTime(idx)) != null) {
      obj.setDeliveryStartTime((java.time.LocalDateTime)val);
    }
    if ((idx = row.getColumnIndex("end_time")) != -1 && (val = row.getLocalDateTime(idx)) != null) {
      obj.setEndTime((java.time.LocalDateTime)val);
    }
    if ((idx = row.getColumnIndex("order_id")) != -1 && (val = row.getLong(idx)) != null) {
      obj.setOrderId((Long)val);
    }
    if ((idx = row.getColumnIndex("package_pos")) != -1 && (val = row.getString(idx)) != null) {
      obj.setPackagePos((String)val);
    }
    if ((idx = row.getColumnIndex("phone")) != -1 && (val = row.getString(idx)) != null) {
      obj.setPhone((String)val);
    }
    if ((idx = row.getColumnIndex("receiver_name")) != -1 && (val = row.getString(idx)) != null) {
      obj.setReceiverName((String)val);
    }
    if ((idx = row.getColumnIndex("start_time")) != -1 && (val = row.getLocalDateTime(idx)) != null) {
      obj.setStartTime((java.time.LocalDateTime)val);
    }
    if ((idx = row.getColumnIndex("station_id")) != -1 && (val = row.getInteger(idx)) != null) {
      obj.setStationId((Integer)val);
    }
    if ((idx = row.getColumnIndex("status")) != -1 && (val = row.getInteger(idx)) != null) {
      obj.setStatus((Integer)val);
    }
    if ((idx = row.getColumnIndex("user_id")) != -1 && (val = row.getInteger(idx)) != null) {
      obj.setUserId((Integer)val);
    }
    if ((idx = row.getColumnIndex("user_id")) != -1 && (val = row.getInteger(idx)) != null) {
      obj.setUserId((Integer)val);
    }
    if ((idx = row.getColumnIndex("package_pos_in_device")) != -1 && (val = row.getInteger(idx)) != null) {
      obj.setPackagePosInDevice((Integer)val);
    }
    return obj;
  }
}
