package com.example.chat.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/8/25.
 */

public class BitmapUtils {
    public static ImageSize getImageSize(Bitmap bitmap) {
        ImageSize imageSize = new ImageSize();
        if (null == bitmap || bitmap.isRecycled()) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteTmp = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(byteTmp, 0, byteTmp.length, bitmapOptions);
        int outWidth = bitmapOptions.outWidth;
        int outHeight = bitmapOptions.outHeight;
        int maxWidth = 400;
        int maxHeight = 400;
        int minWidth = 150;
        int minHeight = 150;
        if (outWidth / maxWidth > outHeight / maxHeight) {//
            if (outWidth >= maxWidth) {//
                imageSize.setWidth(maxWidth);
                imageSize.setHeight(outHeight * maxWidth / outWidth);
            } else {
                imageSize.setWidth(outWidth);
                imageSize.setHeight(outHeight);
            }
            if (outHeight < minHeight) {
                imageSize.setHeight(minHeight);
                int width = outWidth * minHeight / outHeight;
                if (width > maxWidth) {
                    imageSize.setWidth(maxWidth);
                } else {
                    imageSize.setWidth(width);
                }
            }
        } else {
            if (outHeight >= maxHeight) {
                imageSize.setHeight(maxHeight);
                imageSize.setWidth(outWidth * maxHeight / outHeight);
            } else {
                imageSize.setHeight(outHeight);
                imageSize.setWidth(outWidth);
            }
            if (outWidth < minWidth) {
                imageSize.setWidth(minWidth);
                int height = outHeight * minWidth / outWidth;
                if (height > maxHeight) {
                    imageSize.setHeight(maxHeight);
                } else {
                    imageSize.setHeight(height);
                }
            }
        }

        return imageSize;
    }

    public static ImageSize getImageSizeNew(String path) {
        //int[] widthHeight=getImageWidthHeight(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        ImageSize imageSize = new ImageSize();
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int maxWidth = 400;
        int maxHeight = 400;
        int minWidth = 150;
        int minHeight = 150;
        if (outWidth / maxWidth > outHeight / maxHeight) {//
            if (outWidth >= maxWidth) {//
                imageSize.setWidth(maxWidth);
                imageSize.setHeight(outHeight * maxWidth / outWidth);
            } else {
                imageSize.setWidth(outWidth);
                imageSize.setHeight(outHeight);
            }
            if (outHeight < minHeight) {
                imageSize.setHeight(minHeight);
                int width = outWidth * minHeight / outHeight;
                if (width > maxWidth) {
                    imageSize.setWidth(maxWidth);
                } else {
                    imageSize.setWidth(width);
                }
            }
        } else {
            if (outHeight >= maxHeight) {
                imageSize.setHeight(maxHeight);
                imageSize.setWidth(outWidth * maxHeight / outHeight);
            } else {
                imageSize.setHeight(outHeight);
                imageSize.setWidth(outWidth);
            }
            if (outWidth < minWidth) {
                imageSize.setWidth(minWidth);
                int height = outHeight * minWidth / outWidth;
                if (height > maxHeight) {
                    imageSize.setHeight(maxHeight);
                } else {
                    imageSize.setHeight(height);
                }
            }
        }

        return imageSize;
    }

    public static int[] getImageWidthHeight(String path)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }
}
