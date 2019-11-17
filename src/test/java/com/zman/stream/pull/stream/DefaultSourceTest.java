package com.zman.stream.pull.stream;

import com.zman.stream.pull.stream.impl.DefaultSink;
import com.zman.stream.pull.stream.impl.DefaultSource;
import com.zman.stream.pull.stream.impl.DefaultStreamBuffer;
import com.zman.stream.pull.stream.impl.DefaultThrough;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSourceTest {

    @Mock
    ISinkCallback<Integer> sinkCallback;

    @Test
    public void read(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        ISource<Integer> source = new DefaultSource<>(buffer);
        ISink<Integer> sink = new DefaultSink<>(sinkCallback);

        buffer.offer(1);

        sink.read(source);

        buffer.offer(2);

        source.close();
        sink.read(source);

        // 验证
        verify(sinkCallback, times(1)).onNext(1);
        verify(sinkCallback, times(1)).onNext(2);
        verify(sinkCallback, times(1)).onClosed();
    }
}
