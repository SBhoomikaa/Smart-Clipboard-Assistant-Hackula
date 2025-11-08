package net.evendanan.pixel;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.anysoftkeyboard.base.utils.Logger;

public class EdgeEffectHacker {

  /**
   * Applies a tint on the overscroll glow and edge effect drawables (if available).
   *
   * @param activity   The Activity context.
   * @param brandColor The color you wish to apply.
   */
  public static void brandGlowEffect(@NonNull Activity activity, int brandColor) {
    try {
      // glow drawable
      int glowDrawableId =
              activity.getResources().getIdentifier("overscroll_glow", "drawable", "android");
      if (glowDrawableId != 0) {
        Drawable androidGlow = ResourcesCompat.getDrawable(
                activity.getResources(), glowDrawableId, activity.getTheme());
        if (androidGlow != null) {
          DrawableCompat.setTint(androidGlow.mutate(), brandColor);
        }
      }

      // edge drawable
      int edgeDrawableId =
              activity.getResources().getIdentifier("overscroll_edge", "drawable", "android");
      if (edgeDrawableId != 0) {
        Drawable androidEdge = ResourcesCompat.getDrawable(
                activity.getResources(), edgeDrawableId, activity.getTheme());
        if (androidEdge != null) {
          DrawableCompat.setTint(androidEdge.mutate(), brandColor);
        }
      }

    } catch (Exception e) {
      Logger.w("EdgeEffectHacker", "Failed to set brandGlowEffect!", e);
    }
  }
}
