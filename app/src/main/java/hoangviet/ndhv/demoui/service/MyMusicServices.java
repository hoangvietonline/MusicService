package hoangviet.ndhv.demoui.service;

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
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import hoangviet.ndhv.demoui.R;
import hoangviet.ndhv.demoui.common.Constant;
import hoangviet.ndhv.demoui.model.Music;
import hoangviet.ndhv.demoui.ui.playmusic.PlayMusicActivity;
import hoangviet.ndhv.demoui.utils.BitmapUtil;


public class MyMusicServices extends Service implements MediaPlayer.OnErrorListener {
    private static final String TAG = "MyMusicServices";
    private static final String CHANNEL_ID = "channel_id";
    public static MediaPlayer mMediaPlayer;
    private int oldPosition = 0;
    private int position = 0;
    private String repeatOne = "";
    private List<Music> musicList;
    private CurrentTimeBroadcast broadcast;
    private RemoteViews notificationLayout;
    private String confirmShuffleOke = "";

    public MyMusicServices() {
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
        addMusic();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, int startId) {
        broadcast = new CurrentTimeBroadcast();
        //get broadcast from Mp3Activity and PlayMusicActivity
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CurrentTimeBroadcast.SEND_PLAY_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_NEXT_PLAY_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_SEEK_BAR_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_PLAY_MP3_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_SHUFFLE_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_REPEAT_ONE_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_UN_SHUFFLE_MUSIC_ACTION);
        intentFilter.addAction(CurrentTimeBroadcast.SEND_REPEAT_PLAY_MUSIC_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcast, intentFilter);

        broadcast.setListener(new onClickBroadcast() {
            //button play Mp3Activity
            @Override
            public void onClickPlay(int position) {
                if (mMediaPlayer != null) {
                    if (oldPosition != position) {
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
                oldPosition = position;
                MyMusicServices.this.position = position;
                mediaPlayerCurrentTime();
                initNotifyMusic(position);
            }

            //button Previous PlayMusicActivity and notification
            @Override
            public void onClickPreviousMusic() {
                if (mMediaPlayer != null) {
                    if (confirmShuffleOke.equals("confirmShuffle")) {
                        randomPosition();
                    } else {
                        position--;
                        if (position < 0) {
                            position = musicList.size() - 1;
                        }
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                        }
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                        mMediaPlayer.start();
                    }
                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                }
                sendPosition();
            }

            //button Next PlayMusicActivity and notification
            @Override
            public void onClickNextMusic() {
                if (mMediaPlayer != null) {
                    if (confirmShuffleOke.equals("confirmShuffle")) {
                        randomPosition();
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
                    }
                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                }
                sendPosition();
            }

            //button Play PlayMusicActivity and notification
            @Override
            public void onClickPlayMusic() {
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
                mediaPlayerCurrentTime();
                initNotifyMusic(position);
            }

            //seekBar PlayMusicActivity
            @Override
            public void seekBarChange(int seekBarChange) {
                mMediaPlayer.seekTo(seekBarChange);
            }

            //clickItem Mp3Activity
            @Override
            public void onClickPlayMp3(int position) {
                if (mMediaPlayer != null) {
                    if (oldPosition != position) {
                        stopMusic();
                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                        mMediaPlayer.start();
                    } else {
                        if (!mMediaPlayer.isPlaying()) {
                            mMediaPlayer.start();
                        }
                    }

                } else {
                    mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                    mMediaPlayer.start();
                }
                oldPosition = position;
                MyMusicServices.this.position = position;
                initNotifyMusic(position);
                mediaPlayerCurrentTime();
            }

            //button shuffle
            @Override
            public void shufflePlayMusic(String confirm) {
                //String confirm shuffle
                confirmShuffleOke = confirm;
                Log.d(TAG, "shufflePlayMusic:confirm " + confirmShuffleOke);
            }

            //button repeatOne Music
            @Override
            public void repeatOnePlayMusic(String oke, int pos) {
                //String confirm repeat One
                repeatOne = oke;
                Log.d(TAG, "repeatOnePlayMusic: repeat one " + repeatOne);
                //position position repeat one music
                position = pos;
                Log.d(TAG, "repeatOnePlayMusic: repeat one position " + position);
            }

            // button unShuffle
            @Override
            public void unShufflePlayMusic() {
                confirmShuffleOke = "";

            }

            //button repeat
            @Override
            public void repeatPlayMusic() {
                repeatOne = "";
            }


        });
        return START_NOT_STICKY;
    }

