package com.oohdev.oohreminder.core.model;

import android.support.annotation.NonNull;

public class BookModelComplete {
    @NonNull
    private String mTitle;
    @NonNull
    private String mAuthor;
    @NonNull
    private String mCoverUrl;

    public BookModelComplete() {
        mTitle = "";
        mAuthor = "";
        mCoverUrl = "";
    }

    public BookModelComplete(@NonNull String title, @NonNull String author) {
        mTitle = title;
        mAuthor = author;
        mCoverUrl = "";
    }

    public BookModelComplete(@NonNull String title, @NonNull String author, @NonNull String coverUrl) {
        mTitle = title;
        mAuthor = author;
        mCoverUrl = coverUrl;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@NonNull String title) {
        mTitle = title;
    }

    @NonNull
    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(@NonNull String author) {
        mAuthor = author;
    }

    @NonNull
    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(@NonNull String coverUrl) {
        mCoverUrl = coverUrl;
    }
}
