package hoangviet.ndhv.demoui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class BroadcastMusic extends BroadcastReceiver {
    public static final String BUTTON_PREVIOUS = "button_previous";
    public static final String BUTTON_PLAY = "button_play";
    public static final String BUTTON_NEXT = "button_next";
    OnclickNotifyBroadcast onclickNotifyBroadcast;

    public void setMyBroadcastCall(OnclickNotifyBroadcast myBroadcastCall) {
        this.onclickNotifyBroadcast = myBroadcastCall;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {
            case BUTTON_PREVIOUS:
                onclickNotifyBroadcast.onClickPrevious();
                break;
            case BUTTON_PLAY:
                onclickNotifyBroadcast.onClickPlay();
                break;
            case BUTTON_NEXT:
                onclickNotifyBroadcast.onClickNext();
                break;
        }

    }

    interface OnclickNotifyBroadcast {
        void onClickPrevious();

        void onClickPlay();

        void onClickNext();
    }
}
