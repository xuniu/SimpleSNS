package me.xuneal.simplesns.app.model;

import com.avos.avoscloud.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by xyz on 2014/11/19.
 */
public class Account extends AVUser {

    private AVFile mAvatarFile;

    public static final String AVATAR = "avatar";

    public Account() {   }

    public void setNickName(String name){
        put("nickName", name);
    }

    public String getNickName(){
        return getString("nickName");
    }

    public String getAvatar(){
        AVFile file = getAVFile(AVATAR);
        if (file == null){
            return "assets://ic_avatar.png";
        } else {
            return file.getUrl();
        }
    }

    public void setAvatar(String filePath){
        try {
            String realPath = filePath;
            if (!filePath.contains("assets:")) {
            realPath = filePath.replace("file://", "");}
            mAvatarFile  = AVFile.withAbsoluteLocalPath(new File(realPath).getName(), realPath);
            put(AVATAR, mAvatarFile);
        } catch (IOException ignored) {

        }
    }

//    public AVRelation<Tweet> getLikes() {
//        AVRelation<Tweet> relation = getRelation("likes");
//        return relation;
//    }




//    public void removeLikes(Tweet tweet) {
//        AVRelation<Tweet> likes = getLikes();
//        likes.remove(tweet);
//        this.saveInBackground();
//    }
//    public void addLiker(Tweet tweet) {
//        AVRelation<Tweet> likes = getLikes();
//        likes.add(tweet);
//        this.saveInBackground();
//    }

    public void saveAsync(final SaveCallback saveCallback){
        mAvatarFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                Account.this.saveInBackground(saveCallback);
            }
        });
    }
}
