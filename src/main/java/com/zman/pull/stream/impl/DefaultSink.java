package com.zman.pull.stream.impl;


import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.bean.ReadResult;

import java.util.function.Consumer;

public class DefaultSink<T> implements ISink<T> {

    private boolean closed;

    protected ISource<T> source;

    private Consumer<T> onNext;

    private Runnable onWaiting;

    private Consumer<Throwable> onClosed;

    public DefaultSink(){
        this(d->{}, t->{});
    }


    public DefaultSink(Consumer<T> onNext, Consumer<Throwable> onClosed){
        this(onNext, ()->{}, onClosed);
    }

    public DefaultSink(Consumer<T> onNext, Runnable onWaiting, Consumer<Throwable> onClosed){
        this.onNext = onNext;
        this.onWaiting = onWaiting;
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
                    onNext.accept(readResult.data);
                    break;
                case Waiting:
                    stop = true;
                    onWaiting.run();
                    break;
                case End:
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

}
