package eu.brosbit.osp.snippet.admin

import scala.xml.Text
import _root_.net.liftweb.util._
import _root_.eu.brosbit.osp.model._
import _root_.net.liftweb.http.{S, SHtml, RequestVar}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._

class AdminMailsSn {

  object notice extends RequestVar[String]("")

  def addContactMail(): CssSel = {
    var id = ""
    var email = ""
    var description = ""

    def saveMail(): Unit = {
      ContactMail.find(id) match {
        case Some(contactMail) => {
          contactMail.description = description
          contactMail.mailAddress = email
          contactMail.save
        }
        case _ => {
          val contactMail = ContactMail.create
          contactMail.description = description
          contactMail.mailAddress = email
          contactMail.save
        }
      }
    }

    def deleteMail(): Unit = {
      ContactMail.find(id) match {
        case Some(contactMail) => contactMail.delete
        case _ =>
      }
    }

    "#id" #> SHtml.text(id, x => id = x, "size" -> "12", "style" -> "display:none;", "id" -> "id") &
      "#descript" #> SHtml.text(description, x => description = x.trim, "maxlength" -> "40", "id" -> "descript") &
      "#mail" #> SHtml.text(email, x => email = x.trim, "maxlength" -> "60", "id" -> "mail") &
      "#save" #> SHtml.submit("Zapisz!", saveMail, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", deleteMail, "onclick" -> "return confirm('Na pewno chcesz usunąć email?');") &
      "#notice" #> Text(notice)
  }

  /** school contacts mail */
  def contactMails(): CssSel = {
    val contactMails = ContactMail.findAll
    "tr" #> contactMails.map(contactMail => {
      <tr ondblclick="setData(this)" id={contactMail._id.toString}>
        <td>
          {contactMail.mailAddress}
        </td> <td>
        {contactMail.description}
      </td>
      </tr>
    })
  }

}