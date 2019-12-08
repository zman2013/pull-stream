package com.zman.pull.stream;

/**
 * Duplex streams, which are used to communicate between two things, (i.e. over a network) are a little different. In a duplex stream, messages go both ways, so instead of a single function that represents the stream, you need a pair of streams. {source: sourceStream, sink: sinkStream}
 * @param <T> 数据类型
 */
public interface IDuplex<T> {

    ISink<T> sink();

    ISource<T> source();

    /**
     * close sink and source
     * @param throwable throwable
     */
    void close(Throwable throwable);

    default void close(){close(null);}

    /**
     * push data into source buffer
     * @param data data
     * @return success or failure
     */
    boolean push(T data);
}
