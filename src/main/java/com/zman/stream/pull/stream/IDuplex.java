package com.zman.stream.pull.stream;

/**
 * A duplex is a stream that is readable and writable.
 * @param <T> 数据类型
 */
public interface IDuplex<T> extends ISink<T>, ISource<T> {

    /**
     * 关闭流
     */
    @Override
    default void close() {}

}