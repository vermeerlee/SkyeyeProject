package com.xmh.deskcontrol;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class MyWidget extends AppWidgetProvider{



	//向桌面上添加第一个控件时调用
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);

		context.startService(new Intent(context,MyService.class));
	}


	//最后一个控件从桌面被删除
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);

		context.stopService(new Intent(context, MyService.class));
		context.startService(new Intent(context, MyService.class));
	}
}
