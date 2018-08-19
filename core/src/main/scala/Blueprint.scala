//: ----------------------------------------------------------------------------
//:
//:   Licensed under the Apache License, Version 2.0 (the "License");
//:   you may not use this file except in compliance with the License.
//:   You may obtain a copy of the License at
//:
//:       http://www.apache.org/licenses/LICENSE-2.0
//:
//:   Unless required by applicable law or agreed to in writing, software
//:   distributed under the License is distributed on an "AS IS" BASIS,
//:   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//:   See the License for the specific language governing permissions and
//:   limitations under the License.
//:
//: ----------------------------------------------------------------------------
package nelson

// import cats.effect.IO
import java.time.Instant
import cats.syntax.either._
// import org.apache.commons.codec.digest.DigestUtils

case class Blueprint(
  name: String,
  description: Option[String],
  revision: Blueprint.Revision,
  state: Blueprint.State,
  sha256: Sha256,
  template: String,
  createdAt: Instant = Instant.now
){
  override def toString: String =
    s"${name}@${revision.toString}"
}

object Blueprint {

  def parseNamedRevision(serialized: String): Either[Throwable,(String, Revision)] =
    serialized.split('@') match {
      case Array(name, "HEAD") => Right((name, Blueprint.Revision.HEAD))
      case Array(name, rstr)   => Revision.fromString(rstr).map(x => (name,x))
      case Array(name)         => Right((name, Blueprint.Revision.HEAD))
    }

  sealed trait Revision
  object Revision {
    final object HEAD extends Revision
    final case class Discrete(number: Int) extends Revision {
      override def toString: String = number.toString
    }

    def fromString(s: String): Either[Throwable,Revision] =
      if (s.toUpperCase == HEAD.toString) Right(HEAD)
      else Either.catchNonFatal(s.toInt).map(Discrete)
  }

  sealed trait State
  object State {
    final object Pending extends State
    final object Validating extends State
    final object Active extends State
    final object Deprecated extends State
    final object Invalid extends State
  }

  // final case class Template(content: String){
  //   val sha256: String = DigestUtils.sha256Hex(content)
  // }
}
