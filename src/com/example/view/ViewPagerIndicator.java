package com.example.view;

import java.util.List;

import com.example.viewpagerindicator.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewPagerIndicator extends LinearLayout {
	private Paint mPaint;// 绘制三角形的画笔
	private Path mPath;// 用于绘制三角形的边
	private int mTriangleWidth;// 三角形的宽
	private int mTriangleHeight;// 三角形的高
	private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;// 用于设置三角形的宽和tab底边的比例，用于屏幕适配
	/**
	 * 三角形底边的最大宽度
	 */
	private final int DIMENSION_TRIANGLE_WIDTH_MAX = (int) (getScreenWidth()/3*RADIO_TRIANGLE_WIDTH);
	private int mInitTranslationX;// 第一个三角形初始化的偏移位置
	private int mTranslationX;// 移动时候的三角形偏移位置
	private int mTabVisibleCount;// 可见tab的数量
	private static final int COUNT_DEFAULT_TAB = 4;// 默认可见tab为4个
	private List<String> mTitles;// 接收传递过来的title
	private static final int COLOR_TEXT_NORMAL = Color.parseColor("#FFFFFF");
	private static final int COLOR_TEXT_HIGHLIGHT = Color.parseColor("#FF4CDA0F");

	public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 获取可见tab的数量
		TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicator);
		mTabVisibleCount = attributes.getInt(
				R.styleable.ViewPagerIndicator_visible_tab_count,
				COUNT_DEFAULT_TAB);
		if (mTabVisibleCount < 0) {
			mTabVisibleCount = COUNT_DEFAULT_TAB;
		}
		// 用完必须释放
		attributes.recycle();

		// 初始化画笔
		mPaint = new Paint();
		// 防止边缘锯齿
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPaint.setStyle(Style.FILL);
		mPaint.setPathEffect(new CornerPathEffect(3));
	}

	public ViewPagerIndicator(Context context) {
		this(context, null);
	}

	/**
	 * 绘制三角形
	 * 绘制VIew本身的内容，通过调用View.onDraw(canvas)函数实现,绘制自己的孩子通过dispatchDraw（canvas）实现
	 * 
	 * 画完背景后，draw过程会调用onDraw(Canvas canvas)方法，然后就是dispatchDraw(Canvas canvas)方法,
	 * dispatchDraw
	 * ()主要是分发给子组件进行绘制，我们通常定制组件的时候重写的是onDraw()方法。值得注意的是ViewGroup容器组件的绘制
	 * ，当它没有背景时直接调用的是dispatchDraw
	 * ()方法,而绕过了draw()方法，当它有背景的时候就调用draw()方法，而draw()方法里包含了
	 * dispatchDraw()方法的调用。因此要在ViewGroup上绘制东西的时候往往重写的是
	 * dispatchDraw()方法而不是onDraw()方法，或者自定制一个Drawable，重写它的draw(Canvas c)和
	 * getIntrinsicWidth(),getIntrinsicHeight()方法，然后设为背景
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		/**
		 * save：用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
		 * 
		 * restore：用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。
		 * 
		 * save和restore要配对使用（restore可以比save少，但不能多），如果restore调用次数比save多，会引发Error。
		 */
		canvas.save();

		canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 2);
		canvas.drawPath(mPath, mPaint);

		canvas.restore();
	}

	/**
	 * 设置三角形的大小
	 * 
	 * onSizeChanged()在控件大小发生变化的时候调用(例如第一次初始化控件的时候) 布局过程中，
	 * 先调onMeasure计算每个child的大小， 然后调用onLayout对child进行布局，
	 * onSizeChanged（）是在布局发生变化时的回调函数，间接回去调用onMeasure, onLayout函数重新布局
	 * onSizeChanged的启动时间在onDraw之前
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		// w/3为每个tab的宽度，目前可见为3个
		mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE_WIDTH);
		//选取最小的那一个作为宽
		mTriangleWidth = Math.min(mTriangleWidth, DIMENSION_TRIANGLE_WIDTH_MAX);
		// 第一个三角形的偏移位置
		mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;

		initTriangle();
	}

	/**
	 * 初始化三角形
	 */
	private void initTriangle() {
		// mTriangleHeight = mTriangleWidth / 2;
		// 将三角形角度设置为30度
		mTriangleHeight = (int) (mTriangleWidth / 2 * Math.tan(Math.PI / 6));

		mPath = new Path();
		mPath.moveTo(0, 0);
		mPath.lineTo(mTriangleWidth, 0);
		mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
		// 关闭当前轮廓，完成闭合
		mPath.close();
	}

	/**
	 * 三角形跟随ViewPager移动
	 * 
	 * @param position
	 * @param positionOffset
	 */
	public void scroll(int position, float positionOffset) {
		int tabWidth = getWidth() / mTabVisibleCount;
		mTranslationX = (int) (tabWidth * (positionOffset + position));

		/**
		 * 容器移动,在tab处于移动至最后一个时
		 */
		if (position >= (mTabVisibleCount - 2) && positionOffset > 0
				&& getChildCount() > mTabVisibleCount) {

			if (mTabVisibleCount != 1) {
				this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth
						+ (int) (tabWidth * positionOffset), 0);
			} else {
				this.scrollTo(position * tabWidth
						+ (int) (tabWidth * positionOffset), 0);
			}
		}

		// 位置发生改变，要进行重绘
		/**
		 * invalidate的意思是“使无效”，其实就是使窗口无效。 使当前的窗口无效的目的就是让Windows知道这个窗口现在该重新绘制一下了。
		 * 所以任何时候当你想 擦除 并 绘制窗口的时候，就可以在别的函数中完成功能代码之后Invalidate()一下。OnDraw马上就会被调用了。
		 * 但是不要在OnDraw, OnPaint中用
		 */
		invalidate();
	}

	/**
	 * xml加载完成之后，回调此方法
	 * 
	 * 设置每个tab的LayoutParams
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int childCount = getChildCount();
		if (childCount == 0) {
			return;
		}

		for (int i = 0; i < childCount; i++) {
			View view = getChildAt(i);
			LinearLayout.LayoutParams params = (LayoutParams) view
					.getLayoutParams();
			params.weight = 0;
			params.width = getScreenWidth() / mTabVisibleCount;
			view.setLayoutParams(params);
		}
		setItemClickEvent();
	}

	/**
	 * 获取屏幕的宽度
	 * 
	 * @return
	 */
	private int getScreenWidth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 动态设置tab的数量
	 * 
	 * @param count
	 */
	public void setVisibleTabCount(int count) {
		mTabVisibleCount = count;
	}

	/**
	 * 动态设置tab
	 * 
	 * @param titles
	 */
	public void setTabItemTitles(List<String> titles) {
		if (titles != null && titles.size() > 0) {
			this.removeAllViews();
			mTitles = titles;
			for (String title : mTitles) {
				this.addView(generateTextView(title));
			}
			setItemClickEvent();
		}
	}

	

	/**
	 * 根据title创建tab
	 * 
	 * @param title
	 * @return
	 */
	private View generateTextView(String title) {
		TextView textView = new TextView(getContext());
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.width = getScreenWidth() / mTabVisibleCount;
		textView.setText(title);
		textView.setGravity(Gravity.CENTER);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		textView.setTextColor(COLOR_TEXT_NORMAL);
		textView.setLayoutParams(params);
		return textView;
	}

	// 接收关联的ViewPager
	private ViewPager mViewPager;

	/**
	 * 提供一个接口供外部ViewPager使用
	 * 
	 * @author Administrator
	 * 
	 */
	public interface PageOnChangeListener {
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		public void onPageSelected(int position);

		public void onPageScrollStateChanged(int state);
	}

	public PageOnChangeListener mListener;

	public void setViewPagerOnPageChangeListener(PageOnChangeListener listener) {
		mListener = listener;
	}

	/**
	 * 设置关联的ViewPager
	 * 
	 * @param viewpager
	 * @param position
	 */
	public void setViewPager(ViewPager viewpager, int position) {
		mViewPager = viewpager;
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (mListener != null) {
					mListener.onPageSelected(position);
				}
				highLightTextView(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// 三角形跟随ViewPager移动的距离就是：
				// tabWidth*positionOffset+position*tabWidth
				scroll(position, positionOffset);

				if (mListener != null) {
					mListener.onPageScrolled(position, positionOffset,
							positionOffsetPixels);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (mListener != null) {
					mListener.onPageScrollStateChanged(state);
				}

			}
		});
		mViewPager.setCurrentItem(position);
		highLightTextView(position);
	}
	/**
	 * 高亮被点击的tab
	 * @param position
	 */
	private void highLightTextView(int position){
		resetTextViewColor();
		View view = getChildAt(position);
		if (view instanceof TextView) {
			((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
		}
	}
	/**
	 * 重置tab文本颜色
	 */
	private void resetTextViewColor(){
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof TextView) {
				((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
			}
		}
	}
	/**
	 * 设置Tab的点击事件
	 */
	private void setItemClickEvent(){
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final int j = i;
			View view = getChildAt(i);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(j);
				}
			});
		}
	}
}
