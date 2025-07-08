/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package eu.brosbit.osp.snippet.register

//import _root_.java.util.{Date, GregorianCalendar, TimeZone}
//import _root_.scala.xml.{NodeSeq, Text, XML}
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{S, SessionVar}
//import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}
import Helpers._
import eu.brosbit.osp.model._

object ClassChoose extends SessionVar[Long](0L)
object ClassString extends SessionVar[String]("Nie wybrano!")

class MainTeacher {
  val user: User = User.currentUser.openOr(S.redirectTo("/login"))
  def classList():CssSel = {
    val classParamStr = S.param("class").openOr("0")
    val paramClass = tryo(classParamStr.toInt).getOrElse(0)
    val classes = if(user.role.get == "t") ClassModel.findAll(By(ClassModel.teacher, user.id.get),OrderBy(ClassModel.level, Ascending)).filter(!_.scratched.get)
    else ClassModel.findAll(OrderBy(ClassModel.level, Ascending)).filter(!_.scratched.get)
    if (paramClass != 0) {
      val chosenClass = classes.filter(theClass => theClass.id.get == paramClass)
      chosenClass match {
        case Nil =>
        case list =>
          val theClass = list.head
          ClassChoose.set(theClass.id.get)
          ClassString.set(theClass.classString())
      }
    }

    "a" #> classes.map(classItem => {
      "a" #> <a href={"/register/index/" + classItem.id.toString}>
        {classItem.classString()}
      </a>
    }) &
      "#choosenclass *" #> {
        if (ClassChoose.is == 0) "wybierz!"
        else ClassString.is
      }
  }
}