    //random position when shuffle play music
    private void randomPosition() {
        while (true) {
            Random random = new Random();
            int ranPosition = random.nextInt(musicList.size() - 1);
            if (position != ranPosition) {
                position = ranPosition;
                break;
            }
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
        mMediaPlayer.start();
    }

    //send position by broadcast mediaPlayerBroadcast every click next,previous music
    private void sendPosition() {
        Intent intentPosition = new Intent();
        intentPosition.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_POSITION_MUSIC_PLAY);
        intentPosition.putExtra(Constant.POSITION_PLAY_MUSIC, position);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentPosition);
        mediaPlayerCurrentTime();
        initNotifyMusic(position);
    }

    //when destroy services , stop music and CurrentBroadcast
    @Override
    public void onDestroy() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcast);
    }

    // hàm lấy currentPosition overtime
    public void mediaPlayerCurrentTime() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentMediaPlayer = mMediaPlayer.getCurrentPosition();
                int durationMediaPlayer = mMediaPlayer.getDuration();
                final Intent intentCurrent = new Intent();
                intentCurrent.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_TIME_MEDIA_PLAYER);
                intentCurrent.putExtra(Constant.CURRENT_KEY, currentMediaPlayer);
                intentCurrent.putExtra(Constant.DURATION_KEY, durationMediaPlayer);
                //send position currentMediaplayer and duration 500/1000 update 1 lần
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentCurrent);
                // kiểm tra thời gian của bài hát khi hết bài --->next
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //repeat one music
                        if (repeatOne.equals("OKE")) {
                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                            Log.d(TAG, "onCompletion: position media " + position);
                            mMediaPlayer.start();
                            int durationMediaPlayer = mMediaPlayer.getDuration();
                            Intent intentPositionPlayMusic = new Intent();
                            intentPositionPlayMusic.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_MUSIC_PLAY_AGAIN);
                            intentPositionPlayMusic.putExtra(Constant.POSITION_MUSIC_PLAY_KEY, position);
                            intentPositionPlayMusic.putExtra(Constant.DURATION_MUSIC_AGAIN, durationMediaPlayer);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentPositionPlayMusic);
                            initNotifyMusic(position);
                        } else {
                            //shuffle music
                            if (confirmShuffleOke.equals("confirmShuffle")) {
                                while (true) {
                                    Random random = new Random();
                                    int ranPosition = random.nextInt(musicList.size() - 1);
                                    if (position != ranPosition) {
                                        position = ranPosition;
                                        break;
                                    }
                                }
                                mMediaPlayer = MediaPlayer.create(getApplicationContext(), musicList.get(position).getFileSong());
                                mMediaPlayer.start();
                                int durationMediaPlayer = mMediaPlayer.getDuration();
                                Intent intentPositionPlayMusic = new Intent();
                                intentPositionPlayMusic.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_MUSIC_PLAY_AGAIN);
                                intentPositionPlayMusic.putExtra(Constant.POSITION_MUSIC_PLAY_KEY, position);
                                intentPositionPlayMusic.putExtra(Constant.DURATION_MUSIC_AGAIN, durationMediaPlayer);
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
                                intentPositionPlayMusic.setAction(PlayMusicActivity.MediaPlayerBroadcast.SEND_MUSIC_PLAY_AGAIN);
                                intentPositionPlayMusic.putExtra(Constant.POSITION_MUSIC_PLAY_KEY, position);
                                intentPositionPlayMusic.putExtra(Constant.DURATION_MUSIC_AGAIN, durationMediaPlayer);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentPositionPlayMusic);
                                initNotifyMusic(position);
                            }

                        }
                    }
                });

                handler.postDelayed(this, 500);

            }
        }, 100);
    }

    private void createNotificationChannel() {
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
                notificationLayout.setImageViewResource(R.id.btnPlayNotify, R.drawable.ic_pause_white_48dp);
            } else {
                notificationLayout.setImageViewResource(R.id.btnPlayNotify, R.drawable.ic_pause_white_48dp);
            }
        }
        Intent intent = new Intent(getApplicationContext(), PlayMusicActivity.class);
        intent.putExtra(Constant.SERVICE_POSITION_NOTIFICATION, position);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //không tạo lại một activity khi back trở về lun màng hình đầu
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intentPrevious = new Intent(Constant.BUTTON_PREVIOUS);
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(this, 1, intentPrevious, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent intentPlay = new Intent(Constant.BUTTON_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this, 2, intentPlay, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent intentNext = new Intent(Constant.BUTTON_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this, 3, intentNext, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icons8_music)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        //pending intent to broadcast
        notificationLayout.setOnClickPendingIntent(R.id.btnPreviousNotify, pendingIntentPrevious);
        notificationLayout.setOnClickPendingIntent(R.id.btnPlayNotify, pendingIntentPlay);
        notificationLayout.setOnClickPendingIntent(R.id.btnNextNotify, pendingIntentNext);
        //chuyển image uri sang bitmap
        Glide.with(this)
                .asBitmap()
                .load(musicList.get(position).getMusicImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        notificationLayout.setImageViewBitmap(R.id.imgAvatarNotify, BitmapUtil.getCircleBitmap(resource));
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

        void onClickPreviousMusic();

        void onClickNextMusic();

        void onClickPlayMusic();

        void seekBarChange(int seekBarChange);

        void onClickPlayMp3(int position);

        void shufflePlayMusic(String confirm);

        void repeatOnePlayMusic(String oke, int position);

        void unShufflePlayMusic();

        void repeatPlayMusic();
    }

    public static class CurrentTimeBroadcast extends BroadcastReceiver {

        public static final String SEND_PLAY_ACTION = "play_action";
        public static final String SEND_PREVIOUS_PLAY_MUSIC_ACTION = "previous_play_music_action";
        public static final String SEND_NEXT_PLAY_MUSIC_ACTION = "next_play_music_action";
        public static final String SEND_PLAY_PLAY_MUSIC_ACTION = "play_play_music_action";
        public static final String SEND_SEEK_BAR_MUSIC_ACTION = "seeBar_music_action";
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
                    int pos = intent.getIntExtra(Constant.POS_KEY, 0);
                    listener.onClickPlay(pos);
                    break;
                case SEND_PREVIOUS_PLAY_MUSIC_ACTION:
                    listener.onClickPreviousMusic();
                    break;
                case SEND_NEXT_PLAY_MUSIC_ACTION:
                    listener.onClickNextMusic();
                    break;
                case SEND_PLAY_PLAY_MUSIC_ACTION:
                    listener.onClickPlayMusic();
                    break;
                case SEND_SEEK_BAR_MUSIC_ACTION:
                    int seekbarChange = intent.getIntExtra(Constant.SEEK_BAR_PLAY_MUSIC, 0);
                    listener.seekBarChange(seekbarChange);
                    break;
                case SEND_PLAY_MP3_ACTION:
                    int positionMp3 = intent.getIntExtra(Constant.POSITION_PLAY_MP3, 0);
                    listener.onClickPlayMp3(positionMp3);
                    break;
                case SEND_SHUFFLE_MUSIC_ACTION:
                    String confirmShuffle = intent.getStringExtra(Constant.CONFIRM_SHUFFLE_OKE);
                    listener.shufflePlayMusic(confirmShuffle);
                    break;
                case SEND_REPEAT_ONE_ACTION:
                    String repeatOne = intent.getStringExtra(Constant.OKE_REPEAT_ONE);
                    int positionRepeatOne = intent.getIntExtra(Constant.POSITION_REPEAT_ONE, 0);
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
