package ai.yunxi.chat.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InitConfiguration {

    @Value("${chat.zk.switch}")
    private boolean zkSwitch;
    @Value("${chat.zk.addr}")
    private String addr;
    @Value("${chat.zk.root}")
    private String root;
    @Value("${chat.route.logout}")
    private String routeLogoutUrl;

    @Value("${server.port}")
    private Integer httpPort;
    @Value("${chat.server.port}")
    private Integer nettyPort;

    public boolean isZkSwitch() {
        return zkSwitch;
    }

    public void setZkSwitch(boolean zkSwitch) {
        this.zkSwitch = zkSwitch;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getRouteLogoutUrl() {
        return routeLogoutUrl;
    }

    public void setRouteLogoutUrl(String routeLogoutUrl) {
        this.routeLogoutUrl = routeLogoutUrl;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    public Integer getNettyPort() {
        return nettyPort;
    }

    public void setNettyPort(Integer nettyPort) {
        this.nettyPort = nettyPort;
    }
}
