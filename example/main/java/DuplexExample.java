import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.impl.DefaultDuplex;

public class DuplexExample {

    private IDuplex duplex;

    public DuplexExample(){
        duplex = new DefaultDuplex(this::onData, t->{});
    }

    private void onData(Object data){}

    private void onError(Object e){}

}
