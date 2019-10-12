package ai.yunxi.chat.server.controller;

import ai.yunxi.chat.commons.constant.MessageConstant;
import ai.yunxi.chat.commons.pojo.UserInfo;
import ai.yunxi.chat.commons.protocol.ChatInfo;
import ai.yunxi.chat.commons.protocol.MessageProto;
import ai.yunxi.chat.server.handle.ChannelMap;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 小五老师-云析学院
 * @createTime 2019年3月22日 下午8:32:57
 */
@RestController
@RequestMapping("/")
public class ChatServerController {

    private final static Logger logger = LoggerFactory.getLogger(ChatServerController.class);
    private ChannelMap CHANNEL_MAP = ChannelMap.newInstance();
    private final AttributeKey<Integer> uid = AttributeKey.valueOf("userId");

    /**
     * 服务端接收消息， 推送到指定的客户端
     */
    @RequestMapping(value = "/pushMessage", method = RequestMethod.POST)
    public void pushMessage(@RequestBody ChatInfo chat) {
        //ChannelMap得到所有的与该服务端连接的客户端Channel
        if (MessageConstant.CHAT.equals(chat.getCommand())) {
            logger.info("---服务端收到了消息: " + chat.getContent() +"，消息来自userId:" + chat.getUserId());
            //消息封装为protobuf对象
            MessageProto.MessageProtocol message = MessageProto.MessageProtocol.newBuilder()
                    .setCommand(chat.getCommand())
                    .setTime(chat.getTime())
                    .setUserId(chat.getUserId())
                    .setContent(chat.getContent()).build();

            for (Map.Entry<Integer, Channel> entry : CHANNEL_MAP.getChannelMap().entrySet()) {
                if (!chat.getUserId().equals(entry.getKey())) {
                    entry.getValue().writeAndFlush(message);//向客户端发送消息
                    logger.info("---服务端向客户端[" + entry.getValue().attr(uid).get() + "]发送了消息，消息来自userId:" + chat.getUserId());
                }
            }
        }
    }

    /**
     * 服务端处理客户端下线事件
     **/
    @RequestMapping(value = "/clientLogout", method = RequestMethod.POST)
    public void clientLogout(@RequestBody UserInfo user) {
        CHANNEL_MAP.getChannelMap().remove(user.getUserId());
        logger.info("---服务端处理客户端下线【" + user.getUserId() + "】");
    }
}
