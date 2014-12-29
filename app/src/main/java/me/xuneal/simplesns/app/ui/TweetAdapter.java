package me.xuneal.simplesns.app.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.avos.avoscloud.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Tweet;
import me.xuneal.simplesns.app.ui.components.ReverseInterpolator;
import me.xuneal.simplesns.app.util.Utils;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xyz on 2014/12/11.
 */
public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int ANIMATED_ITEMS_COUNT = 5;

    private OnFeedItemClickListener mOnFeedItemClickListener;
    boolean mUserHeader = false;

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        mOnFeedItemClickListener = onFeedItemClickListener;
    }

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private View mHeader;

    private List<Tweet> tweets;

    public List<Tweet> getTweets() {
        return tweets;
    }

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
    }

    public TweetAdapter(Context context, List<Tweet> tweets, boolean useHeader) {
        this.tweets = tweets;

        this.context = context;
        mUserHeader = useHeader;
    }

    public void setHeader(View view) {
        mHeader = view;
        mUserHeader = true;

    }

    public View getHeader() {
        return mHeader;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mUserHeader) {
            return 2;
        } else
            return 1;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (i == 2) {
            view = mHeader;
            return new VHeader(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_tweet, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
    }


    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            if (mUserHeader && position == 0) {
                view.setTranslationY(Utils.getScreenHeight(context) / 3 * -1);
            } else {
                view.setTranslationY(Utils.getScreenHeight(context) / 3);
            }
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int i) {
        runEnterAnimation(vh.itemView, i);
        if (mUserHeader) {
            if (i != 0) {
                bindViewHolder((ViewHolder) vh, i - 1);
            }
        } else {
            bindViewHolder((ViewHolder) vh, i);
        }

    }

    private void bindViewHolder(ViewHolder vh, int pos) {
        long now = LocalDateTime.now().toDateTime().getMillis();
        final Tweet tweet = tweets.get(pos);
        final ViewHolder viewHolder = vh;
        ImageLoader.getInstance().displayImage(tweet.getPoster().getAvatar(), viewHolder.ivAvatar);
        viewHolder.tvNickname.setText(tweet.getPoster().getNickName());
        viewHolder.tvContent.setText(tweet.getContent());
        viewHolder.tvPostTime.setText(DateUtils.getRelativeTimeSpanString(
                LocalDateTime.parse(tweet.getPostTime()).toDateTime().getMillis(),
                now, 0, DateUtils.FORMAT_ABBREV_RELATIVE));
        if (tweet.getImageUrl() != null) {
            viewHolder.ivPhoto.setAdapter(new ImageAdapter(tweet.getImageUrl()));
        }
        viewHolder.cbLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AVUser user = AVUser.getCurrentUser();
                AVRelation<AVObject> relation = user.getRelation("likes");
                if (isChecked) {
                    relation.add(tweet);
                } else {
                    relation.remove(tweet);
                }
                user.saveInBackground();

                buttonView.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).setInterpolator(new ReverseInterpolator()).start();
            }
        });
        viewHolder.cbLike.setChecked(tweet.isLike());
        viewHolder.ibComment.setOnClickListener(this);
        viewHolder.ibComment.setTag(pos);
        adjustGridViewColumnNum(viewHolder.ivPhoto, tweet.getImageUrl().size());
        viewHolder.ivPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnFeedItemClickListener != null) {
                    mOnFeedItemClickListener.onImageClick(tweet.getImageUrl(), position);
                }
            }
        });

        if (tweet.isLocal()){
            viewHolder.ivPhoto.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(viewHolder.ivPhoto.getWidth(), viewHolder.ivPhoto.getHeight());
                    viewHolder.vsProgressbarContainer.setLayoutParams(layoutParams);
                    viewHolder.vsProgressbarContainer.inflate();
                }
            });
            tweet.addSaveCallbackListener(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    viewHolder.vsProgressbarContainer.setVisibility(View.GONE);
                }
            });
//            view.findViewById(R.id.pb_upload);
        }
    }

    private void adjustGridViewColumnNum(GridView gridView, int childNum) {
        if (childNum < 3) {
            gridView.setNumColumns(childNum);
        } else if (childNum == 4) {
            gridView.setNumColumns(2);
        } else {
            gridView.setColumnWidth(3);
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public void updateItems() {
        itemsCount = tweets.size();

        if (mUserHeader) {
            itemsCount++;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_comment) {
            if (mOnFeedItemClickListener != null) {
                mOnFeedItemClickListener.onCommentsClick(v, (Integer) v.getTag());
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ViewHolder(View itemView) {
            super(itemView);
            findViews();
        }

        private CircleImageView ivAvatar;
        private TextView tvNickname;
        private TextView tvContent;
        private GridView ivPhoto;
        private TextView tvPostTime;
        private CheckBox cbLike;
        private ImageView ibComment;
        private ViewStub vsProgressbarContainer;

        /**
         * Find the Views in the layout<br />
         * <br />
         * Auto-created on 2014-11-20 14:48:19 by Android Layout Finder
         * (http://www.buzzingandroid.com/tools/android-layout-finder)
         */
        private void findViews() {
            ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
            tvNickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivPhoto = (GridView) itemView.findViewById(R.id.iv_photo);
            tvPostTime = (TextView) itemView.findViewById(R.id.tv_post_time);
            cbLike = (CheckBox) itemView.findViewById(R.id.cb_like);

            ibComment = (ImageView) itemView.findViewById(R.id.ib_comment);
            vsProgressbarContainer = (ViewStub) itemView.findViewById(R.id.vs_progressbar);
            cbLike.setOnClickListener(this);
            ibComment.setOnClickListener(this);
        }

        /**
         * Handle button click events<br />
         * <br />
         * Auto-created on 2014-11-20 14:48:19 by Android Layout Finder
         * (http://www.buzzingandroid.com/tools/android-layout-finder)
         */
        @Override
        public void onClick(View v) {
            if (v == ibComment) {
                // Handle clicks for ibComment
            }
        }


    }

    public static class VHeader extends RecyclerView.ViewHolder {

        private View mView;

        public VHeader(View itemView) {
            super(itemView);
            mView = itemView;
        }


    }

    public interface OnFeedItemClickListener {
        public void onImageClick(List<String> imageUrls, int pos);

        public void onCommentsClick(View v, int position);
    }
}

