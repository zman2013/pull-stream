package example;

import com.zman.pull.stream.IDuplex;
import com.zman.pull.stream.impl.DefaultDuplex;

public class DuplexExample {

    private IDuplex duplex;

    public DuplexExample(){
        duplex = new DefaultDuplex(this::onData, this::onClose, this::onError);
    }

    private void onData(Object data){}

    private void onClose(){}

    private void onError(Object e){}

    public IDuplex getDuplex() {
        return duplex;
    }
}
