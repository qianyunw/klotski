package com.DevStoreDemo;



import java.io.InputStream;
import java.util.Enumeration;
import java.util.Timer;

import com.DevStoreDemo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Font;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;


public class ChessboardView extends View
{
	private int recLen = 0;
	private Context mContext;
	private PlayBoard playBoard;
	private boolean IsWin = false;
	private int currentSelectedValue;//记录当前被选中的格子
	private int prevSelectedValue;//记录前一个被选中的格子
	private int StepNumber;//记录前一个被选中的格子

public ChessboardView(final Context context)
	{
		super(context);
		mContext = context;
		// Initialize the play board.
		//前两个数字表示棋子的宽和长，后两个数字是棋子的左上角位置
		//最后是引用图片
		playBoard = new PlayBoard(4, 5);
		Fragment.setPlayBoard(playBoard);
		Fragment.addFragment(new Fragment("Cao Cao", 1, 2, 2, 1, 0, R.drawable.role_caocao));
		Fragment.addFragment(new Fragment("Zhang Fei", 2, 1, 2, 0, 0, R.drawable.role_zhangfei));
		Fragment.addFragment(new Fragment("Huang Zhong", 3, 1, 2, 3, 0, R.drawable.role_huangzhong));
		Fragment.addFragment(new Fragment("Ma Chao", 4, 1, 2, 0, 2, R.drawable.role_machao));
		Fragment.addFragment(new Fragment("Zhao Yun", 5 , 1, 2, 3, 2, R.drawable.role_zhaoyun));
		Fragment.addFragment(new Fragment("Guan Yu", 6, 2, 1, 1, 2, R.drawable.role_guanyu));
		Fragment.addFragment(new Fragment("Soldier1", 7, 1, 1, 0, 4, R.drawable.role_soldier1));
		Fragment.addFragment(new Fragment("Soldier2", 8, 1, 1, 3, 4, R.drawable.role_soldier2));
		Fragment.addFragment(new Fragment("Soldier3", 9, 1, 1, 1, 3, R.drawable.role_soldier3));
		Fragment.addFragment(new Fragment("Soldier4", 10, 1, 1, 2, 3, R.drawable.role_soldier4));
		StepNumber = 0;

		this.setOnTouchListener(new OnTouchListener()
		{
			private int xPos;
			private int yPos;

			public boolean onTouch(View view, MotionEvent motion) {
				//手指触摸点的坐标，每个格子80像素
				xPos = (int) motion.getX();
				yPos = (int) motion.getY();
				//当前格子的索引
				int x = xPos / 80;
				int y = yPos / 80;
				//下面表示不能选第六格
				if (y == 5)
					return false;
					//以下执行棋子移动
					//可以把移动棋子删去，改成松开棋子的地方，可以拖动棋子

				else {
					prevSelectedValue = currentSelectedValue;
					currentSelectedValue = playBoard.getBoardValue(x, y);
					if(currentSelectedValue > 0 && currentSelectedValue < 11)
						view.invalidate();
					if ((currentSelectedValue != prevSelectedValue) && currentSelectedValue == 0) {
						int direction = decideDirection(x, y, (Fragment) Fragment.fragmentHashTable.get(prevSelectedValue));
						if (direction != Fragment.DIRECTION_DONTMOVE) {
							Fragment.fragmentHashTable.put(prevSelectedValue, ((Fragment) Fragment.fragmentHashTable.get(prevSelectedValue)).move(direction));
							int xx = ((Fragment) Fragment.fragmentHashTable.get(prevSelectedValue)).getxPos();
							int yy = ((Fragment) Fragment.fragmentHashTable.get(prevSelectedValue)).getyPos();
							System.out.println("x: " + xx + " y: " + yy);
							System.out.println("prevSelectedValue: " + prevSelectedValue);
							if (prevSelectedValue == 1 && xx == 1 && yy == 3) {
								IsWin = true;
							}
							StepNumber++;
							System.out.println(StepNumber);
							view.invalidate();
						}
						if(StepNumber == 1) {
							new Thread(new MyThread()).start();
						}
					}
					return false;
				}
			}
		
			private int decideDirection(int xPos, int yPos, Fragment fragment)
			{
				if((xPos == fragment.getxPos() - 1) && (yPos >= fragment.getyPos() && yPos <= fragment.getyPos() + fragment.getHeight() - 1))
					return Fragment.DIRECTION_LEFT;
				if((xPos == fragment.getxPos() + fragment.getLength()) && (yPos >= fragment.getyPos() && yPos <= fragment.getyPos() + fragment.getHeight() - 1))
					return Fragment.DIRECTION_RIGHT;
				if((xPos >= fragment.getxPos() && xPos <= fragment.getxPos() + fragment.getLength() - 1) && (yPos == fragment.getyPos() - 1))
					return Fragment.DIRECTION_UP;
				if((xPos >= fragment.getxPos() && xPos <= fragment.getxPos() + fragment.getLength() - 1) && (yPos == fragment.getyPos() + fragment.getHeight()))
					return Fragment.DIRECTION_DOWN;				
				return Fragment.DIRECTION_DONTMOVE;
			}
		});

	}

