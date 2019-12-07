package com.zman.pull.stream.impl;

import com.zman.pull.stream.*;
import com.zman.pull.stream.bean.ReadResult;

import java.util.function.Consumer;

public class DefaultDuplex<T> extends DefaultSink<T> implements IDuplex<T> {

    protected ISink<T> sink;

    protected IStreamBuffer<T> buffer;

//    protected IDuplexCallback callback;

    public DefaultDuplex(){
        this(new DefaultStreamBuffer<>(), new IDuplexCallback<T>() {});
    }

    public DefaultDuplex(IDuplexCallback<T> callback){
        this(new DefaultStreamBuffer<>(), callback);
    }

    public DefaultDuplex(IStreamBuffer<T> buffer){
        this(buffer, new IDuplexCallback<T>() {});
    }


    public DefaultDuplex(Consumer<T> onData, Runnable onClose, Consumer<Throwable> onException){
        this(new DefaultStreamBuffer<>(), onData, onClose, onException);
    }

    /**
     * 创建一个双工流
     * @param buffer        缓冲区
     * @param onData        pull到数据时回调函数
     * @param onClose       流关闭时的回调函数
     * @param onException   流遇到异常时的回调函数
     */
    public DefaultDuplex(IStreamBuffer<T> buffer, Consumer<T> onData, Runnable onClose, Consumer<Throwable> onException){
        this(buffer, new IDuplexCallback<T>() {
            public void onClosed() {
                onClose.run();
            }

            public void onNext(T data) {
                onData.accept(data);
            }

            public void onError(Throwable throwable) {
                onException.accept(throwable);
            }
        });
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
    }

    /**
     * 返回一条数据
     *
     * @param end  控制source是否结束数据的生产
     * @param sink <code>ISink</code>的引用，当{@link ISource}没有数据可以提供时会保存sink的引用
     * @return 本次读取数据的结果：Available 获取到数据，Waiting 等待回调，End 结束
     */
    @Override
    public ReadResult<T> get(boolean end, ISink<T> sink) {
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
     * 向缓存中添加数据，duplex暴露此接口，可以让使用方更方便，不需要与buffer进行交互。
     * @param data  一条数据
     * @return 成功、失败
     */
    public boolean push(T data){
        return buffer.offer(data);
    }

}
