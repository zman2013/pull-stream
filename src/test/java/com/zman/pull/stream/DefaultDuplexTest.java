package com.zman.pull.stream;

import com.zman.pull.stream.impl.DefaultDuplex;
import com.zman.pull.stream.impl.DefaultStreamBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.zman.pull.stream.util.Pull.pull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDuplexTest {

    @Mock
    Function<Integer,Boolean> onNext;
    @Mock
    Consumer<Throwable> onClosed;

    @Before
    public void before(){
        when(onNext.apply(any())).thenReturn(false);
    }

    @Test
    public void testDuplexCloseSourceFirst(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        IDuplex<Integer> duplexa = new DefaultDuplex<>(buffer, t->{});
        IDuplex<Integer> duplexb = new DefaultDuplex<>(onNext, onClosed);
        buffer.offer(1);

        pull(duplexa.source(), duplexb.sink());

        buffer.offer(2);
        duplexa.source().close();

        verify(onNext, times(1)).apply(1);
        verify(onNext, times(1)).apply(2);
        verify(onClosed, times(0)).accept(null);    // source close，不会主动通知sink close
    }

    /**
     * source关闭了，sink依然尝试拉取数据
     */
    @Test
    public void testDuplexPullDataAfterSourceClosed(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        IDuplex<Integer> duplexa = new DefaultDuplex<>(buffer, t->{});
        IDuplex<Integer> duplexb = new DefaultDuplex<>(onNext, onClosed);
        buffer.offer(1);

        pull(duplexa.source(), duplexb.sink());

        buffer.offer(2);
        duplexa.source().close();

        // source 关闭了，sink依然尝试拉取数据
        duplexb.sink().read(duplexa.source());

        verify(onNext, times(1)).apply(1);
        verify(onNext, times(1)).apply(2);
        verify(onClosed, times(1)).accept(null);
    }

    @Test
    public void testDuplexCloseSinkFirst(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        IDuplex<Integer> duplexa = new DefaultDuplex<>(buffer, t->{});
        IDuplex<Integer> duplexb = new DefaultDuplex<>(onNext, onClosed);
        buffer.offer(1);

        pull(duplexa.source(), duplexb.sink());

        buffer.offer(2);
        duplexa.source().close();
        duplexb.sink().close();

        verify(onNext, times(1)).apply(1);
        verify(onNext, times(1)).apply(2);
        verify(onClosed, times(1)).accept(null);
    }


    @Test
    public void lambda(){
        Function onData = mock(Function.class);
        Consumer onClosed = mock(Consumer.class);
        IDuplex<Integer> duplexa = new DefaultDuplex<>(new DefaultStreamBuffer<>(), t->{});
        IDuplex<Integer> duplexb = new DefaultDuplex<>(onData, onClosed);

        when(onData.apply(any())).thenReturn(false);

        pull(duplexa.source(), duplexb.sink());

        duplexa.source().push(1);
        duplexb.sink().close();

        verify(onData, times(1)).apply(1);
        verify(onClosed, times(1)).accept(null);
    }

}
