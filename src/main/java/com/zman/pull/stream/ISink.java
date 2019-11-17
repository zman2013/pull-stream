package com.zman.pull.stream;

/**
 * A sink is a stream that is not readable.
 * You must have a sink at the end of a pipeline for data to move towards.
 *
 * -- pull-stream
 */
public interface ISink<T> {

    /**
     * 该方法只允许被调用一次
     * @param source readable stream
     */
    void read(ISource<T> source);

    /**
     * 当sink收到waiting之后，当Source再次有了数据后会调用sink的{@link #notifyAvailable}方法进行通知
     * sink收到callback后，可以立刻读取数据
     */
    void notifyAvailable();

    /**
     * 关闭流
     */
    default void close(){}

}
