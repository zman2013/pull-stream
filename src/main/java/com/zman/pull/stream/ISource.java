package com.zman.pull.stream;

import com.zman.pull.stream.bean.ReadResult;

import java.util.function.Consumer;

/**
 * A source is a stream that is not writable.
 * You must have a source at the start of a pipeline for data to move through.
 *
 * -- pull-stream
 */
public interface ISource<T> {

    /**
     *
     * If current source has data, this function will return ReadResult.Available with specific data.
     *
     * While there are other 3 cases:
     *
     * 1. if current source stopped working or the parameter end is true,
     * this function will return ReadResult.Completed.
     *
     * 2. if current source doesn't have any data, this function will return ReadResult.Wait
     * and this source holds the sink reference.
     * And when this source produces more data, it will notify sink to read more data.
     *
     * 3. if current source occurs exception, this function will return ReadResult.Exception with throwable.
     *
     * @param end   control whether the source stop working
     * @param throwable when throwable is not null, the end must be true
     * @param sink  the reference of the <code>ISink</code>.
     *              when <code>ISource</code> doesn't have data, it will hold the sink's reference
     * @return  Available with data, Completion, Wait, Exception with throwable.
     */
    ReadResult<T> get(boolean end, Throwable throwable, ISink<T> sink);

    /**
     * convenient function for {@link #get(boolean, Throwable, ISink)}
     * @param end   {@link #get(boolean, Throwable, ISink)}
     * @param sink  {@link #get(boolean, Throwable, ISink)}
     * @return  {@link #get(boolean, Throwable, ISink)}
     */
    default ReadResult<T> get(boolean end, ISink<T> sink){ return get(end, null, sink);}

    /**
     * close the source stream
     * @param throwable optional
     */
    void close(Throwable throwable);

    default void close(){close(null);}

    //// injections
    /**
     * callback on closed
     * @param callback callback
     * @return self
     */
    default ISource<T> onClosed(Consumer<Throwable> callback){return this;}

    /**
     * callback on buffer empty
     * @param callback callback
     * @return self
     */
    default ISource<T> onBufferEmpty(Runnable callback){return this;}

    /**
     * inject stream buffer
     * @param buffer buffer
     * @return self
     */
    default ISource<T> streamBuffer(IStreamBuffer<T> buffer){return this;}

    //// util functions
    /**
     * a util function for more convenient, this function is not necessary for pull-stream.
     * push data into the source's local buffer
     * @param data  data
     * @return  success or failure
     */
    default boolean push(T data){return false;}
}
