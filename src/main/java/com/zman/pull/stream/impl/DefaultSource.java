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
            return new ReadResult(ReadResultEnum.End, closeReason);
        }

        T data = buffer.poll();
        if( data == null ){
            this.sink = sink;
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


    public boolean push(T data){
        return buffer.offer(data);
    }
}
