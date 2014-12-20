package me.xuneal.simplesns.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import me.xuneal.simplesns.app.R;

/**
 * Created by xyz on 2014/12/16.
 */
public class CheckableRelativeLayout extends RelativeLayout
implements Checkable{

    CheckBox mCheckBox;
    public CheckableRelativeLayout(Context context) {
        super(context);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCheckbox(CheckBox checkbox){
        mCheckBox = checkbox;
    }
    @Override
    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    @Override
    public void toggle() {
        mCheckBox.toggle();
    }
}
