package com.zman.pull.stream;

import com.zman.pull.stream.impl.*;
import com.zman.pull.stream.util.Pull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.ws.Holder;

import static com.zman.pull.stream.util.Pull.pull;

@RunWith(MockitoJUnitRunner.class)
public class PullTest {

    @Test
    public void pullDuplexThroughDuplex(){
        Holder<Integer> holder = new Holder<>(99);

        IDuplex<Integer> source = new DefaultDuplex<>();
        IThrough<Integer, Integer> through = new DefaultThrough<>();
        IDuplex<Integer> sink = new DefaultDuplex<>(d -> {holder.value=d;return true;}, t->{});

        pull(source, through, sink);

        // 验证
        Assert.assertEquals(99, holder.value.intValue());
    }

    @Test
    public void pullDuplexThroughSink(){
        Holder<Integer> holder = new Holder<>(99);

        IDuplex<Integer> source = new DefaultDuplex<>();
        IThrough<Integer, Integer> through = new DefaultThrough<>();
        ISink<Integer> sink = new DefaultSink<>(d -> {holder.value=d;return true;}, t->{});

        pull(source, through, sink);

        // 验证
        Assert.assertEquals(99, holder.value.intValue());
    }

    @Test
    public void link(){
        Holder<Integer> holderA = new Holder<>(0);
        Holder<Integer> holderB = new Holder<>(0);

        IStreamBuffer<Integer> bufferA = new DefaultStreamBuffer<>();
        IDuplex<Integer> a = new DefaultDuplex<>(bufferA, t->{},
                data->{holderA.value = data;return true;}, ()->{}, t->{});


        IStreamBuffer<Integer> bufferB = new DefaultStreamBuffer<>();
        IDuplex<Integer> b = new DefaultDuplex<>(bufferB, t->{},
                data->{holderB.value = data;return true;}, ()->{}, t->{});

        Pull.link(a, b);
        bufferA.offer(100);
        bufferB.offer(-100);

        // 验证
        Assert.assertEquals(-100, holderA.value.intValue());
        Assert.assertEquals(100, holderB.value.intValue());
    }

    @Test
    public void useless(){
        new Pull();
    }


    /**
     * 测试pull(source, duplex, sink)
     */
    @Test
    public void pullDuplex(){
        Holder<Integer> holder = new Holder<>();

        DefaultSource<Integer> source = new DefaultSource<>();
        Holder<DefaultDuplex<Integer>> duplexHolder = new Holder<>();

        DefaultDuplex<Integer> duplex = new DefaultDuplex<>(
                data->duplexHolder.value.source().push(data), t->{});

        duplexHolder.value = duplex;
        DefaultSink<Integer> sink = new DefaultSink<>(d -> {holder.value = d;return true;}, t->{});

        pull(source, duplex, sink);

        Integer expectedValue = 193;
        source.push(expectedValue);

        Assert.assertEquals(expectedValue, holder.value);
    }

    @Test
    public void pullDuplexAndStreams(){
        IDuplex<Integer> duplex = new DefaultDuplex<>();

        // case1: identity
        ISource<Integer> sourceIdentity = pull(duplex);
        // 验证
        Assert.assertEquals(duplex.source(), sourceIdentity);

        // case2: duplex，through，through
        Holder<Integer> holder = new Holder<>(1);
        IThrough<Integer, Integer> through1 = new DefaultThrough<>(a->a*10);
        IThrough<Integer, Integer> through2 = new DefaultThrough<>(a->a*10);

        ISource<Integer> intermediate = pull(duplex, through1, through2);
        ISink<Integer> sink = new DefaultSink<>(d -> {holder.value=d;return true;}, t->{});
        duplex.push(1);
        pull(intermediate, sink);
        // 验证
        Assert.assertEquals(100, holder.value.intValue());

        // case3: duplex, through, through, sink
        duplex.push(1);
        pull(duplex, through1, through2, sink);
        // 验证
        Assert.assertEquals(100, holder.value.intValue());

        // case4: duplex, through, through, duplex
        IDuplex<Integer> target = new DefaultDuplex<>(d->{holder.value=d;return true;}, th->{});
        duplex.push(1);
        pull(duplex, through1, through2, target);
        Assert.assertEquals(100, holder.value.intValue());

    }


    @Test
    public void pullDuplexDuplex(){
        Holder<Integer> holder = new Holder<>();
        IDuplex<Integer> a = new DefaultDuplex<>();
        IDuplex<Integer> target = new DefaultDuplex<>(d->{holder.value=d;return true;}, th->{});

        a.push(99);

        pull(a, target);

        Assert.assertEquals(99, holder.value.intValue());
    }

    @Test
    public void pullDuplexSink(){
        Holder<Integer> holder = new Holder<>();
        IDuplex<Integer> a = new DefaultDuplex<>();
        ISink<Integer> target = new DefaultSink<>(d->{holder.value=d;return true;}, th->{});

        a.push(99);

        pull(a, target);

        Assert.assertEquals(99, holder.value.intValue());
    }
}
