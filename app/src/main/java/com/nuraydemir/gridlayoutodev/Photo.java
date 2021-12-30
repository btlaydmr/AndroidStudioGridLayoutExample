package com.nuraydemir.gridlayoutodev;

import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.sql.Blob;

public class Photo {
    int id;
    Bitmap image;

    public Photo(int id, Bitmap image) {
        this.id = id;
        this.image = image;
    }
}
