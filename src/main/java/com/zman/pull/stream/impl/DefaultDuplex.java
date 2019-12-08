package com.zman.pull.stream.impl;

import com.zman.pull.stream.*;

import java.util.function.Consumer;
import java.util.function.Function;

public class DefaultDuplex<T> implements IDuplex<T> {

    private ISink<T> sink;

    private ISource<T> source;

    public DefaultDuplex(){
        this(new DefaultStreamBuffer<>(), throwable -> {});
    }

    public DefaultDuplex(IStreamBuffer<T> streamBuffer, Consumer<Throwable> onSourceClosed){
        this(streamBuffer, onSourceClosed,
                d->true, ()->{}, t->{});
    }


    public DefaultDuplex(Function<T,Boolean> onSinkNext, Consumer<Throwable> onSinkClosed){
        this(new DefaultStreamBuffer<>(), t->{}, onSinkNext, ()->{}, onSinkClosed);
    }


    public DefaultDuplex(
            IStreamBuffer<T> streamBuffer, Consumer<Throwable> onSourceClosed,
            Function<T,Boolean> onSinkNext, Runnable onSinkWaiting, Consumer<Throwable> onSinkClosed){
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
