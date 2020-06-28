package hoangviet.ndhv.demoui

import android.app.Application
import hoangviet.ndhv.demoui.utils.SharedPrefsFactory

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        SharedPrefsFactory.instance?.initConfig(this)
    }
}