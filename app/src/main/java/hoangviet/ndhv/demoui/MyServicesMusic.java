package hoangviet.ndhv.demoui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MyServicesMusic extends Service implements MediaPlayer.OnErrorListener{
    public static MediaPlayer mMediaPlayer;
    private static final String TAG = "MyServicesMusic";
    private static final String CHANNEL_ID = "channel_id";
    List<Music> musicList;
    int length = 0;
    int position = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicList = new ArrayList<>();
        addMusic();
        initMediaPlayer(1);
        mMediaPlayer.setOnErrorListener(this);
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(true);
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                public boolean onError(MediaPlayer mp, int what, int extra) {
                    onError(mMediaPlayer, what, extra);
                    return true;
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = intent.getIntExtra(Mp3Activity.SERVICES_KEY, 0);
        initNotifyMusic(i);
        position = intent.getIntExtra(Mp3Activity.SERVICES_PLAY_KEY, 0);
        startMusic(position);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.stop();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void initNotifyMusic(int position) {
        String image = musicList.get(position).getMusicImage();
        Log.d(TAG, "initNotifyMusic:ádass " + image);

        createNotificationChannel();
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custum_notifymusic);
        notificationLayout.setTextViewText(R.id.txtMusicNameNotify, musicList.get(position).getMusicName());
        notificationLayout.setTextViewText(R.id.txtSingerNotify, musicList.get(position).getMusicSinger());
        Intent intent = new Intent(this, PlayMusicActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent intentPrevious = new Intent(BroadcastMusic.BUTTON_PREVIOUS);
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(this, 1, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intentPlay = new Intent(BroadcastMusic.BUTTON_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this, 2, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intentNext = new Intent(BroadcastMusic.BUTTON_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this, 3, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_music)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationLayout.setOnClickPendingIntent(R.id.btnPreviousNotify, pendingIntentPrevious);
        notificationLayout.setOnClickPendingIntent(R.id.btnPlayNotify, pendingIntentPlay);
        notificationLayout.setOnClickPendingIntent(R.id.btnNextNotify, pendingIntentNext);
        startForeground(1, builder);
    }

    private void addMusic() {
        musicList.add(new Music("Bạc Phận", "K-ICM ft. JACK", "https://sinhnhathot.com/uploads/celebrities/a692feab60b1f53d821150a87fafea46.png", R.raw.bac_phan));
        musicList.add(new Music("Đừng nói tôi điên", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.dung_noi_toi_dien));
        musicList.add(new Music("Em ngày xưa khác rồi", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.em_ngay_xua_khac_roi));
        musicList.add(new Music("Hồng Nhan", "Jack", "https://kenh14cdn.com/zoom/700_438/2019/4/16/520385336113309193300413143308017856937984n-15554316885891494708426-crop-15554316943631888232929.jpg", R.raw.hong_nhan_jack));
        musicList.add(new Music("Mây và núi", "The Bells", "https://photo-resize-zmp3.zadn.vn/w240_r1x1_jpeg/covers/7/6/764f16e%E2%80%A6_1286532325.jpg", R.raw.may_va_nui));
        musicList.add(new Music("Rồi người thương cũng hóa người dưng", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.roi_nguoi_thuong_cung_hoa_nguoi_dung));
    }
    private void initMediaPlayer(int position) {
        if (mMediaPlayer == null){
            mMediaPlayer = MediaPlayer.create(this, musicList.get(position).getFileSong());
        }
    }
    public void pauseMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                length = mMediaPlayer.getCurrentPosition();
            }
        }
    }

    public void resumeMusic() {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.seekTo(length);
                mMediaPlayer.start();
            }
        }
    }

    public void startMusic(int position) {
        if (mMediaPlayer == null){
            mMediaPlayer = MediaPlayer.create(getApplicationContext(),musicList.get(position).getFileSong());
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
    }

    public void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT).show();
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            } finally {
                mMediaPlayer = null;
            }
        }
        return false;
    }
}
