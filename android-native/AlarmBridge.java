package com.morganello98.planner;

import android.content.Intent;
import android.content.ActivityNotFoundException;
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
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false);

        // Use the Activity directly: resolveActivity() can return null on Android 11+
        // because package visibility hides matching clock apps from this package.
        try {
            getActivity().startActivity(intent);
            call.resolve();
        } catch (ActivityNotFoundException e) {
            call.reject("Android не нашёл приложение для создания будильников. " +
                    "Убедитесь, что на устройстве установлено приложение Часы.", e);
        } catch (SecurityException e) {
            call.reject("Отказано в доступе к приложению Часы. " +
                    "Это может быть вызвано отсутствием queries в AndroidManifest (Android 11+). " +
                    "Ошибка: " + e.getMessage(), e);
        } catch (Exception e) {
            call.reject("Не удалось открыть экран создания будильника. " +
                    "Тип ошибки: " + e.getClass().getSimpleName() + ". " +
                    "Сообщение: " + e.getMessage(), e);
        }
    }
}
