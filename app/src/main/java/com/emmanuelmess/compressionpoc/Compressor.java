package com.emmanuelmess.compressionpoc;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;

import java.util.HashMap;

public class Compressor {

    public static final class CompressedBitmap {
        private final short[] pixelArray;
        private final HashMap<Short, Integer> palette = new HashMap<>();
        private int width, height;

        /**
         * if the drawable is already a set of instructions,
         * like vector graphics, save it as is.
         */
        private final Drawable drawable;

        private CompressedBitmap(final Drawable drawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(drawable instanceof VectorDrawable) {
                    this.drawable = drawable;
                    this.pixelArray = null;
                    return;
                }
            }

            HashMap<Integer, Short> inversePalette = new HashMap<>();

            Bitmap bitmap = drawableToBitmap(drawable);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            pixelArray = new short[bitmap.getWidth() * bitmap.getHeight()];
            short currentColorCode = Short.MIN_VALUE;

            for(int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Integer px = bitmap.getPixel(x, y);

                    if(!inversePalette.containsKey(px)) {
                        if(currentColorCode == Short.MAX_VALUE) {
                            throw new IllegalArgumentException("Drawable has too many colors");
                        }

                        Short colorCode = currentColorCode++;

                        palette.put(colorCode, px);
                        inversePalette.put(px, colorCode);
                    }

                    pixelArray[x + y*width] = inversePalette.get(px);
                }
            }

            this.drawable = null;
        }

        public Drawable getDrawable(final Resources resources) {
            if(drawable != null) {
                return drawable;
            }

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(width, height, conf);
            for(int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, palette.get(pixelArray[x + y*width]));
                }
            }
            return new BitmapDrawable(resources, bmp);
        }

        /**
         * https://stackoverflow.com/a/10600736/3124150
         */
        public static Bitmap drawableToBitmap(final Drawable drawable) {
            Bitmap bitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    public static CompressedBitmap compress(final Drawable drawable) {
        return new CompressedBitmap(drawable);
    }

    public static Drawable decompress(final Resources resources, final CompressedBitmap compressedBitmap) {
        return compressedBitmap.getDrawable(resources);
    }
}
