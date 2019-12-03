package com.zman.pull.stream;

import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.impl.DefaultSink;
import com.zman.pull.stream.impl.DefaultSource;
import com.zman.pull.stream.impl.DefaultStreamBuffer;
import com.zman.pull.stream.impl.DefaultThrough;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSinkTest {

    @Mock
    ISinkCallback<Integer> callback;
    @Mock
    ISource<Integer> source;

    @Test
    public void read(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        ISource<Integer> source = new DefaultSource<>(buffer, ()->{});
        IThrough<Integer, Integer> through = new DefaultThrough<>();
        ISink<Integer> sink = new DefaultSink<>(callback);

        buffer.offer(1);

        sink.read(through.through(source));

        buffer.offer(2);

        sink.close();

        // 验证
        verify(callback, times(1)).onNext(1);
        verify(callback, times(1)).onNext(2);
        verify(callback, times(2)).onWait();
        verify(callback, times(1)).onClosed();
    }

    @Test
    public void sourceComplete1(){

        ISink<Integer> sink = new DefaultSink<>(callback);

        when(source.get(false, sink)).thenReturn(ReadResult.Waiting);
        when(source.get(true, sink)).thenReturn(ReadResult.Completed);

        sink.read(source);
        sink.close();

        // 验证
        verify(callback, times(1)).onClosed();
    }

    @Test
    public void sourceComplete2(){

        ISink<Integer> sink = new DefaultSink<>(callback);

        when(source.get(true, sink)).thenReturn(ReadResult.Completed);

        sink.close();
        sink.read(source);

        // 验证
        verify(callback, times(1)).onClosed();
    }

    @Test
    public void sourceException(){

        ISink<Integer> sink = new DefaultSink<>(callback);

        Throwable throwable = new RuntimeException();
        when(source.get(false, sink)).thenReturn(new ReadResult<>(ReadResultEnum.Exception, throwable));

        sink.read(source);

        // 验证
        verify(callback, times(1)).onError(throwable);
    }

}
