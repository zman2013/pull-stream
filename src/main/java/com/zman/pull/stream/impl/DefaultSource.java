package com.zman.pull.stream.impl;


import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.IStreamBuffer;
import com.zman.pull.stream.bean.ReadResult;

public class DefaultSource<T> implements ISource<T> {

    private ISink<T> sink;

    private IStreamBuffer<T> buffer;

    private Runnable onClosed;

    private boolean closed;

    public DefaultSource(){this(new DefaultStreamBuffer<>(),()->{});}

    public DefaultSource(IStreamBuffer<T> buffer){
        this(buffer, ()->{});
    }

    public DefaultSource(IStreamBuffer<T> buffer, Runnable onClosed){
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
    public ReadResult<T> get(boolean end, ISink<T> sink) {

        if( end || closed){
            closed = true;
            onClosed.run();
            return ReadResult.Completed;
        }

        T data = buffer.poll();
        if( data == null ){
            this.sink = sink;
            return ReadResult.Waiting;
        }else{
            return new ReadResult<>(data);
        }

    }

    /**
     * 关闭流
     */
    @Override
    public void close() {
        closed = true;
    }


    public boolean push(T data){
        return buffer.offer(data);
    }
}
