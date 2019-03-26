package ai.yunxi.chat.route.config;

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
    @Value("${chat.server.sendmsg}")
    private String sendMsgUrl;

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

    public String getSendMsgUrl() {
        return sendMsgUrl;
    }

    public void setSendMsgUrl(String sendMsgUrl) {
        this.sendMsgUrl = sendMsgUrl;
    }
}
