package ai.yunxi.chat.commons.protocol;

/**
 * @author 小五老师-云析学院
 * @createTime 2019年3月22日 下午8:15:12
 */
public class ChatInfo {

    private String command;
    private Long time;
    private Integer userId;
    private String content;

    public ChatInfo(String command, Long time, Integer userId, String content) {
        this.command = command;
        this.time = time;
        this.userId = userId;
        this.content = content;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
