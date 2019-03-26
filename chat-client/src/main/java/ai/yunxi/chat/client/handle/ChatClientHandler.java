package ai.yunxi.chat.client.handle;

import ai.yunxi.chat.client.config.SpringBeanFactory;
import ai.yunxi.chat.client.init.ChatClientInit;
import ai.yunxi.chat.commons.protocol.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    /**
     * IMClientInit start(@PostConstruct) 方法中，调用new IMClientHandler， 此时IMClientInit在Spring中还未完成实例化过程， 如果在此时从Spring容器中获取IMClientInit实例，会导致循环依赖的死锁情况。
     **/
    public ChatClientHandler() {
        logger.info("--ChatClientHandler init");
        //chatClientInit = SpringBeanFactory.getBean(ChatClientInit.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageProto.MessageProtocol message = (MessageProto.MessageProtocol) msg;
        logger.info("---客户端接收到消息：" + message.getUserId() + "-" + message.getContent());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChatClientInit client = SpringBeanFactory.getBean(ChatClientInit.class);
        //服务端强制下线事件
        client.restart();
    }
}
