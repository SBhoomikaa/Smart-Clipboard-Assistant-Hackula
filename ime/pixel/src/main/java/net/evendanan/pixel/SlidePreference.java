package net.evendanan.pixel;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import java.util.Locale;

public class SlidePreference extends Preference implements SeekBar.OnSeekBarChangeListener {

  private TextView mMaxValue;
  private TextView mCurrentValue;
  private TextView mMinValue;
  private String mTitle;
  private String mValueTemplate;

  private int mDefault;
  private int mMax;
  private int mMin;
  private int mValue;

  public SlidePreference(@NonNull Context context, @Nullable android.util.AttributeSet attrs) {
    super(context, attrs);

    if (attrs != null) {
      TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlidePreferenceAttributes);

      mDefault = array.getInteger(R.styleable.SlidePreferenceAttributes_android_defaultValue, 0);
      mMax = array.getInteger(R.styleable.SlidePreferenceAttributes_slideMaximum, 100);
      mMin = array.getInteger(R.styleable.SlidePreferenceAttributes_slideMinimum, 0);

      mValueTemplate = array.getString(R.styleable.SlidePreferenceAttributes_valueStringTemplate);
      if (TextUtils.isEmpty(mValueTemplate)) {
        mValueTemplate = "%d";
      }

      int titleResId = array.getResourceId(R.styleable.SlidePreferenceAttributes_android_title, 0);
      if (titleResId != 0) {
        mTitle = context.getString(titleResId);
      } else {
        mTitle = array.getString(R.styleable.SlidePreferenceAttributes_android_title);
      }

      array.recycle();
    }
  }

  @Override
  public void onAttached() {
    super.onAttached();
    setLayoutResource(R.layout.slide_pref);
  }

  @Override
  public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);

    mValue = shouldPersist() ? getPersistedInt(mDefault) : mDefault;

    // Correct casting
    mCurrentValue = (TextView) holder.findViewById(R.id.pref_current_value);
    mMaxValue     = (TextView) holder.findViewById(R.id.pref_max_value);
    mMinValue     = (TextView) holder.findViewById(R.id.pref_min_value);

    if (mCurrentValue != null)
      mCurrentValue.setText(String.format(Locale.ROOT, mValueTemplate, mValue));

    TextView titleView = (TextView) holder.findViewById(R.id.pref_title);
    if (titleView != null && mTitle != null)
      titleView.setText(mTitle);

    writeBoundaries();

    SeekBar seekBar = (SeekBar) holder.findViewById(R.id.pref_seekbar);
    if (seekBar != null) {
      seekBar.setMax(mMax - mMin);
      seekBar.setProgress(mValue - mMin);
      seekBar.setOnSeekBarChangeListener(this);
    }
  }

  @Override
  protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
    if (restorePersistedValue) {
      mValue = shouldPersist() ? getPersistedInt(mDefault) : mDefault;
    } else if (defaultValue instanceof Integer) {
      mValue = (Integer) defaultValue;
    } else {
      mValue = mDefault;
    }

    if (mValue > mMax) mValue = mMax;
    if (mValue < mMin) mValue = mMin;

    if (mCurrentValue != null)
      mCurrentValue.setText(String.format(Locale.ROOT, mValueTemplate, mValue));
  }

  @Override
  public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
    mValue = value + mMin;
    if (mValue > mMax) mValue = mMax;
    if (mValue < mMin) mValue = mMin;

    if (shouldPersist())
      persistInt(mValue);

    callChangeListener(mValue);

    if (mCurrentValue != null)
      mCurrentValue.setText(String.format(Locale.ROOT, mValueTemplate, mValue));
  }

  @Override
  public void onStartTrackingTouch(SeekBar seek) { }

  @Override
  public void onStopTrackingTouch(SeekBar seek) { }

  private void writeBoundaries() {
    if (mMaxValue != null) mMaxValue.setText(Integer.toString(mMax));
    if (mMinValue != null) mMinValue.setText(Integer.toString(mMin));
    if (mCurrentValue != null) mCurrentValue.setText(String.format(Locale.ROOT, mValueTemplate, mValue));
  }

  @VisibleForTesting
  public int getMax() { return mMax; }

  @VisibleForTesting
  public int getMin() { return mMin; }

  public int getValue() { return mValue; }
}
