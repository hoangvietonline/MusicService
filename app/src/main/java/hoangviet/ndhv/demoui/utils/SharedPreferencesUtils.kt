package hoangviet.ndhv.demoui.utils

class SharedPreferencesUtils {
    private val MODE_PLAY_MUSIC = "mode_play_music"

    private fun SharedPreferencesUtils() {
        // No-op
    }

    fun putModePlayMusic(value: String?) {
        SharedPrefsFactory.instance?.put(MODE_PLAY_MUSIC, value)
    }

    fun getModePlayMusic(): String? {
        return SharedPrefsFactory.instance?.get(MODE_PLAY_MUSIC, String::class.java)
    }
}