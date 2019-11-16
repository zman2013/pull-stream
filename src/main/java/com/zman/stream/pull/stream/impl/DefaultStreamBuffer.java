package com.zman.stream.pull.stream.impl;

import com.zman.scuttlebutt.EventEmitter;
import com.zman.scuttlebutt.pull.stream.IStreamBuffer;

import java.util.concurrent.ArrayBlockingQueue;

public class DefaultStreamBuffer<T> extends EventEmitter implements IStreamBuffer<T> {

    private ArrayBlockingQueue<T> buffer = new ArrayBlockingQueue<>(1000);


    @Override
    public boolean offer(T data){
        boolean result = buffer.offer(data);

        emit("update");

        return result;
    }


    @Override
    public T poll() {
        return buffer.poll();
    }
}
