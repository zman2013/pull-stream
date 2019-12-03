package com.zman.pull.stream;

import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.impl.DefaultSink;
import com.zman.pull.stream.impl.DefaultThrough;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultThroughTest {

    @Mock
    ISinkCallback<Integer> callback;
    @Mock
    ISource<Integer> source;

    @Test
    public void read(){
        IThrough<Integer, Integer> through = new DefaultThrough<>(d-> d*10);
        ISink<Integer> sink = new DefaultSink<>(callback);

        when(source.get(false, sink))
                .thenReturn(new ReadResult<>(ReadResultEnum.Available,1))
                .thenReturn(new ReadResult<>(ReadResultEnum.Available,2))
                .thenReturn(ReadResult.Completed);

        sink.read(through.through(source));

        // 验证
        verify(callback, times(1)).onNext(10);
        verify(callback, times(1)).onNext(20);
        verify(callback, times(1)).onClosed();
    }


}
