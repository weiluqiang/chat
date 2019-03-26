package ai.yunxi.chat.commons.pojo;

/**
 * @author 小五老师-云析学院
 * @createTime 2019年3月20日 下午8:28:00
 * 封装Server节点的信息
 */
public class ServerInfo {

    private String ip;
    private Integer neetyPort;
    private Integer httpPort;

    /**
     * @param ip
     * @param neetyPort
     * @param httpPort
     */
    public ServerInfo(String ip, Integer neetyPort, Integer httpPort) {
        super();
        this.ip = ip;
        this.neetyPort = neetyPort;
        this.httpPort = httpPort;
    }

    public ServerInfo() {
        super();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getNeetyPort() {
        return neetyPort;
    }

    public void setNeetyPort(Integer neetyPort) {
        this.neetyPort = neetyPort;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "ip='" + ip + '\'' +
                ", neetyPort=" + neetyPort +
                ", httpPort=" + httpPort +
                '}';
    }
}