	private final Handler handler = new Handler(){          // handle
		public void handleMessage(Message msg){
			switch (msg.what) {
				case 1:
					if(!IsWin) {
						recLen++;
						ChessboardView.this.invalidate();
					}
			}
			super.handleMessage(msg);
		}
	};

	public class MyThread implements Runnable{      // thread
		@Override
		public void run(){
			while(true) {
				try {
					Thread.sleep(1000);     // sleep 1000ms
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				} catch (Exception e) {}
			}
		}
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		Bitmap mBackGround  = ((BitmapDrawable) this.getResources().getDrawable(R.drawable.background)).getBitmap(); //获取背景图片
		mBackGround = resizeImage(mBackGround,4 * 80,3 * 80);
		Paint mPaint = new Paint();
		canvas.drawBitmap(mBackGround, 0, 4 * 80, mPaint); //画背景图片

		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
		textPaint.setTextSize(60);// 字体大小
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
		textPaint.setColor(Color.YELLOW);// 采用的颜色
		textPaint.setAntiAlias(true);//去除锯齿
		textPaint.setFilterBitmap(true);//对位图进行滤波处理
		canvas.drawText(Integer.toString(StepNumber), 10, 450, textPaint);
		int minus = recLen / 60;
		int second = recLen % 60;
		textPaint.setTextSize(30);// 字体大小
		canvas.drawText(Integer.toString(minus) + ":" + Integer.toString(second), 240, 450, textPaint);


		Enumeration<Fragment> enumeration = Fragment.fragmentHashTable.elements();
		while(enumeration.hasMoreElements())
		{
			Fragment fragment = enumeration.nextElement();
			drawFragment(canvas, fragment);
		}
		if(IsWin) {
			Bitmap mWin  = ((BitmapDrawable) this.getResources().getDrawable(R.drawable.win)).getBitmap(); //获取背景图片
			mWin = resizeImage(mWin,4 * 80, 5 * 80);
			canvas.drawBitmap(mWin, 0, 0, mPaint); //画背景图片
		}
	}

	//绘制矩形形状和尺寸
	private void drawFragment(Canvas canvas, Fragment fragment)
	{
		Paint paint = new Paint();

		Rect rect = new Rect();//rect表示矩形
		rect.left = fragment.getxPos() * 80;//矩形左侧边界坐标等于位置（0,1,2,3,4）*80，上侧同理
		rect.top = fragment.getyPos() * 80;
		rect.right = (fragment.getxPos() + fragment.getLength()) * 80;
		rect.bottom = (fragment.getyPos() + fragment.getHeight()) * 80;

		int currentValue = 0;
		if(currentSelectedValue > 0 && currentSelectedValue < 11)
			currentValue = currentSelectedValue;
		else if(prevSelectedValue > 0 && prevSelectedValue < 11) {
			currentValue = prevSelectedValue;
		}
		if(fragment.getValue() == currentValue) {
			int selectBmpID = 0;
			switch (currentValue) {
				case 1:
					selectBmpID = R.drawable.role_caocao_selected;
					break;
				case 2:
					selectBmpID = R.drawable.role_zhangfei_selected;
					break;
				case 3:
					selectBmpID = R.drawable.role_huangzhong_selected;
					break;
				case 4:
					selectBmpID = R.drawable.role_machao_selected;
					break;
				case 5:
					selectBmpID = R.drawable.role_zhaoyun_selected;
					break;
				case 6:
					selectBmpID = R.drawable.role_guanyu_selected;
					break;
				case 7:
					selectBmpID = R.drawable.role_soldier1_selected;
					break;
				case 8:
					selectBmpID = R.drawable.role_soldier2_selected;
					break;
				case 9:
					selectBmpID = R.drawable.role_soldier3_selected;
					break;
				case 10:
					selectBmpID = R.drawable.role_soldier4_selected;
					break;
			}
			BitmapDrawable bmpDraw = (BitmapDrawable) this.getResources().getDrawable(selectBmpID);//下划线在腰上，疑似旧的API可以被新的代替，但是也能用
			Bitmap mPic = bmpDraw.getBitmap();
			canvas.drawBitmap(mPic, null, rect, paint);
		}
		else {
			InputStream is = this.getContext().getResources().openRawResource(fragment.getPicture());
			@SuppressWarnings("deprecation")
			BitmapDrawable bmpDraw = new BitmapDrawable(is);//下划线在腰上，疑似旧的API可以被新的代替，但是也能用
			Bitmap mPic = bmpDraw.getBitmap();
			canvas.drawBitmap(mPic, null, rect, paint);
		}
	}
	public static Bitmap resizeImage(Bitmap bitmap, int w, int h)
	{
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}
}
