package hoangviet.ndhv.demoui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayMusicActivity extends AppCompatActivity implements BroadcastMusic.OnclickNotifyBroadcast {
    public static final String POSITION_PREVIOUS_MUSIC_PLAY = "position_previous_music_play";
    public static final String POSITION_PLAY_MUSIC_PLAY = "position_play_music_play";
    public static final String POSITION_NEXT_MUSIC_PLAY = "position_next_music_play";
    public static final String SEEKBAR_PLAY_MUSIC = "seekBar_play_music";
    private static final String TAG = "PlayMusicActivity";
    private CircleImageView imgAvatarPlayMusic;
    private TextView txtNamePlayMusic, txtSingerMusic, txtTimeRun, txtTimeSum;
    private ImageButton btnPlayMusic, btnPreviousMusic, btnNextMusic, btnRepeatMusic, btnShuffleMusic;
    private SeekBar seekBarPlayMusic;
    private List<Music> list;
    private int position;
    private Music music;
    private MediaPlayerBroadcast mediaPlayerBroadcast;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        animation = AnimationUtils.loadAnimation(this,R.anim.round_image);
        BroadcastMusic broadcastMusic = new BroadcastMusic();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastMusic.BUTTON_PREVIOUS);
        intentFilter.addAction(BroadcastMusic.BUTTON_PLAY);
        intentFilter.addAction(BroadcastMusic.BUTTON_NEXT);
        registerReceiver(broadcastMusic, intentFilter);
        broadcastMusic.setMyBroadcastCall(this);


        mediaPlayerBroadcast = new MediaPlayerBroadcast();
        IntentFilter intentFilterMediaPlayer = new IntentFilter();
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.SEND_TIME_MEDIAPLAYER);
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.SEND_POSITION_PLAY_MUSIC);
        LocalBroadcastManager.getInstance(this).registerReceiver(mediaPlayerBroadcast, intentFilterMediaPlayer);

        mediaPlayerBroadcast.setTakeMediaPlayer(new TakeMediaPlayer() {

            //function run time : 100/1000
            @Override
            public void takeCurrentMediaPlayer(final int current, int duration) {
                SimpleDateFormat formatMinute = new SimpleDateFormat("mm:ss");
                txtTimeRun.setText(formatMinute.format(current));
                seekBarPlayMusic.setProgress(current);
                setTimetotal(duration);
            }

            @Override
            public void playMusicAgain(int positionMusic, int duration) {
                initMediaPlayer(positionMusic);
                setTimetotal(duration);
                position++;
                if (position > list.size() - 1) {
                    position = 0;
                }
            }

        });
        bind();
        list = new ArrayList<>();
        addMusic();
        final Intent intent = getIntent();
        final Bundle bundle = intent.getBundleExtra(Mp3Activity.BUNDLE_KEY);
        if (bundle != null) {
            position = bundle.getInt(Mp3Activity.POSITION_KEY);
            music = bundle.getParcelable(Mp3Activity.MUSIC_KEY);
            music.setPlay(true);
        }


        if (music.isPlay() == true) {
            btnPlayMusic.setImageResource(R.drawable.icon_pause);
            music.setPlay(false);

        } else {
            btnPlayMusic.setImageResource(R.drawable.icon_play);
            music.setPlay(true);

        }

        initMediaPlayer(position);

