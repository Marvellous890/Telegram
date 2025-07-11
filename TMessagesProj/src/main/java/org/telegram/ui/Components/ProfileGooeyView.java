package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.os.Build;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.math.MathUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotchInfoUtils;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ProfileActivity;

import java.util.Arrays;

public class ProfileGooeyView extends FrameLayout {
    private static float BLACK_KING_BAR = 32;

    private Paint fadeToTop = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint fadeToBottom = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Impl impl;
    private boolean enabled;

    private NotchInfoUtils.NotchInfo notchInfo;

    public ProfileGooeyView(Context context) {
        super(context);

        impl = new NoopImpl();
        setWillNotDraw(false);
    }

    public float getEndOffset(boolean occupyStatusBar, float avatarScale) {
        if (notchInfo != null) {
            return -(AndroidUtilities.dp(16) + (notchInfo.isLikelyCircle ? notchInfo.bounds.width() + notchInfo.bounds.width() * getAvatarEndScale() : notchInfo.bounds.height() - notchInfo.bounds.top));
        }

        return -((occupyStatusBar ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(16) + AndroidUtilities.dp(ProfileActivity.AVATAR_SIZE_DP));
    }

    public float getAvatarEndScale() {
        if (notchInfo != null) {
            float f;
            if (notchInfo.isLikelyCircle) {
                f = (notchInfo.bounds.width() - AndroidUtilities.dp(2)) / AndroidUtilities.dp(ProfileActivity.AVATAR_SIZE_DP);
            } else {
                f = Math.min(notchInfo.bounds.width(), notchInfo.bounds.height()) / AndroidUtilities.dp(ProfileActivity.AVATAR_SIZE_DP);
            }
            return Math.min(0.8f, f);
        }

        return 0.8f;
    }

    public boolean hasNotchInfo() {
        return notchInfo != null;
    }

    public void setIntensity(float intensity) {
    }

    public void setBlurIntensity(float intensity) {

    }

    public void setGooeyEnabled(boolean enabled) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        notchInfo = NotchInfoUtils.getInfo(getContext());
        if (notchInfo != null && (notchInfo.gravity != Gravity.CENTER) || getWidth() > getHeight()) {
            notchInfo = null;
        }

        impl.onSizeChanged(w, h);

        fadeToTop.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        fadeToTop.setShader(new LinearGradient(getWidth() / 2f, 0, getWidth() / 2f, AndroidUtilities.dp(100), new int[]{0xFF000000, 0xFFFFFFFF}, new float[]{0.15f, 1f}, Shader.TileMode.CLAMP));

        fadeToBottom.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        fadeToBottom.setShader(new LinearGradient(getWidth() / 2f, 0, getWidth() / 2f, AndroidUtilities.dp(100), new int[]{0xFFFFFFFF, 0xFF000000}, new float[]{0.25f, 1f}, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        impl.release();
    }

    private final static class NoopImpl implements Impl {

        @Override
        public void setIntensity(float intensity) {
        }

        @Override
        public void onSizeChanged(int w, int h) {
        }

        @Override
        public void draw(Drawer drawer, Canvas canvas) {
            canvas.save();
            canvas.translate(0, -AndroidUtilities.dp(BLACK_KING_BAR));
            drawer.draw(canvas);
            canvas.restore();
        }
    }

    private interface Impl {
        void setIntensity(float intensity);

        void onSizeChanged(int w, int h);

        void draw(Drawer drawer, Canvas canvas);

        default void release() {
        }
    }

    private interface Drawer {
        void draw(Canvas canvas);
    }
}
