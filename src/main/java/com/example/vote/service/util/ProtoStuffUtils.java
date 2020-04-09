package com.example.vote.service.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: ProtoStuffUtils
 * @description ProtoStuff工具类
 * @author: luffy
 * @date: 2020/4/7 16:10
 * @version:V1.0
 */
@Component
public class ProtoStuffUtils {
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    private static Map<Class<?>, Schema<?>> schemaMap = new ConcurrentHashMap<>();

    public static <T> byte[] serialize(T t){
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(t, getSchema((Class<T>) t.getClass()), buffer);
        }finally {
            buffer.clear();
        }
        return bytes;

    }


    public static <T> T deSerialize(byte[] bytes,Class<T> tClass){
        Schema<T> schema = getSchema(tClass);
        T object = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, object, schema);
        return object;
    }


    private static <T> Schema<T> getSchema(Class<T> tClass){

        if (schemaMap.containsKey(tClass)){
            return (Schema<T>)schemaMap.get(tClass);
        }else{
            Schema<T> schema =  RuntimeSchema.getSchema(tClass);
            if (schema!=null){
                schemaMap.put(tClass,schema);
            }
            return schema;
        }
    }

}
