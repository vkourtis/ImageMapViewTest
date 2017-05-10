package com.example.vkourtis.imagemapviewtest;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.qozix.tileview.graphics.BitmapProvider;
import com.qozix.tileview.tiles.Tile;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Vassilis Kourtis on 20/02/2017.
 */

public class BitmapProviderGlide implements BitmapProvider {
    public Bitmap getBitmap(Tile tile, Context context ) {
        Object data = tile.getData();
        if( data instanceof TiledImage) {
            try {
                TiledImage tiledImage = (TiledImage) data;
                return Glide.with(context)
                        .load(tiledImage.getFilePath())
                        .asBitmap()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .fitCenter()
                        .into(tiledImage.getWidth(), tiledImage.getHeight())
                        .get();
            } catch (CancellationException e) {
                e.printStackTrace();
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
