package bruhcollective.itaysonlab.abroulette.hook.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.core.content.edit
import bruhcollective.itaysonlab.abroulette.BuildConfig
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileOutputStream
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException

sealed class SpotifyPreferences {
  abstract fun getBoolean(key: String, default: Boolean = false): Boolean

  // Xposed Env
  class Internal (spotifyContext: Context): SpotifyPreferences() {
    private val sp: SharedPreferences

    init {
      if (spotifyContext.packageName != "com.spotify.music") throw IllegalArgumentException("SPInternal is not attached to Spotify application")
      sp = spotifyContext.getSharedPreferences("ABRPrefs", Context.MODE_PRIVATE)
    }

    override fun getBoolean(key: String, default: Boolean) = sp.getBoolean(key, default)
  }

  // Configurator
  object Root : SpotifyPreferences() {
    override fun getBoolean(key: String, default: Boolean) = default // stub

    fun copy(ctx: Context) {

      try {
        val internalFile = File(
          ctx.filesDir.parentFile,
          "shared_prefs/${BuildConfig.APPLICATION_ID}_preferences.xml"
        )

        val externalFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          SuFile.open("/data_mirror/data_ce/null/0/com.spotify.music/shared_prefs/ABRPrefs.xml")
        } else {
          SuFile.open("${ctx.filesDir.parentFile!!.parent}/com.spotify.music/shared_prefs/ABRPrefs.xml")
        }

        externalFile.parentFile?.mkdirs()

        internalFile.inputStream().use { input ->
          SuFileOutputStream.open(externalFile).use { output ->
            input.copyTo(output, DEFAULT_BUFFER_SIZE)
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(ctx, "Error when copying to target! Check your root access.", Toast.LENGTH_LONG).show()
      }
    }
  }
}