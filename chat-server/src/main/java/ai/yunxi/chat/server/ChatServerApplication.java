package ai.yunxi.chat.server;

import ai.yunxi.chat.server.zk.RegisterToZK;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread thread = new Thread(new RegisterToZK());
        thread.setName("chat-server-regist2ZK-thread");
        thread.start();
        System.out.println("regist2ZK success...");
    }
}
