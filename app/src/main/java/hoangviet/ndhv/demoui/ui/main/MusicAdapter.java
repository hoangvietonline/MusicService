package hoangviet.ndhv.demoui.ui.main;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hoangviet.ndhv.demoui.R;
import hoangviet.ndhv.demoui.model.Music;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private onClickItemMusicListener mOnClickItemMusicListener;
    private Context mContext;
    private List<Music> mMusicList;

    MusicAdapter(Context mContext) {
        this.mContext = mContext;
        this.mMusicList = new ArrayList<>();
    }

    public void addMusicList(List<Music> musicList) {
        if (this.mMusicList.size() > 0)
            this.mMusicList.clear();
        this.mMusicList = musicList;
        notifyDataSetChanged();
    }

    public void setOnClickItemMusicListener(onClickItemMusicListener mOnClickItemMusicListener) {
        this.mOnClickItemMusicListener = mOnClickItemMusicListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.line_music, viewGroup, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MusicViewHolder holder, int i) {
        Music music = mMusicList.get(i);
        holder.txtMusicName.setText(music.getMusicName());
        holder.txtMusicSinger.setText(music.getMusicSinger());
        Glide.with(mContext).load(music.getMusicImage()).into(holder.imgMusicAvatar);
        //set pause or play khi click button play
        if (mMusicList.get(i).isPlay()) {
            holder.btnPlay.setImageResource(R.drawable.ic_pause_white_48dp);
        } else {
            holder.btnPlay.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        }
        holder.itemView.setOnClickListener(v -> mOnClickItemMusicListener.onclickItem(i, music));
        holder.btnPlay.setOnClickListener(v -> mOnClickItemMusicListener.onClickButtonPlay(music, i));
    }

    @Override
    public int getItemCount() {
        return mMusicList == null ? 0 : mMusicList.size();
    }

    interface onClickItemMusicListener {
        void onclickItem(int position, Music music);

        void onClickButtonPlay(Music music, int position);
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMusicName;
        private TextView txtMusicSinger;
        private ImageView imgMusicAvatar;
        private ImageView btnPlay;

        MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMusicName = itemView.findViewById(R.id.txtMusicName);
            txtMusicSinger = itemView.findViewById(R.id.txtMusicSinger);
            imgMusicAvatar = itemView.findViewById(R.id.imgAvatarMusic);
            btnPlay = itemView.findViewById(R.id.buttonMusicPlay);
        }
    }
}
