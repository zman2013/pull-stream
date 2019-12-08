package com.zman.pull.stream;

import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.impl.DefaultSink;
import com.zman.pull.stream.impl.DefaultSource;
import com.zman.pull.stream.impl.DefaultStreamBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSinkTest {

    @Mock
    Function<Integer,Boolean> onNext;
    @Mock
    Runnable onWait;
    @Mock
    Consumer<Throwable> onClosed;
    @Mock
    ISource<Integer> source;

    @Test
    public void read(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        ISource<Integer> source = new DefaultSource<>(buffer, t->{});
        ISink<Integer> sink = new DefaultSink<>(onNext, onWait, onClosed);

        buffer.offer(1);

        when(onNext.apply(any())).thenReturn(false);

        sink.read(source);

        buffer.offer(2);

        sink.close(null);

        // 验证
        verify(onNext, times(1)).apply(1);
        verify(onNext, times(1)).apply(2);
        verify(onWait, times(2)).run();
        verify(onClosed, times(1)).accept(null);
    }

    @Test
    public void sourceComplete1(){

        ISink<Integer> sink = new DefaultSink<>(onNext, onClosed);

        when(source.get(false, null, sink)).thenReturn(ReadResult.Waiting);
        when(source.get(true, null, sink)).thenReturn(ReadResult.Completed);

        sink.read(source);
        sink.close(null);

        // 验证
        verify(onClosed, times(1)).accept(null);
    }

    @Test
    public void sourceComplete2(){

        ISink<Integer> sink = new DefaultSink<>(onNext, onClosed);

        when(source.get(true, null, sink)).thenReturn(ReadResult.Completed);

        sink.close();
        sink.read(source);

        // 验证
        verify(onClosed, times(1)).accept(null);
    }

    @Test
    public void sourceException(){

        ISink<Integer> sink = new DefaultSink<>(onNext, onClosed);

        Throwable throwable = new RuntimeException();
        when(source.get(false, null, sink)).thenReturn(new ReadResult<>(ReadResultEnum.Closed, throwable));

        sink.read(source);

        // 验证
        verify(onClosed, times(1)).accept(throwable);
    }

}
