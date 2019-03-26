package ai.yunxi.chat.route.controller;

import ai.yunxi.chat.commons.Utils.StringUtil;
import ai.yunxi.chat.commons.constant.BasicConstant;
import ai.yunxi.chat.commons.pojo.ServerInfo;
import ai.yunxi.chat.commons.pojo.UserInfo;
import ai.yunxi.chat.commons.protocol.ChatInfo;
import ai.yunxi.chat.route.config.InitConfiguration;
import ai.yunxi.chat.route.zk.ZKUtil;
import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/")
public class ChatRouteController {

    private final static Logger logger = LoggerFactory.getLogger(ChatRouteController.class);
    @Autowired
    private InitConfiguration conf;
    @Autowired
    private ZKUtil zkUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private OkHttpClient okHttpClient;
    //zk节点 下标    高并发下的
    private AtomicLong index = new AtomicLong();

    /**
     * 客户端用户发现服务端的接口
     * 1.获取所有的ZK上的server节点
     * 2.自己实现一个轮询算法(其他算法) 得到一个Server节点
     * 3.保存客户端与server的映射关系（redis）(userId  ->   ip+port)
     * 4.返回这个Server节点的信息（ip+port）
     **/
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ServerInfo login(@RequestBody UserInfo userInfo) {
        String serverStr = "";
        List<String> all = zkUtil.getAllNode();

        //高并发轮询算法
        Long idx = index.incrementAndGet() % all.size();
        serverStr = all.get(idx.intValue());

        redisTemplate.opsForValue().set(BasicConstant.ROUTE_PREFIX + userInfo.getUserId(), serverStr);
        String[] split = serverStr.split("-");
        ServerInfo serverInfo = new ServerInfo(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        return serverInfo;
    }

    /**
     * 路由端分发消息
     **/
    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public void chat(@RequestBody ChatInfo chat) {
        //如果是群发
        //判断userId登录状态
        String isLogin = redisTemplate.opsForValue().get(BasicConstant.ROUTE_PREFIX + chat.getUserId());
        if (StringUtil.isEmpty(isLogin)) {
            logger.info("---该用户并没有登录[" + chat.getUserId() + "]。");
            return;
        }

        //拿到所有的server节点
        List<String> all = zkUtil.getAllNode();
        for (String server : all) {
            String[] split = server.split("-");
            String url = "http://" + split[0] + ":" + split[2] + conf.getSendMsgUrl();

            //调用服务端 API 进行消息推送
            JSONObject json = new JSONObject();
            json.put("command", chat.getCommand());
            json.put("time", chat.getTime());
            json.put("userId", chat.getUserId());
            json.put("content", chat.getContent());

            MediaType mediaType = MediaType.parse("application/json");
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(mediaType, json.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    logger.error("---路由端调用server端 pushMessage API 失败");
                    throw new IOException("---路由端调用server端 pushMessage API 失败！");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        }
    }

    /**
     * 路由端处理客户端下线，从缓存中删除客户端与服务端之间的映射关系
     **/
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout(@RequestBody UserInfo user) {
        redisTemplate.opsForValue().getOperations().delete(BasicConstant.ROUTE_PREFIX + user.getUserId());
        logger.info("---路由端处理了用户下线逻辑 " + user.getUserId());
    }
}
