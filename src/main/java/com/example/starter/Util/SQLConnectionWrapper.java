package com.example.starter.Util;

import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SQLConnectionWrapper<T> {
    public SqlConnection sqlConnection;
    public Transaction transaction;
    public T value;
    public static<T> SQLConnectionWrapper<T> create(SqlConnection sqlConnection,T value,Transaction transaction){
        return new SQLConnectionWrapper<T>(sqlConnection,transaction,value);
    }
    public static<T> SQLConnectionWrapper<T> create(SqlConnection sqlConnection,T value){
        return new SQLConnectionWrapper<T>(sqlConnection,null,value);
    }



}
