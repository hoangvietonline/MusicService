package hoangviet.ndhv.demoui;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static hoangviet.ndhv.demoui.MyServicesMusic.mMediaPlayer;


public class PlayMusicActivity extends AppCompatActivity implements BroadcastMusic.OnclickNotifyBroadcast {
    private static final String TAG = "PlayMusicActivity";

    private CircleImageView imgAvatarPlayMusic;
    private TextView txtNamePlayMusic, txtSingerMusic, txtTimeRun, txtTimeSum;
    private ImageButton btnPlayMusic, btnPreviousMusic, btnNextMusic, btnRepeatMusic, btnShuffleMusic;
    private SeekBar seekBarPlayMusic;
    private List<Music> list;
    private List<Music> listRepeat;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        BroadcastMusic broadcastMusic = new BroadcastMusic();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastMusic.BUTTON_PREVIOUS);
        intentFilter.addAction(BroadcastMusic.BUTTON_PLAY);
        intentFilter.addAction(BroadcastMusic.BUTTON_NEXT);
        registerReceiver(broadcastMusic, intentFilter);

        broadcastMusic.setMyBroadcastCall(this);
        bind();
        list = new ArrayList<>();
        listRepeat = new ArrayList<>();
        addMusic();
        listRepeat = list;

        Log.d(TAG, "onCreate:size "+listRepeat.size());
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Mp3Activity.BUNDLE_KEY);
        if (bundle != null) {
            position = bundle.getInt(Mp3Activity.POSITION_KEY);
        }

        if (!mMediaPlayer.isPlaying()) {
            initMediaPlayer(position);
            mMediaPlayer.start();
            btnPlayMusic.setImageResource(R.drawable.icon_pause);
        } else {
            btnPlayMusic.setImageResource(R.drawable.icon_play);
        }
        setTimetotal();
        updateTimeSong();

        btnShuffleMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.shuffle(list);
            }
        });
        btnRepeatMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = listRepeat;
            }
        });
        btnPreviousMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if (position < 0) {
                    position = list.size() - 1;
                }
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                initMediaPlayer(position);
                mMediaPlayer.start();
                setTimetotal();
                updateTimeSong();
            }
        });
        btnNextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position > list.size() - 1) {
                    position = 0;
                }
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                initMediaPlayer(position);
                mMediaPlayer.start();
                setTimetotal();
                updateTimeSong();
            }
        });

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    btnPlayMusic.setImageResource(R.drawable.icon_play);
                } else {
                    mMediaPlayer.start();
                    btnPlayMusic.setImageResource(R.drawable.icon_pause);
                }
                updateTimeSong();
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
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
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
        list.add(new Music("Bạc Phận", "K-ICM ft. JACK", "https://sinhnhathot.com/uploads/celebrities/a692feab60b1f53d821150a87fafea46.png", R.raw.bac_phan));
        list.add(new Music("Đừng nói tôi điên", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.dung_noi_toi_dien));
        list.add(new Music("Em ngày xưa khác rồi", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.em_ngay_xua_khac_roi));
        list.add(new Music("Hồng Nhan", "Jack", "https://kenh14cdn.com/zoom/700_438/2019/4/16/520385336113309193300413143308017856937984n-15554316885891494708426-crop-15554316943631888232929.jpg", R.raw.hong_nhan_jack));
        list.add(new Music("Mây và núi", "The Bells", "https://photo-resize-zmp3.zadn.vn/w240_r1x1_jpeg/covers/7/6/764f16e%E2%80%A6_1286532325.jpg", R.raw.may_va_nui));
        list.add(new Music("Rồi người thương cũng hóa người dưng", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", R.raw.roi_nguoi_thuong_cung_hoa_nguoi_dung));
    }

    private void initMediaPlayer(int position) {
        mMediaPlayer = MediaPlayer.create(this, list.get(position).getFileSong());
        Glide.with(this).load(list.get(position).getMusicImage()).into(imgAvatarPlayMusic);
        txtNamePlayMusic.setText(list.get(position).getMusicName());
        txtSingerMusic.setText(list.get(position).getMusicSinger());
    }

    private void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dinhdanggio = new SimpleDateFormat("mm:ss");
                txtTimeRun.setText(dinhdanggio.format(mMediaPlayer.getCurrentPosition()));
                seekBarPlayMusic.setProgress(mMediaPlayer.getCurrentPosition());
                // kiểm tra thời gian của bài hát khi hết bài --->next
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        position++;
                        if (position > list.size() - 1) {
                            position = 0;
                        }
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                        }
                        initMediaPlayer(position);
                        mMediaPlayer.start();
                        btnPlayMusic.setImageResource(R.drawable.icon_pause);
                        setTimetotal();
                        updateTimeSong();

                    }
                });

                handler.postDelayed(this, 500);

            }
        }, 100);
    }

    private void setTimetotal() {
        SimpleDateFormat dinhDangPhut = new SimpleDateFormat("mm:ss");
        txtTimeSum.setText(dinhDangPhut.format(mMediaPlayer.getDuration()));
        seekBarPlayMusic.setMax(mMediaPlayer.getDuration());
    }

    @Override
    public void onClickPrevious() {
        position--;
        if (position < 0) {
            position = list.size() - 1;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        initMediaPlayer(position);
        mMediaPlayer.start();
        setTimetotal();
        updateTimeSong();
    }

    @Override
    public void onClickPlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            btnPlayMusic.setImageResource(R.drawable.icon_play);
        } else {
            mMediaPlayer.start();
            btnPlayMusic.setImageResource(R.drawable.icon_pause);
        }
        updateTimeSong();
    }

    @Override
    public void onClickNext() {
        position++;
        if (position > list.size() - 1) {
            position = 0;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        initMediaPlayer(position);
        mMediaPlayer.start();
        setTimetotal();
        updateTimeSong();
    }
}
