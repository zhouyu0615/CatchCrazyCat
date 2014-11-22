package com.example.catchcrazycat;

import java.util.HashMap;
import java.util.Vector;

import android.R.bool;
import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class Playground extends SurfaceView implements OnTouchListener {

	public Playground(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		getHolder().addCallback(callback);

		matrix = new Dot[ROW][COL];
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {

				matrix[i][j] = new Dot(j, i);
			}

		}
		setOnTouchListener(this);
		initgame();

	}

	private static int WIDTH = 60;
	private static final int ROW = 10;
	private static final int COL = 10;
	private static final int BLOCKS = 12;

	private Dot matrix[][];
	private Dot Cat;

	public Dot getDot(int x, int y) {

		return matrix[y][x];
	}

	public void redraw() {
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawColor(Color.LTGRAY);

		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		// paint.setColor(Color.RED);

		for (int i = 0; i < ROW; i++) {
			int offset = 0;
			if (i % 2 != 0) {
				offset = WIDTH / 2;
			}

			for (int j = 0; j < COL; j++) {
				Dot one = getDot(j, i);

				switch (one.getStatus()) {
				case Dot.STATUS_OFF:
					paint.setColor(0xFFEEEEEE); // 白色
					break;
				case Dot.STATUS_ON:
					paint.setColor(0xFFEEAA00); // 黄色
					break;
				case Dot.STATUS_IN:
					paint.setColor(0xFFEE0000);// 红色
					break;

				default:
					break;
				}

				canvas.drawOval(
						new RectF(one.getX() * WIDTH + offset, one.getY()
								* WIDTH, (one.getX() + 1) * WIDTH + offset,
								(one.getY() + 1) * WIDTH), paint);

			}

		}
		getHolder().unlockCanvasAndPost(canvas);

	}

	Callback callback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			redraw();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			WIDTH = width / (COL + 1);
			redraw();

		}
	};

	private void initgame() {
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j].setStatus(Dot.STATUS_OFF);
			}

		}

		Cat = new Dot(5, 4);
		Cat.setStatus(Dot.STATUS_IN);

		for (int i = 0; i < BLOCKS; i++) {

			int x = (int) ((Math.random() * 1000) % COL);
			int y = (int) ((Math.random() * 1000) % ROW);

			matrix[y][x].setStatus(Dot.STATUS_ON);
		}

		matrix[4][5].setStatus(Dot.STATUS_IN);

	}

	private boolean isAtEdge(Dot one) {
		if (one.getX() * one.getY() == 0 || (one.getX() + 1) == COL
				|| (one.getY() + 1) == ROW) {
			return true;
		}
		return false;
	}

	private Dot getNeghbourDot(Dot one, int direction) {

		switch (direction) {
		case 1:
			return getDot(one.getX() - 1, one.getY());
		case 2:
			if (one.getY() % 2 == 0) {
				return getDot(one.getX() - 1, one.getY() - 1);
			} else {
				return getDot(one.getX(), one.getY() - 1);
			}
		case 3:
			if (one.getY() % 2 == 0) {
				return getDot(one.getX(), one.getY() - 1);
			} else {
				return getDot(one.getX() + 1, one.getY() - 1);
			}
		case 4:
			return getDot(one.getX() + 1, one.getY());
		case 5:
			if (one.getY() % 2 == 0) {
				return getDot(one.getX(), one.getY() + 1);
			} else {
				return getDot(one.getX() + 1, one.getY() + 1);
			}
		case 6:
			if (one.getY() % 2 == 0) {
				return getDot(one.getX() - 1, one.getY() + 1);
			} else {
				return getDot(one.getX(), one.getY() + 1);
			}

		default:
			return null;
		}

	}

	public int getDistance(Dot one, int dir) {
		int distance = 0;
		if (isAtEdge(one)) {
			return 1;
		}
		Dot oriDot = one;
		Dot nexDot;
		while (true) {
			nexDot = getNeghbourDot(oriDot, dir);
			if (nexDot.getStatus() == Dot.STATUS_ON) {
				return distance * -1;
			}

			if (isAtEdge(nexDot)) {
				distance++;
				return distance;
			}
			distance++;
			oriDot = nexDot;
		}

	}

	private void move() {
		if (isAtEdge(Cat)) {
			lose();
			return;
		}

		Vector<Dot> avalibleVector = new Vector<Dot>();
		Vector<Dot> positveDots = new Vector<Dot>();
		HashMap<Dot, Integer> al = new HashMap<Dot, Integer>();
		for (int i = 1; i <= 6; i++) {
			Dot oneDot = getNeghbourDot(Cat, i);
			if (oneDot.getStatus() == Dot.STATUS_OFF) {
				avalibleVector.add(oneDot);
				System.out.println("方向" + i + "可用的点：" + oneDot + "边界距离"
						+ getDistance(oneDot, i));
				al.put(oneDot, i);
				if (getDistance(oneDot, i) > 0) {
					positveDots.add(oneDot);
				}
			}
		}
		if (avalibleVector.size() == 0) {
			win();
		} else if (avalibleVector.size() == 1) {
			moveto(avalibleVector.get(0));
		} else {
			int min = 1000;
			int max = 0;
			Dot best = null;

			if (positveDots.size() > 0) { // 存在直达边界的点
				System.out.println("到边界！");
				for (int i = 0; i < positveDots.size(); i++) {
					int a = getDistance(positveDots.get(i),
							al.get(positveDots.get(i)));
					if (a <= min) {
						min = a;
						best = positveDots.get(i);
					}
				}
			} else {
				System.out.println("躲路障！可用点有"+avalibleVector.size());
				for (int i = 0; i < avalibleVector.size(); i++) {
					int a = getDistance(avalibleVector.get(i),
							al.get(avalibleVector.get(i)));
					System.out.println("距离a="+a+"方向："+al.get(avalibleVector.get(i)));
					if (a<= max) {
						max = a;
						best = avalibleVector.get(i);
					    System.out.println("best dot:"+best);
					}
				}
				
			//	best = avalibleVector.get(0);

			}

			moveto(best);
			System.out.println("移动到：" + best);

		}

	}

	private void moveto(Dot one) {

		one.setStatus(Dot.STATUS_IN);
		getDot(Cat.getX(), Cat.getY()).setStatus(Dot.STATUS_OFF);
		Cat.setXY(one.getX(), one.getY());

	}

	private void lose() {
		Toast.makeText(getContext(), "Sorry,You lose!", Toast.LENGTH_SHORT)
				.show();
	}

	private void win() {
		Toast.makeText(getContext(), "Congratuions, You win!",
				Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() == MotionEvent.ACTION_UP) {
			int x, y;
			y = (int) event.getY() / WIDTH;
			if (y % 2 == 0) {
				x = (int) event.getX() / WIDTH;
			} else {
				x = (int) (event.getX() - WIDTH / 2) / WIDTH;
			}

			if (x < COL && y < ROW) {
				Dot one = getDot(x, y);
				if (one.getStatus() == Dot.STATUS_OFF) {
					one.setStatus(Dot.STATUS_ON);
					move();
				}
			} else {
				initgame();
			}

			redraw();

			System.out.println(String.format("你按下了 x=%d，y=%d", x, y));

		}
		return true;
	}

}
