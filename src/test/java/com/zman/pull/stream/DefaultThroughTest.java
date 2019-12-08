package com.zman.pull.stream;

import com.zman.pull.stream.bean.ReadResult;
import com.zman.pull.stream.bean.ReadResultEnum;
import com.zman.pull.stream.impl.DefaultSink;
import com.zman.pull.stream.impl.DefaultThrough;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultThroughTest {

    @Mock
    Function<Integer,Boolean> onNext;
    @Mock
    Consumer<Throwable> onClosed;
    @Mock
    ISource<Integer> source;

    @Before
    public void before(){
        when(onNext.apply(any())).thenReturn(false);
    }

    @Test
    public void read(){
        IThrough<Integer, Integer> through = new DefaultThrough<>(d-> d*10);
        ISink<Integer> sink = new DefaultSink<>(onNext, onClosed);

        when(source.get(false, null, sink))
                .thenReturn(new ReadResult<>(ReadResultEnum.Available,1))
                .thenReturn(new ReadResult<>(ReadResultEnum.Available,2))
                .thenReturn(ReadResult.Completed);

        sink.read(through.through(source));

        // 验证
        verify(onNext, times(1)).apply(10);
        verify(onNext, times(1)).apply(20);
        verify(onClosed, times(1)).accept(null);
    }


}
