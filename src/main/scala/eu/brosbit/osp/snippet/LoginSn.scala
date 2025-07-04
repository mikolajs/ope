package eu.brosbit.osp.snippet
import _root_.net.liftweb.util._

import _root_.net.liftweb.common._
import net.liftweb._
import http.{S, SHtml, SessionVar}
import mapper.By
import eu.brosbit.osp.model._
import json.JsonDSL._
//import json.JsonAST._
//import json.JsonParser
//import org.bson.types.ObjectId
import Helpers._
import eu.brosbit.osp.model.SubjectTeach
//import scala.util.matching.Regex

object SubjectChoose extends SessionVar[Long](0L)

object LevelChoose extends SessionVar[Int](1)

class LoginSn {
  val redirectUrl = S.queryString.map(s => {
    val arr = s.drop(2).split('&')
    arr.head + (if(arr.length > 1) "?" + arr.drop(1).mkString("&") else "")
  }).getOrElse("/login")
  val userBox = User.currentUser
  //println("RedirectTo: " + redirectUrl)

  def show() = {
    userBox match {
      case Full(user) =>
        "#logInfo" #> <span>
          <span class="glyphicon glyphicon-user"></span>{user.getFullName}
          <a href="/user_mgt/logout" class="btn btn-lg btn-danger" role="button" title="Wyloguj" id="logoutButton">
            Wyloguj
          </a>
        </span>
      case _ =>
        "#logInfo" #> <span>
          <span class="glyphicon glyphicon-user"></span>
          Niezalogowano
          <a href="/login" role="button" class="btn btn-lg btn-info" title="Zaloguj" id="loginButton">
            Zaloguj
          </a>
        </span> &
          "#changePass" #> <span></span>

    }
  }

  def mkLogIn() = {
    var login = ""
    var pass = ""
    var message = ""

    def mkLog():Unit = {
      val reg = "^[0-9]{11}$".r
      val pesel_? = reg.findFirstIn(login.trim) match {
        case Some(str) => true
        case _ => false
      }

      if (pesel_?) {
        User.findAll(By(User.pesel, login.trim)) match {
          case user :: other => {
            if (user.role == "u" || user.role == "r") {
              if (user.password.match_?(pass.trim)) {
                User.logUserIn(user)
                S.redirectTo(redirectUrl)
              } else message = " Błędne hasło "
            } else message = " Będąc nauczycielem wpisz email jako login"
          }
          case _ => message = " Nie znaleziono PESELu. "
        }
      }
      else {
        User.findAll(By(User.email, login.trim)) match {
          case user :: other => {
            if (user.role == "n" || user.role == "a" || user.role == "d") {
              if (user.password.match_?(pass.trim)) {
                User.logUserIn(user)
                S.redirectTo(redirectUrl)
              }
              else message = " Błędne hasło "
            } else message = " Będąc uczeniem wpisz PESEL jako login"
          }
          case _ => message = " Nie znaleziono adresu email. "
        }
      }
      S.notice(message)
    }

    userBox match {
      case Full(user) => {
        "form" #> <span></span>
      }
      case _ => {
        "#login" #> SHtml.text(login, login = _) &
          "#password" #> SHtml.password(pass, pass = _) &
          "#mkLog" #> SHtml.submit("Zaloguj", mkLog)
      }
    }
  }

  def showMenu():CssSel = {
    val loged_? = !userBox.isEmpty
    var viewH = "#"
    var viewC = "btn btn-default btn-lg"
    var secretariatH = "#"
    var secretariatC = "btn btn-default btn-lg"
    var educontentH = "#"
    var educontentC = "btn btn-default btn-lg"
    var registerH = "#"
    var registerC = "btn btn-default btn-lg"
    var docH = "#"
    var docC = "btn btn-default btn-lg"
    var galH = "#"
    var galC = "btn btn-default btn-lg"
    var slideH = "#"
    var slideC = "btn btn-default btn-lg"
    if (loged_?) {
      val user = userBox.openOrThrowException("Niemożliwe box nie jest pusty!")
      if (user.role.get == "n" || user.role.get == "a" || user.role.get == "d") {
        educontentH = "/educontent/index"
        educontentC = "btn btn-info btn-lg"
        registerH = "/register/index"
        registerC = "btn btn-info btn-lg"
        docH = "/documents/doctemplate"
        docC = "btn btn-info btn-lg"
        galH = "/galleries"
        galC = "btn btn-info btn-lg"
        slideH = "/editslideimg"
        slideC = "btn btn-info btn-lg"

      }
      else {
        if (user.role.get == "u" || user.role.get == "r") {
          viewH = "/view/courses"
          viewC = "btn btn-info btn-lg"
        }
      }
      if (user.role.get == "s" || user.role.get == "a" || user.role.get == "d") {
        secretariatH = "/secretariat/index"
        secretariatC = "btn btn-info btn-lg"
      }
    }
    "#viewA [href]" #> viewH &
      "#viewA [class]" #> viewC &
      "#secretariatA [href]" #> secretariatH &
      "#secretariatA [class]" #> secretariatC &
      "#educontentA [href]" #> educontentH &
      "#educontentA [class]" #> educontentC &
      "#registerA [href]" #> registerH &
      "#registerA [class]" #> registerC &
      "#docA [href]" #> docH &
      "#docA [class]" #> docC &
      "#slideimg [href]" #> slideH &
      "#slideimg [class]" #> slideC &
      "#galleries [href]" #> galH &
      "#galleries [class]" #> galC


  }

  private def intalizeSubjectAndLevelChoice(user: User):Unit = {
    val subjs = SubjectTeach.findAll(("authorId" -> user.id.get), ("$orderby" -> ("prior" -> 1)))
    if (subjs.nonEmpty) {
      SubjectChoose.set(subjs.head.id)
      LevelChoose.set(subjs.head.lev)
    }

  }

}