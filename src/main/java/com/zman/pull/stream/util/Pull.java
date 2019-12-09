package com.zman.pull.stream.util;

import com.zman.pull.stream.*;

public class Pull {

    public static <T> void pull(ISource<T> source, ISink<T> sink){
        sink.read(source);
    }

    public static <T> void pull(IDuplex<T> a, IDuplex<T> b){
        pull(a.source(), b);
    }

    public static <T> void pull(IDuplex<T> a, ISink<T> b){
        pull(a.source(), b);
    }

    public static <T> void pull(ISource<T> a, IDuplex<T> b){
        pull(a, b.sink());
    }

    public static <T, R> void pull(ISource<T> source, IThrough<T, R> through, ISink<R> sink){
        sink.read(through.through(source));
    }

    public static <T, R> void pull(IDuplex<T> a, IThrough<T, R> through, IDuplex<R> b){
        pull(a.source(), through, b);
    }

    public static <T, R> void pull(ISource<T> a, IThrough<T, R> through, IDuplex<R> b){
        pull(a, through, b.sink());
    }

    public static <T, R> void pull(IDuplex<T> a, IThrough<T, R> through, ISink<R> b){
        pull(a.source(), through, b);
    }

    public static <T,R> ISource<R> pull(ISource<T> source, IThrough... throughs){
        ISource intermediate = source;
        for(IThrough through : throughs){
            intermediate = through.through(intermediate);
        }
        return intermediate;
    }

    public static <T,R> ISource<R> pull(IDuplex<T> duplex, IThrough... throughs){
        return pull(duplex.source(), throughs);
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

    public static <T> void link(IDuplex<T> source, IDuplex<T> sink){
        pull(source.source(), sink.sink());
        pull(sink.source(), source.sink());
    }

    public static <R> ISource<R> pull(ISource source, IStream... streams){
        if(streams.length == 0 ){
            return (ISource<R>) source;
        }

        for(int i = 0; i < streams.length-1; i ++ ){
            IThrough through = (IThrough) streams[i];
            source = through.through(source);
        }

        IStream stream = streams[streams.length-1];
        if(stream instanceof ISink){
            ISink sink = (ISink) stream;
            sink.read(source);
            return null;
        }else if(stream instanceof IDuplex){
            IDuplex duplex = (IDuplex)stream;
            duplex.sink().read(source);
            return null;
        }else{
            IThrough through = (IThrough) stream;
            return through.through(source);
        }
    }

    public static <R> ISource<R> pull(IDuplex duplex, IStream... streams){
        return pull(duplex.source(), streams);
    }

}
