package com.zman.stream.pull.stream;

import com.zman.stream.pull.stream.impl.DefaultDuplex;
import com.zman.stream.pull.stream.impl.DefaultStreamBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.zman.stream.pull.stream.util.Pull.pull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDuplexTest {

    @Mock
    IDuplexCallback<Integer> duplexCallback;

    @Test
    public void testDuplexCloseSourceFirst(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        IDuplex<Integer> source = new DefaultDuplex<>(buffer);
        IDuplex<Integer> sink = new DefaultDuplex<>(duplexCallback);
        buffer.offer(1);

        pull(source, sink);

        buffer.offer(2);
        source.close();

        verify(duplexCallback, times(1)).onNext(1);
        verify(duplexCallback, times(1)).onNext(2);
        verify(duplexCallback, times(0)).onClosed();    // source close，不会主动通知sink close
    }

    /**
     * source关闭了，sink依然尝试拉取数据
     */
    @Test
    public void testDuplexPullDataAfterSourceClosed(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        IDuplex<Integer> source = new DefaultDuplex<>(buffer);
        IDuplex<Integer> sink = new DefaultDuplex<>(duplexCallback);
        buffer.offer(1);

        pull(source, sink);

        buffer.offer(2);
        source.close();

        // source 关闭了，sink依然尝试拉取数据
        sink.read(source);

        verify(duplexCallback, times(1)).onNext(1);
        verify(duplexCallback, times(1)).onNext(2);
        verify(duplexCallback, times(1)).onClosed();
    }

    @Test
    public void testDuplexCloseSinkFirst(){
        IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
        IDuplex<Integer> source = new DefaultDuplex<>(buffer);
        IDuplex<Integer> sink = new DefaultDuplex<>(duplexCallback);
        buffer.offer(1);

        pull(source, sink);

        buffer.offer(2);
        sink.close();
        source.close();

        verify(duplexCallback, times(1)).onNext(1);
        verify(duplexCallback, times(1)).onNext(2);
        verify(duplexCallback, times(1)).onClosed();
    }

}
