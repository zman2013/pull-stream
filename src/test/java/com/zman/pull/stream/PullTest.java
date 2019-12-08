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
    public void pullThrough(){
        Holder<Integer> holder = new Holder<>(1);

        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        ISource<Integer> source = new DefaultSource<>(buffer);
        IThrough<Integer, Integer> through = new DefaultThrough<>();
        ISink<Integer> sink = new DefaultSink<>(d -> {holder.value=d;return true;}, t->{});

        pull(source, through, sink);

        // 验证
        Assert.assertEquals(1, holder.value.intValue());
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
}
