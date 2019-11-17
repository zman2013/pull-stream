# pull-stream
A java implementation of the pull-stream while resolved the error propagation and the non-blocking back-pressure.
https://pull-stream.github.io/

## Getting started
### Setting up the dependency
* Gradle
```groovy
implementation "com.zmannotes.stream:pull-stream:0.0.8"
```
* Maven
```xml
<dependency>
    <groupId>com.zmannotes.stream</groupId>
    <artifactId>pull-stream</artifactId>
    <version>0.0.8</version>
</dependency>
```
### Hello World
The sink will pull random numbers through the `through` from the source.
```java
public static void main(String[] args){
    // prepare source
    ISource<Integer> source = new DefaultSource<>(
            () -> new Random().nextInt()
    );

    // mod 1000
    IThrough<Integer> through = new DefaultThrough<>(d->d%1000);

    // output data
    ISink<Integer> sink = new DefaultSink<>(System.out::println);

    // pull: source -> through -> sink
    pull(source, through, sink);
}
```
### Base classes
Pull-stream features several base interfaces you can discover operators on:  
 - [`com.zman.pull.stream.ISource`](https://github.com/zman2013/pull-stream/blob/master/src/main/java/com/zman/pull/stream/ISource.java): A Source is a stream that is not writable.
 - [`com.zman.pull.stream.IThrough`](https://github.com/zman2013/pull-stream/blob/master/src/main/java/com/zman/pull/stream/IThrough.java): A Through is a stream that both reads and is read by another stream.
 - [`com.zman.pull.stream.ISink`](https://github.com/zman2013/pull-stream/blob/master/src/main/java/com/zman/pull/stream/ISink.java): A Sink is a stream that is not readable.
 - [`com.zman.pull.stream.IDuplex`](https://github.com/zman2013/pull-stream/blob/master/src/main/java/com/zman/pull/stream/IDuplex.java): A Duplex is a stream that is both readable and writable.
 
### Example
#### support lambda
```java
public class DuplexExample {
    
    private IDuplex duplex;
    
    public DuplexExample(){
        duplex = new DefaultDuplex(this::onData, this::onClose, this::onError);
    }
    
    // 消费数据
    private void onData(Object data){}
    // 流关闭时回调
    private void onClose(){}
    // 流异常时回调
    private void onError(Object e){}
    
}
```
#### duplex
```java
Holder<Integer> holderA = new Holder<>(0);
Holder<Integer> holderB = new Holder<>(0);

IStreamBuffer<Integer> bufferA = new DefaultStreamBuffer<>();
IDuplex<Integer> a = new DefaultDuplex<>(bufferA, new IDuplexCallback<Integer>() {
    public void onNext(Integer data) {
        holderA.value = data;
    }
});


IStreamBuffer<Integer> bufferB = new DefaultStreamBuffer<>();
IDuplex<Integer> b = new DefaultDuplex<>(bufferB, new IDuplexCallback<Integer>() {
    public void onNext(Integer data) {
        holderB.value = data;
    }
});

Pull.link(a, b);
bufferA.offer(100);
bufferB.offer(-100);

a.close();
b.close();
```

### callback
You can create an `ISinkCallback\ISourceCallback\IDuplexCallback` to be invoked when sth. occurs on the streams.
```java
IStreamBuffer<Integer> buffer = new DefaultStreamBuffer<>();
ISource<Integer> source = new DefaultSource<>(buffer, ()->{});
IThrough<Integer> through = new DefaultThrough<>();
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
```
