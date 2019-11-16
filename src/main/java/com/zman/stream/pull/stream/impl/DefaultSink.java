package com.zman.stream.pull.stream.impl;

import com.zman.scuttlebutt.pull.stream.ISink;
import com.zman.scuttlebutt.pull.stream.ISinkCallback;
import com.zman.scuttlebutt.pull.stream.ISource;
import com.zman.scuttlebutt.pull.stream.ReadResult;

public class DefaultSink<T> implements ISink<T> {

    private boolean running = true;

    private ISinkCallback<T> callback;

    private ISource<T> source;

    public DefaultSink(ISinkCallback<T> callback){
        this.callback = callback;
    }

    @Override
    public void read(ISource<T> source) {
        this.source = source;

        boolean stop = false;
        while(!stop) {
            boolean notifySourceEnd = false;

            // 如果该sink已经停止了，通知source
            if( !running){
                notifySourceEnd = true;
                stop = true;
            }

            ReadResult<T> readResult = source.produce(notifySourceEnd, this);

            switch (readResult.status){
                case Available:
                    callback.onNext(readResult.data);
                    break;
                case Waiting:
                    stop = true;
                    callback.waiting();
                    break;
                case Exception:
                    callback.onError(readResult.throwable);
                    break;
                case End:
                    callback.onComplete();
                    stop = true;
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

    @Override
    public void stop() {
        running = false;
    }

}
