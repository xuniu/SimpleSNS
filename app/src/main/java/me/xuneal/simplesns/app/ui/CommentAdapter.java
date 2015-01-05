package me.xuneal.simplesns.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Comment;

import java.util.List;

/**
 * Created by xyz on 2014/12/11.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Comment> mComments;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public CommentAdapter(List<Comment> comments) {
        mComments = comments;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_content, viewGroup, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder commentHolder, int i) {
        runEnterAnimation(commentHolder.itemView,i);
        ((CommentHolder)commentHolder).bindComment(mComments.get(i));
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    private void runEnterAnimation(View view, int pos){
        if (animationsLocked) return;

        if (pos > lastAnimatedPosition){
            lastAnimatedPosition = pos;
            view.setTranslationY(100);
            view.setAlpha(0f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (pos) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    public void updateItems(){
        itemsCount = mComments.size();
        notifyDataSetChanged();
    }

    public void addItem(Comment comment) {
        mComments.add(comment);
        itemsCount = mComments.size();
        notifyItemInserted(itemsCount);
    }


    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        private CircleImageView mIvAvatar;
        private TextView mTvContent;
        private TextView mTvNickName;
        private TextView mTvPostTime;

        /**
         * Find the Views in the layout<br />
         * <br />
         * Auto-created on 2014-12-11 13:23:16 by Android Layout Finder
         * (http://www.buzzingandroid.com/tools/android-layout-finder)
         */
        public void bindComment(Comment comment) {
            ImageLoader.getInstance().displayImage(comment.getPoster().getAvatar(), mIvAvatar);
            mTvContent.setText(comment.getContent());
            mTvNickName.setText(comment.getPoster().getNickName());
            mTvPostTime.setText(comment.getPostTime());
        }


        public CommentHolder(View itemView) {
            super(itemView);
            mIvAvatar = (CircleImageView)itemView.findViewById(R.id.iv_avatar);
            mTvNickName = (TextView)itemView.findViewById(R.id.tv_nickname);
            mTvContent = (TextView)itemView.findViewById(R.id.tv_content);
            mTvPostTime = (TextView)itemView.findViewById(R.id.tv_post_time);
        }
    }
}

