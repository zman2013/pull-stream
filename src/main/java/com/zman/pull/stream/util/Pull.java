package com.zman.pull.stream.util;

import com.zman.pull.stream.ISink;
import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.IThrough;

public class Pull {

    public static <T> void pull(ISource<T> source, ISink<T> sink){
        sink.read(source);
    }

    public static <T> void pull(ISource<T> source, IThrough<T> through, ISink<T> sink){
        sink.read(through.through(source));
    }

    public static <T> void link(IDuplex<T> source, IDuplex<T> sink){
        pull(source, sink);
        pull(sink, source);
    }

}