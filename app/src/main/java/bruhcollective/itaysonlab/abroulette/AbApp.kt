package bruhcollective.itaysonlab.abroulette

import android.app.Application
import android.util.Log
import com.topjohnwu.superuser.Shell

class AbApp: Application() {
  override fun onCreate() {
    super.onCreate()
    Log.w("ABRoulette", "ABRoulette ${BuildConfig.VERSION_NAME} by iTaysonLab")
    Shell.enableVerboseLogging = BuildConfig.DEBUG
    Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_REDIRECT_STDERR).setTimeout(10))
  }
}