package com.zman.pull.stream;

/**
 * Duplex streams, which are used to communicate between two things, (i.e. over a network) are a little different. In a duplex stream, messages go both ways, so instead of a single function that represents the stream, you need a pair of streams. {source: sourceStream, sink: sinkStream}
 * @param <T> 数据类型
 */
public interface IDuplex<T> {

    ISink<T> sink();

    ISource<T> source();

}
