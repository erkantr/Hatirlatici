package com.bysoftware.uyku;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button olustur;
    ImageView settings,power,hadisoner,videooner,zoom,toplanti,add_alarm;
    TextView hadis, text;
    FirebaseAuth mAuth;
    String video1, video2;
    String timeString;
    String dayString,monthString,YearString;

    private MaterialTimePicker picker,picker2,picker3;
    private Calendar calendar,calendar2,calendar3;
    private AlarmManager alarmManager, alarmManager2,alarmManager3;
    private PendingIntent pendingIntent;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String MyPREFERENCES2 = "MyPrefs2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        olustur = findViewById(R.id.olustur);
        hadis = findViewById(R.id.hadis);
        settings = findViewById(R.id.settings);
        text = findViewById(R.id.text);
        power = findViewById(R.id.power);
        hadisoner = findViewById(R.id.hadisoner);
        videooner = findViewById(R.id.videooner);
        toplanti = findViewById(R.id.toplanti);
        add_alarm = findViewById(R.id.add_alarm);
        zoom = findViewById(R.id.zoom);
        verileriAl();
        createNotificationChannel();
        createNotificationChannel2();
        mAuth = FirebaseAuth.getInstance();
        com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view2);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        String videoId = video2;
                        initializedYouTubePlayer.cueVideo(videoId, 0);
                    }
                });
            }
        }, true);

        com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView youTubePlayerView2 = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView2);

        youTubePlayerView2.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        String videoId = video1;
                        initializedYouTubePlayer.cueVideo( videoId, 0);
                    }
                });
            }
        }, true);

        SharedPreferences sharedpreferences1 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String durum = sharedpreferences1.getString("durum", "");
        if (durum == null || durum == "0") {
            text.setText("Hatırlatıcı Oluştur");
            olustur.setBackgroundResource(R.drawable.button);
            power.setElevation(6);
            power.setImageResource(R.drawable.powerorange);
        } else if (durum.equals("1")) {
            text.setText("Hatırlatıcıyı Kapat");
            olustur.setBackgroundResource(R.drawable.button2);
            power.setElevation(0);
            power.setImageResource(R.drawable.power2);
        } else {
            text.setText("Hatırlatıcı Oluştur");
            olustur.setBackgroundResource(R.drawable.button);
            power.setElevation(6);
            power.setImageResource(R.drawable.powerorange);
        }

        add_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedpreferences2 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String durum2 = sharedpreferences2.getString("durum2","");
                if (durum2 == null || durum2.equals("0")){
                    openEkDialog();
                } else if (durum2.equals("1")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Ek Hatırlatıcı Kapatılıyor");
                    builder.setMessage("Ek Hatırlatıcıyı kapatmak istediğinize emin misiniz?");
                    //evet seçeneğine tıklanırsa
                    builder.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelAlarm2();
                            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("durum2", "0");
                            editor.commit();
                        }
                    });
                    builder.setPositiveButton("Hayır",null);
                    builder.show();
                } else {
                    openEkDialog();
                }
            }
        });

        toplanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openToplantiDialog();
            }
        });

        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + video1));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } catch (ActivityNotFoundException e) {

                    // youtube is not installed.Will be opened in other available apps

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/watch?v=" + video2));
                    startActivity(i);
                }
            }
        });

        hadisoner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOneriDialog("Hadis Öner", "Lütfen önermek istediğiniz hadisi aşağıya yazın","Hadis Öner");
            }
        });

        videooner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOneriDialog("Video Öner", "Lütfen önermek istediğiniz video ismini veya linkini aşağıya yazın","Video Öner");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        olustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedpreferences1 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String durum = sharedpreferences1.getString("durum", "");
                if (durum == null || durum.equals("0")) {
                    showTimePicker();
                } else if (durum.equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Hatırlatıcı Kapatılıyor");
                    builder.setMessage("Hatırlatıcıyı kapatmak istediğinize emin misiniz?");
                    //evet seçeneğine tıklanırsa
                    builder.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelAlarm();
                            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("durum", "0");
                            editor.commit();
                            text.setText("Hatırlatıcı Oluştur");

                            power.setElevation(6);
                            power.setImageResource(R.drawable.powerorange);
                            olustur.setBackgroundResource(R.drawable.button);
                        }
                    });
                    builder.setPositiveButton("Hayır",null);
                    builder.show();

                } else {
                    showTimePicker();
                }
            }
        });


        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("İzin Gerekli");
                builder.setCancelable(false);
                builder.setMessage("Diğer uygulamaların üzerinde görüntüleme izni vermeniz gerekiyor");
                //evet seçeneğine tıklanırsa
                builder.setNegativeButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    }
                });
                //hayır seçeneğine tıklanırsa uyarı mesajını kapatıyoruz
                builder.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.show();
            }
        } else {
            Intent intent = new Intent(this, Service.class);
            startService(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {

                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                        startActivity(intent);
            }
        }
    }

    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Hatırlatıcı Zamanı Seçin")
                .build();

        picker.show(getSupportFragmentManager(), "bysoftware");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                setAlarm();
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("durum", "1");
                editor.commit();
                text.setText("Hatırlatıcıyı Kapat");
                olustur.setBackgroundResource(R.drawable.button2);
                power.setElevation(0);
                power.setImageResource(R.drawable.power2);
            }
        });


    }

    private void showTimePicker3() {

        picker3 = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Hatırlatıcı Zamanı Seçin")
                .build();

        picker3.show(getSupportFragmentManager(), "bysoftware2");

        picker3.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar3 = Calendar.getInstance();
                calendar3.set(Calendar.HOUR_OF_DAY, picker3.getHour());
                calendar3.set(Calendar.MINUTE, picker3.getMinute());
                calendar3.set(Calendar.SECOND, 0);
                calendar3.set(Calendar.MILLISECOND, 0);
                setAlarm3();
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("durum2", "1");
                editor.commit();
            }
        });


    }


    private void showCalendarPicker(){
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Tarih Seçin");
        MaterialDatePicker<Long> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());
        picker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<? super Long>) view -> {
            dayString = DateFormat.format("dd", new Date(view)).toString();
            monthString = DateFormat.format("MM", new Date(view)).toString();
            YearString = DateFormat.format("yyyy", new Date(view)).toString();
            showTimePicker2();
            //calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            //calendar2.setTimeInMillis(view);
            //Toast.makeText(this, dateString, Toast.LENGTH_SHORT).show();
        });
    }

    private void showTimePicker2() {

        picker2 = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Hatırlatıcı Zamanı Seçin")
                .build();



        picker2.show(getSupportFragmentManager(), "bysoftware");

        picker2.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.YEAR, Integer.parseInt(YearString));
                calendar2.set(Calendar.MONTH, Integer.parseInt(monthString));
                calendar2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayString));
                calendar2.set(Calendar.HOUR_OF_DAY, picker2.getHour());
                calendar2.set(Calendar.MINUTE, picker2.getMinute());
                calendar2.set(Calendar.SECOND, 0);
                calendar2.set(Calendar.MILLISECOND, 0);
                timeString = picker2.getHour() + ":" +picker2.getMinute();
                setAlarm2();
                openToplantiDialog();
            }
        });


    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("bysoftware", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void createNotificationChannel2() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "foxandroidReminderChannel2";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("bysoftware2", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void cancelAlarm() {

        Intent intent = new Intent(this, ReminderBroadcast.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (alarmManager == null) {

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Hatırlatıcı kapatıldı", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm2() {

        Intent intent = new Intent(this, ReminderBroadcast2.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (alarmManager3 == null) {

            alarmManager3 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        }
        alarmManager3.cancel(pendingIntent);
        Toast.makeText(this, "Hatırlatıcı kapatıldı", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderBroadcast.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Hatırlatıcı Oluşturuldu", Toast.LENGTH_SHORT).show();


    }

    private void setAlarm3() {

        alarmManager3 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderBroadcast2.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager3.setRepeating(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Hatırlatıcı Oluşturuldu", Toast.LENGTH_SHORT).show();


    }

    private void setAlarm2() {

        alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderBroadcast.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
          //      AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager2.set(AlarmManager.RTC_WAKEUP,calendar2.getTime().getTime(),pendingIntent);

        Toast.makeText(this, "Hatırlatıcı Oluşturuldu", Toast.LENGTH_SHORT).show();


    }

    public void verileriAl() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://hatirlatici-a96f4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Veriler");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veriler veriler = snapshot.getValue(Veriler.class);
                hadis.setText(veriler.getHadis());
                video1 = veriler.getVideo1();
                video2 = veriler.getVideo2();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openEkDialog(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ek_alarm_dialog);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
            }

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            windowAttributes.gravity = Gravity.CENTER;
            window.setAttributes(windowAttributes);
            dialog.setCancelable(true);
            TextInputEditText baslik = dialog.findViewById(R.id.baslik);
            TextInputEditText mesaj = dialog.findViewById(R.id.mesaj);

        SharedPreferences sharedpreferences1 = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String baslik_text = sharedpreferences1.getString("baslik", "");
        String mesaj_text = sharedpreferences1.getString("mesaj", "");
                baslik.setText(baslik_text);
            mesaj.setText(mesaj_text);

            Button gonder = dialog.findViewById(R.id.gonder);
            Button iptal = dialog.findViewById(R.id.iptal);

            gonder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (baslik.getText() != null && mesaj.getText() != null) {

                        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("baslik", baslik.getText().toString());
                        editor.putString("mesaj", mesaj.getText().toString());
                        editor.commit();
                        dialog.dismiss();
                        showTimePicker3();
                    } else {
                        Toast.makeText(MainActivity.this, "Tüm alanlar zorunludur!", Toast.LENGTH_SHORT).show();
                    }
                    }
            });

            iptal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    public void openToplantiDialog(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.zoom_design_alert);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);
        TextInputEditText tarih = dialog.findViewById(R.id.tarih);
        TextInputEditText saat = dialog.findViewById(R.id.saat);

        if (timeString != null){
            saat.setText(timeString);
        }

        if (dayString != null){
            tarih.setText(dayString+"/"+monthString+"/"+YearString);
        }

        /*
            tarih.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCalendarPicker();
                    if (dateString != null){
                        saat.setText(dateString);
                    }
                }
            });

            saat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePicker2();
                    if (timeString != null){
                        saat.setText(timeString);
                    }
                }
            });

         */

        Button gonder = dialog.findViewById(R.id.gonder);
        Button iptal = dialog.findViewById(R.id.iptal);

        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showCalendarPicker();
            }
        });

        iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openOneriDialog(String metin1, String metin2, String hint){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.oneri_alert);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);
        EditText editText = dialog.findViewById(R.id.edittext);
        TextView onerimetin1 = dialog.findViewById(R.id.onerimetin1);
        TextView onerimetin2 = dialog.findViewById(R.id.onerimetin2);
        Button gonder = dialog.findViewById(R.id.gonder);
        Button iptal = dialog.findViewById(R.id.iptal);

        onerimetin1.setText(metin1);
        onerimetin2.setText(metin2);
        editText.setHint(hint);

        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MailSender.toast = "Öneriniz Gönderildi!";
                MailSender.title = "Gönderiliyor";
                MailSender.message = "Lütfen Bekleyiniz...";
                MailSender mg = new MailSender(MainActivity.this, "erkanm11t@gmail.com",
                        metin1 +"i" + " Talebi" , editText.getText().toString());
                // Mail Gönderme işlemini başlatıyoruz
                mg.execute();
                dialog.dismiss();
            }
        });

        iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    }