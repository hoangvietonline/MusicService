package hoangviet.ndhv.demoui;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private onClickItemMusicLitener onClickItemMusicLitener;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Music> musicList;

    public MusicAdapter(onClickItemMusicLitener onClickItemMusicLitener, Context mContext, List<Music> musicList) {
        this.mContext = mContext;
        this.musicList = musicList;
        this.onClickItemMusicLitener = onClickItemMusicLitener;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.line_music, viewGroup, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MusicViewHolder musicViewHolder, final int i) {
        final Music music = musicList.get(i);
        musicViewHolder.txtMusicName.setText(music.getMusicName());
        musicViewHolder.txtMusicSinger.setText(music.getMusicSinger());
        Glide.with(mContext).load(music.getMusicImage()).into(musicViewHolder.imgMusicAvatar);

        if (musicList.get(i).isPlay() == true) {
            musicViewHolder.btnPlay.setImageResource(R.drawable.icon_pause);
        } else {
            musicViewHolder.btnPlay.setImageResource(R.drawable.icon_play);
        }


        musicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemMusicLitener.onclickNotifications(i);
                onClickItemMusicLitener.onclickItem(i);
            }
        });

        musicViewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemMusicLitener.onClickButtonPlay(music,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMusicName;
        private TextView txtMusicSinger;
        private CircleImageView imgMusicAvatar;
        private ImageView btnPlay;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMusicName = itemView.findViewById(R.id.txtMusicName);
            txtMusicSinger = itemView.findViewById(R.id.txtMusicSinger);
            imgMusicAvatar = itemView.findViewById(R.id.imgAvatarMusic);
            btnPlay = itemView.findViewById(R.id.buttonMusicPlay);
        }
    }
    interface onClickItemMusicLitener {
        void onclickItem(int position);

        void onclickNotifications(int position);

        void onClickButtonPlay(Music music,int position);
    }
}
