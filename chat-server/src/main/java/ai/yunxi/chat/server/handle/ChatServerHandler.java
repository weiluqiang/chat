package ai.yunxi.chat.server.handle;

import ai.yunxi.chat.commons.constant.MessageConstant;
import ai.yunxi.chat.commons.protocol.MessageProto;
import ai.yunxi.chat.server.config.InitConfiguration;
import ai.yunxi.chat.server.config.SpringBeanFactory;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    private final AttributeKey<Integer> uid = AttributeKey.valueOf("userId");
    private ChannelMap channelMap = ChannelMap.newInstance();

    private InitConfiguration conf;
    private OkHttpClient okHttpClient;

    public ChatServerHandler() {
        conf = SpringBeanFactory.getBean(InitConfiguration.class);
        okHttpClient = SpringBeanFactory.getBean(OkHttpClient.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageProto.MessageProtocol message = (MessageProto.MessageProtocol) msg;

        if (message.getCommand().equals(MessageConstant.LOGIN)) {
            //系统指令：登录
            //保存channel到Map中
            channelMap.putClient(message.getUserId(), ctx.channel());

            //用户登录时，绑定一个userId属性在Channel
            ctx.channel().attr(uid).set(message.getUserId());
            logger.info("---客户端登录成功。userId:" + message.getUserId());
        } else if (message.getCommand().equals(MessageConstant.CHAT)) {
            logger.info("---服务端接收到数据：" + message.getContent() + "，发送人：" + message.getUserId());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 客户端强制下线
        Integer userId = ctx.channel().attr(uid).get();
        channelMap.getChannelMap().remove(userId); //从服务端删除

        JSONObject json = new JSONObject();
        json.put("userId", userId);

        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(conf.getRouteLogoutUrl())
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                logger.error("---服务端调用路由 logout失败");
                throw new IOException("---服务端调用路由 logout失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
