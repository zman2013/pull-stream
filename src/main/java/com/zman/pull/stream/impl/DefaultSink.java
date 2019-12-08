package com.zman.pull.stream.impl;


import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.bean.ReadResult;

import java.util.function.Consumer;
import java.util.function.Function;

public class DefaultSink<T> implements ISink<T> {

    private boolean closed;

    private ISource<T> source;

    private Function<T, Boolean> onNext;

    private Runnable onWait;

    private Consumer<Throwable> onClosed;

    public DefaultSink(){
        this(d->true, t->{});
    }


    public DefaultSink(Function<T, Boolean> onNext, Consumer<Throwable> onClosed){
        this(onNext, ()->{}, onClosed);
    }


    public DefaultSink(Function<T, Boolean> onNext, Runnable onWait, Consumer<Throwable> onClosed){
        this.onNext = onNext;
        this.onWait = onWait;
        this.onClosed = onClosed;
    }

    @Override
    public void read(ISource<T> source) {
        this.source = source;

        boolean stop = false;
        while(!stop) {

            ReadResult<T> readResult = source.get(closed, closeReason,this);

            switch (readResult.status){
                case Available:
                    stop = onNext.apply(readResult.data);
                    break;
                case Wait:
                    stop = true;
                    onWait.run();
                    break;
                case Closed:
                    onClosed.accept(readResult.throwable);
                    stop = true;
                    closed = true;
                    source = null;
            }
        }
    }

    /**
     * sink收到waiting之后，当Source再次有了数据会调用sink的callback方法进行通知
     * sink收到callback后，可以立刻从source中读取数据
     */
    @Override
    public void notifyAvailable() {
        this.read(source);
    }

    private Throwable closeReason;
    public void close(Throwable throwable) {
        if(closed) return;

        this.closeReason = throwable;
        closed = true;
        // 停止后主动通知source端sink停止读取数据了
        if( source != null ) {
            this.read(source);
        }
    }

    /**
     * @return the parameter source from previous {@link #read(ISource)}
     */
    @Override
    public ISource<T> source() {
        return source;
    }

    /**
     * callback on closed
     *
     * @param callback callback
     * @return self
     */
    @Override
    public ISink<T> onClosed(Consumer<Throwable> callback) {
        this.onClosed = callback;
        return this;
    }

    /**
     * callback on next
     *
     * @param callback callback
     * @return self
     */
    @Override
    public ISink<T> onNext(Function<T, Boolean> callback) {
        this.onNext = callback;
        return this;
    }

    /**
     * callback on wait
     *
     * @param callback callback
     * @return self
     */
    @Override
    public ISink<T> onWait(Runnable callback) {
        this.onWait = callback;
        return this;
    }
}
