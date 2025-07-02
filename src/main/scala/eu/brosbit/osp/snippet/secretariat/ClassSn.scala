package eu.brosbit.osp.snippet.secretariat

import _root_.net.liftweb.util._
import _root_.net.liftweb.http.SHtml
import _root_.net.liftweb.common._
import eu.brosbit.osp.model._
import _root_.net.liftweb.mapper.{Ascending, By, OrderBy}
import Helpers._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JE._
import _root_.net.liftweb.http.js.JsCmd

class ClassSn extends {

  def teachers(): List[(String, String)] = User.findAll(By(User.role, "n")).filter(teacher => !teacher.scratched.get).
    map(teacher => {
      val teacherString = teacher.shortInfo
      (teacherString, teacherString)
    })

  def classList(): CssSel = {
    //var out:NodeSeq = NodeSeq.Empty
    val classList: List[ClassModel] = ClassModel.findAll(OrderBy(ClassModel.level, Ascending))
    //val user:List[User] = User.find(class.id.get).get
    "tr" #> classList.map(theClass => {
      "tr [class]" #> {
        if (theClass.scratched.get) "scratched" else ""
      } &
        ".id" #> <td>
          {theClass.id.get.toString}
        </td> &
        ".level" #> <td>
          {theClass.level.get.toString}
        </td> &
        ".division" #> <td>
          {if (theClass.level.get > -1) theClass.division.get
          else "Absolwenci"}
        </td> &
        ".teacher" #> <td>
          {theClass.teacher.obj match {
            case Full(teacher) => teacher.shortInfo
            case _ => "Brak"
          }}
        </td> &
        ".description" #> <td>
          {theClass.descript.get}
        </td>
    })
  }

  def editAjax():CssSel = {
    var id = ""
    var level = ""
    var division = ""
    var teacher = ""
    var description = ""
    var errorInfo = ""

    def save() = {
      val theClass = ClassModel.find(id).openOr(ClassModel.create)
      val teacherId = refitTeacherIdFromShortInfo(teacher)
      val teacherModel = User.find(teacherId).openOr(User.create)
      if (teacherModel.role.get == "n") {
        val levelInt = tryo(level.toInt).openOr(0)
        if(levelInt > -1) {
          theClass.level(levelInt).descript(description).division(division).
            teacher(teacherModel.id.get).scratched(false).save
          changeAllPupils(theClass.id.get, levelInt, levelInt.toString + division)
        }
        else {
          theClass.level(levelInt).descript(description).division("--").
            teacher(teacherModel.id.get).scratched(true).save
          changeAllPupils(theClass.id.get, levelInt, "-ab")
        }
        if (id == "") {
          id = theClass.id.get.toString
          JsFunc("editForm.insertRowAndClear", id).cmd
        }
        else {
          id = theClass.id.get.toString
          JsFunc("editForm.insertRowAndClose", id).cmd
        }

      }
      else {
        errorInfo = "Nieprawidłowy nauczyciel"
        JsNull.cmd
      }

    }

    def delete():JsCmd = {
      ClassModel.find(id) match {
        case Full(theClass) =>
          theClass.scratched(true).save
          JsFunc("editForm.scratchRow", id).cmd
        case _ => Alert("Nie ma takiej klasy")
      }
    }

    def changeAllPupils(idClass: Long, lev:Int, desc: String): Unit ={
      User.findAll(By(User.classId, idClass)).map( u =>
        u.classNumber(lev).classInfo(desc).save
      )
    }

    val teacherPairList = teachers()
    val levels = (0 to 6).toList.map(level => (level.toString, level.toString)) ::: List(("-1", "Absolwenci"))

    val form = "#id" #> SHtml.text(id, x => id = x.trim, "readonly" -> "readonly") &
      "#level" #> SHtml.select(levels, Full("0"), level = _) &
      "#division" #> SHtml.text(division, x => division = x.trim) &
      "#teacher" #> SHtml.select(teacherPairList, Full(""), teacher = _) &
      "#description" #> SHtml.text(description, x => description = x.trim) &
      "#addInfo *" #> errorInfo &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image",
        "onclick" -> "return confirm('Na pewno wprowadzić zmiany?')") andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }

  private def refitTeacherIdFromShortInfo(shortInfo: String) =
    shortInfo.split('[').last.split(']').head

}
 
