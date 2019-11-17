package com.zman.pull.stream.impl;


import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.ISourceCallback;
import com.zman.pull.stream.IStreamBuffer;
import com.zman.pull.stream.bean.ReadResult;

public class DefaultSource<T> implements ISource<T> {

    private ISink<T> sink;

    private IStreamBuffer<T> buffer;

    private ISourceCallback callback;

    private boolean closed;

    public DefaultSource(IStreamBuffer<T> buffer){
        this(buffer, ()->{});
    }

    public DefaultSource(IStreamBuffer<T> buffer, ISourceCallback callback){
        this.buffer = buffer;
        buffer.on("update", (data)->{
            if( sink != null ){
                ISink<T> iSink = sink;
                sink = null;
                iSink.notifyAvailable();
            }
        });

        this.callback = callback;
    }

    @Override
    public ReadResult<T> produce(boolean end, ISink<T> sink) {

        if( end || closed){
            closed = true;
            callback.onClosed();
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
}
