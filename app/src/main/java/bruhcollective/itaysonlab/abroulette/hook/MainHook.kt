package bruhcollective.itaysonlab.abroulette.hook

import android.content.Context
import android.util.Log
import bruhcollective.itaysonlab.abroulette.BuildConfig
import bruhcollective.itaysonlab.abroulette.hook.modules.*
import bruhcollective.itaysonlab.abroulette.hook.obf.ObfuscatedPaths
import bruhcollective.itaysonlab.abroulette.hook.utils.SpotifyPreferences
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy

@InjectYukiHookWithXposed
class MainHook : YukiHookXposedInitProxy {
  private var preferences: SpotifyPreferences? = null

  private fun PackageParam.safeHook(path: ObfuscatedPaths, vararg hooks: BaseModule) {
    hooks.forEach { it.setPreferences(preferences!!).safeHook(this, path) }
  }

  override fun onHook() {
    YukiHookAPI.configs {
      isDebug = BuildConfig.DEBUG
    }

    YukiHookAPI.encase {
      loadApp("com.spotify.music") {
        findClass("com.spotify.music.SpotifyApplication").hook {
          injectMember {
            method {
              name = "onCreate"
            }

            beforeHook {
              (Context::class.java.getDeclaredMethod("getApplicationContext").invoke(instance) as Context).also { context ->
                if (preferences == null) {
                  preferences = SpotifyPreferences.Internal(context)
                  val appVer = context.packageManager.getPackageInfo(context.packageName, 0).versionName
                  val op = ObfuscatedPaths.values().firstOrNull { it.appVersionName == appVer }
                  if (op == null) {
                    Log.e("ABRoulette", "Unsupported version! (version = $appVer, supported = ${ObfuscatedPaths.values().joinToString { it.appVersionName }}")
                  } else {
                    Log.w("ABRoulette", "Version is supported! (version = $appVer, info = $op)")
                    this@loadApp.safeHook(
                      op,
                      RemoteConfigModule(),
                      TransparentSystemBarsModule(),
                      RoundedArtworkModule(),
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}