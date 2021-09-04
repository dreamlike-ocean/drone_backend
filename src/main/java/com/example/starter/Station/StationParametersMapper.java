package com.example.starter.Station;

public interface StationParametersMapper extends io.vertx.sqlclient.templates.TupleMapper<Station> {

    StationParametersMapper INSTANCE = new StationParametersMapper() {};

    default io.vertx.sqlclient.Tuple map(java.util.function.Function<Integer, String> mapping, int size, Station params) {
        java.util.Map<String, Object> args = map(params);
        Object[] array = new Object[size];
        for (int i = 0;i < array.length;i++) {
            String column = mapping.apply(i);
            array[i] = args.get(column);
        }
        return io.vertx.sqlclient.Tuple.wrap(array);
    }

    default java.util.Map<String, Object> map(Station obj) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("latitudes", obj.getLatitudes());
        params.put("longitudes", obj.getLongitudes());
        params.put("pass", obj.getPass());
        params.put("station_id", obj.getStationId());
        params.put("station_name", obj.getStationName());
        params.put("user_id", obj.getUserId());
        params.put("lat_mark",obj.getLatMark());
        params.put("long_mark", obj.getLongMark());
        return params;
    }
}
