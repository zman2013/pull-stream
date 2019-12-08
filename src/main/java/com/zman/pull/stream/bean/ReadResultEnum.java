package com.zman.pull.stream.bean;

/**
 * Sink从Source读取数据，Source返回的状态
 */
public enum ReadResultEnum{
    // 读取到一条数据
    Available,
    // Source暂无数据
    Wait,
    // Source抛出异常
    Closed
}
