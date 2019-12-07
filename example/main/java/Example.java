import com.zman.pull.stream.ISink;
import com.zman.pull.stream.ISource;
import com.zman.pull.stream.IThrough;
import com.zman.pull.stream.impl.DefaultSink;
import com.zman.pull.stream.impl.DefaultSource;
import com.zman.pull.stream.impl.DefaultThrough;

import java.util.Random;

import static com.zman.pull.stream.util.Pull.pull;

public class Example {

    public static void main(String[] args){
        // 准备source
        ISource<Integer> source = new DefaultSource<>(
                () -> new Random().nextInt()
        );

        // 所有数据对1000取模
        IThrough<Integer, Integer> through = new DefaultThrough<>(d->d%1000);

        // 数据最终消费方
        ISink<Integer> sink = new DefaultSink<>(System.out::println, t->{});

        // pull: source -> through -> sink
        pull(source, through, sink);
    }

}
