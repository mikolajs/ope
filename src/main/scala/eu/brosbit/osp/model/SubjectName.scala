package eu.brosbit.osp.model
import net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

class SubjectName extends LongKeyedMapper[SubjectName] with IdPK {
  def getSingleton: SubjectName.type = SubjectName
  object name extends MappedString(this, 40)
  object short extends MappedString(this, 5)
  object nr extends MappedInt(this)
  object scratched extends MappedBoolean(this)
}
object SubjectName extends SubjectName with LongKeyedMetaMapper[SubjectName] {

}
