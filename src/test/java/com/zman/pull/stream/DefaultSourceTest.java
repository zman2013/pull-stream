package com.zman.pull.stream;

import com.zman.pull.stream.impl.DefaultSink;
import com.zman.pull.stream.impl.DefaultSource;
import com.zman.pull.stream.impl.DefaultStreamBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSourceTest {

    @Mock
    Consumer<Integer> onNext;
    @Mock
    Consumer<Throwable> onClosed;

    @Test
    public void read(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        ISource<Integer> source = new DefaultSource<>(buffer);
        ISink<Integer> sink = new DefaultSink<>(onNext, onClosed);

        buffer.offer(1);

        sink.read(source);

        buffer.offer(2);

        source.close(null);
        sink.read(source);

        // 验证
        verify(onNext, times(1)).accept(1);
        verify(onNext, times(1)).accept(2);
        verify(onClosed, times(1)).accept(null);
    }
}
