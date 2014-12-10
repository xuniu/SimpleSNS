package me.xuneal.simplesns.app.model;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;

import java.io.File;
import java.io.IOException;

/**
 * Created by xyz on 2014/11/19.
 */
public class Account extends AVUser {

    public Account() {   }

    public void setNickName(String name){
        put("nickName", name);
    }

    public String getNickName(){
        return getString("nickName");
    }

    public String getAvatar(){
        AVFile file = getAVFile("avatar");
        if (file == null){
            return "assets://ic_avatar.png";
        } else {
            return file.getUrl();
        }
    }

    public void setAvatar(String filePath){
        try {
            AVFile file  = AVFile.withAbsoluteLocalPath(new File(filePath).getName(), filePath);
            put("avatar", file );
        } catch (IOException ignored) {

        }
    }
}
