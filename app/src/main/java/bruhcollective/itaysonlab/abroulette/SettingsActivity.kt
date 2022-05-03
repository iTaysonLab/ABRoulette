package bruhcollective.itaysonlab.abroulette

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import bruhcollective.itaysonlab.abroulette.hook.obf.ObfuscatedPaths
import bruhcollective.itaysonlab.abroulette.hook.utils.SpotifyPreferences
import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus
import com.topjohnwu.superuser.Shell

class SettingsActivity : AppCompatActivity(),
  PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.settings_activity)
    if (savedInstanceState == null) {
      supportFragmentManager
        .beginTransaction()
        .replace(R.id.settings, HeaderFragment())
        .commit()
    } else {
      title = savedInstanceState.getCharSequence("settingsActivityTitle")
    }

    supportFragmentManager.addOnBackStackChangedListener {
      if (supportFragmentManager.backStackEntryCount == 0) {
        setTitle(R.string.title_activity_settings)
      }
    }

    supportActionBar?.setDisplayHomeAsUpEnabled(false)
    supportActionBar?.subtitle = "module status: ${if (YukiHookModuleStatus.isActive()) "enabled" else "disabled"}"
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    // Save current activity title so we can set it again after a configuration change
    outState.putCharSequence("settingsActivityTitle", title)
  }

  override fun onSupportNavigateUp(): Boolean {
    if (supportFragmentManager.popBackStackImmediate()) {
      return true
    }

    return super.onSupportNavigateUp()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val mItem = menu.add(0, 1, 0, "Apply")
    mItem.setIcon(R.drawable.ic_baseline_check_24)
    mItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == 1) {
      SpotifyPreferences.Root.copy(this)
      Shell.sh("am force-stop com.spotify.music").submit {
        startActivity(packageManager.getLaunchIntentForPackage("com.spotify.music"))
      }
      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onPreferenceStartFragment(
    caller: PreferenceFragmentCompat,
    pref: Preference
  ): Boolean {
    // Instantiate the new Fragment
    val args = pref.extras
    val fragment = supportFragmentManager.fragmentFactory.instantiate(
      classLoader,
      pref.fragment!!
    ).apply {
      arguments = args
      setTargetFragment(caller, 0)
    }
    // Replace the existing Fragment with the new Fragment
    supportFragmentManager.beginTransaction()
      .replace(R.id.settings, fragment)
      .addToBackStack(null)
      .commit()
    title = pref.title
    return true
  }

  class HeaderFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      setPreferencesFromResource(R.xml.abr_prefs, rootKey)

      val supportedVersions = ObfuscatedPaths.values().map { it.appVersionName }

      findPreference<Preference>("about")?.summary = "ABRoulette for Spotify v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) by iTaysonLab\n\nMade for Spotify ${supportedVersions.joinToString()} [auto-handling TODO]"

      val pi = try { requireActivity().packageManager.getPackageInfo("com.spotify.music", 0) } catch (e: Exception) { null }
      if (pi == null) {
        findPreference<Preference>("sp_version")?.summary = "application is not installed"
      } else if (supportedVersions.contains(pi.versionName)) {
        findPreference<Preference>("sp_version")?.setIcon(R.drawable.ic_baseline_check_circle_24)
        findPreference<Preference>("sp_version")?.summary = "version is supported"
      } else {
        findPreference<Preference>("sp_version")?.summary = "version not supported: ${pi.versionName}\n\n${supportedVersions.joinToString()} is supported at the moment"
      }
    }
  }
}