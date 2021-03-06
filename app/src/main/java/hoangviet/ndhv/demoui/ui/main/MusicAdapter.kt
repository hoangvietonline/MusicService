package hoangviet.ndhv.demoui.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hoangviet.ndhv.demoui.R
import hoangviet.ndhv.demoui.model.Music
import hoangviet.ndhv.demoui.ui.main.MusicAdapter.MusicViewHolder
import java.util.*

class MusicAdapter constructor(private val mContext: Context) : RecyclerView.Adapter<MusicViewHolder>() {
    private var mOnClickItemMusicListener: OnClickItemMusicListener? = null
    private var mMusicList: MutableList<Music>?
    fun addMusicList(musicList: MutableList<Music>?) {
        if (mMusicList!!.size > 0) mMusicList!!.clear()
        mMusicList = musicList
        notifyDataSetChanged()
    }

    fun setOnClickItemMusicListener(mOnClickItemMusicListener: OnClickItemMusicListener?) {
        this.mOnClickItemMusicListener = mOnClickItemMusicListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MusicViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.line_music, viewGroup, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, i: Int) {
        val music = mMusicList!![i]
        holder.txtMusicName.text = music.musicName
        holder.txtMusicSinger.text = music.musicSinger
        Glide.with(mContext).load(music.musicImage).into(holder.imgMusicAvatar)
        //set pause or play khi click button play
        if (mMusicList!![i].isPlay) {
            holder.btnPlay.setImageResource(R.drawable.ic_pause_white_48dp)
        } else {
            holder.btnPlay.setImageResource(R.drawable.ic_play_arrow_white_48dp)
        }
        holder.itemView.setOnClickListener { mOnClickItemMusicListener!!.onclickItem(i, music) }
        holder.btnPlay.setOnClickListener { mOnClickItemMusicListener!!.onClickButtonPlay(music, i) }
    }

    override fun getItemCount(): Int {
        return if (mMusicList == null) 0 else mMusicList!!.size
    }

    interface OnClickItemMusicListener {
        fun onclickItem(position: Int, music: Music)
        fun onClickButtonPlay(music: Music, position: Int)
    }

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtMusicName: TextView = itemView.findViewById(R.id.txtMusicName)
        val txtMusicSinger: TextView = itemView.findViewById(R.id.txtMusicSinger)
        val imgMusicAvatar: ImageView = itemView.findViewById(R.id.imgAvatarMusic)
        val btnPlay: ImageView = itemView.findViewById(R.id.buttonMusicPlay)

    }

    init {
        mMusicList = ArrayList()
    }
}