package hoangviet.ndhv.demoui;

public class Music {
    private String musicName;
    private String musicSinger;
    private String musicImage;
    private int fileSong;
    private boolean isPlay;

    public Music(String musicName, String musicSinger, String musicImage, int fileSong) {
        this.musicName = musicName;
        this.musicSinger = musicSinger;
        this.musicImage = musicImage;
        this.fileSong = fileSong;
    }

    public Music(String musicName, String musicSinger, String musicImage, int fileSong, boolean isPlay) {
        this.musicName = musicName;
        this.musicSinger = musicSinger;
        this.musicImage = musicImage;
        this.fileSong = fileSong;
        this.isPlay = isPlay;
    }

    public Music(boolean isPlay) {
        this.isPlay = isPlay;
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
}
