package com.wjika.client.cardpackage.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.common.utils.ToastUtil;
import com.wjika.cardagent.client.R;
import com.wjika.client.base.fragment.BaseFragment;
import com.wjika.client.store.controller.StoreDetailActivity;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import java.io.IOException;

/**
 * Created by Liu_Zhichao on 2016/6/27 15:46.
 * 扫一扫
 * Fragment中包含有SurfaceView,在第一次初始化的时候屏幕会出现黑屏闪一下，
 * 以后再进行Fragment切换的时候却不会再出现这种情况，Activity里面包含SurfaceView却不会出现闪屏，
 * 解决办法是在包含Fragment的Activity的onCreate()方法中加上getWindow().setFormat(PixelFormat.TRANSLUCENT);
 * 设置当前Activity窗口支持透明度
 */
public class ScanFragment extends BaseFragment implements SurfaceHolder.Callback {

	private static final long VIBRATE_DURATION = 200L;//震动时长
	private static final float BEEP_VOLUME = 0.50f;//音量

	private InactivityTimer inactivityTimer;
	private RelativeLayout mContainer;//全布局
	private RelativeLayout mCropLayout;//裁剪范围
	private SurfaceView surfaceView;//相机显示范围
	private MediaPlayer mediaPlayer;//媒体播放器，播放扫码成功后的声音
	private CaptureActivityHandler handler;//扫描信息处理
	private boolean hasSurface;
	private boolean flag = true;//闪光灯
	private boolean playBeep;//播放声音
	private boolean vibrate = false;//震动
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private boolean isNeedCapture;//是否需要截图，不截图也可以正常扫码

	/**
	 * 音频播放结束回调
	 */
	private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化 CameraManager
		CameraManager.init();
		hasSurface = false;
		inactivityTimer = new InactivityTimer(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_scan, container, false);
		mContainer = (RelativeLayout) view.findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) view.findViewById(R.id.capture_crop_layout);
		surfaceView = (SurfaceView) view.findViewById(R.id.capture_preview);

		ImageView mQrLineView = (ImageView) view.findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.95f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			//第一次时执行，通过回调将hasSurface修改为true
			surfaceHolder.addCallback(this);
		}
		playBeep = true;
		//AudioManager类提供访问音量和振铃模式的控制。
		AudioManager audioService = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		// RINGER_MODE_NORMAL 振铃模式可以是听得见的或振动的。
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();//初始化声音播放
		vibrate = true;//震动
	}

	@Override
	public void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	/**
	 * 初始化声音播放
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepareAsync();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			int rootWidth = mContainer.getWidth();
			int rootHeight = mContainer.getHeight();
			//打开相机，并设置预览宽高(不是最终的，会选一个最接近系统预置的预览尺寸)
			CameraManager.get().openDriver(surfaceHolder, rootWidth, rootHeight);

			//拿到相机分辨率，计算屏幕截图范围
			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / rootWidth;
			int y = mCropLayout.getTop() * height / rootHeight;

			int cropWidth = mCropLayout.getWidth() * width / rootWidth;
			int cropHeight = mCropLayout.getHeight() * height / rootHeight;

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			setNeedCapture(true);
		} catch (IOException | RuntimeException ioe) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.app_name));
			builder.setMessage(getString(R.string.msg_camera_framework_bug));
			builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					((CardPackageActivity) getActivity()).selectQrFrag(true);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					((CardPackageActivity) getActivity()).selectQrFrag(true);
				}
			});
			builder.show();
			return;
		}
		if (handler == null) {
			//开启相机预览，解码
			handler = new CaptureActivityHandler(this);
		}
	}

	/**
	 * 管理闪光灯
	 */
	protected void light() {
		if (flag) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}
	}

	/**
	 * 扫描结果处理
	 */
	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		if (!TextUtils.isEmpty(result)) {
			Intent newIntent = new Intent(getActivity(), StoreDetailActivity.class);
			newIntent.putExtra(StoreDetailActivity.EXTRA_STORE_ID, result);
			newIntent.putExtra(StoreDetailActivity.EXTRA_FROM, StoreDetailActivity.FROM_CARD_PACKAGE);
			startActivity(newIntent);
		} else {
			ToastUtil.shortShow(getActivity(), "无效二维码，请出示万家卡商家二维码");
			// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
			handler.sendEmptyMessage(R.id.restart_preview);
		}
	}

	/**
	 * 播放声音和震动
	 */
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean needCapture) {
		isNeedCapture = needCapture;
	}

	public Handler getHandler() {
		return handler;
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
}
