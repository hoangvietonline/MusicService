package hoangviet.ndhv.demoui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayMusicActivity extends AppCompatActivity implements BroadcastMusic.OnclickNotifyBroadcast {
    public static final String POSITION_PREVIOUS_MUSIC_PLAY = "position_previous_music_play";
    public static final String POSITION_PLAY_MUSIC_PLAY = "position_play_music_play";
    public static final String POSITION_NEXT_MUSIC_PLAY = "position_next_music_play";
    public static final String SEEKBAR_PLAY_MUSIC = "seekBar_play_music";
    public static final String POSITION_RESULTS = "position_results";
    public static final String POSITION_REPEAT_ONE = "position_repeat_one";
    public static final String OKE_REPEAT_ONE = "oke_repeat_one";
    private static final String TAG = "PlayMusicActivity";
    private CircleImageView imgAvatarPlayMusic;
    private TextView txtNamePlayMusic, txtSingerMusic, txtTimeRun, txtTimeSum;
    private ImageButton btnPlayMusic, btnPreviousMusic, btnNextMusic, btnRepeatMusic, btnShuffleMusic, btnRepeatOneMusic, btnUnShuffleMusic;
    private SeekBar seekBarPlayMusic;
    private List<Music> list;
    private int position;
    private Music music;
    private MediaPlayerBroadcast mediaPlayerBroadcast;
    private Animation animation;
    private BroadcastMusic broadcastMusic;
    private int stateRepeatOnePlayMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_play_music);
        bind();
        list = new ArrayList<>();
        addMusic();
        animation = AnimationUtils.loadAnimation(this, R.anim.round_image);
        broadcastMusic = new BroadcastMusic();
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
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.SEND_MUSIC_SHUFFLE_PLAY_MUSIC);
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.SEND_UN_SHUFFLE_PLAY_MUSIC);
        LocalBroadcastManager.getInstance(this).registerReceiver(mediaPlayerBroadcast, intentFilterMediaPlayer);

        mediaPlayerBroadcast.setTakeMediaPlayer(new TakeMediaPlayer() {

            //function run time : 100/1000
            @Override
            public void takeCurrentMediaPlayer(final int current, int duration) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatMinute = new SimpleDateFormat("mm:ss");
                txtTimeRun.setText(formatMinute.format(current));
                seekBarPlayMusic.setProgress(current);
                setTimetotal(duration);
            }

            @Override
            public void playMusicAgain(int positionMusic, int duration) {
                initMediaPlayer(positionMusic);
                setTimetotal(duration);

                music = list.get(positionMusic);
                music.setPlay(true);
                btnPlayMusic.setImageResource(R.drawable.icon_pause);
                for (int i = 0; i < list.size(); i++) {
                    if (i != positionMusic) {
                        list.get(i).setPlay(false);
                    }
                }
                position = positionMusic;
            }

            @Override
            public void takeMusicShuffle(List<Music> musicList) {
                list = musicList;
                Log.d(TAG, "takeMusicShuffle: "+music.toString());
                Log.d(TAG, "takeMusicShuffle: "+position);
                Log.d(TAG, "takeMusicShuffle:name " + music.getMusicName());
            }

            @Override
            public void takeMusicRepeat(List<Music> musicList) {
                list = musicList;
            }
        });
        final Intent intent = getIntent();
        final Bundle bundle = intent.getBundleExtra(Mp3Activity.BUNDLE_KEY);
        if (bundle != null) {
            position = bundle.getInt(Mp3Activity.POSITION_KEY);
            stateRepeatOnePlayMusic = bundle.getInt(Mp3Activity.STATE_REPEAT_ONE);
            music = list.get(position);
            music.setPlay(true);
            btnPlayMusic.setImageResource(R.drawable.icon_pause);
        } else {
            Bundle bundleNotify = getIntent().getExtras();
            position = bundleNotify != null ? bundleNotify.getInt(MyMusicServices.SERVICE_POSITION_NOTIFICATION) : 0;
            Log.d(TAG, "onCreate: postition  " + position);
            music = list.get(position);
            music.setPlay(true);
            btnPlayMusic.setImageResource(R.drawable.icon_pause);
        }

        initMediaPlayer(position);

