package com.zman.pull.stream.impl;


import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.IThrough;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.bean.ReadResult;

import java.util.function.Function;

public class DefaultThrough<T, R> implements IThrough<T, R> {

    protected ISource<T> source;

    protected Function<T, R> function;

    public DefaultThrough(){
        function = d -> (R)d;
    }

    public DefaultThrough(Function<T, R> function){
        this.function = function;
    }

    @Override
    public ISource<R> through(ISource<T> source) {

        this.source = source;

        return this;
    }

    @Override
    public ReadResult get(boolean end, ISink sink) {

        ReadResult readResult = source.get(end, sink);
        if(ReadResultEnum.Available.equals(readResult.status)){
            readResult.data = function.apply((T)readResult.data);
        }

        return readResult;
    }

}
