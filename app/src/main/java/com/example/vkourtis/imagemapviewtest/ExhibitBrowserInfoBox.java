package com.example.vkourtis.imagemapviewtest;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is just a random View used to show callouts.
 * It's old and is probably chock-full of errors and
 * bad-practice, but was handy and looks decent.
 *
 * taken from https://github.com/moagrius/TileView/tree/master/demo
 */

public class ExhibitBrowserInfoBox extends LinearLayout {

	private TextView titleView;
	private TextView textView;

	public ExhibitBrowserInfoBox(Context context) {
		super(context);
		init();

	}

	private void init() {
		inflate(getContext(), R.layout.exhibit_browser_info_box, this);
//		this.setBackground(getResources().getDrawable(R.drawable.bubble_bottom));

		titleView = (TextView) findViewById(R.id.exhibit_browser_bubble_title);
		textView = (TextView) findViewById(R.id.exhibit_browser_bubble_text);

        titleView.setVisibility(GONE);
        textView.setVisibility(GONE);
	}

	public void setTitle( CharSequence title ) {
		if (titleView != null && title != null) {
            titleView.setText(title);
            titleView.setVisibility(VISIBLE);
        }
        else {
            titleView.setVisibility(GONE);
        }
	}

	public void setText(CharSequence text ) {
		if (textView != null && text != null) {
            textView.setText(text);
            textView.setVisibility(VISIBLE);
        }
        else {
            textView.setVisibility(GONE);
        }
	}

	public static final int NUB_POSITION_TOP = 0;
	public static final int NUB_POSITION_RIGHT = 1;
	public static final int NUB_POSITION_BOTTOM = 2;
	public static final int NUB_POSITION_LEFT = 4;
	public void setNubPosition(int position) {
		switch (position) {
			case NUB_POSITION_TOP:
				setBackground(getResources().getDrawable(R.drawable.bubble_top));
				break;
			case NUB_POSITION_RIGHT:
				setBackground(getResources().getDrawable(R.drawable.bubble_right));
				break;
			case NUB_POSITION_BOTTOM:
				setBackground(getResources().getDrawable(R.drawable.bubble_bottom));
				break;
			case NUB_POSITION_LEFT:
				setBackground(getResources().getDrawable(R.drawable.bubble_left));
				break;
			default:
				setBackground(getResources().getDrawable(R.drawable.bubble_top));
		}
	}

	public void transitionIn() {

		ScaleAnimation scaleAnimation = new ScaleAnimation( 0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f );
		scaleAnimation.setInterpolator( new OvershootInterpolator( 1.2f ) );
		scaleAnimation.setDuration( 250 );

		AlphaAnimation alphaAnimation = new AlphaAnimation( 0, 1f );
		alphaAnimation.setDuration( 200 );

		AnimationSet animationSet = new AnimationSet( false );

		animationSet.addAnimation( scaleAnimation );
		animationSet.addAnimation( alphaAnimation );

		startAnimation( animationSet );

	}
}