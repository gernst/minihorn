import mill._
import mill.scalalib._

object horn extends ScalaModule {
  def scalaVersion = "2.12.15"
  def mainClass = Some("horn.Main")

  def ivyDeps = Agg(ivy"com.lihaoyi::sourcecode:0.2.0")

  def unmanagedClasspath = T {
    Agg.from(os.list(millSourcePath / "lib").map(PathRef(_)))
  }
}
