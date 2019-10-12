package ai.yunxi.chat.client.init;

import ai.yunxi.chat.client.config.InitConfiguration;
import ai.yunxi.chat.client.handle.ChatClientHandler;
import ai.yunxi.chat.commons.constant.MessageConstant;
import ai.yunxi.chat.commons.pojo.ServerInfo;
import ai.yunxi.chat.commons.protocol.ChatInfo;
import ai.yunxi.chat.commons.protocol.MessageProto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class ChatClientInit {

    private final static Logger logger = LoggerFactory.getLogger(ChatClientInit.class);

    public Channel channel;
    private ServerInfo serverInfo;
    @Autowired
    private OkHttpClient okHttpClient;
    @Autowired
    private InitConfiguration conf;

    @PostConstruct
    public void start() throws Exception {
        if (serverInfo != null) {
            logger.info("--客户端当前是登录状态！");
            return;
        }
        //1.从Route得到服务端的IP+port
        getServerInfo();
        //2.启动服务
        startClient();
        //3.登录到服务端（服务端保存UserId与Channel的映射关系）
        registerToServer();
    }

    private void registerToServer() {
        MessageProto.MessageProtocol message = MessageProto.MessageProtocol.newBuilder()
                .setCommand(MessageConstant.LOGIN)
                .setTime(System.currentTimeMillis())
                .setUserId(conf.getUserId())
                .setContent("login").build();
        channel.writeAndFlush(message);
    }

    private void startClient() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bs = new Bootstrap();
            bs.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel arg0) throws Exception {
                            ChannelPipeline pipeline = arg0.pipeline();
                            //google protobuf 编解码
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());

                            pipeline.addLast(new ChatClientHandler());
                        }
                    });
            ChannelFuture cf = bs.connect(serverInfo.getIp(), serverInfo.getNeetyPort()).sync();
            if (cf.isSuccess()) {
                logger.info("---客户端启动成功，连接的是===server：" + serverInfo);
                channel = cf.channel();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从Route得到服务端的IP+port
     **/
    private void getServerInfo() {
        JSONObject json = new JSONObject();
        json.put("userId", conf.getUserId());
        json.put("userName", conf.getUserName());

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(conf.getRouteLoginUrl())
                .post(requestBody)
                .build();

        Response response = null;
        ResponseBody body = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                logger.error("---客户端获取server节点失败！");
                throw new IOException("---客户端获取server节点失败！");
            }

            body = response.body();
            String responseJson = body.string();
            this.serverInfo = JSON.parseObject(responseJson, ServerInfo.class);
            logger.info("--得到服务端Server节点  serverInfo:" + serverInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.close();
            if (body != null)
                body.close();
        }
    }

    public void sendMessage(ChatInfo chat) {
        JSONObject json = new JSONObject();
        json.put("command", chat.getCommand());
        json.put("time", chat.getTime());
        json.put("userId", chat.getUserId());
        json.put("content", chat.getContent());

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(conf.getRouteChatUrl())
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                logger.error("---客户端调用Route chat api失败！");
                throw new IOException("---客户端调用Route chat api失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }

    }

    public void restart() throws Exception {
        //清理客户端信息（路由）
        logoutRoute();
        serverInfo = null;
        start();
    }

    /**
     * 调用路由端清理数据
     **/
    private void logoutRoute() {
        JSONObject json = new JSONObject();
        json.put("userId", conf.getUserId());

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
                logger.error("---客户端调用路由 logout失败");
                throw new IOException("---客户端调用路由 logout失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
    }

    public void clear() {
        //调用路由端 清除缓存数据
        logoutRoute();
        //调用服务端清除channel数据
        logoutServer();

        serverInfo = null;
    }

    /**
     * 调用服务端清理数据
     **/
    private void logoutServer() {
        JSONObject json = new JSONObject();
        json.put("userId", conf.getUserId());

        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url("http://" + serverInfo.getIp() + ":" + serverInfo.getHttpPort() + conf.getServerClientLogout())
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                logger.error("---客户端调用服务端 clientLogout失败");
                throw new IOException("---客户端调用服务端 clientLogout失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
    }
}
