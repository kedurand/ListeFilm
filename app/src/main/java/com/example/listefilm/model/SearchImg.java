package com.example.listefilm.model;

import android.graphics.Bitmap;

import com.example.listefilm.utility.ImageUtil;

public class SearchImg {
    private Search search;
    private byte[] img;

    public SearchImg() {
    }

    public SearchImg(Search search, byte[] img) {
        this.search = search;
        this.img = img;
    }

    public SearchImg(Search search, Bitmap img) {
        this.search = search;
        this.img = ImageUtil.GetByteFromBitmap(img);
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Bitmap getImg() {
        return ImageUtil.convertByteArrayToBitmap(this.img);
    }

    public void setImg(Bitmap img) {
        this.img = ImageUtil.GetByteFromBitmap(img);
    }
}
