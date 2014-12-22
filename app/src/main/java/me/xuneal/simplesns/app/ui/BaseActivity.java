package me.xuneal.simplesns.app.ui;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import me.xuneal.simplesns.app.R;

import java.lang.reflect.Field;

/**
 * Created by xyz on 2014/11/19.
 */
public class BaseActivity extends ActionBarActivity {
    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    private TextView getActionBarTextView() {
        TextView titleTextView = null;

        try {
            Field f = getActionBarToolbar().getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(getActionBarToolbar());
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return titleTextView;
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
//        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//        getActionBarTextView().setLayoutParams(layoutParams);
        getActionBarTextView().setTextColor(getResources().getColor(R.color.white));
    }
}
