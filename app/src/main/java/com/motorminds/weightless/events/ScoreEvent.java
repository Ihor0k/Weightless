package com.motorminds.weightless.events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.widget.TextView;

public class ScoreEvent extends AbstractGameEvent {
    private final TextView view;
    private final int value;

    public ScoreEvent(TextView view, int value) {
        this.value = value;
        this.view = view;
    }

    @Override
    protected Animator getEventAnimator() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f);
        animatorX.setRepeatMode(ValueAnimator.REVERSE);
        animatorX.setRepeatCount(1);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f);
        animatorY.setRepeatMode(ValueAnimator.REVERSE);
        animatorY.setRepeatCount(1);

        animatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                int score = Integer.valueOf(view.getText().toString());
                view.setText(String.valueOf(score + value));
            }
        });
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(animatorX, animatorY);
        return animator;
    }
}
