package hoangviet.ndhv.demoui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Mp3Activity extends AppCompatActivity implements MusicAdapter.onClickItemMusicLitener {
    public static final String SERVICES_KEY = "services_key";
    public static final String POS_KEY = "pos_key";
    public static final String POSITION_KEY = "position_key";
    public static final String BUNDLE_KEY = "bundle_key";
    public static final String MUSIC_KEY = "music_key";
    private List<Music> musicList;
    private MusicAdapter adapter;
    private RecyclerView recyclerView;
    private int oldPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3);
        recyclerView = findViewById(R.id.recyclerViewMusic);
        musicList = new ArrayList<>();
        adapter = new MusicAdapter(this, this, musicList);
        addMusic();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        Intent intentService = new Intent(this, MyServicesMusic.class);
        startService(intentService);
    }

    private void addMusic() {
        musicList.add(new Music("Bạc Phận", "K-ICM ft. JACK", "https://sinhnhathot.com/uploads/celebrities/a692feab60b1f53d821150a87fafea46.png", false));
        musicList.add(new Music("Đừng nói tôi điên", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", false));
        musicList.add(new Music("Em ngày xưa khác rồi", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", false));
        musicList.add(new Music("Hồng Nhan", "Jack", "https://kenh14cdn.com/zoom/700_438/2019/4/16/520385336113309193300413143308017856937984n-15554316885891494708426-crop-15554316943631888232929.jpg",false));
        musicList.add(new Music("Mây và núi", "The Bells", "https://photo-resize-zmp3.zadn.vn/w240_r1x1_jpeg/covers/7/6/764f16e%E2%80%A6_1286532325.jpg",false));
        musicList.add(new Music("Rồi người thương cũng hóa người dưng", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png", false));
    }


    @Override
    public void onclickItem(int position,Music music) {
        Intent intent = new Intent(Mp3Activity.this, PlayMusicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION_KEY, position);
        bundle.putParcelable(MUSIC_KEY,music);
        intent.putExtra(BUNDLE_KEY, bundle);
        startActivity(intent);
    }


    @Override
    public void onClickButtonPlay(Music music, int i) {
        for (int j = 0; j < musicList.size(); j++) {
            if (j != i) {
                musicList.get(j).setPlay(false);
            } else {
                if (music.isPlay() == true) {
                    music.setPlay(false);
                } else {
                    music.setPlay(true);
                }
            }
        }
        Intent intent = new Intent();
        intent.setAction(MyServicesMusic.CurrentTimeBroadcast.SEND_PLAY_ACTION);
        intent.putExtra(POS_KEY, i);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        adapter.notifyDataSetChanged();
        oldPosition = i;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
