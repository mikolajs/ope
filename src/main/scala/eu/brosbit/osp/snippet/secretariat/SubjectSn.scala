
package eu.brosbit.osp.snippet.secretariat

import _root_.net.liftweb.util._
import _root_.net.liftweb.http.SHtml
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{Ascending, OrderBy}
import Helpers._
import eu.brosbit.osp.model.SubjectName
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmd

class SubjectSn {

  def subjectList():CssSel = {
    //var out:NodeSeq = NodeSeq.Empty
    val subjects: List[SubjectName] = SubjectName.findAll(OrderBy(SubjectName.nr, Ascending))
    "tr" #> subjects.map(subject => {
      "tr [class]" #> {
        if (subject.scratched.get) "scratched" else ""
      } &
        ".id" #> <td>
          {subject.id.get.toString}
        </td> &
        ".longname" #> <td>
          {subject.name.get}
        </td> &
        ".shortname" #> <td>
          {subject.short.get}
        </td> &
        ".ordernumber" #> <td>
          {subject.nr.get.toString}
        </td>
    })
  }

  def editAjax():CssSel = {
    var id = ""
    var longName = ""
    var shortName = ""
    var ordernumber = ""
    var errorInfo = ""

    def save(): JsCmd = {
      val subject = SubjectName.find(id).openOr(SubjectName.create)
      val number = tryo(ordernumber.toInt).openOr(36)
      if (subject.name(longName.trim).short(shortName.trim).nr(number).scratched(false).save)
        errorInfo = ""
      else errorInfo = "Nieudany zapis"
      if (id == "") {
        id = subject.id.toString()
        JsFunc("editForm.insertRowAndClear", id).cmd
      }
      else {
        id = subject.id.toString()
        JsFunc("editForm.insertRowAndClose", id).cmd
      }
    }

    def delete(): JsCmd = {
      SubjectName.find(id) match {
        case Full(subject) => {
          subject.scratched(true).save
          JsFunc("editForm.scratchRow", id).cmd
        }
        case _ => Alert("Nie ma takiego przedmiotu")
      }
    }
    val numbers = (1 to 32).toList.map(i => (i.toString, i.toString))

    val form = "#id" #> SHtml.text(id, x => id = x.trim, "readonly" -> "readonly") &
      "#longname" #> SHtml.text(longName, x => longName = x.trim) &
      "#shortname" #> SHtml.text(shortName, x => shortName = x.trim) &
      "#ordernumber" #> SHtml.select(numbers, Full("32"), ordernumber = _) &
      "#addInfo *" #> errorInfo &
      "#delete" #> SHtml.ajaxSubmit("Usuń", delete, "type" -> "image",
        "onclick" -> "return confirm('Na pewno usunąć klasę?')") &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image") andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }

}

 
