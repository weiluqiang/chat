package ai.yunxi.chat.server.handle;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelMap {

    //多线程保障可见性  禁止指令重排
    private static volatile ChannelMap channelMap;
    private final Map<Integer, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private ChannelMap() {

    }

    public static ChannelMap newInstance() {
        if (channelMap == null) {
            synchronized (ChannelMap.class) {
                if (channelMap == null) {
                    channelMap = new ChannelMap();
                }
            }
        }
        return channelMap;
    }

    public Map<Integer, Channel> getChannelMap() {
        return CHANNEL_MAP;
    }

    public void putClient(Integer userId, Channel channel) {
        CHANNEL_MAP.put(userId, channel);
    }

    public io.netty.channel.Channel getClient(Integer userId) {
        return CHANNEL_MAP.get(userId);
    }
}
