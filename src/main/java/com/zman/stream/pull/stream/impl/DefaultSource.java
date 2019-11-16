package com.zman.stream.pull.stream.impl;

import com.zman.scuttlebutt.pull.stream.ISink;
import com.zman.scuttlebutt.pull.stream.ISource;
import com.zman.scuttlebutt.pull.stream.IStreamBuffer;
import com.zman.scuttlebutt.pull.stream.ReadResult;

public class DefaultSource<T> implements ISource<T>{

    private ISink<T> sink;

    private IStreamBuffer<T> buffer;

    public DefaultSource(IStreamBuffer<T> buffer){
        this.buffer = buffer;
        buffer.on("update", (data)->{
            if( sink != null ){
                ISink<T> iSink = sink;
                sink = null;
                iSink.notifyAvailable();
            }
        });
    }

    @Override
    public ReadResult<T> produce(boolean end, ISink<T> sink) {

        if( end ){
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



}
