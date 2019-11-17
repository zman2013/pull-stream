import com.zman.stream.pull.stream.ISink;
import com.zman.stream.pull.stream.ISource;
import com.zman.stream.pull.stream.IThrough;
import com.zman.stream.pull.stream.impl.DefaultSink;
import com.zman.stream.pull.stream.impl.DefaultSource;
import com.zman.stream.pull.stream.impl.DefaultThrough;

import java.util.Random;

import static com.zman.stream.pull.stream.util.Pull.pull;

public class Example {

    public static void main(String[] args){
        // 准备source
        ISource<Integer> source = new DefaultSource<>(
                () -> new Random().nextInt()
        );

        // 所有数据对1000取模
        IThrough<Integer> through = new DefaultThrough<>(d->d%1000);

        // 数据最终消费方
        ISink<Integer> sink = new DefaultSink<>(System.out::println);

        // pull: source -> through -> sink
        pull(source, through, sink);
    }

}
