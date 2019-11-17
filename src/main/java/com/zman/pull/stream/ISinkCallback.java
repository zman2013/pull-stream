package com.zman.pull.stream;

/**
 * Sink从Source读取数据可能获得以下四个状态，分别回调业务方不同的回调方法：
 * Available：成功读取数据
 * Waiting：Source暂时没有数据，有了数据会主动通知Sink来pull
 * End: Source已经停止生产数据，以后也不会有数据产生了
 * Exception：Source出现了异常
 * @param <T> 数据类型
 */
public interface ISinkCallback<T> {

    /**
     * 成功读取数据data
     * @param data 一条数据
     */
    void onNext(T data);

    /**
     * source暂无数据，有了数据会主动通知Sink来pull
     */
    default void onWait(){}

    /**
     * 流已关闭，Source停止生产数据，以后也不会有数据产生了
     */
    default void onClosed(){System.out.println("stream closed");}

    /**
     * Source出现了异常
     * @param throwable 异常
     */
    default void onError(Throwable throwable){throwable.printStackTrace();}

}
