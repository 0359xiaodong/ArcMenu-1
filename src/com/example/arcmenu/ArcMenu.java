package com.example.arcmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.provider.OpenableColumns;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class ArcMenu extends ViewGroup implements OnClickListener {
	
	private static final int POS_LEFT_TOP = 0;
	private static final int POS_LEFT_BOTTOM = 1;
	private static final int POS_RIGHT_TOP = 2;
	private static final int POS_RIGHT_BOTTOM = 3;
	
	private Position mPosition = Position.RIGHT_BOTTOM;
	private int mRadius;
	
	/**
	 * 菜单的状态
	 */
	private Status mCurrentStatus = Status.CLOSE;
	
	/**
	 * 菜单的主按钮
	 */
	private View mCButton;
	
	private OnMenuItemClickListener mMenuItemClickListener;
	
	public enum Status{
		OPEN, CLOSE
	}
	/**
	 * 菜单位置枚举类
	 */
	public enum Position{
		LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
	}
	
	/**
	 * 点击子菜单项的回调接口
	 */
	public interface OnMenuItemClickListener{
		void onClick(View view, int pos);
	}
	

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener mMenuItemClickListener) {
		this.mMenuItemClickListener = mMenuItemClickListener;
	}

	public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
		
		// 获取自定义属性
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ArcMenu, defStyle, 0);
		
		int pos = ta.getInt(R.styleable.ArcMenu_position, 3);
		switch (pos) {
		case POS_LEFT_TOP:
			mPosition = Position.LEFT_TOP;
			break;
		case POS_LEFT_BOTTOM:
			mPosition = Position.LEFT_BOTTOM;
			break;
		case POS_RIGHT_TOP:
			mPosition = Position.RIGHT_TOP;
			break;
		case POS_RIGHT_BOTTOM:
			mPosition = Position.RIGHT_BOTTOM;
			break;
		}
		mRadius = ta.getDimensionPixelOffset(R.styleable.ArcMenu_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
		
		System.out.println("position = " + mPosition + " , radius =  " + mRadius);
		ta.recycle();
	}

	public ArcMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ArcMenu(Context context) {
		this(context, null);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int count = getChildCount();
		
		for (int i = 0; i < count; i++) {
			// 测量child
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		if(changed){
			layoutCButton();
			
			int count = getChildCount();
			for (int i = 0; i < count-1; i++) {
				View child = getChildAt(i+1);
				
				child.setVisibility(View.GONE);
				
				int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count-2) * i));
				int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
				
				int cWidth = child.getMeasuredWidth();
				int cHeight = child.getMeasuredHeight();
				
				// 如果菜单位置在底部
				if(mPosition == Position.LEFT_BOTTOM||mPosition == Position.RIGHT_BOTTOM){
					ct = getMeasuredHeight() - ct - cHeight;
				}
				
				// 如果菜单位置在右部
				if(mPosition == Position.RIGHT_TOP||mPosition == Position.RIGHT_BOTTOM){
					cl = getMeasuredWidth() - cl - cWidth;
				}
				
				child.layout(cl, ct, cl+cWidth, ct+cHeight);
			}
		}
	}

	/**
	 * 定位主菜单按钮
	 */
	private void layoutCButton() {
		mCButton = getChildAt(0);
		mCButton.setOnClickListener(this);
		
		int l = 0;
		int t = 0;
		
		int width = mCButton.getMeasuredWidth();
		int height = mCButton.getMeasuredHeight();
		
		switch (mPosition) {
		case LEFT_TOP:
			l = 0;
			t = 0;
			break;

		case LEFT_BOTTOM:
			l = 0;
			t = getMeasuredHeight() - height;
			break;
			
		case RIGHT_TOP:
			l = getMeasuredWidth() - width;
			t = 0;
			break;

		case RIGHT_BOTTOM:
			l = getMeasuredWidth() - width;
			t = getMeasuredHeight() - height;
			System.out.println("measureHeight = " + getMeasuredHeight());
			System.out.println("t = " + t);
			break;
		}
		
		mCButton.layout(l, t, l + width, t + width);
	}

	@Override
	public void onClick(View v) {
		// mCButton = findViewById(R.id.id_btn);
		// if(mCButton == null){
		// mCButton = getChildAt(0);
		// }
		
		rotateCButton(v, 0f, 360f, 300);
		toggleMenu(300);
	}

	public void toggleMenu(int duration) {
		// 为munuItem添加平移动画和旋转动画
		int count = getChildCount();
		for (int i = 0; i < count-1; i++) {
			final View child = getChildAt(i+1);
			child.setVisibility(View.VISIBLE);
			
			int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count-2) * i));
			int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
			
			int xflag = 1;
			int yflag = 1;
			
			// 如果菜单位置在左部
			if(mPosition == Position.LEFT_TOP||mPosition == Position.LEFT_BOTTOM){
				xflag = -1;
			}
			
			// 如果菜单位置在上部
			if(mPosition == Position.LEFT_TOP||mPosition == Position.RIGHT_TOP){
				yflag = -1;
			}
			
			AnimationSet as = new AnimationSet(true);
			TranslateAnimation ta = null;
			
			// to open
			if(mCurrentStatus == Status.CLOSE){
				ta = new TranslateAnimation(xflag*cl, 0, yflag*ct, 0);
				child.setClickable(true);
				child.setFocusable(true);
			}else{ // to close
				ta = new TranslateAnimation(0, xflag*cl, 0, yflag*ct);
				child.setClickable(false);
				child.setFocusable(false);
			}
			ta.setFillAfter(true);
			ta.setDuration(duration);
			ta.setStartOffset((i*100)/count);
			
			ta.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					if(mCurrentStatus == Status.CLOSE){
						child.setVisibility(View.GONE);
					}
				}
			});
			
			// 旋转动画
			RotateAnimation ra = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			ra.setDuration(duration);
			ra.setFillAfter(true);
			
			as.addAnimation(ra);
			as.addAnimation(ta);
			
			child.startAnimation(as);
			
			final int pos = i + 1;
			child.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mMenuItemClickListener != null)
						mMenuItemClickListener.onClick(child, pos);

					menuItemAnim(pos - 1);
					changeStatus();

				}
			});
		}
		

		// 切换菜单状态
		changeStatus();
	}

	/**
	 * 添加menuItem的点击动画
	 * @param i
	 */
	protected void menuItemAnim(int pos) {
		for (int i = 0; i < getChildCount() - 1; i++) {
			View childView = getChildAt(i+1);
			if(i == pos){// 点中的放大
				childView.startAnimation(scaleBigAnim(300));
			}else{       // 其他的缩小
				childView.startAnimation(scaleSmallAnim(300));
			}
			
			childView.setClickable(false);
			childView.setFocusable(false);
		}
	}

	private Animation scaleSmallAnim(int duration) {
		AnimationSet as = new AnimationSet(true);
		ScaleAnimation sa = new ScaleAnimation(1.0f, 0, 1.0f, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0);
		as.addAnimation(sa);
		as.addAnimation(aa);
		
		as.setDuration(duration);
		as.setFillAfter(true);
		return as;
	}

	private Animation scaleBigAnim(int duration) {
		AnimationSet as = new AnimationSet(true);
		ScaleAnimation sa = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0);
		as.addAnimation(sa);
		as.addAnimation(aa);
		
		as.setDuration(duration);
		as.setFillAfter(true);
		return as;
	}

	/**
	 * 切换菜单状态
	 */
	private void changeStatus() {
		mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
	}
	
	public boolean isOpen()	{
		return mCurrentStatus == Status.OPEN;
	}

	private void rotateCButton(View v, float start, float end, int duration) {
		RotateAnimation ra = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(duration);
		ra.setFillAfter(true);
		v.startAnimation(ra);
	}

}
