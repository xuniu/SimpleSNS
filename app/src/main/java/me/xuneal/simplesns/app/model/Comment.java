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
    public static final String TABLE_NAME="Comments";

    private static final String POSTER = "poster";
    private static final String CONTENT= "content";
    private static final String POST_TIME = "post_time";
    private static final String TWEET = "tweet";

    public Comment(){}

    public Account getPoster() {
        return getAVUser(POSTER, Account.class);
    }

    public void setPoster(Account poster) {
        put(POSTER, poster);
    }

    public String getContent() {
        return getString(CONTENT);
    }

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public String getPostTime() {
        return getString(POST_TIME);
    }

    public void setPostTime(String postTime) {
        put(POST_TIME, postTime);
    }

    public Tweet getTweet(){
        return getAVObject(TWEET);
    }

    public void setTweet(Tweet tweet){
        put(TWEET, tweet);
    }

}
