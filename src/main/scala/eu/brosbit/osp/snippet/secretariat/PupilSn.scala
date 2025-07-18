
package eu.brosbit.osp.snippet.secretariat

import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{S, SHtml, SessionVar}
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.By
import Helpers._
import eu.brosbit.osp.model._
import eu.brosbit.osp.lib.Formater
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JE._

import scala.xml.NodeSeq

object ClassChoose extends SessionVar[Long](0L)

class PupilSn {
  private val classList = ClassModel.findAll.map(classModel => (classModel.id.toString, classModel.classString()))
  if(ClassChoose.get == 0L)  ClassChoose(classList.head._1.toLong)
  def pupilList(): CssSel = {

    val pupils = User.findAll(By(User.role, "u"), By(User.classId, ClassChoose.get))
    "tr" #> pupils.map(pupil => {
      "tr [class]" #> {
        if (pupil.scratched.get) "scratched" else ""
      } &
        ".id" #> <td>
          {pupil.id.get.toString}
        </td> &
        ".firstname" #> <td>
          {pupil.firstName.get}
        </td> &
        ".lastname" #> <td>
          {pupil.lastName.get}
        </td> &
        ".birthdate" #> <td>
          {Formater.formatDate(pupil.birthDate.get)}
        </td> &
        ".classInfo" #> <td>
          {pupil.classInfo.get}
        </td> &
        ".pesel" #> <td>
          {pupil.pesel.get}
        </td> &
        ".email" #> <td>
          {pupil.email.get}
        </td>
    })
  }

  def selectClass(): CssSel = {
     "#getClass" #> SHtml.ajaxSelect(classList, Full(ClassChoose.get.toString), (idClass:String) => {
       ClassChoose(idClass.toLong)
       S.redirectTo("/secretariat/pupils")
     }) &
       "#csv [href]" #> ("/secretariat/classimport?id=" + ClassChoose.get.toString)
  }

  def editAjax(): CssSel = {
    var id = ""
    var firstName = ""
    var lastName = ""
    var birthDate = ""
    var pesel = ""
    var classId = ""
    var email = ""
    var errorInfo = ""

    def save() = {
      val pupil = User.find(id).openOr(User.create)
      ClassModel.find(classId) match {
        case Full(classModel) =>
          pupil.classInfo(classModel.classString()).
            classId(classModel.id.get)

        case _ =>
      }
      pupil.birthDate(Formater.fromStringToDate(birthDate)).firstName(firstName).
        lastName(lastName).pesel(pesel).scratched(false).role("u").email(email).
        password(Helpers.randomString(10)).validated(true).save
      if (id == "") {
        id = pupil.id.toString
        JsFunc("editForm.insertRowAndClear", id).cmd
      }
      else {
        id = pupil.id.toString
        JsFunc("editForm.insertRowAndClose", id).cmd
      }
    }

    def delete() = User.find(id) match {
        case Full(user) => {
          user.scratched(true).email("").save
          JsFunc("editForm.scratchRow", id).cmd
        }
        case _ => Alert("Nie ma takiego ucznia")
      }

    val form = "#id" #> SHtml.text(id, x => id = x.trim, "readonly" -> "readonly") &
      "#lastname" #> SHtml.text(lastName, x => lastName = x.trim) &
      "#firstname" #> SHtml.text(firstName, x => firstName = x.trim) &
      "#birthdate" #> SHtml.text(birthDate, x => birthDate = x.trim) &
      "#pesel" #> SHtml.text(pesel, x => pesel = x.trim) &
      "#classInfo" #> SHtml.select(classList, Full(ClassChoose.get.toString), classId = _) &
    "#email" #> SHtml.text(email, x => email = x.trim) &
      "#addInfo *" #> errorInfo &
      "#delete" #> SHtml.ajaxSubmit("Usuń", delete, "type" -> "image",
        "onclick" -> "return confirm('Na pewno usunąć klasę?')") &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image",
        "onclick" -> "return validateForm();") andThen SHtml.makeFormsAjax

    "form" #> ((in:NodeSeq) => form(in))
  }

}

