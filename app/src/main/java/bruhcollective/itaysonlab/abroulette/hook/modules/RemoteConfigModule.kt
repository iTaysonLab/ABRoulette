package bruhcollective.itaysonlab.abroulette.hook.modules

import android.util.Log
import bruhcollective.itaysonlab.abroulette.hook.obf.ObfuscatedPaths
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.StringType

class RemoteConfigModule: BaseModule() {
  private fun bool(name: String, default: Boolean = false) = preferences.getBoolean(name, default)

  override fun hook(pkg: PackageParam, path: ObfuscatedPaths) = with(pkg) {
    findClass(name = path.remoteConfig.first).hook {
      injectMember {
        method {
          name = path.remoteConfig.second
          param(StringType, StringType, BooleanType)
          returnType = BooleanType
        }

        afterHook {
          val module = args[0] as String
          val name = args[1] as String

          val default = args[2] as Boolean
          val current = result as Boolean

          when (module) {
            "android-lib-content-feed-flags" -> result = bool("ab_home_encore")
            "android-libs-voice-common" -> result = bool("ab_voice", true)
            "android-libs-greenroom" -> result = bool("ab_greenroom")
            "android-libs-micdrop-lyrics" -> result = bool("ab_micdrop")
            "android-feature-settings" -> result = bool("ab_settings")
            "android-feature-listening-history" -> result = bool("ab_filters_history")
            "arch-california" -> result = bool("ab_california")
            "android-libs-car-mode-engine" -> result = bool("ab_car", true)
          }

          result = when (name) {
            "enable_vocal_removal" -> bool("ab_micdrop")
            "hide_settings_button", "enable_editor", "navigation_to_profile_enabled" -> bool("ab_hide_settings")
            "enable_settings" -> !bool("ab_hide_settings")
            "enable_ignore_in_recs" -> bool("ab_exclude_rcm")
            "enable_by_you_and_by_spotify_filters" -> bool("ab_filters")
            "enable_silence_trimmer_android" -> bool("ab_silence")
            "enable_expand_button" -> bool("ab_lyr_expand")
            "enable_fullscreen_track_change" -> bool("ab_lyr_change")
            "show_lyrics_badge" -> bool("ab_lyr_badge", true)
            "enable_haptic_feedback_on_heart" -> bool("ab_haptic")
            "enable_encore_trackrows" -> bool("ab_encore_tracks")
            "encore_icons_redraw_enabled" -> bool("ab_redraw", true)
            "enable_hide_on_scroll" -> bool("ab_hide")
            "material_you_enabled" -> bool("ab_material")
            "use_shimmering" -> bool("ab_shimmer")
            "enable_discover_now_feed" -> bool("ab_discover")
            "enable_syllable_sync" -> bool("ab_lyr_syllables")
            "enable_floating_npb", "enable_transparent_tabbar" -> bool("ab_npb_float", true)
            "enable_color_extraction" -> bool("ab_npb_color", true)
            "enable_encore_header" -> bool("ab_encore_album", true)
            "enable_encore_search_header", "enable_encore_track_row_search" -> bool("ab_encore_search")
            "enable_swipeable_trackrows" -> bool("ab_encore_swipeable")
            "show_header_with_canvas" -> bool("ab_encore_canvasheader")
            "new_ui_enabled" -> bool("ab_sl_newui")
            "enable_find_header", "enable_find_header_with_buttons" -> bool("ab_find")
            "homething_settings_enable" -> true
            "content_filter_stacking_enabled" -> true
            "enable_mobius_widget" -> true
            // "enable_dac_artist_page" -> true
            else -> result
          }

          if (bool("ab_debug")) Log.d(
            "ABRoulette",
            "[BOOLEAN] [$module.${name}] default: $default, real: $current -> return $result"
          )
        }
      }

      injectMember {
        method {
          name = "b"
          param(StringType, StringType, java.lang.Enum::class.java)
          returnType = java.lang.Enum::class.java
        }

        afterHook {
          val module = args[0] as String
          val name = args[1] as String
          val default = args[2] as Enum<*>
          val current = result as Enum<*>

          if (name == "page_source" && bool("ab_dac")) {
            result = java.lang.Enum.valueOf(default::class.java as Class<Enum<*>>, "DAC_HOME")
          }

          if (bool("ab_debug")) Log.d(
            "ABRoulette",
            "[ENUM] [$module.${name}] default: $default, real: $current -> return $result"
          )
        }
      }
    }
  }
}