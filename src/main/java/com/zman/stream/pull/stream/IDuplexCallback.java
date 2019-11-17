package com.zman.stream.pull.stream;

public interface IDuplexCallback<T> extends ISourceCallback, ISinkCallback<T> {

    /**
     * 流已关闭
     */
    default void onClosed(){}

    /**
     * 成功读取数据data
     *
     * @param data 一条数据
     */
    @Override
    default void onNext(T data){}
}
