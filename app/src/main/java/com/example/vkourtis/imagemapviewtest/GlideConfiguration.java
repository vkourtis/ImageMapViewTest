package com.example.vkourtis.imagemapviewtest;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Configuration class for the Glide library that is used to efficiently load
 * and present images either from local files or the network
 */
public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.

        // Changing bitmap format to achieve better quality (as stated in
        // http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en)
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}