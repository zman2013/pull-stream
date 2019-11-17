package com.zman.stream.pull.stream.impl;

import com.zman.stream.pull.stream.*;
import com.zman.stream.pull.stream.bean.ReadResult;

public class DefaultDuplex<T> extends DefaultSink<T> implements IDuplex<T> {

    private ISink<T> sink;

    private IStreamBuffer<T> buffer;

    private IDuplexCallback callback;

    /**
     * 创建一个source，建议使用{@link DefaultSource<T>}
     * @param callback 回调方法
     */
    public DefaultDuplex(IDuplexCallback<T> callback){
        super.callback = callback;
        this.callback = callback;
    }

    /**
     * 创建一个sink，建议使用{@link DefaultSink<T>}
     * @param buffer 缓冲区
     */
    public DefaultDuplex(IStreamBuffer<T> buffer){
        this(buffer, new IDuplexCallback<T>() {});
    }

    /**
     * 创建一个双工流：source 和 sink
     * @param buffer    缓冲区
     * @param callback  回调方法
     */
    public DefaultDuplex(IStreamBuffer<T> buffer, IDuplexCallback<T> callback){
        this.buffer = buffer;
        buffer.on("update", (data)->{
            if( sink != null ){
                ISink<T> iSink = sink;
                sink = null;
                iSink.notifyAvailable();
            }
        });

        super.callback = callback;
        this.callback = callback;
    }

    /**
     * 返回一条数据
     *
     * @param end  控制source是否结束数据的生产
     * @param sink <code>ISink</code>的引用，当<code>ISource</code>没有数据可以提供时会保存sink的引用
     * @return 本次读取数据的结果：Available 获取到数据，Waiting 等待回调，End 结束
     */
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


}
