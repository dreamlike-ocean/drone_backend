package com.example.starter.Util;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntity <T>{
    private Integer status;
    private String err;
    private T body;

    public static<T> ResponseEntity<T> success(T body){
        return new ResponseEntity<>(200,null,body);
    }

    public static<T> ResponseEntity<T> success(T body,Integer status){
        return new ResponseEntity<>(status,null,body);
    }
    public static<T> ResponseEntity<T> failure(String err,Integer status){
        return new ResponseEntity<>(status,err,null);
    }
    public static<T> ResponseEntity<T> failure(String err){
        return new ResponseEntity<>(500,err,null);
    }
    public String toJson(){
        return JsonObject.mapFrom(this).encodePrettily();
    }


}
