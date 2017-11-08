/**
 * 
 */
package com.common.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.common.R;


/**
 * @author songxudong
 * 
 */
public class FProgressDialog extends ProgressDialog {

	/**
	 * @param context
	 * @param theme
	 */
	public FProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * @param context
	 */
	public FProgressDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.f_progress_dialog);
		setCanceledOnTouchOutside(false);
		setCancelable(true);
	}

	@Override
	public void show() {
		super.show();
		findViewById(R.id.progress_loading).setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		super.hide();
		findViewById(R.id.progress_loading).setVisibility(View.INVISIBLE);
	}
}
