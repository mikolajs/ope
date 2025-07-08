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

object LoginStrings {
  val main: Array[String] = Array("Strona główna", "Informacje ogólne, aktualności, kontakt", "/index")
  val pupils: Array[String] = Array("Uczniowie", "Dostęp do platformy: materiały, sprawdziany", "/view/courses")
  val teachers: Array[String] = Array("Nauczyciele – edycja materiałów", "Dodawanie materiałów i edycja lekcji udostępnianych uczniom", "/educontent/index")
  val secretariat: Array[String] = Array("Edycja nauczycieli i uczniów", "Dodawanie nauczycieli, klas, i uczniów", "/secretariat/index")
  val register: Array[String] = Array("Dziennik", "Dane uczniów, zmiana haseł", "/register/index")
  val admin: Array[String] = Array("Zarządzanie aplikacją", "Zmiana ustwień strony głównej, usuwanie nieużytków", "/admin/index")
}

class LoginSn {
  private val redirectUrl = S.queryString.map(s => {
    val arr = s.drop(2).split('&')
    arr.head + (if(arr.length > 1) "?" + arr.drop(1).mkString("&") else "")
  }).getOrElse("/login")
  private val userBox = User.currentUser
  //println("RedirectTo: " + redirectUrl)

  def show(): CssSel = {
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

  def mkLogIn(): CssSel = {
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
    var allMenu: List[Array[String]] = List(LoginStrings.main)
    if (loged_?) {
      val user = userBox.openOrThrowException("Niemożliwe box nie jest pusty!")
      val role = user.role.get
      if (role == "n" || role == "a") allMenu = LoginStrings.register :: allMenu
      if (role == "n") allMenu = LoginStrings.teachers::allMenu
      if (role == "u" ) allMenu = LoginStrings.pupils::allMenu
      if (role == "s" || role == "a") allMenu = LoginStrings.secretariat::allMenu
      if (role == "a" ) allMenu = LoginStrings.admin::allMenu
    }
    val htmlToAdd = createHTML(allMenu.reverse)

      "#menuInfo" #> htmlToAdd
  }

  private def initializeSubjectAndLevelChoice(user: User):Unit = {
    val subjs: Seq[SubjectTeach] = SubjectTeach.findAll("authorId" -> user.id.get, "$orderby" -> ("prior" -> 1))
    if (subjs.nonEmpty) {
      SubjectChoose.set(subjs.head.id)
      LevelChoose.set(subjs.head.lev)
    }
  }

  private def mkOptionMenu(title:String, info:String, link:String) = {
    <div class="col-lg-6">
      <h2>{title}</h2>
      <p>{info}</p>
      <p>
        <a id="pageA" href={link} role="button" class="btn btn-info btn-lg">Przejdź
          <span class="glyphicon glyphicon-forward"></span>
        </a>
      </p>
      </div>
  }

  private def createHTML(menu:List[Array[String]])  = {
    menu.map(arr => mkOptionMenu(arr(0), arr(1), arr(2)))
  }

}