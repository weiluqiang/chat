package ai.yunxi.chat.server.init;

import ai.yunxi.chat.commons.protocol.MessageProto;
import ai.yunxi.chat.server.config.InitConfiguration;
import ai.yunxi.chat.server.handle.ChatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ChatServerInit {

    private final static Logger logger = LoggerFactory.getLogger(ChatServerInit.class);

    @Autowired
    private InitConfiguration conf;

    @PostConstruct
    public void start() throws Exception{
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel arg0) throws Exception {
                            ChannelPipeline pipeline = arg0.pipeline();
                            //google protobuf 编解码
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());

                            pipeline.addLast(new ChatServerHandler());
                        }
                    });
            ChannelFuture cf = sb.bind(conf.getNettyPort()).sync();
            if(cf.isSuccess()){
                logger.info("---服务端启动Netty成功 ["+conf.getNettyPort()+"]");
            }
        } catch (Exception e) {

        }
    }
}
