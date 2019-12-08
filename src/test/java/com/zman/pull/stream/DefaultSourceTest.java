package com.zman.pull.stream;

import com.zman.pull.stream.impl.DefaultSink;
import com.zman.pull.stream.impl.DefaultSource;
import com.zman.pull.stream.impl.DefaultStreamBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSourceTest {

    @Mock
    Function<Integer,Boolean> onNext;
    @Mock
    Consumer<Throwable> onClosed;

    @Before
    public void before(){
        when(onNext.apply(any())).thenReturn(false);
    }

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
        verify(onNext, times(1)).apply(1);
        verify(onNext, times(1)).apply(2);
        verify(onClosed, times(1)).accept(null);
    }
}
