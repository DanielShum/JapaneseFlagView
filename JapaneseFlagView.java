/**
 * Author Matin Shum
 * 我听说世界上有一种代码。它没有bug。它只能够一直运行呀一直运行。累了就在手机里睡觉。
 * 这种代码一辈子只出一次错，那就是服务宕掉的时候。－－ 啊甘（美国地产大亨）
 */


package com.daililol.moody.customized.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


@SuppressLint("ClickableViewAccessibility") 
public class JapaneseFlagView extends View implements OnTouchListener {
	
	
	//super head
	public Callback callback;
	private float density = getContext().getResources().getDisplayMetrics().density;
	
	//view measure
	//private boolean init_button = false;
	private boolean is_measured = false;
	private int VH = 0;
	private int VW = 0;
	private int MINSIZE = (int)(48.0f * density);
	
	//public draw
	private Paint paint = new Paint();
	private Paint bg_paint = new Paint();
	private boolean can_record = false;
	private long draw_frequency = 10;
	private int draw_step = 1;
	private float current_size;
	private int current_draw_item = 0;
	private boolean motion_event_outside = false;
	private boolean set_bg_color = false;
	
	
	
	//1st action -- start recording
	private Timer firstTimer;
	private ThisTimerTask firstTimerTask;
	private ArrayList<Float> firstList;
	private long firstDuration = 300;
	private int firstDistination = (int)(32 * density);
	private int fristCancelDuration = 60;
	
	
	//2nd action -- cancel first action if hold duration too short
	private ArrayList<Float> secondList;
	private Timer secondTimer;
	private ThisTimerTask secondTimerTask;
	
	//3rd action -- release recording
	private Timer thirdTimer;
	private ThisTimerTask thirdTimerTask;
	private ArrayList<Map<String, Object>> thirdList;
	
