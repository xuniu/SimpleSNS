package me.xuneal.simplesns.app.ui;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import me.xuneal.simplesns.app.R;

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

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }
}
