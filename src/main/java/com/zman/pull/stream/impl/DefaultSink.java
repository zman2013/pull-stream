package com.zman.pull.stream.impl;


import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.ISinkCallback;
import com.zman.pull.stream.bean.ReadResult;

public class DefaultSink<T> implements ISink<T> {

    protected boolean closed;

    protected ISinkCallback<T> callback;

    private ISource<T> source;

    public DefaultSink(){
        callback = data -> {};
    }

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
            if( closed){
                notifySourceEnd = true;
                stop = true;
            }

            ReadResult<T> readResult = source.get(notifySourceEnd, this);

            switch (readResult.status){
                case Available:
                    callback.onNext(readResult.data);
                    break;
                case Waiting:
                    stop = true;
                    callback.onWait();
                    break;
                case Exception:
                    callback.onError(readResult.throwable);
                    stop = true;
                    closed = true;
                    break;
                case End:
                    callback.onClosed();
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

    @Override
    public void close() {
        closed = true;
        // 停止后主动通知source端sink停止读取数据了
        if( source != null ) {
            this.read(source);
        }
    }

}