	//4rd action -- move outside
	private Timer fourTimer;
	private ThisTimerTask fourTimerTask;
	private ArrayList<Float> fourList;
	
	
	//5th action -- move inside
	private Timer fifthTimer;
	private ThisTimerTask fifthTimerTask;
	private ArrayList<Float> fifthList;

	
	public JapaneseFlagView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
		
	}
	
	public JapaneseFlagView(Context context, AttributeSet attr) {
		super(context, attr);
		// TODO Auto-generated constructor stub
		initializate();
	}
	
	private void initializate(){
		//this.setClickable(true);
		this.setClickable(true);
		this.setOnTouchListener(this);
		paint.setAntiAlias(true);
		paint.setColor(0xffff0000);
		bg_paint.setAntiAlias(true);
		bg_paint.setColor(0xffff0000);
	}

	
	
	
	/**
	 * draw_frequency 10
	 * first action
	 * firstTimer
	 * firstTimerTask
	 * firstList
	 * firstDuration 300
	 * firstDistination 16
	 */
	public void runFirstAction(){
		
		stopAllAction();
		
		paint.setColor(0XFFFF0000);  //SHOULD SET RED COLOR
		
		if (firstList == null || firstList.size() == 0){
			
			firstList = new ArrayList<Float>();
			int draws = (int)(firstDuration / draw_frequency);
			float each_dis = firstDistination / (float)draws;
			float current_draw = (float)VH;
			for (int i =0; i < draws; i++){
				current_draw -= each_dis;
				firstList.add(current_draw);
				/*Log.v("", 
						"first dis: " + firstDistination
						+"  draws:" + draws + ""
						+"  each_dis:" + each_dis +""
						+"  current_draw" + current_draw + "");*/
			}
			
			
		}
		current_draw_item = 0;
		firstTimer  = new Timer();
		firstTimerTask = new ThisTimerTask(handler, 1);
		firstTimer.schedule(firstTimerTask, 0, draw_frequency);
		
		/**
		 * draws: 300 / 10 = draw 30 times
		 * each draw: 16 / 30 = each time draw 0.5 dp
		 */
		
		
	}
	
	public void stopFirstAction(){
		if (firstTimerTask != null) firstTimerTask.cancel();
		if (firstTimer != null) firstTimer.cancel();
		firstTimerTask = null;
		firstTimer =  null;
	}
	
	
	
	/**
	 * cancel recording
	 */
	public void runSecondAction(){
		
		stopAllAction();

		paint.setColor(0XFFFF0000);  //SHOULD SET RED COLOR
		
		
		secondList = new ArrayList<Float>();
		
		//calculate the size change
		float distination = (float)VH - current_size;
		if (distination < 0) distination = 0;
		
		//calculate draw times
		int duration = (int)((float)fristCancelDuration / (float)firstDistination * distination);
		int draws = (int)((float)duration / (float)draw_frequency);
		if (draws < 1) draws = 1;
		
		
		Log.v("opening", "duration: " + duration +
				"  distination: " + distination
				+"  draws: " + draws + "");
		
		//create animation path
		float each_dis = distination / (float)draws;
		float current_draw = current_size;
		for (int i =0; i < draws; i++){
			current_draw += each_dis;
			secondList.add(current_draw);
			Log.v("", 
					"distination: " + distination
					+"  draws: " + draws + ""
					+"  each_dis: " + each_dis +""
					+"  current_draw: " + current_draw + "");
		}
		
		
		/**
		 * duration = 60
		 * draws: 60 / 10 = draw 6 times
		 */
		
		current_draw_item = 0;
		secondTimer  = new Timer();
		secondTimerTask = new ThisTimerTask(handler, 2);
		secondTimer.schedule(secondTimerTask, 0, draw_frequency);
		
	
		
		
	}
	
	public void stopSecondAction(){
		if (secondTimerTask != null) secondTimerTask.cancel();
		if (secondTimer != null) secondTimer.cancel();
		secondTimerTask = null;
		secondTimer =  null;
	}
	
	
	/**
	 * release record
	 */
	public void runThirdAction(){
		
		stopAllAction();
		paint.setColor(0XFFFF0000);  //SHOULD SET RED COLOR
		
		
		current_draw_item = 0;
		float changes[][] = new float[][]{
				new float[]{16.0f * density, VH, 80},
				new float[]{16.0f * density, VH, 80},
				new float[]{VH, (float)VH - ((float)firstDistination / 2.0f), 96},
				new float[]{(float)VH - ((float)firstDistination / 2.0f), VH, 160},
		};
		
		//Log.v("l", changes.toString());
		if (thirdList == null || thirdList.size() == 0){
			
			thirdList = new ArrayList<Map<String, Object>>();
			
			
			for (int i = 0; i < changes.length; i ++){
				float change[] = changes[i];
				float destination = Math.abs(change[0] - change[1]);
				int draw_times = (int)(change[2] / draw_frequency);
				float each_draw = (destination / (float)draw_times);
				float current_draw = change[0];
				
				int color = 0xffffffff;
				int bg_color = 0xffff0000;
			
				if (i == 0) {
					color = 0xffffffff;
					bg_color = 0xffff0000;
				}else{
					color = 0xffff0000;
					bg_color = 0xffffffff;
				}
				
				for (int c = 0; c < draw_times; c++){
					
					if (change[0] < change[1]){
						//20 - 100
						current_draw += each_draw;
					}else{
						//100 - 20
						current_draw -= each_draw;
					}
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("radus", current_draw);
					map.put("color", color);
					map.put("bg_color", bg_color);
					
					thirdList.add(map);
					
					
					Log.v("current_draw", current_draw + "");
				}
				
				
				/** 
				 * destination: (20 - 100) = 80
				 * draw times: 200 / 20 = 10
				 * each time draw: 80 / 10
				 */

			}
		}
		
		thirdTimer = new Timer();
		thirdTimerTask = new ThisTimerTask(handler, 3);
		thirdTimer.schedule(thirdTimerTask, 0, draw_frequency);
	}
	
	
	public void stopThirdAction(){
		if (thirdTimerTask != null) thirdTimerTask.cancel();
		if (thirdTimer != null) thirdTimer.cancel();
		thirdTimerTask = null;
		thirdTimer =  null;
	}
	
	/**
	 * move outside
	 */
	public void runFourAction(){
		
		stopAllAction();
		paint.setColor(0XFF999999);  //SHOULD SET WHITE COLOR
		
		
		current_draw_item = 0;
		float changes[][] = new float[][]{
				new float[]{(float)VH - firstDistination, VH, 40},
				new float[]{VH, (float)VH - ((float)firstDistination / 2.0f), 60},
				new float[]{(float)VH - ((float)firstDistination / 2.0f), VH, 90},
		};
		
		//Log.v("l", changes.toString());
		if (fourList == null || fourList.size() == 0){
			
			fourList = new ArrayList<Float>();
			
			
			for (int i = 0; i < changes.length; i ++){
				float change[] = changes[i];
				float destination = Math.abs(change[0] - change[1]);
				int draw_times = (int)(change[2] / draw_frequency);
				float each_draw = (destination / (float)draw_times);
				float current_draw = change[0];
				
				for (int c = 0; c < draw_times; c++){
					
					if (change[0] < change[1]){
						//20 - 100
						current_draw += each_draw;
					}else{
						//100 - 20
						current_draw -= each_draw;
					}
					fourList.add(current_draw);
					//Log.v("current_draw", current_draw + "");
				}
				
				
				/** 
				 * destination: (20 - 100) = 80
				 * draw times: 200 / 20 = 10
				 * each time draw: 80 / 10
				 */

			}
		}
		
		fourTimer = new Timer();
		fourTimerTask = new ThisTimerTask(handler, 4);
		fourTimer.schedule(fourTimerTask, 0, draw_frequency);
	}
	
	
	public void stopFourAction(){
		if (fourTimerTask != null) fourTimerTask.cancel();
		if (fourTimer != null) fourTimer.cancel();
		fourTimerTask = null;
		fourTimer =  null;
	}
	
	
	/**
	 * move inside
	 */
	public void runFifthAction(){
		
		stopAllAction();
		paint.setColor(0XFFFF0000);  //SHOULD SET RED COLOR
		
		
		current_draw_item = 0;
		float changes[][] = new float[][]{
				new float[]{VH, (float)VH - firstDistination - (8 * density), 40},
				new float[]{(float)VH - firstDistination - (8 * density), (float)VH - firstDistination, 90},
		};
		
		//Log.v("l", changes.toString());
		if (fifthList == null || fifthList.size() == 0){
			
			fifthList = new ArrayList<Float>();
			
			
			for (int i = 0; i < changes.length; i ++){
				float change[] = changes[i];
				float destination = Math.abs(change[0] - change[1]);
				int draw_times = (int)(change[2] / draw_frequency);
				float each_draw = (destination / (float)draw_times);
				float current_draw = change[0];
				
				for (int c = 0; c < draw_times; c++){
					
					if (change[0] < change[1]){
						//20 - 100
						current_draw += each_draw;
					}else{
						//100 - 20
						current_draw -= each_draw;
					}
					fifthList.add(current_draw);
				}
				
				
				/** 
				 * destination: (20 - 100) = 80
				 * draw times: 200 / 20 = 10
				 * each time draw: 80 / 10
				 */

			}
		}
		
		fifthTimer = new Timer();
		fifthTimerTask = new ThisTimerTask(handler, 5);
		fifthTimer.schedule(fifthTimerTask, 0, draw_frequency);
	}
	
	
	public void stopFifthAction(){
		if (fifthTimerTask != null) fifthTimerTask.cancel();
		if (fifthTimer != null) fifthTimer.cancel();
		fifthTimerTask = null;
		fifthTimer =  null;
	}
	
	public void stopAllAction(){
		stopFirstAction();
		stopSecondAction();
		stopThirdAction();
		stopFourAction();
		stopFifthAction();
	}
	
	
	public float getNextRadus(){
		
		ArrayList<?> list = null;
		if (draw_step == 1){
			list = firstList;
		}else if(draw_step == 2){
			list = secondList;
		}else if(draw_step == 3){
			list = thirdList;
		}else if(draw_step == 4){
			list = fourList;
		}else if(draw_step == 5){
			list = fifthList;
		}
		
		
		
		if (list == null || list.size() == 0) return 0;
		
		if (current_draw_item > list.size() - 1) {
			current_draw_item = list.size() -1;
			
			//if first action is perform completely then should start recording
			if (draw_step == 1){
				if (callback != null) callback.shouldStartRecord();
				can_record = true;
			}
			
			stopAllAction();
		}
		
		float radus = 0;
		Object bj = list.get(current_draw_item);
		
		if (bj instanceof Map){
			radus = (Float) ((Map) bj).get("radus");
			if (((Map) bj).containsKey("color")) paint.setColor((Integer) ((Map) bj).get("color"));
			if (((Map) bj).containsKey("bg_color")) {
				bg_paint.setColor((Integer) ((Map) bj).get("bg_color"));
				set_bg_color  = true;
			}else{
				set_bg_color  = false;
			}
		}else{
			radus = Float.parseFloat(list.get(current_draw_item).toString());
			set_bg_color  = false;
		}
		
		Log.v("radus", "radus:" + radus + "   color: " + paint.getColor());
		
		current_size = radus;
		current_draw_item ++;
		
		return radus;
		
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		float postion = (float)VH / 2.0f;
		
		/*if (!init_button){
			canvas.drawCircle(postion, postion, postion, paint);
			init_button = true;
			return;
		}*/
		
		float radus = 0;
		if (getNextRadus() == 0) {
			radus = current_size / 2;
		}else{
			radus = getNextRadus() / 2.0f;
		}
		
		if (set_bg_color){
			float bg_size = ((float)VH - (float)firstDistination) / 2;
			canvas.drawCircle(postion, postion, bg_size, bg_paint);
		}
		canvas.drawCircle(postion, postion, radus, paint);
		
		//Log.v("getNextRadus()", getNextRadus() + "");
		
		
	}
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (is_measured) return;
		VW = MeasureSpec.getSize(widthMeasureSpec);
		VH = MeasureSpec.getSize(heightMeasureSpec);
		
		if (VW < MINSIZE || VH < MINSIZE){
			VW = MINSIZE;
			VH = MINSIZE;
		}
		
		if (VH != VW){
			if (VH > VW){
				VW = VH;
			}else{
				VH = VW;
			}
		}
		
		current_size = VH;
		setMeasuredDimension(VW, VH);
		is_measured = true;
		
	}
	
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//Log.v("action", event.getAction() + "");
		
		switch (event.getAction()){
		
		case MotionEvent.ACTION_DOWN:
			motion_event_outside = false;
			runFirstAction();
			if (callback != null) callback.onTouch();
			break;
		
		case MotionEvent.ACTION_UP:
			if (can_record){
				
				if (motion_event_outside){
					runSecondAction();
					if (callback != null) callback.shouldCancel();
				}else{
					runThirdAction();
					if (callback != null) callback.shouldSendRecord();
				}
				
			}else{
				runSecondAction();
				if (callback != null) callback.didNotStart();
			}
			
			can_record = false;
			break;
			
		case MotionEvent.ACTION_MOVE:
			if (event.getX() < 1 || event.getY() < 1 || event.getX() > VH || event.getY() > VH){
				
				
				
				if (!motion_event_outside){
					runFourAction();
					if (callback != null) callback.onMoveOutside();
					motion_event_outside = true;
				}
				
			}else{
				
				if (motion_event_outside){
					runFifthAction();
					if (callback != null) callback.onMoveInside();
					motion_event_outside = false;
				}
			}
			
			break;
		}
		
		return false;
	}

	
	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			draw_step = msg.what;
			invalidate();
		}
	};
	
	
	public void setCallback(Callback callback){
		this.callback = callback;
	}
	
	public interface Callback{
		public void onTouch();
		public void onMoveOutside();
		public void onMoveInside();
		public void didNotStart();
		public void shouldCancel();
		public void shouldStartRecord();
		public void shouldSendRecord();
		
	}
	
	
	class ThisTimerTask extends TimerTask{
		
		Handler mHandler;
		int mWhat;
		
		ThisTimerTask(Handler handler, int what){
			mHandler = handler;
			mWhat = what;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(mWhat);
			
		}
	}


	
}
