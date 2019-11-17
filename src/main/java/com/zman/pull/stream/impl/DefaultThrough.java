package com.zman.pull.stream.impl;


import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.IThrough;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.bean.ReadResult;

import java.util.function.UnaryOperator;

public class DefaultThrough<T> implements IThrough<T> {

    private ISource<T> source;

    private UnaryOperator<T> unaryOperator;

    public DefaultThrough(){
        unaryOperator = d -> d;
    }

    public DefaultThrough(UnaryOperator<T> unaryOperator){
        this.unaryOperator = unaryOperator;
    }

    @Override
    public ISource<T> through(ISource<T> source) {

        this.source = source;

        return this;
    }

    @Override
    public ReadResult<T> produce(boolean end, ISink<T> sink) {

        ReadResult<T> readResult = source.produce(end, sink);
        if(ReadResultEnum.Available.equals(readResult.status)){
            readResult.data = unaryOperator.apply(readResult.data);
        }

        return readResult;
    }

}
