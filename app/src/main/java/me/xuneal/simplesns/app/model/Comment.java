package me.xuneal.simplesns.app.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import org.joda.time.LocalDateTime;

/**
 * Created by xyz on 2014/11/20.
 */
@AVClassName("Comments")
public class Comment extends AVObject{

    private AVUser poster;
    private String content;
    private String postTime;

    public Comment(){}

    public AVUser getPoster() {
        return poster;
    }

    public void setPoster(AVUser poster) {
        this.poster = poster;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}
