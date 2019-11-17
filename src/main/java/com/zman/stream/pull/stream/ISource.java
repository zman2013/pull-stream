package com.zman.stream.pull.stream;

import com.zman.stream.pull.stream.bean.ReadResult;

/**
 * A source is a stream that is not writable.
 * You must have a source at the start of a pipeline for data to move through.
 *
 * -- pull-stream
 */
public interface ISource<T> {

    /**
     * 返回一条数据
     * @param end   控制source是否结束数据的生产
     * @param sink  <code>ISink</code>的引用，当<code>ISource</code>没有数据可以提供时会保存sink的引用
     * @return  本次读取数据的结果：Available 获取到数据，Waiting 等待回调，End 结束
     */
    ReadResult<T> produce(boolean end, ISink<T> sink);

    /**
     * 关闭流
     */
    default void close(){}

}
