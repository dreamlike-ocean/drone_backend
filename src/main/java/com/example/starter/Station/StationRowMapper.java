package com.example.starter.Station;


public interface StationRowMapper extends io.vertx.sqlclient.templates.RowMapper<Station> {


    StationRowMapper INSTANCE = new StationRowMapper() { };


    java.util.stream.Collector<io.vertx.sqlclient.Row, ?, java.util.List<Station>> COLLECTOR = java.util.stream.Collectors.mapping(INSTANCE::map, java.util.stream.Collectors.toList());

    default Station map(io.vertx.sqlclient.Row row) {
        Station obj = new Station();
        Object val;
        int idx;
        if ((idx = row.getColumnIndex("latitudes")) != -1 && (val = row.getBigDecimal(idx)) != null) {
            obj.setLatitudes((java.math.BigDecimal)val);
        }
        if ((idx = row.getColumnIndex("longitudes")) != -1 && (val = row.getBigDecimal(idx)) != null) {
            obj.setLongitudes((java.math.BigDecimal)val);
        }
        if ((idx = row.getColumnIndex("pass")) != -1 && (val = row.getBoolean(idx)) != null) {
            obj.setPass((Boolean)val);
        }
        if ((idx = row.getColumnIndex("station_id")) != -1 && (val = row.getInteger(idx)) != null) {
            obj.setStationId((Integer)val);
        }
        if ((idx = row.getColumnIndex("station_name")) != -1 && (val = row.getString(idx)) != null) {
            obj.setStationName((String)val);
        }
        if ((idx = row.getColumnIndex("user_id")) != -1 && (val = row.getInteger(idx)) != null) {
            obj.setUserId((Integer)val);
        }
        if ((idx = row.getColumnIndex("long_mark")) != -1 && (val = row.getString(idx)) != null) {
            obj.setLongMark(((String) val).charAt(0));
        }
        if ((idx = row.getColumnIndex("lat_mark")) != -1 && (val = row.getString(idx)) != null) {
            obj.setLatMark(((String) val).charAt(0));
        }
        return obj;
    }
}
