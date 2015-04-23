package com.zjk.wifiproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zjk.wifiproject.R;

public class A {

	public static void goOtherActivity(Context context, Class<?> cls) {
		context.startActivity(new Intent(context, cls));
		((Activity) context).overridePendingTransition(R.anim.activity_right_enter, R.anim.activity_left_exit);
	}

	public static void goOtherActivityFinish(Context context, Class<?> cls) {
		context.startActivity(new Intent(context, cls));
		((Activity) context).finish();
		((Activity) context).overridePendingTransition(R.anim.activity_right_enter, R.anim.activity_left_exit);
	}

	public static void goOtherActivity(Context context, Intent intent) {
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.activity_right_enter, R.anim.activity_left_exit);
	}

	public static void goOtherActivityFinish(Context context, Intent intent) {
		context.startActivity(intent);
		((Activity) context).finish();
		((Activity) context).overridePendingTransition(R.anim.activity_right_enter, R.anim.activity_left_exit);
	}
	

	public static void goOtherActivityFinishNoAnim(Context context, Class<?> cls) {
		context.startActivity(new Intent(context, cls));
		((Activity) context).finish();
		((Activity) context).overridePendingTransition(0, 0);
	}

	public static void finishSelf(Context context) {
		((Activity) context).finish();
		((Activity) context).overridePendingTransition(R.anim.activity_left_enter, R.anim.activity_right_exit);
	}

	public static void goOtherActivityBottomIn(Context context, Intent intent) {
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(0, R.anim.slide_in_from_bottom);
	}

	public static void goOtherActivityNoAnim(Context context, Intent intent) {
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(0, 0);
	}

	public static void finishSelfNoAnim(Context context) {
		((Activity) context).finish();
		((Activity) context).overridePendingTransition(0, 0);
	}

}
