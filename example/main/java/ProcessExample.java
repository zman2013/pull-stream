import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class ProcessExample {

    public static void main(String[] args) throws IOException {
        List<String> cmd = new LinkedList<>();
        cmd.add("telnet");
        cmd.add("lixiang.com");
        cmd.add("443");
        Process process = Runtime.getRuntime().exec(cmd.toArray(new String[0]));
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
            String line = reader.readLine();
            System.out.println(line);
        }
        System.out.println("extract delta package start");

        System.in.read();
    }

}