////button shuffle array and repeat music
        btnShuffleMusic.setVisibility(View.VISIBLE);
        btnUnShuffleMusic.setVisibility(View.GONE);

        if (stateRepeatOnePlayMusic != 0) {
            if (stateRepeatOnePlayMusic == View.VISIBLE) {
                btnRepeatOneMusic.setVisibility(View.VISIBLE);
                btnRepeatMusic.setVisibility(View.GONE);
            } else {
                btnRepeatOneMusic.setVisibility(View.GONE);
                btnRepeatMusic.setVisibility(View.VISIBLE);
            }

        } else {
            btnRepeatMusic.setVisibility(View.GONE);
            btnRepeatOneMusic.setVisibility(View.VISIBLE);
        }

        btnShuffleMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentShuffle = new Intent();
                intentShuffle.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_SHUFFLE_MUSIC_ACTION);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentShuffle);
                btnShuffleMusic.setVisibility(View.GONE);
                btnUnShuffleMusic.setVisibility(View.VISIBLE);
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
                intentRepeatOne.putExtra(OKE_REPEAT_ONE, "OKE");
                intentRepeatOne.putExtra(POSITION_REPEAT_ONE, position);
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
                position--;
                if (position < 0) {
                    position = list.size() - 1;
                }
                Intent intentPreviousMusic = new Intent();
                intentPreviousMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION);
                intentPreviousMusic.putExtra(POSITION_PREVIOUS_MUSIC_PLAY, position);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPreviousMusic);
                initMediaPlayer(position);

                music = list.get(position);
                music.setPlay(true);
                btnPlayMusic.setImageResource(R.drawable.icon_pause);
                for (int i = 0; i < list.size(); i++) {
                    if (i != position) {
                        list.get(i).setPlay(false);
                    }
                }
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

                music = list.get(position);
                music.setPlay(true);
                btnPlayMusic.setImageResource(R.drawable.icon_pause);
                for (int i = 0; i < list.size(); i++) {
                    if (i != position) {
                        list.get(i).setPlay(false);
                    }
                }
            }
        });

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPlayMusic = new Intent();
                intentPlayMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION);
                intentPlayMusic.putExtra(POSITION_PLAY_MUSIC_PLAY, position);
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPlayMusic);
                music = list.get(position);
                if (music != null) {
                    if (music.isPlay()) {
                        btnPlayMusic.setImageResource(R.drawable.icon_play);
                        music.setPlay(false);
                    } else {
                        btnPlayMusic.setImageResource(R.drawable.icon_pause);
                        music.setPlay(true);
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
        btnPlayMusic.setImageResource(R.drawable.icon_pause);
        for (int i = 0; i < list.size(); i++) {
            if (i != position) {
                list.get(i).setPlay(false);
            }
        }
    }

    @Override
    public void onClickPlay() {
        Intent intentPlayMusic = new Intent();
        intentPlayMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION);
        intentPlayMusic.putExtra(POSITION_PLAY_MUSIC_PLAY, position);
        LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentPlayMusic);
        initMediaPlayer(position);
        music = list.get(position);
        if (music != null) {
            if (music.isPlay()) {
                btnPlayMusic.setImageResource(R.drawable.icon_play);
                music.setPlay(false);
            } else {
                btnPlayMusic.setImageResource(R.drawable.icon_pause);
                music.setPlay(true);
            }
        }

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
        music = list.get(position);
        music.setPlay(true);
        btnPlayMusic.setImageResource(R.drawable.icon_pause);
        for (int i = 0; i < list.size(); i++) {
            if (i != position) {
                list.get(i).setPlay(false);
            }
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent intentUnShuffle = new Intent();
        intentUnShuffle.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_UN_SHUFFLE_MUSIC_ACTION);
        LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intentUnShuffle);
        Intent intentMp3 = new Intent();
        int stateRepeatOne = btnRepeatOneMusic.getVisibility();
        intentMp3.putExtra("state_repeat_one", stateRepeatOne);
        intentMp3.putExtra(POSITION_RESULTS, music);
        setResult(Mp3Activity.RESULT_OK, intentMp3);
        finish();
        super.onBackPressed();
        Log.d(TAG, "onBackPressed:position "+position);
        Log.d(TAG, "onBackPressed:name "+music.getMusicName());

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
        mediaPlayerBroadcast.setTakeMediaPlayer(new TakeMediaPlayer() {

            //function run time : 100/1000
            @Override
            public void takeCurrentMediaPlayer(final int current, int duration) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatMinute = new SimpleDateFormat("mm:ss");
                txtTimeRun.setText(formatMinute.format(current));
                seekBarPlayMusic.setProgress(current);
                setTimetotal(duration);

            }

            @Override
            public void playMusicAgain(int positionMusic, int duration) {
                initMediaPlayer(positionMusic);
                setTimetotal(duration);
                if (position > list.size() - 1) {
                    position = 0;
                }
                music = list.get(position);
                music.setPlay(true);
                btnPlayMusic.setImageResource(R.drawable.icon_pause);
                for (int i = 0; i < list.size(); i++) {
                    if (i != position) {
                        list.get(i).setPlay(false);
                    }
                }
            }

            @Override
            public void takeMusicShuffle(List<Music> musicList) {

            }

            @Override
            public void takeMusicRepeat(List<Music> musicList) {

            }
        });
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

        void takeMusicShuffle(List<Music> musicList);

        void takeMusicRepeat(List<Music> musicList);
    }

    public class MediaPlayerBroadcast extends BroadcastReceiver {
        public static final String SEND_TIME_MEDIAPLAYER = "time_mediaPlayer";
        public static final String SEND_POSITION_PLAY_MUSIC = "send_position_play_music";
        public static final String SEND_MUSIC_SHUFFLE_PLAY_MUSIC = "send_music_shuffle_play_music";
        public static final String SEND_UN_SHUFFLE_PLAY_MUSIC = "send_repeat_play_music";
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
                case SEND_MUSIC_SHUFFLE_PLAY_MUSIC:
                    List<Music> musicList = intent.getParcelableArrayListExtra(MyMusicServices.MUSIC_LIST_SHUFFLE);
                    takeMediaPlayer.takeMusicShuffle(musicList);
                    break;
                case SEND_UN_SHUFFLE_PLAY_MUSIC:
                    List<Music> musicListRepeat = intent.getParcelableArrayListExtra(MyMusicServices.MUSIC_LIST_UNSHUFFLE);
                    takeMediaPlayer.takeMusicRepeat(musicListRepeat);
                    break;
            }
        }
    }
}
