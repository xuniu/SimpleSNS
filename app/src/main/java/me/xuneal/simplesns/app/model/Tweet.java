package me.xuneal.simplesns.app.model;

import android.util.Log;
import com.avos.avoscloud.*;
import me.xuneal.simplesns.app.MyApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xyz on 2014/11/20.
 */
@AVClassName("Tweets")
public class Tweet extends AVObject {

//    private CountDownLatch mSaveLatch;
//    private AtomicInteger

    public static final String CONTENT = "content";
    public static final String POST_TIME = "post_time";
    public static final String POSTER = "poster";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";
    public static final String IMAGES = "images";
    public static final String TWEET_ID = "tweetId";
    public static final String TABLE_NAME = "Tweets";

    private List<AVFile> mAVFiles = new ArrayList<>();
    private boolean saved = true;
    private boolean like;
    private SaveAsyncRunnable mSaveAsyncRunnable= new SaveAsyncRunnable();

    private List<String> mImageUrls;

    public boolean isLike() {
        return like;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public Tweet() {
    }

    public String getContent() {
        return getString(CONTENT);
    }

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public List<String> getImageUrl() {
        if (!isSaved()) {
            return mImageUrls;
        } else {
            List<AVFile> files = getList(IMAGES);
            List<String> result = new LinkedList<>();
            if (files == null) return result;
            for (AVFile file : files) {
                result.add(file.getUrl());
            }
            return result;
        }
    }

    public void setImageUrl(List<String> imageUrl) {
        setSaved(false);
        this.mImageUrls = imageUrl;
        for (String url : imageUrl) {
            try {
                AVFile file = AVFile.withAbsoluteLocalPath(new File(url.replace("file://", "")).getName(), url.replace("file://", ""));
                mAVFiles.add(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPostTime() {
        return getString(POST_TIME);
    }

    public void setPostTime(String postTime) {
        put(POST_TIME, postTime);
    }

    public Account getPoster() {
        return AVUser.cast(getAVUser(POSTER), Account.class);
    }

    public void setPoster(Account poster) {
        put(POSTER, poster);
    }

    public void getLikes(FindCallback<AVObject> callback) {
        //假设myPost是已知的Post对象
        AVQuery<AVObject> userQuery = AVRelation.reverseQuery("_User", LIKES, this);
        userQuery.findInBackground(callback);
    }

    public List<AVObject> getLikes() throws AVException {
        //假设myPost是已知的Post对象
        AVQuery<AVObject> userQuery = AVRelation.reverseQuery("_User", LIKES, this);
        return userQuery.find();
    }


    public List<AVObject> getComments() throws AVException {
        AVQuery<AVObject> query = AVQuery.getQuery("Comments");
        query.whereEqualTo("tweet", this);
        return query.find();
    }

    public void getComments(FindCallback<AVObject> callback) {

        AVQuery<AVObject> query = AVQuery.getQuery("Comments");
        query.whereEqualTo("tweet", this);
        query.findInBackground(callback);
    }

    public int getTweetId() {
        return getInt(TWEET_ID);
    }

    public void setTweetId(int tweetId) {
        put(TWEET_ID, tweetId);
    }
//    public void addComment(Comment comment) {
//        AVRelation relation = getRelation(COMMENTS);
//        relation.setTargetClass("Comments");
//        relation.add(comment);
//    }

    private class SaveAsyncRunnable implements Runnable {

        private List<SaveCallback> mSaveCallback = new ArrayList<>();

        public void addSaveCallbackListener(SaveCallback saveCallback) {
            mSaveCallback.add(saveCallback);
        }

        @Override
        public void run() {
            AVException exception = null;
//            mSaveLatch = new CountDownLatch(mAVFiles.size());
            try {

                for (AVFile file : mAVFiles) {
//                    file.saveInBackground(new SaveCallback() {
//                                              @Override
//                                              public void done(AVException e) {
//                                                  mSaveLatch.countDown();
//                                              }
//                                          },
//                            new ProgressCallback() {
//                                @Override
//                                public void done(Integer integer) {
//
//                                }
//                            });
                    file.save();
                }
                Tweet.this.addAll(IMAGES, mAVFiles);
                Tweet.this.save();

            } catch (AVException e) {
                exception = e;

                Log.e("LEANCLOUD", e.getMessage());
            }
            final AVException finalException = exception;
            MyApplication.getInstance().getUIHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (mSaveCallback.size()>0){
                        for (SaveCallback callback: mSaveCallback){
                            callback.done(finalException);
                        }
                    }
                }
            });
        }
    }


    public void addSaveCallbackListener(SaveCallback saveCallback) {
        mSaveAsyncRunnable.addSaveCallbackListener(saveCallback);
    }

    public void saveAsync() {
        new Thread(mSaveAsyncRunnable).start();
    }
}
