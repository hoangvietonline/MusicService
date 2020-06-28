package hoangviet.ndhv.demoui.ui.playmusic

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import hoangviet.ndhv.demoui.BroadcastMusic
import hoangviet.ndhv.demoui.BroadcastMusic.OnclickNotifyBroadcast
import hoangviet.ndhv.demoui.R
import hoangviet.ndhv.demoui.common.Constant
import hoangviet.ndhv.demoui.model.Music
import hoangviet.ndhv.demoui.service.MyMusicServices
import java.text.SimpleDateFormat
import java.util.*

class PlayMusicActivity : AppCompatActivity(), OnclickNotifyBroadcast {
    private var imgAvatarPlayMusic: CircleImageView? = null
    private var txtNamePlayMusic: TextView? = null
    private var txtSingerMusic: TextView? = null
    private var txtTimeRun: TextView? = null
    private var txtTimeSum: TextView? = null
    private var btnPlayMusic: ImageButton? = null
    private var btnPreviousMusic: ImageButton? = null
    private var btnNextMusic: ImageButton? = null
    private var btnRepeatMusic: ImageButton? = null
    private var btnShuffleMusic: ImageButton? = null
    private var btnRepeatOneMusic: ImageButton? = null
    private var btnUnShuffleMusic: ImageButton? = null
    private var seekBarPlayMusic: SeekBar? = null
    private var mMusicList: MutableList<Music>? = null
    private var position = 0
    private var music: Music? = null
    private var animation: ObjectAnimator? = null
    private var mediaPlayerBroadcast: MediaPlayerBroadcast? = null
    private var broadcastMusic: BroadcastMusic? = null
    private var stateRepeatOnePlayMusic = 0
    private var stateShufflePlayMusic = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)
        bind()
        mMusicList = ArrayList()
        addMusic()
        broadcastMusic = BroadcastMusic()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constant.BUTTON_PREVIOUS)
        intentFilter.addAction(Constant.BUTTON_PLAY)
        intentFilter.addAction(Constant.BUTTON_NEXT)
        registerReceiver(broadcastMusic, intentFilter)
        broadcastMusic!!.setMyBroadcastCall(this)
        initAnimationAvatar()
        mediaPlayerBroadcast = MediaPlayerBroadcast()
        val intentFilterMediaPlayer = IntentFilter()
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.Companion.SEND_TIME_MEDIA_PLAYER)
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.Companion.SEND_MUSIC_PLAY_AGAIN)
        intentFilterMediaPlayer.addAction(MediaPlayerBroadcast.Companion.SEND_POSITION_MUSIC_PLAY)
        LocalBroadcastManager.getInstance(this).registerReceiver(mediaPlayerBroadcast!!, intentFilterMediaPlayer)
        mediaPlayerBroadcast!!.setTakeMediaPlayer(object : TakeMediaPlayer {
            //function run time : 100/1000
            override fun takeCurrentMediaPlayer(current: Int, duration: Int) {
                val formatMinute = SimpleDateFormat("mm:ss", Locale.getDefault())
                txtTimeRun!!.text = formatMinute.format(current)
                seekBarPlayMusic!!.progress = current
                setTimeTotal(duration)
            }

            override fun playMusicAgain(positionMusic: Int, duration: Int) {
                initMediaPlayer(positionMusic)
                setTimeTotal(duration)
                music = mMusicList!![positionMusic]
                music!!.isPlay = true
                btnPlayMusic!!.setImageResource(R.drawable.ic_pause_white_48dp)
                for (i in mMusicList!!.indices) {
                    if (i != positionMusic) {
                        mMusicList!![i].isPlay = false
                    }
                }
                position = positionMusic
            }

            override fun takePositionMusic(positionMusic: Int) {
                initMediaPlayer(positionMusic)
                music = mMusicList!![positionMusic]
                music!!.isPlay = true
                btnPlayMusic!!.setImageResource(R.drawable.ic_pause_white_48dp)
                for (i in mMusicList!!.indices) {
                    if (i != positionMusic) {
                        mMusicList!![i].isPlay = false
                    }
                }
                position = positionMusic
            }
        })
        val intent = intent
        val bundle = intent.getBundleExtra(Constant.BUNDLE_KEY)
        if (bundle != null) {
            position = bundle.getInt(Constant.POSITION_KEY)
            stateRepeatOnePlayMusic = bundle.getInt(Constant.STATE_REPEAT_ONE)
            stateShufflePlayMusic = bundle.getInt(Constant.STATE_SHUFFLE)
            music = mMusicList!![position]
            music!!.isPlay = true
            btnPlayMusic!!.setImageResource(R.drawable.ic_play_arrow_white_48dp)
        } else {
            val bundleNotify = getIntent().extras
            position = bundleNotify?.getInt(Constant.SERVICE_POSITION_NOTIFICATION) ?: 0
            Log.d(TAG, "onCreate: postition  $position")
            music = mMusicList!![position]
            music!!.isPlay = true
            btnPlayMusic!!.setImageResource(R.drawable.ic_pause_white_48dp)
        }
        initMediaPlayer(position)

