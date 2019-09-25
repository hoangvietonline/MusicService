package hoangviet.ndhv.demoui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Mp3Activity extends AppCompatActivity implements MusicAdapter.onClickItemMusicLitener {
    private static final String TAG = "Mp3Activity";
    public static final String POS_KEY = "pos_key";
    public static final String POSITION_KEY = "position_key";
    public static final String BUNDLE_KEY = "bundle_key";
    public static final String MUSIC_KEY = "music_key";
    public static final String POSITION_PLAY_MP3 = "position_play_mp3";
    public static final int REQUEST_CODE_PLAY_MUSIC = 1245;
    public static final String STATE_REPEAT_ONE = "state_repeat_one";
    public static final String STATE_SHUFFLE = "state_shuffle" ;
    private List<Music> musicList;
    private MusicAdapter adapter;
    private int stateRepeatOne = 0;
    private int stateShuffle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3);
        Log.d(TAG, "onCreate: ");
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMusic);
        musicList = new ArrayList<>();
        adapter = new MusicAdapter(this, this, musicList);
        addMusic();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        Intent intentService = new Intent(this, MyMusicServices.class);
        startService(intentService);
    }

    private void addMusic() {
        musicList.add(new Music("Bạc Phận", "K-ICM ft. JACK", "https://cdn.tuoitre.vn/thumb_w/640/2019/6/19/jack-1560931851558668237008.jpg", false));
        musicList.add(new Music("Đừng nói tôi điên", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", false));
        musicList.add(new Music("Em ngày xưa khác rồi", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", false));
        musicList.add(new Music("Hồng Nhan", "Jack", "https://kenh14cdn.com/zoom/700_438/2019/4/16/520385336113309193300413143308017856937984n-15554316885891494708426-crop-15554316943631888232929.jpg",false));
        musicList.add(new Music("Mây và núi", "The Bells", "https://www.pngkey.com/png/detail/129-1296419_cartoon-mountains-png-mountain-animation-png.png",false));
        musicList.add(new Music("Rồi người thương cũng hóa người dưng", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", false));
    }


    @Override
    public void onclickItem(int position,Music music) {
        for (int j = 0; j < musicList.size(); j++) {
            if (j != position) {
                musicList.get(j).setPlay(false);
            } else {
                if (music.isPlay()) {
                    music.setPlay(false);
                } else {
                    music.setPlay(true);
                }
            }
        }
        Intent intent = new Intent(Mp3Activity.this, PlayMusicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION_KEY, position);
        bundle.putParcelable(MUSIC_KEY,music);
        bundle.putInt(STATE_REPEAT_ONE,stateRepeatOne);
        bundle.putInt(STATE_SHUFFLE,stateShuffle);
        intent.putExtra(BUNDLE_KEY, bundle);


        Intent intentPlayMusic = new Intent();
        intentPlayMusic.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_MP3_ACTION);
        intentPlayMusic.putExtra(POSITION_PLAY_MP3, position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentPlayMusic);
        startActivityForResult(intent,REQUEST_CODE_PLAY_MUSIC);

    }


    @Override
    public void onClickButtonPlay(Music music, int i) {
        for (int j = 0; j < musicList.size(); j++) {
            if (j != i) {
                musicList.get(j).setPlay(false);
            } else {
                if (music.isPlay()) {
                    music.setPlay(false);
                } else {
                    music.setPlay(true);
                }
            }
        }
        Intent intent = new Intent();
        intent.setAction(MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_ACTION);
        intent.putExtra(POS_KEY, i);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_PLAY_MUSIC && resultCode == RESULT_OK && data != null){
            Music music1 = data.getParcelableExtra(PlayMusicActivity.POSITION_RESULTS);
            stateRepeatOne = data.getIntExtra("state_repeat_one",0);
            stateShuffle = data.getIntExtra("state_shuffle",0);
            for (int j = 0; j < musicList.size(); j++) {
                if (!musicList.get(j).getMusicName().equals(music1.getMusicName())){
                    musicList.get(j).setPlay(false);
                }else {
                    if (music1.isPlay()){
                        musicList.get(j).setPlay(true);
                    }else {
                        musicList.get(j).setPlay(false);
                    }

                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
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
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }
}
