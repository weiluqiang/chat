package ai.yunxi.chat.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InitConfiguration {

    @Value("${chat.user.userId}")
    private Integer userId;
    @Value("${chat.user.userName}")
    private String userName;
    @Value("${chat.route.login}")
    private String routeLoginUrl;
    @Value("${chat.route.chat}")
    private String routeChatUrl;
    @Value("${chat.route.logout}")
    private String routeLogoutUrl;
    @Value("${chat.server.clientLogout}")
    private String serverClientLogout;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRouteLoginUrl() {
        return routeLoginUrl;
    }

    public void setRouteLoginUrl(String routeLoginUrl) {
        this.routeLoginUrl = routeLoginUrl;
    }

    public String getRouteChatUrl() {
        return routeChatUrl;
    }

    public void setRouteChatUrl(String routeChatUrl) {
        this.routeChatUrl = routeChatUrl;
    }

    public String getRouteLogoutUrl() {
        return routeLogoutUrl;
    }

    public void setRouteLogoutUrl(String routeLogoutUrl) {
        this.routeLogoutUrl = routeLogoutUrl;
    }

    public String getServerClientLogout() {
        return serverClientLogout;
    }

    public void setServerClientLogout(String serverClientLogout) {
        this.serverClientLogout = serverClientLogout;
    }
}
