package ai.yunxi.chat.client;

import ai.yunxi.chat.client.scanner.Scan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ChatClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread thread = new Thread(new Scan());
        thread.setName("chat-client-thread"); //程序出错的时候，通过线程名称可以快速定位问题
        thread.start();
    }
}
