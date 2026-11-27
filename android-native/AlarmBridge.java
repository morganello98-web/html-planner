package com.morganello98.planner;

import android.content.Intent;
import android.provider.AlarmClock;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "AlarmBridge")
public class AlarmBridge extends Plugin {
    @PluginMethod
    public void createAlarm(PluginCall call) {
        Integer hour = call.getInt("hour");
        Integer minute = call.getInt("minute");
        String label = call.getString("label", "Планировщик режима");

        if (hour == null || minute == null || hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            call.reject("Некорректное время будильника");
            return;
        }

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, label);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        if (intent.resolveActivity(getContext().getPackageManager()) == null) {
            call.reject("На устройстве не найдено приложение для создания будильников");
            return;
        }

        try {
            getContext().startActivity(intent);
            call.resolve();
        } catch (Exception e) {
            call.reject("Не удалось передать будильник приложению «Часы»", e);
        }
    }
}
