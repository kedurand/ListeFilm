package com.example.listefilm.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageUtil{

    public static byte[] GetByteFromBitmap(Bitmap bmp){
        if (bmp == null) return null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArrayToBeCOnvertedIntoBitMap) {
        if(byteArrayToBeCOnvertedIntoBitMap == null) return null;

        return BitmapFactory.decodeByteArray(   byteArrayToBeCOnvertedIntoBitMap, 0,
                    byteArrayToBeCOnvertedIntoBitMap.length);
    }
}
