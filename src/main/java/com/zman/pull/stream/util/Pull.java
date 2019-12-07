package com.zman.pull.stream.util;

import com.zman.pull.stream.ISink;
import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.IThrough;

public class Pull {

    public static <T> void pull(ISource<T> source, ISink<T> sink){
        sink.read(source);
    }

    public static <T, R> void pull(ISource<T> source, IThrough<T, R> through, ISink<R> sink){
        sink.read(through.through(source));
    }

    public static <T,R> ISource<R> pull(ISource<T> source, IThrough... throughs){
        ISource intermediate = source;
        for(IThrough through : throughs){
            intermediate = through.through(intermediate);
        }
        return intermediate;
    }

    public static <T> void link(IDuplex<T> source, IDuplex<T> sink){
        pull(source.source(), sink.sink());
        pull(sink.source(), source.sink());
    }

    /**
     * build two stream flow: source =》 duplex.sink
     * and duplex.source =》 sink
     * @param source    source
     * @param duplex    duplex acts as both source and sink
     * @param sink      sink
     * @param <T>       element type
     */
    public static <T> void pull(ISource<T> source, IDuplex<T> duplex, ISink<T> sink){
        pull(source, duplex.sink());
        pull(duplex.source(), sink);
    }

}
