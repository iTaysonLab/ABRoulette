package bruhcollective.itaysonlab.abroulette.hook.modules

import android.app.Activity
import android.graphics.Color
import android.view.Window
import android.view.WindowManager
import bruhcollective.itaysonlab.abroulette.hook.obf.ObfuscatedPaths
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.android.BundleClass

class TransparentSystemBarsModule: BaseModule() {
  override fun hook(pkg: PackageParam, path: ObfuscatedPaths) = with(pkg) {
    findClass(path.transparentBars).hook {
      injectMember {
        method {
          name = "onCreate"
          param(BundleClass)
        }

        afterHook {
          if (!preferences.getBoolean("ah_transparent")) return@afterHook
          (Activity::class.java.getDeclaredMethod("getWindow").invoke(instance) as Window).apply {
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
          }
        }
      }
    }
  }
}