package com.zman.stream.pull.stream;

import java.util.function.Consumer;

/**
 * <code>ISource</code>的数据缓存
 * @param <T> 数据类型
 */
public interface IStreamBuffer<T> {

    /**
     * 外部向buffer提供了一条数据
     * @param data 数据
     * @return 成功、失败
     */
    default boolean offer(T data){return true;}

    /**
     * 从buffer中获取一条数据
     * @return data 如果没有数据返回null
     */
    T poll();

    /**
     * 订阅buffer内部发生的事件
     * 当有事件发生时，会通知订阅方
     * @param event     事件
     * @param consumer  订阅方
     */
    default void on(Object event, Consumer consumer){}


}
