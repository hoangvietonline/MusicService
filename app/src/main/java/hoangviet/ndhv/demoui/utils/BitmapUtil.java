package hoangviet.ndhv.demoui.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

public class BitmapUtil {
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        Bitmap tmp;
        Rect srcRect, dstRect;
        float r = 50;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        output = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        if (width > height) {
            tmp = Bitmap.createScaledBitmap(bitmap, 100 * width / height, 100, false);
            int left = (tmp.getWidth() - tmp.getHeight()) / 2;
            int right = left + tmp.getHeight();
            srcRect = new Rect(left, 0, right, tmp.getHeight());
            dstRect = new Rect(0, 0, tmp.getHeight(), tmp.getHeight());
        } else {
            tmp = Bitmap.createScaledBitmap(bitmap, 100, 100 * height / width, false);
            int top = (tmp.getHeight() - tmp.getWidth()) / 2;
            int bottom = top + tmp.getWidth();
            srcRect = new Rect(0, top, tmp.getWidth(), bottom);
            dstRect = new Rect(0, 0, tmp.getWidth(), tmp.getWidth());
        }
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(tmp, srcRect, dstRect, paint);
        return output;
    }
}
