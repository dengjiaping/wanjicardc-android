package com.wjika.client.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 自定义流式布局。
 * Created by Leo_Zhang on 2016/7/19.
 */
public class FlowLayout extends ViewGroup {

	//用于存放所有的line对象
	private ArrayList<Line> lines = new ArrayList<>();

	private int horizontalSpacing = 20;//子viwe之间的间距
	private int verticalSpacing = 30;//行与行之间的间距

	public void setHorizontalSpacing(int horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
	}

	public void setVerticalSpacing(int verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
	}

	public FlowLayout(Context context) {
		super(context);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	//遍历所有textview进行分行逻辑。
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		//由于onMeasure可能被多次调用，所以先清空lines
		lines.clear();
		//获取当前FlowLayout的宽度
		int width = MeasureSpec.getSize(widthMeasureSpec);
		//获取用于实际比较的宽度。减去左右padding
		int noPaddingWidth = width - getPaddingLeft() - getPaddingRight();
		//遍历所有的textview
		Line line = new Line();
		for (int i = 0; i < getChildCount(); i++) {
			View childAt = getChildAt(i);//获取当前子view、
			childAt.measure(0, 0);//
			if (line.getViewlist().size() == 0) {
				line.addView(childAt);
			} else if (line.getWidth() + horizontalSpacing + childAt.getMeasuredWidth() > noPaddingWidth) {
				lines.add(line);
				line = new Line();
				line.addView(childAt);
			} else {
				line.addView(childAt);
			}
			if (i == (getChildCount() - 1)) {
				lines.add(line);
			}
		}

		int height = getPaddingTop() + getPaddingBottom();
		for (int i = 0; i < lines.size(); i++) {
			height += lines.get(i).getHeight();
		}
		height += (lines.size() - 1) * verticalSpacing;
		setMeasuredDimension(width, height);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();

		for (int i = 0; i < lines.size(); i++) {
			Line line = lines.get(i);//获取line对象

			//从第二行开始，每行的top都比上一行多个行高+垂直间距
			if (i > 0) {
				paddingTop += lines.get(i - 1).getHeight() + verticalSpacing;
			}

			ArrayList<View> viewList = line.getViewlist();//获取line的TextView的集合
			//1.计算当前line的留白的区域
			float remainSpacing = getLineRemainSpacing(line);
			//2.计算每个TextView平均分到的值
			float perSpacing = remainSpacing / viewList.size();
			for (int j = 0; j < viewList.size(); j++) {
				View childView = viewList.get(j);
				//3.将perSapcing增加到childView原来的宽度上
//				int widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (childView.getMeasuredWidth()+perSpacing)
//						, MeasureSpec.EXACTLY);
//				childView.measure(widthMeasureSpec,0);//重新测量childVIew

				if (j == 0) {
					//摆放第一个TextView，需要靠左边摆放
					childView.layout(paddingLeft, paddingTop, paddingLeft + childView.getMeasuredWidth()
							, paddingTop + childView.getMeasuredHeight());
				} else {
					//后面的TextView都可以参考前一个TextView
					View preView = viewList.get(j - 1);//获取前一个TextView对象
					int left = preView.getRight() + horizontalSpacing;//获取自身的left
					childView.layout(left, preView.getTop(), left + childView.getMeasuredWidth(), preView.getBottom());
				}
			}
		}
	}

	/**
	 * 获取指定line的留白
	 */
	private float getLineRemainSpacing(Line line) {
//		noPaddingWidth-line的宽度
		return getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - line.getWidth();
	}

	/*
	* 封装每行的数据。
	* */
	class Line {
		private ArrayList<View> viewlist = new ArrayList<>();//用于存放所有的textview。
		private int width;//表示所有textview+水平间距的宽度。
		private int height;

		//存放view
		public void addView(View view) {
			if (!viewlist.contains(view)) {
				//更新所有width
				if (viewlist.size() == 0) {
					width = view.getMeasuredWidth();
				} else {
					width += horizontalSpacing + view.getMeasuredWidth();
				}

				//更新所有height
				height = Math.max(height, view.getMeasuredHeight());
				viewlist.add(view);
			}
		}

		ArrayList<View> getViewlist() {
			return viewlist;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
}
