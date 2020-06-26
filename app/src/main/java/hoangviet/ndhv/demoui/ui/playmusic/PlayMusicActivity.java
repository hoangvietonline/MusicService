package hoangviet.ndhv.demoui.ui.playmusic;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hoangviet.ndhv.demoui.BroadcastMusic;
import hoangviet.ndhv.demoui.common.Constant;
import hoangviet.ndhv.demoui.service.MyMusicServices;
import hoangviet.ndhv.demoui.R;
import hoangviet.ndhv.demoui.model.Music;
import hoangviet.ndhv.demoui.ui.main.MainActivity;

public class PlayMusicActivity extends AppCompatActivity implements BroadcastMusic.OnclickNotifyBroadcast {
    private static final String TAG = "PlayMusicActivity";
    private CircleImageView imgAvatarPlayMusic;
    private TextView txtNamePlayMusic, txtSingerMusic, txtTimeRun, txtTimeSum;
    private ImageButton btnPlayMusic, btnPreviousMusic, btnNextMusic, btnRepeatMusic, btnShuffleMusic, btnRepeatOneMusic, btnUnShuffleMusic;
    private SeekBar seekBarPlayMusic;
    private List<Music> list;
    private int position;
    private Music music;
    private ObjectAnimator animation;
    private MediaPlayerBroadcast mediaPlayerBroadcast;
    private BroadcastMusic broadcastMusic;
    private int stateRepeatOnePlayMusic;
    private int stateShufflePlayMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_play_music);
        bind();
        list = new ArrayList<>();
        addMusic();
        broadcastMusic = new BroadcastMusic();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BUTTON_PREVIOUS);
        intentFilter.addAction(Constant.BUTTON_PLAY);
        intentFilter.addAction(Constant.BUTTON_NEXT);
        registerReceiver(broadcastMusic, intentFilter);
        broadcastMusic.setMyBroadcastCall(this);
        // animation image avatar
        animation = ObjectAnimator.ofFloat(imgAvatarPlayMusic, "rotation", 0, 360);
        animation.setDuration(10000);
        animation.setRepeatMode(ValueAnimator.RESTART);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

        mediaPlayerBroadcast = new MediaPlayerBroadcast();
        IntentFilter intentFilterMediaPlayer = new IntentFilter();
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.SEND_TIME_MEDIA_PLAYER);
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.SEND_MUSIC_PLAY_AGAIN);
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.SEND_POSITION_MUSIC_PLAY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mediaPlayerBroadcast, intentFilterMediaPlayer);

        mediaPlayerBroadcast.setTakeMediaPlayer(new TakeMediaPlayer() {

            //function run time : 100/1000
            @Override
            public void takeCurrentMediaPlayer(final int current, int duration) {
              SimpleDateFormat formatMinute = new SimpleDateFormat("mm:ss", Locale.getDefault());
                txtTimeRun.setText(formatMinute.format(current));
                seekBarPlayMusic.setProgress(current);
                setTimeTotal(duration);
            }

            @Override
            public void playMusicAgain(int positionMusic, int duration) {
                initMediaPlayer(positionMusic);
                setTimeTotal(duration);

                music = list.get(positionMusic);
                music.setPlay(true);
                btnPlayMusic.setImageResource(R.drawable.ic_pause_white_48dp);
                for (int i = 0; i < list.size(); i++) {
                    if (i != positionMusic) {
                        list.get(i).setPlay(false);
                    }
                }
                position = positionMusic;
            }

            @Override
            public void takePositionMusic(int positionMusic) {
                initMediaPlayer(positionMusic);
                music = list.get(positionMusic);
                music.setPlay(true);
                btnPlayMusic.setImageResource(R.drawable.ic_pause_white_48dp);
                for (int i = 0; i < list.size(); i++) {
                    if (i != positionMusic) {
                        list.get(i).setPlay(false);
                    }
                }
                position = positionMusic;
            }

        });
        final Intent intent = getIntent();
        final Bundle bundle = intent.getBundleExtra(Constant.BUNDLE_KEY);
        if (bundle != null) {
            position = bundle.getInt(Constant.POSITION_KEY);
            stateRepeatOnePlayMusic = bundle.getInt(Constant.STATE_REPEAT_ONE);
            stateShufflePlayMusic = bundle.getInt(Constant.STATE_SHUFFLE);
            music = list.get(position);
            music.setPlay(true);
            btnPlayMusic.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        } else {
            Bundle bundleNotify = getIntent().getExtras();
            position = bundleNotify != null ? bundleNotify.getInt(Constant.SERVICE_POSITION_NOTIFICATION) : 0;
            Log.d(TAG, "onCreate: postition  " + position);
            music = list.get(position);
            music.setPlay(true);
            btnPlayMusic.setImageResource(R.drawable.ic_pause_white_48dp);
        }

        initMediaPlayer(position);

