package bruhcollective.itaysonlab.abroulette.hook.obf

enum class ObfuscatedPaths (
  val appVersionName: String,
  val remoteConfig: Pair<String, String>,
  val transparentBars: String,
) {
  SP1081("8.7.6.1081", "p.tno" to "a", "p.bsr"),
  SP1087("8.7.6.1087", "p.tno" to "a", "p.bsr"),
  SP127("8.7.10.127", "p.jvo" to "a", "p.f0s"),
  SP901("8.7.26.901", "p.qjq" to "a", "p.nst"),
}