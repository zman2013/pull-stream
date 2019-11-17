package com.zman.stream.pull.stream;

import com.zman.stream.pull.stream.impl.*;
import com.zman.stream.pull.stream.util.Pull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.ws.Holder;

@RunWith(MockitoJUnitRunner.class)
public class PullTest {

    @Test
    public void pullThrough(){
        Holder<Integer> holder = new Holder<>(1);

        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        ISource<Integer> source = new DefaultSource<>(buffer);
        IThrough<Integer> through = new DefaultThrough<>();
        ISink<Integer> sink = new DefaultSink<>(d -> holder.value=d);

        Pull.pull(source, through, sink);

        // 验证
        Assert.assertEquals(1, holder.value.intValue());
    }

    @Test
    public void link(){
        Holder<Integer> holderA = new Holder<>(0);
        Holder<Integer> holderB = new Holder<>(0);

        IStreamBuffer<Integer> bufferA = new DefaultStreamBuffer<>();
        IDuplex<Integer> a = new DefaultDuplex<>(bufferA, new IDuplexCallback<Integer>() {
            public void onNext(Integer data) {
                holderA.value = data;
            }
        });


        IStreamBuffer<Integer> bufferB = new DefaultStreamBuffer<>();
        IDuplex<Integer> b = new DefaultDuplex<>(bufferB, new IDuplexCallback<Integer>() {
            public void onNext(Integer data) {
                holderB.value = data;
            }
        });

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

}
