package eu.brosbit.osp.model

import net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

class UserChangeList extends LongKeyedMapper[UserChangeList] with IdPK {
  def getSingleton = UserChangeList
  object firstName extends MappedString(this, 30)
  object lastName extends MappedString(this, 40)
  object email extends MappedEmail(this, 48)
  object passStr extends MappedString(this, 12)
  object phone extends MappedString(this, 12)
  object date extends MappedDate(this)
  object user extends MappedLongForeignKey(this, User)
}

object UserChangeList extends UserChangeList with LongKeyedMetaMapper[UserChangeList] {
}


