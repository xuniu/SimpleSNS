package me.xuneal.simplesns.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.avos.avoscloud.AVUser;
import me.xuneal.simplesns.app.model.Account;

/**
 * Created by xyz on 2014/11/19.
 */
public class AccountUtils {

    public static final String PREF_ACCOUNT_USERNAME = "account_username";
    public static final String PREF_ACCOUNT_PASSWORD = "account_password";
    public static final String PREF_ACCOUNT_AUTH_TOKEN = "account_auth_token";
    public static final String PREF_ACCOUNT_EMAIL = "account_email";

    private static Account mAccount;

//    public static Account getDefaultAccount(Context context){
//        Account account = new Account();
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        String email = sharedPreferences.getString(PREF_ACCOUNT_EMAIL, "");
//        String password = sharedPreferences.getString(PREF_ACCOUNT_PASSWORD, "");
//        account.setEmail(email);
//        account.setPassword(password);
//        return account;
//    }

    public static Account getDefaultAccount(){
        if (mAccount==null){
            mAccount = AVUser.cast(AVUser.getCurrentUser(), Account.class);
        }
        return mAccount;
    }

    public static void logout(){
        mAccount=null;
        AVUser.logOut();
    }

}