//button shuffle array and repeat music
        btnShuffleMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.shuffle(list);
            }
        });
        btnRepeatMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnPreviousMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if (position < 0) {
                    position = list.size() - 1;
                }
                Intent intentPreviousMusic = new Intent();
                intentPreviousMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION);
                intentPreviousMusic.putExtra(POSITION_PREVIOUS_MUSIC_PLAY, position);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPreviousMusic);
                initMediaPlayer(position);
            }
        });
        btnNextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position > list.size() - 1) {
                    position = 0;
                }
                Intent intentNextMusic = new Intent();
                intentNextMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_NEXT_PLAY_MUSIC_ACTION);
                intentNextMusic.putExtra(POSITION_NEXT_MUSIC_PLAY, position);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentNextMusic);
                initMediaPlayer(position);
            }
        });

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPlayMusic = new Intent();
                intentPlayMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION);
                intentPlayMusic.putExtra(POSITION_PLAY_MUSIC_PLAY, position);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPlayMusic);
                if (music.isPlay() == true) {
                    btnPlayMusic.setImageResource(R.drawable.icon_pause);
                    music.setPlay(false);
                } else {
                    btnPlayMusic.setImageResource(R.drawable.icon_play);
                    music.setPlay(true);
                }
            }
        });

        seekBarPlayMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                int seekBarChange = seekBar.getProgress();
                Intent intentSeekBar = new Intent();
                intentSeekBar.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_SEEKBAR_MUSIC_ACTION);
                intentSeekBar.putExtra(SEEKBAR_PLAY_MUSIC, seekBarChange);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentSeekBar);

            }
        });
    }

    private void bind() {
        imgAvatarPlayMusic = findViewById(R.id.imgAvatarPlayMusic);
        txtNamePlayMusic = findViewById(R.id.txtNamePlayMusic);
        txtSingerMusic = findViewById(R.id.txtSingerPlayMusic);
        txtTimeSum = findViewById(R.id.txtTimeSum);
        txtTimeRun = findViewById(R.id.txtTimeRun);
        btnNextMusic = findViewById(R.id.btnNextPlayMusic);
        btnPlayMusic = findViewById(R.id.btnPlayPlayMusic);
        btnPreviousMusic = findViewById(R.id.btnPreviousPlayMusic);
        btnRepeatMusic = findViewById(R.id.btnRepeatPlayMusic);
        btnShuffleMusic = findViewById(R.id.btnShufflePlayMusic);
        seekBarPlayMusic = findViewById(R.id.seeBarPlayMusic);
    }

    private void addMusic() {
        list.add(new Music("Bạc Phận", "K-ICM ft. JACK", "https://sinhnhathot.com/uploads/celebrities/a692feab60b1f53d821150a87fafea46.png"));
        list.add(new Music("Đừng nói tôi điên", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"));
        list.add(new Music("Em ngày xưa khác rồi", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"));
        list.add(new Music("Hồng Nhan", "Jack", "https://kenh14cdn.com/zoom/700_438/2019/4/16/520385336113309193300413143308017856937984n-15554316885891494708426-crop-15554316943631888232929.jpg"));
        list.add(new Music("Mây và núi", "The Bells", "https://photo-resize-zmp3.zadn.vn/w240_r1x1_jpeg/covers/7/6/764f16e%E2%80%A6_1286532325.jpg"));
        list.add(new Music("Rồi người thương cũng hóa người dưng", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"));
    }

    private void initMediaPlayer(int position) {
        Glide.with(this).load(list.get(position).getMusicImage()).into(imgAvatarPlayMusic);
        txtNamePlayMusic.setText(list.get(position).getMusicName());
        txtSingerMusic.setText(list.get(position).getMusicSinger());
        imgAvatarPlayMusic.setAnimation(animation);
    }

    private void setTimetotal(int duration) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dinhDangPhut = new SimpleDateFormat("mm:ss");
        txtTimeSum.setText(dinhDangPhut.format(duration));
        seekBarPlayMusic.setMax(duration);
    }

    @Override
    public void onClickPrevious() {
        position--;
        if (position < 0) {
            position = list.size() - 1;
        }
        Intent intentPreviousMusic = new Intent();
        intentPreviousMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION);
        intentPreviousMusic.putExtra(POSITION_PREVIOUS_MUSIC_PLAY, position);
        LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPreviousMusic);
        initMediaPlayer(position);
    }

    @Override
    public void onClickPlay() {
        Intent intentPlayMusic = new Intent();
        intentPlayMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION);
        intentPlayMusic.putExtra(POSITION_PLAY_MUSIC_PLAY, position);
        LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPlayMusic);
        initMediaPlayer(position);
    }

    @Override
    public void onClickNext() {
        position++;
        if (position > list.size() - 1) {
            position = 0;
        }
        Intent intentNextMusic = new Intent();
        intentNextMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_NEXT_PLAY_MUSIC_ACTION);
        intentNextMusic.putExtra(POSITION_NEXT_MUSIC_PLAY, position);
        LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentNextMusic);
        initMediaPlayer(position);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mediaPlayerBroadcast);
    }

    interface TakeMediaPlayer {

        void takeCurrentMediaPlayer(int current, int duration);

        void playMusicAgain(int position, int durationMusic);
    }

    public class MediaPlayerBroadcast extends BroadcastReceiver {
        public static final String SEND_TIME_MEDIAPLAYER = "time_mediaPlayer";
        public static final String SEND_POSITION_PLAY_MUSIC = "send_position_play_music";
        TakeMediaPlayer takeMediaPlayer;

        public void setTakeMediaPlayer(TakeMediaPlayer takeMediaPlayer) {
            this.takeMediaPlayer = takeMediaPlayer;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case SEND_TIME_MEDIAPLAYER:
                    int current = intent.getIntExtra(MyMusicServices.CURRENT_KEY, 0);
                    int durationMedia = intent.getIntExtra(MyMusicServices.DURATION_KEY, 0);
                    takeMediaPlayer.takeCurrentMediaPlayer(current, durationMedia);
                    break;
                case SEND_POSITION_PLAY_MUSIC:
                    int positionMusic = intent.getIntExtra(MyMusicServices.POSITION_MUSIC_PLAY_KEY, 0);
                    int durationMusic = intent.getIntExtra(MyMusicServices.DURATION_MUSIC_AGAIN, 0);
                    takeMediaPlayer.playMusicAgain(positionMusic, durationMusic);
                    break;
            }
        }
    }
}
