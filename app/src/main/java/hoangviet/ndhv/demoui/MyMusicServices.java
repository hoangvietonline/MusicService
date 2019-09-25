package hoangviet.ndhv.demoui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MyMusicServices extends Service implements MediaPlayer.OnErrorListener {
    public static final String DURATION_KEY = "duration_key";
    public static final String CURRENT_KEY = "current_key";
    public static final String POSITION_MUSIC_PLAY_KEY = "position_music_play_key";
    public static final String DURATION_MUSIC_AGAIN = "duration_music_again";
    public static final String SERVICE_POSITION_NOTIFICATION = "service_position_notification";
    public static final String MUSIC_LIST_SHUFFLE = "music_list_shuffle";
    public static final String MUSIC_LIST_UNSHUFFLE = "music_list_repeat";
    private static final String TAG = "MyMusicServices";
    private static final String CHANNEL_ID = "channel_id";
    public static MediaPlayer mMediaPlayer;
    private int oldPossition = 0;
    private int position = 0;
    private String repeatOne = "";
    private List<Music> musicList;
    private List<Music> musicListReplace;
    private CurrentTimeBroadcast broadcast;
    private RemoteViews notificationLayout;
    private Music music;

    public MyMusicServices() {

    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        Bitmap tmp;
        Rect srcRect, dstRect;
        float r = 50;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        output = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        if (width > height) {
            tmp = Bitmap.createScaledBitmap(bitmap, 100 * width / height, 100, false);
            int left = (tmp.getWidth() - tmp.getHeight()) / 2;
            int right = left + tmp.getHeight();
            srcRect = new Rect(left, 0, right, tmp.getHeight());
            dstRect = new Rect(0, 0, tmp.getHeight(), tmp.getHeight());
        } else {
            tmp = Bitmap.createScaledBitmap(bitmap, 100, 100 * height / width, false);
            int top = (tmp.getHeight() - tmp.getWidth()) / 2;
            int bottom = top + tmp.getWidth();
            srcRect = new Rect(0, top, tmp.getWidth(), bottom);
            dstRect = new Rect(0, 0, tmp.getWidth(), tmp.getWidth());
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(tmp, srcRect, dstRect, paint);
        return output;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicList = new ArrayList<>();
        musicListReplace = new ArrayList<>();
        addMusic();
        musicListReplace.addAll(musicList);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, int startId) {
        broadcast = new CurrentTimeBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CurrentTimeBroadcast.SEND_PLAY_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_NEXT_PLAY_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_SEEKBAR_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_PLAY_MP3_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_SHUFFLE_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_REPEAT_ONE_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_UN_SHUFFLE_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_REPEAT_PLAY_MUSIC_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcast, intentFilter);

        broadcast.setListener(new onClickBroadcast() {
            @Override
            public void onClickPlay(int position) {
                if (mMediaPlayer != null) {
                    if (oldPossition != position) {
                        stopMusic();
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                        mMediaPlayer.start();
                    } else {
                        if (mMediaPlayer.isPlaying()) {
                            pauseMusic();
                        } else {
                            resumeMusic();
                        }
                    }

                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                }
                oldPossition = position;
                music = musicList.get(position);
                MyMusicServices.this.position = position;
                mediaPlayerCurrentTime();
                initNotifyMusic(position);
            }

            @Override
            public void onClickPreviousMusic(int position) {
                if (mMediaPlayer != null) {
                    if (position < 0) {
                        position = musicList.size() - 1;
                    }
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                }
                MyMusicServices.this.position = position;
                mediaPlayerCurrentTime();
                initNotifyMusic(position);
                music = musicList.get(position);
            }

            @Override
            public void onClickNextMusic(int position) {
                if (mMediaPlayer != null) {
                    if (position > musicList.size() - 1) {
                        position = 0;
                    }
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                }
                mediaPlayerCurrentTime();
                MyMusicServices.this.position = position;
                Log.d(TAG, "onClickNextMusic:next music " + position);
                initNotifyMusic(position);
                music = musicList.get(position);

            }

            @Override
            public void onClickPlayMusic(int position) {
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        pauseMusic();
                    } else {
                        resumeMusic();
                    }
                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                }
                MyMusicServices.this.position = position;
                mediaPlayerCurrentTime();
                initNotifyMusic(position);
                music = musicList.get(position);
            }

            @Override
            public void seekBarChange(int seekBarChange) {
                mMediaPlayer.seekTo(seekBarChange);
            }

            @Override
            public void onClickPlayMp3(int position) {
                if (mMediaPlayer != null) {
                    if (oldPossition != position) {
                        stopMusic();
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                        mMediaPlayer.start();
                    }else {
                        if (!mMediaPlayer.isPlaying()){
                            mMediaPlayer.start();
                        }
                    }

                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                }
                oldPossition = position;
                MyMusicServices.this.position = position;
                initNotifyMusic(position);
                mediaPlayerCurrentTime();
                music = musicList.get(position);
            }

            @Override
            public void shufflePlayMusic() {
                Collections.shuffle(musicList);
                Log.d(TAG, "shufflePlayMusic: position shuffle " +position);
                Intent intentMusicShuffle = new Intent();
                intentMusicShuffle.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_MUSIC_SHUFFLE_PLAY_MUSIC);
                intentMusicShuffle.putParcelableArrayListExtra(MUSIC_LIST_SHUFFLE, (ArrayList<? extends Parcelable>) musicList);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentMusicShuffle);
            }

            @Override
            public void repeatOnePlayMusic(String oke, int pos) {
                repeatOne = oke;
                Log.d(TAG, "repeatOnePlayMusic: repeat one " + repeatOne);
                position = pos;
                Log.d(TAG, "repeatOnePlayMusic: repeat one position " + position);
            }

            @Override
            public void unShufflePlayMusic() {
                Intent intentUnShuffle = new Intent();
                intentUnShuffle.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_UN_SHUFFLE_PLAY_MUSIC);
                intentUnShuffle.putParcelableArrayListExtra(MUSIC_LIST_UNSHUFFLE, (ArrayList<? extends Parcelable>) musicListReplace);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentUnShuffle);
            }

            @Override
            public void repeatPlayMusic() {
                repeatOne = "";
            }


        });
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcast);
    }

    public void mediaPlayerCurrentTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentMediaPlayer = mMediaPlayer.getCurrentPosition();
                int durationMediaPlayer = mMediaPlayer.getDuration();
                final Intent intentCurrent = new Intent();
                intentCurrent.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_TIME_MEDIAPLAYER);
                intentCurrent.putExtra(CURRENT_KEY, currentMediaPlayer);
                intentCurrent.putExtra(DURATION_KEY, durationMediaPlayer);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentCurrent);

                // kiểm tra thời gian của bài hát khi hết bài --->next
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (repeatOne.equals("OKE")) {
                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), music.getFileSong());
                            Log.d(TAG, "onCompletion: position media " + position);
                            mMediaPlayer.start();
                            int durationMediaPlayer = mMediaPlayer.getDuration();
                            Intent intentPositionPlayMusic = new Intent();
                            intentPositionPlayMusic.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_POSITION_PLAY_MUSIC);
                            intentPositionPlayMusic.putExtra(POSITION_MUSIC_PLAY_KEY, position);
                            intentPositionPlayMusic.putExtra(DURATION_MUSIC_AGAIN, durationMediaPlayer);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentPositionPlayMusic);
                            initNotifyMusic(position);
                        } else {
                            position++;
                            if (position > musicList.size() - 1) {
                                position = 0;
                            }
                            if (mMediaPlayer.isPlaying()) {
                                mMediaPlayer.stop();
                            }
                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                            mMediaPlayer.start();
                            int durationMediaPlayer = mMediaPlayer.getDuration();
                            Intent intentPositionPlayMusic = new Intent();
                            intentPositionPlayMusic.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_POSITION_PLAY_MUSIC);
                            intentPositionPlayMusic.putExtra(POSITION_MUSIC_PLAY_KEY, position);
                            intentPositionPlayMusic.putExtra(DURATION_MUSIC_AGAIN, durationMediaPlayer);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentPositionPlayMusic);
                            initNotifyMusic(position);
                        }
                    }
                });

                handler.postDelayed(this, 500);

            }
        }, 100);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library`
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
        createNotificationChannel();
        notificationLayout = new RemoteViews(getPackageName(), R.layout.custum_notifymusic);
        notificationLayout.setTextViewText(R.id.txtMusicNameNotify, musicList.get(position).getMusicName());
        notificationLayout.setTextViewText(R.id.txtSingerNotify, musicList.get(position).getMusicSinger());
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                notificationLayout.setImageViewResource(R.id.btnPlayNotify, R.drawable.icon_pause);
            } else {
                notificationLayout.setImageViewResource(R.id.btnPlayNotify, R.drawable.icon_play);
            }
        }
        Intent intent = new Intent(getApplicationContext(), PlayMusicActivity.class);
        intent.putExtra(SERVICE_POSITION_NOTIFICATION, position);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentPrevious = new Intent(BroadcastMusic.BUTTON_PREVIOUS);
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(this, 1, intentPrevious, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent intentPlay = new Intent(BroadcastMusic.BUTTON_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this, 2, intentPlay, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent intentNext = new Intent(BroadcastMusic.BUTTON_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this, 3, intentNext, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icons8_music)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationLayout.setOnClickPendingIntent(R.id.btnPreviousNotify, pendingIntentPrevious);
        notificationLayout.setOnClickPendingIntent(R.id.btnPlayNotify, pendingIntentPlay);
        notificationLayout.setOnClickPendingIntent(R.id.btnNextNotify, pendingIntentNext);
        Glide.with(this)
                .asBitmap()
                .load(musicList.get(position).getMusicImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        notificationLayout.setImageViewBitmap(R.id.imgAvatarNotify, getCircleBitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        startForeground(1, builder);


    }

    private void addMusic() {
        musicList.add(new Music("Bạc Phận", "K-ICM ft. JACK", "https://cdn.tuoitre.vn/thumb_w/640/2019/6/19/jack-1560931851558668237008.jpg", R.raw.bac_phan));
        musicList.add(new Music("Đừng nói tôi điên", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.dung_noi_toi_dien));
        musicList.add(new Music("Em ngày xưa khác rồi", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.em_ngay_xua_khac_roi));
        musicList.add(new Music("Hồng Nhan", "Jack", "https://kenh14cdn.com/zoom/700_438/2019/4/16/520385336113309193300413143308017856937984n-15554316885891494708426-crop-15554316943631888232929.jpg", R.raw.hong_nhan_jack));
        musicList.add(new Music("Mây và núi", "The Bells", "https://www.pngkey.com/png/detail/129-1296419_cartoon-mountains-png-mountain-animation-png.png", R.raw.may_va_nui));
        musicList.add(new Music("Rồi người thương cũng hóa người dưng", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.roi_nguoi_thuong_cung_hoa_nguoi_dung));
    }

    public void pauseMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    public void resumeMusic() {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
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

    public interface onClickBroadcast {
        void onClickPlay(int pos);

        void onClickPreviousMusic(int position);

        void onClickNextMusic(int pos);

        void onClickPlayMusic(int pos);

        void seekBarChange(int seekBarChange);

        void onClickPlayMp3(int position);

        void shufflePlayMusic();

        void repeatOnePlayMusic(String oke, int position);

        void unShufflePlayMusic();

        void repeatPlayMusic();
    }

    static class CurrentTimeBroadcast extends BroadcastReceiver {

        public static final String SEND_PLAY_ACTION = "play_action";
        public static final String SEND_PREVIOUS_PLAY_MUSIC_ACTION = "previous_play_music_action";
        public static final String SEND_NEXT_PLAY_MUSIC_ACTION = "next_play_music_action";
        public static final String SEND_PLAY_PLAY_MUSIC_ACTION = "play_play_music_action";
        public static final String SEND_SEEKBAR_MUSIC_ACTION = "seeBar_music_action";
        public static final String SEND_PLAY_MP3_ACTION = "send_play_mp3_action";
        public static final String SEND_SHUFFLE_MUSIC_ACTION = "send_shuffle_music_action";
        public static final String SEND_REPEAT_ONE_ACTION = "send_repeat_one_action";
        public static final String SEND_UN_SHUFFLE_MUSIC_ACTION = "send_repeat_music_action";
        public static final String SEND_REPEAT_PLAY_MUSIC_ACTION = "send_repeat_play_music_action";
        private onClickBroadcast listener;

        void setListener(onClickBroadcast listener) {
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case SEND_PLAY_ACTION:
                    int pos = intent.getIntExtra(Mp3Activity.POS_KEY, 0);
                    listener.onClickPlay(pos);
                    break;
                case SEND_PREVIOUS_PLAY_MUSIC_ACTION:
                    int positionPrevious = intent.getIntExtra(PlayMusicActivity.POSITION_PREVIOUS_MUSIC_PLAY, 0);
                    listener.onClickPreviousMusic(positionPrevious);
                    break;
                case SEND_NEXT_PLAY_MUSIC_ACTION:
                    int positionNext = intent.getIntExtra(PlayMusicActivity.POSITION_NEXT_MUSIC_PLAY, 0);
                    listener.onClickNextMusic(positionNext);
                    break;
                case SEND_PLAY_PLAY_MUSIC_ACTION:
                    int positionPlay = intent.getIntExtra(PlayMusicActivity.POSITION_PLAY_MUSIC_PLAY, 0);
                    listener.onClickPlayMusic(positionPlay);
                    break;
                case SEND_SEEKBAR_MUSIC_ACTION:
                    int seekbarChange = intent.getIntExtra(PlayMusicActivity.SEEKBAR_PLAY_MUSIC, 0);
                    listener.seekBarChange(seekbarChange);
                    break;
                case SEND_PLAY_MP3_ACTION:
                    int positionMp3 = intent.getIntExtra(Mp3Activity.POSITION_PLAY_MP3, 0);
                    listener.onClickPlayMp3(positionMp3);
                    break;
                case SEND_SHUFFLE_MUSIC_ACTION:
                    listener.shufflePlayMusic();
                    break;
                case SEND_REPEAT_ONE_ACTION:
                    String repeatOne = intent.getStringExtra(PlayMusicActivity.OKE_REPEAT_ONE);
                    int positionRepeatOne = intent.getIntExtra(PlayMusicActivity.POSITION_REPEAT_ONE, 0);
                    listener.repeatOnePlayMusic(repeatOne, positionRepeatOne);
                    break;
                case SEND_UN_SHUFFLE_MUSIC_ACTION:
                    listener.unShufflePlayMusic();
                    break;
                case SEND_REPEAT_PLAY_MUSIC_ACTION:
                    listener.repeatPlayMusic();
                    break;

            }
        }
    }
}
