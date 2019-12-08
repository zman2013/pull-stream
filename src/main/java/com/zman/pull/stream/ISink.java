package com.zman.pull.stream;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A sink is a stream that is not readable.
 * You must have a sink at the end of a pipeline for data to move towards.
 *
 * -- pull-stream
 */
public interface ISink<T> {

    /**
     * read data from source
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
     * @param throwable optional
     */
    void close(Throwable throwable);

    default void close(){close(null);}

    /**
     * @return the parameter source from previous {@link #read(ISource)}
     */
    ISource<T> source();

    ////// inject callbacks
    /**
     * callback on closed
     * @param callback callback
     * @return self
     */
    default ISink<T> onClosed(Consumer<Throwable> callback){return this;}

    /**
     * callback on next
     * @param callback callback: apply data and return if stop
     * @return self
     */
    default ISink<T> onNext(Function<T, Boolean> callback){return this;}

    /**
     * callback on wait
     * @param callback callback
     * @return self
     */
    default ISink<T> onWait(Runnable callback){return this;}

}
