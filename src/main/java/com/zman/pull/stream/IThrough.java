package com.zman.pull.stream;

/**
 * A Through is a stream that both reads and is read by another stream.
 *
 * Through streams are optional.
 *
 * -- pull-stream
 */
public interface IThrough<T> extends ISource<T>{

    ISource<T> through(ISource<T> source);

}
