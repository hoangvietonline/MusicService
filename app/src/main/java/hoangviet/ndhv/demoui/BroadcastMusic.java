package hoangviet.ndhv.demoui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import hoangviet.ndhv.demoui.common.Constant;

/*
broadcast bắn từ pending intent of notification to PlayMusicActivity
 */
public class BroadcastMusic extends BroadcastReceiver {
    OnclickNotifyBroadcast onclickNotifyBroadcast;

    public void setMyBroadcastCall(OnclickNotifyBroadcast myBroadcastCall) {
        this.onclickNotifyBroadcast = myBroadcastCall;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {
            case Constant.BUTTON_PREVIOUS:
                onclickNotifyBroadcast.onClickPrevious();
                break;
            case Constant.BUTTON_PLAY:
                onclickNotifyBroadcast.onClickPlay();
                break;
            case Constant.BUTTON_NEXT:
                onclickNotifyBroadcast.onClickNext();
                break;
        }

    }

    public interface OnclickNotifyBroadcast {
        void onClickPrevious();

        void onClickPlay();

        void onClickNext();
    }
}
