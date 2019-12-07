package com.zman.pull.stream.impl;

import com.zman.pull.stream.*;

import java.util.function.Consumer;

public class DefaultDuplex<T> implements IDuplex<T> {

    protected ISink<T> sink;

    protected ISource<T> source;

    public DefaultDuplex(IStreamBuffer<T> streamBuffer, Consumer<Throwable> onSourceClosed){
        this(streamBuffer, onSourceClosed,
                d->{}, ()->{}, t->{});
    }


    public DefaultDuplex(Consumer<T> onSinkNext, Consumer<Throwable> onSinkClosed){
        this(new DefaultStreamBuffer<>(), t->{}, onSinkNext, ()->{}, onSinkClosed);
    }


    public DefaultDuplex(
            IStreamBuffer<T> streamBuffer, Consumer<Throwable> onSourceClosed,
            Consumer<T> onSinkNext, Runnable onSinkWaiting, Consumer<Throwable> onSinkClosed){
        this.sink = new DefaultSink<>(onSinkNext, onSinkWaiting, onSinkClosed);
        this.source = new DefaultSource<>(streamBuffer, onSourceClosed);
    }

    @Override
    public ISource<T> source() {
        return source;
    }

    @Override
    public ISink<T> sink() {
        return sink;
    }



}
