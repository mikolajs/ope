package eu.brosbit.osp.model

import net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

class MarkMap extends LongKeyedMapper[MarkMap] with IdPK {
  def getSingleton = MarkMap
  object name extends MappedString(this, 2)
  object value extends MappedInt(this)
}

object MarkMap extends MarkMap with LongKeyedMetaMapper[MarkMap] {
  override def fieldOrder = List(id, name, value)
}

