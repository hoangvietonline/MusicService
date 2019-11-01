package hoangviet.ndhv.musicplay;

import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Parcelable {
    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
    private String musicName;
    private String musicSinger;
    private String musicImage;
    private int fileSong;
    private boolean isPlay;

    Music(String musicName, String musicSinger, String musicImage, int fileSong) {
        this.musicName = musicName;
        this.musicSinger = musicSinger;
        this.musicImage = musicImage;
        this.fileSong = fileSong;
    }

    Music(String musicName, String musicSinger, String musicImage) {
        this.musicName = musicName;
        this.musicSinger = musicSinger;
        this.musicImage = musicImage;
    }

    Music(String musicName, String musicSinger, String musicImage, boolean isPlay) {
        this.musicName = musicName;
        this.musicSinger = musicSinger;
        this.musicImage = musicImage;
        this.isPlay = isPlay;
    }


    public Music(String musicName, String musicSinger, String musicImage, int fileSong, boolean isPlay) {
        this.musicName = musicName;
        this.musicSinger = musicSinger;
        this.musicImage = musicImage;
        this.fileSong = fileSong;
        this.isPlay = isPlay;
    }

    private Music(Parcel in) {
        musicName = in.readString();
        musicSinger = in.readString();
        musicImage = in.readString();
        fileSong = in.readInt();
        isPlay = in.readByte() != 0;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicSinger() {
        return musicSinger;
    }

    public void setMusicSinger(String musicSinger) {
        this.musicSinger = musicSinger;
    }

    public String getMusicImage() {
        return musicImage;
    }

    public void setMusicImage(String musicImage) {
        this.musicImage = musicImage;
    }

    public int getFileSong() {
        return fileSong;
    }

    public void setFileSong(int fileSong) {
        this.fileSong = fileSong;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(musicName);
        dest.writeString(musicSinger);
        dest.writeString(musicImage);
        dest.writeInt(fileSong);
        dest.writeByte((byte) (isPlay ? 1 : 0));
    }
}
