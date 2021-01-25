package com.heshmat.mydietwatcher;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {
    private ProgressBar progressBar;
    private float from;
    private float  to;
    private TextView textView;

    public ProgressBarAnimation(ProgressBar progressBar, float from, float to,TextView textView) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
        this.textView=textView;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
        textView.setText((int)value+"%");
    }

}