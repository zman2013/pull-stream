package com.zman.pull.stream.impl;


import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.IStreamBuffer;
import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;

import java.util.function.Consumer;

import static com.zman.pull.stream.bean.ReadResult.Completed;

public class DefaultSource<T> implements ISource<T> {

    private ISink<T> sink;

    private IStreamBuffer<T> buffer;

    private Consumer<Throwable> onClosed;

    private Runnable onBufferEmpty = ()->{};

    private boolean closed;

    public DefaultSource(){this(new DefaultStreamBuffer<>(),t->{});}

    public DefaultSource(IStreamBuffer<T> buffer){
        this(buffer, t->{});
    }

    public DefaultSource(IStreamBuffer<T> buffer, Consumer<Throwable> onClosed){
        this.buffer = buffer;
        buffer.on("update", (data)->{
            if( sink != null ){
                ISink<T> iSink = sink;
                sink = null;
                iSink.notifyAvailable();
            }
        });

        this.onClosed = onClosed;
    }

    @Override
    public ReadResult<T> get(boolean end, Throwable throwable, ISink<T> sink) {

        if( end ){
            closed = true;
            onClosed.accept(throwable);
            return Completed;
        }

        if( closed ){
            return new ReadResult(ReadResultEnum.Closed, closeReason);
        }

        T data = buffer.poll();
        if( data == null ){
            this.sink = sink;
            onBufferEmpty.run();
            return ReadResult.Waiting;
        }else{
            return new ReadResult<>(data);
        }

    }


    private Throwable closeReason;
    public void close(Throwable throwable) {
        this.closeReason = throwable;
        closed = true;
    }

    /**
     * callback on closed
     *
     * @param callback callback
     * @return self
     */
    @Override
    public ISource<T> onClosed(Consumer<Throwable> callback) {
        onClosed = callback;
        return this;
    }

    /**
     * callback on buffer empty
     *
     * @param callback callback
     * @return self
     */
    @Override
    public ISource<T> onBufferEmpty(Runnable callback) {
        this.onBufferEmpty = callback;
        return this;
    }

    /**
     * inject stream buffer
     *
     * @param buffer buffer
     * @return self
     */
    @Override
    public ISource<T> streamBuffer(IStreamBuffer<T> buffer) {
        this.buffer =buffer;
        return this;
    }

    public boolean push(T data){
        return buffer.offer(data);
    }
}
