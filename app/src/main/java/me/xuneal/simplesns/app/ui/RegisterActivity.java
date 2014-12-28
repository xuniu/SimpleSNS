package me.xuneal.simplesns.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;

public class RegisterActivity extends BaseActivity {

    private ScrollView mRegisterFormView;
    private ProgressBar mProgressView;
    private MaterialEditText mEtUsername;
    private MaterialEditText mEtPassword;
    private Button mBtnSignUp;
    private ImageView mIvAvatar;
    private MaterialEditText mEtNickname;

    private String mAvatarSrc = "asset://ic_avatar.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegisterFormView = (ScrollView)findViewById(R.id.register_form);
        mProgressView = (ProgressBar) findViewById(R.id.pb_register);
        mEtUsername = (MaterialEditText) findViewById(R.id.et_username);
        mEtPassword = (MaterialEditText) findViewById(R.id.et_password);
        mEtNickname = (MaterialEditText) findViewById(R.id.et_nickname);
        mIvAvatar = (ImageView)findViewById(R.id.iv_avatar);
        mIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RegisterActivity.this, PickPhotos.class), 0);
            }
        });
        mBtnSignUp = (Button) findViewById(R.id.btn_sign_up);
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp(){
        showProgress(true);
        Account account = new Account();
        account.setAvatar(mAvatarSrc);
        account.setNickName(mEtNickname.getText().toString());
        account.setUsername(mEtUsername.getText().toString());
        account.setPassword(mEtPassword.getText().toString());
        account.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVUser.logInInBackground(mEtUsername.getText().toString(), mEtPassword.getText().toString(), new LogInCallback() {
                    public void done(AVUser user, AVException e) {
                        if (user != null) {
                            // 登录成功
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // 登录失败
                            Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==0 && resultCode == RESULT_OK && data!=null){
            mAvatarSrc = data.getStringExtra("url");
            ImageLoader.getInstance().displayImage(mAvatarSrc, mIvAvatar);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
