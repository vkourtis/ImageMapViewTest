package com.example.vkourtis.imagemapviewtest;

import android.graphics.BitmapFactory;

/**
 * Created by Vassilis Kourtis on 28/02/2017.
 */

public class TiledImage {
    private String filePath;
    private int sampleSize;
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;

    public TiledImage(String filePath) {
        this.filePath = filePath;

        init();
    }

    public String getFilePath() {
        return filePath;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    private void init() {
        // Create a BitmapFactory to detect the image's dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(this.filePath, options);
        this.width = options.outWidth;
        this.height = options.outHeight;
        this.sampleSize = 1;


        // Because we use single tile images for now, we have to check if downsampling is needed
        int maxSize = 2048;     //TODO: find a way to dynamically query this depending on the device

        if(this.width >= maxSize || this.height >= maxSize){
            if (this.width >= this.height) {
                this.sampleSize =
                        calculateSampleSize(
                                this.width,
                                this.height,
                                maxSize,
                                maxSize * this.height/this.width);
            }
            else {
                this.sampleSize =
                        calculateSampleSize(
                                this.width,
                                this.height,
                                maxSize * this.width/this.height,
                                maxSize);
            }

            // Fix the dimensions given the needed resizing
            this.width /= this.sampleSize;
            this.height /= this.sampleSize;
        }

        // For now we use the whole image as a single tile
        this.tileWidth = this.width;
        this.tileHeight = this.height;
    }

    private int calculateSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int sampleSize = 1;

//            Log.d(TAG, "Calculating sampleSize");

        if (height > reqHeight || width > reqWidth) {

//                Log.d(TAG, "Dimensions: " + width + "," + height);
//                Log.d(TAG, "Required dimensions: " + reqWidth + "," + reqHeight);

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as sampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            sampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

//            Log.d(TAG, "Returning sampleSize: " + sampleSize);

        return sampleSize;
    }
}
