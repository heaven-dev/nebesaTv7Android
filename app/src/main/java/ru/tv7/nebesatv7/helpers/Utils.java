package ru.tv7.nebesatv7.helpers;

import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import static ru.tv7.nebesatv7.helpers.Constants.FADE_ANIMATION_DURATION;
import static ru.tv7.nebesatv7.helpers.Constants.FADE_ANIMATION_END;
import static ru.tv7.nebesatv7.helpers.Constants.FADE_ANIMATION_START;
import static ru.tv7.nebesatv7.helpers.Constants.SHOW_ANIMATIONS;
import static ru.tv7.nebesatv7.helpers.Constants.ZERO_STR;

/**
 * Util methods.
 */
public abstract class Utils {

    public static void showErrorToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void fadePageAnimation(ViewGroup viewGroup) {
        if (SHOW_ANIMATIONS) {
            viewGroup.startAnimation(createAnimation());
        }
    }

    public static String prependZero(int value) {
        if (value < 10) {
            return ZERO_STR + String.valueOf(value);
        }
        return String.valueOf(value);
    }

    private static Animation createAnimation() {
        Animation animation = new AlphaAnimation(FADE_ANIMATION_START, FADE_ANIMATION_END);
        animation.setDuration(FADE_ANIMATION_DURATION);
        return animation;
    }
}
