package net.evendanan.pixel;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.DrawableRes;

/** A custom settings tile view */
public class SettingsTileView extends LinearLayout {
  private TextView mLabel;
  private ImageView mImage;
  private Drawable mSettingsTile;
  private CharSequence mSettingsLabel;
  private AttributeSet mStoredAttrs; // store attrs to use later

  public SettingsTileView(Context context) {
    super(context);
  }

  public SettingsTileView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.mStoredAttrs = attrs;
  }

  public SettingsTileView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.mStoredAttrs = attrs;
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    // Apply styleable attributes
    if (mStoredAttrs != null) {
      TypedArray array = getContext().obtainStyledAttributes(mStoredAttrs, R.styleable.SettingsTileView);
      mSettingsTile = array.getDrawable(R.styleable.SettingsTileView_tileImage);
      mSettingsLabel = array.getText(R.styleable.SettingsTileView_tileLabel);
      array.recycle();
    }

    // Inflate internal layout
    inflate(getContext(), R.layout.settings_tile_view, this);

    // Bind children
    mImage = findViewById(R.id.tile_image);
    if (mSettingsTile != null) {
      mImage.setImageDrawable(mSettingsTile);
    }

    mLabel = findViewById(R.id.tile_label);
    if (mSettingsLabel != null) {
      mLabel.setText(mSettingsLabel);
    }

    // Configure background/orientation
    setupBasicLayoutConfiguration();
  }

  private void setupBasicLayoutConfiguration() {
    setBackgroundResource(R.drawable.transparent_click_feedback_background);

    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      setOrientation(LinearLayout.VERTICAL);
      setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
    } else {
      setOrientation(LinearLayout.HORIZONTAL);
      setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
  }

  public CharSequence getLabel() {
    return mLabel != null ? mLabel.getText() : null;
  }

  public void setLabel(CharSequence label) {
    if (mLabel != null) {
      mLabel.setText(label);
    }
  }

  public Drawable getImage() {
    return mImage != null ? mImage.getDrawable() : null;
  }

  public void setImage(@DrawableRes int imageId) {
    if (mImage != null) {
      mImage.setImageResource(imageId);
    }
  }
}