////button shuffle array and repeat music
        if (stateRepeatOnePlayMusic != 0) {
            if (stateRepeatOnePlayMusic == View.GONE) {
                btnRepeatOneMusic!!.visibility = View.GONE
                btnRepeatMusic!!.visibility = View.VISIBLE
            } else {
                btnRepeatOneMusic!!.visibility = View.GONE
                btnRepeatMusic!!.visibility = View.VISIBLE
            }
        } else {
            btnRepeatMusic!!.visibility = View.GONE
            btnRepeatOneMusic!!.visibility = View.VISIBLE
        }
        if (stateShufflePlayMusic != 0) {
            if (stateShufflePlayMusic == View.GONE) {
                btnShuffleMusic!!.visibility = View.GONE
                btnUnShuffleMusic!!.visibility = View.VISIBLE
            } else {
                btnShuffleMusic!!.visibility = View.VISIBLE
                btnUnShuffleMusic!!.visibility = View.GONE
            }
        } else {
            btnShuffleMusic!!.visibility = View.VISIBLE
            btnUnShuffleMusic!!.visibility = View.GONE
        }
        btnShuffleMusic!!.setOnClickListener {
            btnShuffleMusic!!.visibility = View.GONE
            btnUnShuffleMusic!!.visibility = View.VISIBLE
            val intentShuffle = Intent()
            intentShuffle.action = MyMusicServices.CurrentTimeBroadcast.SEND_SHUFFLE_MUSIC_ACTION
            intentShuffle.putExtra(Constant.CONFIRM_SHUFFLE_OKE, "confirmShuffle")
            LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentShuffle)
        }
        btnUnShuffleMusic!!.setOnClickListener {
            btnShuffleMusic!!.visibility = View.VISIBLE
            btnUnShuffleMusic!!.visibility = View.GONE
            val intentUnShuffle = Intent()
            intentUnShuffle.action = MyMusicServices.CurrentTimeBroadcast.SEND_UN_SHUFFLE_MUSIC_ACTION
            LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentUnShuffle)
        }
        btnRepeatOneMusic!!.setOnClickListener {
            btnRepeatMusic!!.visibility = View.VISIBLE
            btnRepeatOneMusic!!.visibility = View.GONE
            val intentRepeatOne = Intent()
            intentRepeatOne.action = MyMusicServices.CurrentTimeBroadcast.SEND_REPEAT_ONE_ACTION
            intentRepeatOne.putExtra(Constant.OKE_REPEAT_ONE, "OKE")
            intentRepeatOne.putExtra(Constant.POSITION_REPEAT_ONE, position)
            LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentRepeatOne)
        }
        btnRepeatMusic!!.setOnClickListener {
            btnRepeatMusic!!.visibility = View.GONE
            btnRepeatOneMusic!!.visibility = View.VISIBLE
            val intentRepeat = Intent()
            intentRepeat.action = MyMusicServices.CurrentTimeBroadcast.SEND_REPEAT_PLAY_MUSIC_ACTION
            LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentRepeat)
        }
        btnPreviousMusic!!.setOnClickListener {
            animation!!.start()
            val intentPreviousMusic = Intent()
            intentPreviousMusic.action = MyMusicServices.CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION
            LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentPreviousMusic)
        }
        btnNextMusic!!.setOnClickListener {
            animation!!.start()
            val intentNextMusic = Intent()
            intentNextMusic.action = MyMusicServices.CurrentTimeBroadcast.SEND_NEXT_PLAY_MUSIC_ACTION
            LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentNextMusic)
        }
        btnPlayMusic!!.setOnClickListener {
            val intentPlayMusic = Intent()
            intentPlayMusic.action = MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION
            LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentPlayMusic)
            if (music != null) {
                if (music!!.isPlay) {
                    btnPlayMusic!!.setImageResource(R.drawable.ic_play_arrow_white_48dp)
                    music!!.isPlay = false
                    animation!!.pause()
                } else {
                    btnPlayMusic!!.setImageResource(R.drawable.ic_pause_white_48dp)
                    music!!.isPlay = true
                    animation!!.resume()
                }
            }
        }
        seekBarPlayMusic!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val seekBarChange = seekBar.progress
                val intentSeekBar = Intent()
                intentSeekBar.action = MyMusicServices.CurrentTimeBroadcast.SEND_SEEK_BAR_MUSIC_ACTION
                intentSeekBar.putExtra(Constant.SEEK_BAR_PLAY_MUSIC, seekBarChange)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intentSeekBar)
            }
        })
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun initAnimationAvatar() {
        animation = ObjectAnimator.ofFloat(imgAvatarPlayMusic, "rotation", 0f, 360f)
        animation?.duration = 10000
        animation?.repeatMode = ValueAnimator.RESTART
        animation?.repeatCount = ObjectAnimator.INFINITE
        animation?.interpolator = LinearInterpolator()
        animation?.start()
    }

    private fun bind() {
        imgAvatarPlayMusic = findViewById(R.id.imgAvatarPlayMusic)
        txtNamePlayMusic = findViewById(R.id.txtNamePlayMusic)
        txtSingerMusic = findViewById(R.id.txtSingerPlayMusic)
        txtTimeSum = findViewById(R.id.txtTimeSum)
        txtTimeRun = findViewById(R.id.txtTimeRun)
        btnNextMusic = findViewById(R.id.btnNextPlayMusic)
        btnPlayMusic = findViewById(R.id.btnPlayPlayMusic)
        btnPreviousMusic = findViewById(R.id.btnPreviousPlayMusic)
        btnRepeatMusic = findViewById(R.id.btnRepeatOnePlayMusic)
        btnShuffleMusic = findViewById(R.id.btnUnShufflePlayMusic)
        seekBarPlayMusic = findViewById(R.id.seeBarPlayMusic)
        btnRepeatOneMusic = findViewById(R.id.btnRepeatPlayMusic)
        btnUnShuffleMusic = findViewById(R.id.btnShufflePlayMusic)
    }

    private fun addMusic() {
        mMusicList!!.add(Music("Bạc Phận", "K-ICM ft. JACK", "https://cdn.tuoitre.vn/thumb_w/640/2019/6/19/jack-1560931851558668237008.jpg"))
        mMusicList!!.add(Music("Đừng nói tôi điên", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"))
        mMusicList!!.add(Music("Em ngày xưa khác rồi", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"))
        mMusicList!!.add(Music("Hồng Nhan", "Jack", "https://kenh14cdn.com/zoom/700_438/2019/4/16/520385336113309193300413143308017856937984n-15554316885891494708426-crop-15554316943631888232929.jpg"))
        mMusicList!!.add(Music("Mây và núi", "The Bells", "https://www.pngkey.com/png/detail/129-1296419_cartoon-mountains-png-mountain-animation-png.png"))
        mMusicList!!.add(Music("Rồi người thương cũng hóa người dưng", "Hiền Hồ", "https://vcdn-ione.vnecdn.net/2018/12/13/43623062-928967060639978-82410-4074-2366-1544693013.png"))
    }

    private fun initMediaPlayer(position: Int) {
        Glide.with(this).load(mMusicList!![position].musicImage).into(imgAvatarPlayMusic!!)
        txtNamePlayMusic!!.text = mMusicList!![position].musicName
        txtSingerMusic!!.text = mMusicList!![position].musicSinger
    }

    private fun setTimeTotal(duration: Int) {
        @SuppressLint("SimpleDateFormat") val dinhDangPhut = SimpleDateFormat("mm:ss")
        txtTimeSum!!.text = dinhDangPhut.format(duration)
        seekBarPlayMusic!!.max = duration
    }

    override fun onClickPrevious() {
        val intentPreviousMusic = Intent()
        intentPreviousMusic.action = MyMusicServices.CurrentTimeBroadcast.SEND_PREVIOUS_PLAY_MUSIC_ACTION
        LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentPreviousMusic)
        animation!!.start()
    }

    override fun onClickPlay() {
        val intentPlayMusic = Intent()
        intentPlayMusic.action = MyMusicServices.CurrentTimeBroadcast.SEND_PLAY_PLAY_MUSIC_ACTION
        LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentPlayMusic)
        if (music != null) {
            if (music!!.isPlay) {
                btnPlayMusic!!.setImageResource(R.drawable.ic_play_arrow_white_48dp)
                music!!.isPlay = false
                animation!!.pause()
            } else {
                btnPlayMusic!!.setImageResource(R.drawable.ic_pause_white_48dp)
                music!!.isPlay = true
                animation!!.resume()
            }
        }
    }

    override fun onClickNext() {
        val intentNextMusic = Intent()
        intentNextMusic.action = MyMusicServices.CurrentTimeBroadcast.SEND_NEXT_PLAY_MUSIC_ACTION
        LocalBroadcastManager.getInstance(this@PlayMusicActivity).sendBroadcast(intentNextMusic)
        animation!!.start()
    }

    override fun onStop() {
        Log.d(TAG, "onStop: ")
        super.onStop()
    }

    override fun onBackPressed() {
        val intentMp3 = Intent()
        val stateRepeatOne = btnRepeatOneMusic!!.visibility
        val stateShuffle = btnShuffleMusic!!.visibility
        intentMp3.putExtra("state_shuffle", stateShuffle)
        intentMp3.putExtra("state_repeat_one", stateRepeatOne)
        intentMp3.putExtra(Constant.POSITION_RESULTS, music)
        setResult(RESULT_OK, intentMp3)
        finish()
        super.onBackPressed()
        Log.d(TAG, "onBackPressed:position $position")
        Log.d(TAG, "onBackPressed:name " + music!!.musicName)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mediaPlayerBroadcast!!)
        unregisterReceiver(broadcastMusic)
    }

    override fun onRestart() {
        super.onRestart()
        if (btnRepeatOneMusic!!.visibility == View.VISIBLE) {
            btnRepeatMusic!!.visibility = View.GONE
        } else {
            btnRepeatMusic!!.visibility = View.VISIBLE
        }
        if (btnShuffleMusic!!.visibility == View.VISIBLE) {
            btnUnShuffleMusic!!.visibility = View.GONE
        } else {
            btnUnShuffleMusic!!.visibility = View.VISIBLE
        }
        Log.d(TAG, "onRestart: ")
    }

    interface TakeMediaPlayer {
        fun takeCurrentMediaPlayer(current: Int, duration: Int)
        fun playMusicAgain(position: Int, durationMusic: Int)
        fun takePositionMusic(position: Int)
    }

    class MediaPlayerBroadcast : BroadcastReceiver() {
        private var takeMediaPlayer: TakeMediaPlayer? = null
        fun setTakeMediaPlayer(takeMediaPlayer: TakeMediaPlayer?) {
            this.takeMediaPlayer = takeMediaPlayer
        }

        override fun onReceive(context: Context, intent: Intent) {
            when (Objects.requireNonNull(intent.action)) {
                SEND_TIME_MEDIA_PLAYER -> {
                    val current = intent.getIntExtra(Constant.CURRENT_KEY, 0)
                    val durationMedia = intent.getIntExtra(Constant.DURATION_KEY, 0)
                    takeMediaPlayer!!.takeCurrentMediaPlayer(current, durationMedia)
                }
                SEND_MUSIC_PLAY_AGAIN -> {
                    val positionMusic = intent.getIntExtra(Constant.POSITION_MUSIC_PLAY_KEY, 0)
                    val durationMusic = intent.getIntExtra(Constant.DURATION_MUSIC_AGAIN, 0)
                    takeMediaPlayer!!.playMusicAgain(positionMusic, durationMusic)
                }
                SEND_POSITION_MUSIC_PLAY -> {
                    val position = intent.getIntExtra(Constant.POSITION_PLAY_MUSIC, 0)
                    takeMediaPlayer!!.takePositionMusic(position)
                }
            }
        }

        companion object {
            const val SEND_TIME_MEDIA_PLAYER = "time_mediaPlayer"
            const val SEND_MUSIC_PLAY_AGAIN = "send_music_play_again"
            const val SEND_POSITION_MUSIC_PLAY = "send_position_play_music"
        }
    }

    companion object {
        private const val TAG = "PlayMusicActivity"
    }
}