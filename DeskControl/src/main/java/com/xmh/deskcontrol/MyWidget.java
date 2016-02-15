package com.xmh.deskcontrol;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyWidget extends AppWidgetProvider{

	//向桌面上添加第一个控件时调用
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);

		context.startService(new Intent(context, MyService.class));
		Log.e("xmh","on");
	}

	//最后一个控件从桌面被删除
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);

		context.stopService(new Intent(context, MyService.class));
		Log.e("xmh", "off");
	}
}
