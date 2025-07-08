package eu.brosbit.osp.snippet.register

//import _root_.java.util.{Date, GregorianCalendar, TimeZone}
//import _root_.scala.xml.{NodeSeq, Text, XML}
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.S
//import _root_.net.liftweb.common._
//import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}
import Helpers._
import eu.brosbit.osp.model._

class BaseTeacher {
  val user: User.TheUserType = User.currentUser.openOr(S.redirectTo("/login"))

  def chosenClass():CssSel = {
    if (ClassChoose.is == 0) S.redirectTo("/register/index")
    else "#choosenclass *" #> ClassString.is
  }

  protected def isBriningUp(theClass:ClassModel): Boolean = {
    if (user.id.get == theClass.teacher.get || user.role.get == "a") true
    else false
  }

  protected def isAdmin: Boolean = user.role.get == "a"

}


