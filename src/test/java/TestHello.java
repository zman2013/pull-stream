import org.junit.Assert;
import org.junit.Test;

public class TestHello {


    @Test
    public void test(){
        Hello hello = new Hello();
        String a = "hello world";
        hello.setA(a);
        Assert.assertEquals(a, hello.getA());
    }
}
