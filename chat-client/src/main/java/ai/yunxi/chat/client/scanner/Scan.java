package ai.yunxi.chat.client.scanner;

import ai.yunxi.chat.client.config.InitConfiguration;
import ai.yunxi.chat.client.config.SpringBeanFactory;
import ai.yunxi.chat.client.init.ChatClientInit;
import ai.yunxi.chat.commons.Utils.StringUtil;
import ai.yunxi.chat.commons.constant.MessageConstant;
import ai.yunxi.chat.commons.protocol.ChatInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Scan implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(Scan.class);

    private ChatClientInit client;
    private InitConfiguration conf;

    public Scan() {
        this.client = SpringBeanFactory.getBean(ChatClientInit.class);
        this.conf = SpringBeanFactory.getBean(InitConfiguration.class);
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);
        try {
            while (true) {
                String msg = scan.nextLine();
                if (StringUtil.isEmpty(msg)) {
                    logger.info("---不允许发送空消息！");
                    continue;
                }

                if (msg.equals(MessageConstant.LOGIN)) {
                    //客户端主动登录
                    try {
                        client.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    logger.info("--客户端重新登录成功");
                    continue;
                }

                if (msg.equals(MessageConstant.LOGOUT)) {
                    //客户端主动下线
                    client.clear();
                    logger.info("--客户端下线成功，如果需要加入聊天室，请重新登录");
                    continue;
                }

                //普通消息，通过路由端发送给服务端
                ChatInfo chat = new ChatInfo(MessageConstant.CHAT, System.currentTimeMillis(), conf.getUserId(), msg);
                client.sendMessage(chat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scan.close();
        }
    }
}
