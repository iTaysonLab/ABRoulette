package bruhcollective.itaysonlab.abroulette.hook.modules

import android.content.res.Resources
import bruhcollective.itaysonlab.abroulette.hook.obf.ObfuscatedPaths
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.android.ImageViewClass
import com.highcapable.yukihookapi.hook.type.android.ViewClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntType

class RoundedArtworkModule: BaseModule() {
  val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
  val Float.dp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

  override fun hook(pkg: PackageParam, path: ObfuscatedPaths) = with(pkg) {
    findClass("com.spotify.encore.consumer.elements.artwork.ArtworkView").hook {
      injectMember {
        method {
          name = "a"
          param(findClass("p.kf1").instance!!)
        }

        afterHook {
          if (!preferences.getBoolean("ah_round")) return@afterHook
          val coverSize = method {
            name = "getCoverArtSize"
            result = IntType
          }.get(instance).invoke<Int>()!!

          val coverRadius = coverSize / 12f

          field {
            name = "G"
            type = FloatType
          }.get(instance).set(coverRadius)

          findClass("p.e8r").instance!!.getDeclaredMethod("c", ViewClass, FloatType).invoke(null, instance, coverRadius)
        }
      }
    }

    findClass("p.lwr").hook {
      injectMember {
        method {
          name = "o"
        }

        afterHook {
          if (!preferences.getBoolean("ah_round")) return@afterHook
          val coverSize = field {
            name = "R"
            type = ImageViewClass
          }.get(instance).self

          findClass("p.e8r").instance!!.getDeclaredMethod("c", ViewClass, FloatType)
            .invoke(null, coverSize, 16.dp)
        }
      }
    }
  }
}