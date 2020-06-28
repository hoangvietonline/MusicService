package hoangviet.ndhv.demoui.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsFactory private constructor() {
    private var mSharedPreferences: SharedPreferences? = null
    fun initConfig(context: Context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    operator fun <T> get(key: String?, anonymousClass: Class<T>): T? {
        if (mSharedPreferences == null) {
            throw RuntimeException("Please int Shared Preferences first!")
        }
        when (anonymousClass) {
            String::class.java -> {
                return mSharedPreferences!!.getString(key, "") as T?
            }
            Boolean::class.java -> {
                return java.lang.Boolean.valueOf(mSharedPreferences!!.getBoolean(key, false)) as T
            }
            Float::class.java -> {
                return java.lang.Float.valueOf(mSharedPreferences!!.getFloat(key, 0f)) as T
            }
            Int::class.java -> {
                return Integer.valueOf(mSharedPreferences!!.getInt(key, 0)) as T
            }
            Long::class.java -> {
                return java.lang.Long.valueOf(mSharedPreferences!!.getLong(key, 0)) as T
            }
            else -> return null
        }
    }

    fun <T> put(key: String?, data: T) {
        if (mSharedPreferences == null) {
            throw RuntimeException("Please int Shared Preferences first!")
        }
        val editor = mSharedPreferences!!.edit()
        if (data is String) {
            editor.putString(key, data as String)
        } else if (data is Boolean) {
            editor.putBoolean(key, (data as Boolean))
        } else if (data is Float) {
            editor.putFloat(key, (data as Float))
        } else if (data is Int) {
            editor.putInt(key, (data as Int))
        } else if (data is Long) {
            editor.putLong(key, (data as Long))
        }
        editor.apply()
    }

    fun clear() {
        if (mSharedPreferences == null) {
            throw RuntimeException("Please int Shared Preferences first!")
        }
        mSharedPreferences!!.edit().clear().apply()
    }

    fun contain(key: String?): Boolean {
        return mSharedPreferences != null && mSharedPreferences!!.contains(key)
    }

    companion object {
        private const val PREFS_NAME = "MusicPrefs"
        private var mInstance: SharedPrefsFactory? = null
        val instance: SharedPrefsFactory?
            get() {
                if (mInstance == null) {
                    mInstance = SharedPrefsFactory()
                }
                return mInstance!!
            }
    }
}