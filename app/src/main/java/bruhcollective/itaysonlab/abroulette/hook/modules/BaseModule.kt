package bruhcollective.itaysonlab.abroulette.hook.modules

import bruhcollective.itaysonlab.abroulette.hook.obf.ObfuscatedPaths
import bruhcollective.itaysonlab.abroulette.hook.utils.SpotifyPreferences
import com.highcapable.yukihookapi.hook.param.PackageParam

abstract class BaseModule {
  lateinit var preferences: SpotifyPreferences
  private set

  fun setPreferences(preferences: SpotifyPreferences) = apply { this@BaseModule.preferences = preferences }
  fun safeHook(pkg: PackageParam, path: ObfuscatedPaths) = try { hook(pkg, path) } catch (e: Exception) { e.printStackTrace() }
  abstract fun hook(pkg: PackageParam, path: ObfuscatedPaths)
}