package com.zman.stream.pull.stream.impl;

import com.zman.scuttlebutt.pull.stream.*;

public class DefaultThrough<T> implements IThrough<T> {

    private ISource<T> source;

    @Override
    public ISource<T> through(ISource<T> source) {

        this.source = source;

        return this;
    }

    @Override
    public ReadResult<T> produce(boolean end, ISink<T> sink) {

        ReadResult readResult = source.produce(end, sink);
        if(ReadResultEnum.Available.equals(readResult.status)){
            if( readResult.data instanceof Integer ){
                readResult.data = 2 * (int)readResult.data;
            }
        }

        return readResult;
    }

}