////button shuffle array and repeat music


        if (stateRepeatOnePlayMusic != 0) {
            if (stateRepeatOnePlayMusic == View.GONE) {
                btnRepeatOneMusic.setVisibility(View.GONE);
                btnRepeatMusic.setVisibility(View.VISIBLE);
            } else {
                btnRepeatOneMusic.setVisibility(View.GONE);
                btnRepeatMusic.setVisibility(View.VISIBLE);
            }

        } else {
            btnRepeatMusic.setVisibility(View.GONE);
            btnRepeatOneMusic.setVisibility(View.VISIBLE);
        }


        if (stateShufflePlayMusic != 0) {
            if (stateShufflePlayMusic == View.GONE) {
                btnShuffleMusic.setVisibility(View.GONE);
                btnUnShuffleMusic.setVisibility(View.VISIBLE);
            } else {
                btnShuffleMusic.setVisibility(View.VISIBLE);
                btnUnShuffleMusic.setVisibility(View.GONE);
            }
        } else {
            btnShuffleMusic.setVisibility(View.VISIBLE);
            btnUnShuffleMusic.setVisibility(View.GONE);
        }


        btnShuffleMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShuffleMusic.setVisibility(View.GONE);
                btnUnShuffleMusic.setVisibility(View.VISIBLE);
                Intent intentShuffle = new Intent();
                intentShuffle.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_SHUFFLE_MUSIC_ACTION);
                intentShuffle.putExtra(Constant.CONFIRM_SHUFFLE_OKE, "confirmShuffle");
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentShuffle);

            }
        });
        btnUnShuffleMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShuffleMusic.setVisibility(View.VISIBLE);
                btnUnShuffleMusic.setVisibility(View.GONE);
                Intent intentUnShuffle = new Intent();
                intentUnShuffle.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_UN_SHUFFLE_MUSIC_ACTION);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentUnShuffle);
            }
        });

        btnRepeatOneMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRepeatMusic.setVisibility(View.VISIBLE);
                btnRepeatOneMusic.setVisibility(View.GONE);
                Intent intentRepeatOne = new Intent();
                intentRepeatOne.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_REPEAT_ONE_ACTION);
                intentRepeatOne.putExtra(Constant.OKE_REPEAT_ONE, "OKE");
                intentRepeatOne.putExtra(Constant.POSITION_REPEAT_ONE, position);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentRepeatOne);

            }
        });
        btnRepeatMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRepeatMusic.setVisibility(View.GONE);
                btnRepeatOneMusic.setVisibility(View.VISIBLE);
                Intent intentRepeat = new Intent();
                intentRepeat.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_REPEAT_PLAY_MUSIC_ACTION);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentRepeat);
            }
        });

        btnPreviousMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation.start();
                Intent intentPreviousMusic = new Intent();
                intentPreviousMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPreviousMusic);

            }
        });
        btnNextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation.start();
                Intent intentNextMusic = new Intent();
                intentNextMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_NEXT_PLAY_MUSIC_ACTION);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentNextMusic);

            }
        });

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPlayMusic = new Intent();
                intentPlayMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPlayMusic);

                if (music != null) {
                    if (music.isPlay()) {
                        btnPlayMusic.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        music.setPlay(false);
                        animation.pause();

                    } else {
                        btnPlayMusic.setImageResource(R.drawable.ic_pause_white_48dp);
                        music.setPlay(true);
                        animation.resume();
                    }
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
                intentSeekBar.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_SEEK_BAR_MUSIC_ACTION);
                intentSeekBar.putExtra(Constant.SEEK_BAR_PLAY_MUSIC, seekBarChange);
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
        btnRepeatMusic = findViewById(R.id.btnRepeatOnePlayMusic);
        btnShuffleMusic = findViewById(R.id.btnUnShufflePlayMusic);
        seekBarPlayMusic = findViewById(R.id.seeBarPlayMusic);
        btnRepeatOneMusic = findViewById(R.id.btnRepeatPlayMusic);
        btnUnShuffleMusic = findViewById(R.id.btnShufflePlayMusic);
    }

    private void addMusic() {
        list.add(new Music("Bạc Phận", "K-ICM ft. JACK", "https://cdn.tuoitre.vn/thumb_w/640/2019/6/19/jack-1560931851558668237008.jpg"));
        list.add(new Music("Đừng nói tôi điên", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"));
        list.add(new Music("Em ngày xưa khác rồi", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"));
        list.add(new Music("Hồng Nhan", "Jack", "https://kenh14cdn.com/zoom/700_438/2019/4/16/520385336113309193300413143308017856937984n-15554316885891494708426-crop-15554316943631888232929.jpg"));
        list.add(new Music("Mây và núi", "The Bells", "https://www.pngkey.com/png/detail/129-1296419_cartoon-mountains-png-mountain-animation-png.png"));
        list.add(new Music("Rồi người thương cũng hóa người dưng", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"));
    }

    private void initMediaPlayer(int position) {
        Glide.with(this).load(list.get(position).getMusicImage()).into(imgAvatarPlayMusic);
        txtNamePlayMusic.setText(list.get(position).getMusicName());
        txtSingerMusic.setText(list.get(position).getMusicSinger());
    }

    private void setTimeTotal(int duration) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dinhDangPhut = new SimpleDateFormat("mm:ss");
        txtTimeSum.setText(dinhDangPhut.format(duration));
        seekBarPlayMusic.setMax(duration);
    }

    @Override
    public void onClickPrevious() {
        Intent intentPreviousMusic = new Intent();
        intentPreviousMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION);
        LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPreviousMusic);
        animation.start();
    }

    @Override
    public void onClickPlay() {
        Intent intentPlayMusic = new Intent();
        intentPlayMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION);
        LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPlayMusic);
        if (music != null) {
            if (music.isPlay()) {
                btnPlayMusic.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                music.setPlay(false);
                animation.pause();
            } else {
                btnPlayMusic.setImageResource(R.drawable.ic_pause_white_48dp);
                music.setPlay(true);
                animation.resume();
            }
        }
    }

    @Override
    public void onClickNext() {
        Intent intentNextMusic = new Intent();
        intentNextMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_NEXT_PLAY_MUSIC_ACTION);
        LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentNextMusic);
        animation.start();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent intentMp3 = new Intent();
        int stateRepeatOne = btnRepeatOneMusic.getVisibility();
        int stateShuffle = btnShuffleMusic.getVisibility();
        intentMp3.putExtra("state_shuffle", stateShuffle);
        intentMp3.putExtra("state_repeat_one", stateRepeatOne);
        intentMp3.putExtra(Constant.POSITION_RESULTS, music);
        setResult(MainActivity.RESULT_OK, intentMp3);
        finish();
        super.onBackPressed();
        Log.d(TAG, "onBackPressed:position " + position);
        Log.d(TAG, "onBackPressed:name " + music.getMusicName());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mediaPlayerBroadcast);
        unregisterReceiver(broadcastMusic);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (btnRepeatOneMusic.getVisibility() == View.VISIBLE) {
            btnRepeatMusic.setVisibility(View.GONE);
        } else {
            btnRepeatMusic.setVisibility(View.VISIBLE);
        }
        if (btnShuffleMusic.getVisibility() == View.VISIBLE) {
            btnUnShuffleMusic.setVisibility(View.GONE);
        } else {
            btnUnShuffleMusic.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "onRestart: ");
    }

    interface TakeMediaPlayer {

        void takeCurrentMediaPlayer(int current, int duration);

        void playMusicAgain(int position, int durationMusic);

        void takePositionMusic(int position);

    }

    public class MediaPlayerBroadcast extends BroadcastReceiver {
        public static final String SEND_TIME_MEDIA_PLAYER = "time_mediaPlayer";
        public static final String SEND_MUSIC_PLAY_AGAIN = "send_music_play_again";
        public static final String SEND_POSITION_MUSIC_PLAY = "send_position_play_music";
        TakeMediaPlayer takeMediaPlayer;

        public void setTakeMediaPlayer(TakeMediaPlayer takeMediaPlayer) {
            this.takeMediaPlayer = takeMediaPlayer;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case SEND_TIME_MEDIA_PLAYER:
                    int current = intent.getIntExtra(Constant.CURRENT_KEY, 0);
                    int durationMedia = intent.getIntExtra(Constant.DURATION_KEY, 0);
                    takeMediaPlayer.takeCurrentMediaPlayer(current, durationMedia);
                    break;
                case SEND_MUSIC_PLAY_AGAIN:
                    int positionMusic = intent.getIntExtra(Constant.POSITION_MUSIC_PLAY_KEY, 0);
                    int durationMusic = intent.getIntExtra(Constant.DURATION_MUSIC_AGAIN, 0);
                    takeMediaPlayer.playMusicAgain(positionMusic, durationMusic);
                    break;
                case SEND_POSITION_MUSIC_PLAY:
                    int position = intent.getIntExtra(Constant.POSITION_PLAY_MUSIC, 0);
                    takeMediaPlayer.takePositionMusic(position);
                    break;
            }
        }
    }
}
