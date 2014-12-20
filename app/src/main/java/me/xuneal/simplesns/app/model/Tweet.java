package me.xuneal.simplesns.app.model;

import com.avos.avoscloud.*;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xyz on 2014/11/20.
 */
@AVClassName("Tweets")
public class Tweet extends AVObject{

    public static final String CONTENT = "content";
    public static final String POST_TIME = "post_time";
    public static final String POSTER = "poster";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";
    public static final String IMAGES = "images";
    public static final String TABLE_NAME = "Tweets";

    public Tweet(){}

    public String getContent() {
        return getString(CONTENT);
    }

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public List<String> getImageUrl() {
        List<AVFile> files = getList(IMAGES);
        List<String> result = new LinkedList<>();
        if (files == null) return result;
        for (AVFile file : files){
            result.add(file.getUrl());
        }
        return result;
    }

    public void setImageUrl(List<String> imageUrl) {
        List<AVFile> fileList = new LinkedList<AVFile>();
        for (String url : imageUrl){
            try {
                AVFile file = AVFile.withAbsoluteLocalPath(new File(url).getName(), url);
                fileList.add(file);
                file.saveInBackground();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        addAll(IMAGES, fileList);
    }

    public String getPostTime() {
        return getString(POST_TIME);
    }

    public void setPostTime(String postTime) {
        put(POST_TIME, postTime);
    }

    public Account getPoster() {
        return getAVUser(POSTER, Account.class);
    }

    public void setPoster(Account poster) {
        put(POSTER, poster);
    }

    public void getLikes(FindCallback<AVObject> callback) {
        //假设myPost是已知的Post对象
        AVQuery<AVObject> userQuery = AVRelation.reverseQuery("_User",LIKES,this);
        userQuery.findInBackground(callback);
    }

    public List<AVObject> getLikes() throws AVException {
        //假设myPost是已知的Post对象
        AVQuery<AVObject> userQuery = AVRelation.reverseQuery("_User",LIKES,this);
        return userQuery.find();
    }



    public List<AVObject> getComments() throws AVException {
        AVQuery<AVObject> query = AVQuery.getQuery("Comments");
        query.whereEqualTo("tweet", this);
        return  query.find();
    }

    public void getComments(FindCallback<AVObject> callback){

        AVQuery<AVObject> query = AVQuery.getQuery("Comments");
        query.whereEqualTo("tweet", this);
        query.findInBackground(callback);


    }
//    public void addComment(Comment comment) {
//        AVRelation relation = getRelation(COMMENTS);
//        relation.setTargetClass("Comments");
//        relation.add(comment);
//    }
}
