package eu.brosbit.osp.snippet.secretariat

//import scala.xml.{NodeSeq, Text, XML}
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{SHtml, S}
import _root_.net.liftweb.common._
//import java.util.{Date, Random, GregorianCalendar, TimeZone}
import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}
import Helpers._
import eu.brosbit.osp.model._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._

class AddTeacherSn {

  def teacherList():CssSel = {
    val teachers: List[User] = User.findAll(By(User.role, "n"), OrderBy(User.id, Ascending))

    "tr" #> teachers.map(teacher => {
      "tr [class]" #> {
        if (teacher.scratched.get) "scratched" else ""
      } &
        ".id" #> <td>
          {teacher.id.get.toString}
        </td> &
        ".firstname" #> <td>
          {teacher.firstName.get}
        </td> &
        ".lastname" #> <td>
          {teacher.lastName.get}
        </td> &
        ".email" #> <td>
          {teacher.email.get}
        </td> &
        ".phone" #> <td>
          {teacher.phone.get}
        </td>
    })
  }

  def editAjax():CssSel = {
    var userId = ""
    var lastName = ""
    var firstName = ""
    var telephone = ""
    var email = ""

    def save(): JsCmd = {
      val user = User.find(userId).openOr(User.create)
      user.lastName(lastName).firstName(firstName).phone(telephone).email(email).
        role("n").scratched(false).validated(true).save

      if (userId == "") {
        userId = user.id.toString
        JsFunc("editForm.insertRowAndClear", userId).cmd
      }
      else {
        userId = user.id.toString
        JsFunc("editForm.insertRowAndClose", userId).cmd
      }
    }

    def delete():JsCmd = {
      User.find(userId) match {
        case Full(user) => {
          user.scratched(true).validated(false).save
          JsFunc("editForm.scratchRow", userId).cmd
        }
        case _ => Alert("Nie ma takiego użytkownika")
      }
    }

    val form = "#id" #> SHtml.text(userId, x => userId = x.trim, "readonly" -> "readonly") &
      "#lastname" #> SHtml.text(lastName, x => lastName = x.trim) &
      "#firstname" #> SHtml.text(firstName, x => firstName = x.trim) &
      "#email" #> SHtml.text(email, x => email = x.trim) &
      "#telephone" #> SHtml.text(telephone, x => telephone = x.trim) &
      "#delete" #> SHtml.ajaxSubmit("Usuń", delete, "type" -> "image",
        "onclick" -> "return confirm('Na pewno usunąć użytkownika?')") &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image",
        "onclick" -> "return editForm.validate();") andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }
}


