package me.xuneal.simplesns.app.model;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;

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

    public AVFile getAvatar(){
        return getAVFile("avatar");
    }

    public void setAvatar(AVFile file){
        put("avatar", file);
    }
}
