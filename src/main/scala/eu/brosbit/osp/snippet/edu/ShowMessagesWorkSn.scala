package eu.brosbit.osp.snippet.edu

import java.util.Date
import eu.brosbit.osp.lib.{Formater, ZeroObjectId}
import eu.brosbit.osp.model.{LessonCourse, MessageItem, MessagesWork, Work}
import eu.brosbit.osp.snippet.WorkCommon
import eu.brosbit.osp.snippet.view.BaseSnippet
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel

import java.util.Date
import scala.xml.{NodeSeq, Unparsed}

class ShowMessagesWorkSn extends BaseSnippet with WorkCommon {

  protected val workId:String  = S.param("id").getOrElse("0")
  protected val work:Work = Work.find(workId).getOrElse(S.redirectTo("/educontent/showworks/"+workId))
  protected val messagesWork:MessagesWork = MessagesWork.findAll("work" -> work._id.toString).headOption
    .getOrElse(MessagesWork.create)
  protected val lesson:LessonCourse = LessonCourse.find(work.lessonId).getOrElse(LessonCourse.create)


  def showInfo():CssSel = {
    //Work.update("_id"->answer._id.toString, "$set" -> ("pupilChanged" -> false))
    if(messagesWork.work.toString  == ZeroObjectId.get.toString) {
      messagesWork.work = work._id
      messagesWork.save
    }
    "#theme *" #> work.theme &
    "#lessonTitle *" #> work.lessonTitle &
      "#descript *" #> Unparsed(mkDescription(work.description)) &
      "#subject *" #> work.subjectName &
      "#classInfo *" #> work.groupName &
      "#messages" #> createAllMessages(messagesWork.messages)
  }

  def addMessage():CssSel = {
    var link = ""
    var messageStr = ""
    def send():JsCmd = {
      val(good, answer) = if(link.trim.isEmpty && messageStr.trim.nonEmpty){
        val ansItem = MessageItem(Formater.formatTime(new Date()), user.getFullName,
          t = false, messageStr, l = false)
        (true, ansItem)
      } else if(link.nonEmpty) {
        val ansItem = MessageItem(Formater.formatTime(new Date()), user.getFullName, t = false, link, l = true)
        (true, ansItem)
      } else (false, null)
      if(good){
        MessagesWork.update("_id"->messagesWork._id.toString, "$addToSet" -> ("messages" -> answer.toJson))
        Work.update("_id" -> messagesWork.work.toString, "$set" -> ("lastNews" -> new Date().getTime))
      }
      Run("messageWork.messageSuccess()")
    }
    val ajaxForm = "#date" #> <span></span> &
    "#comment" #> SHtml.textarea(messageStr, t => messageStr = t.trim) &
    "#link" #> SHtml.text(link, b => link = b.trim) &
    "#send" #> SHtml.ajaxSubmit("Wyślij", send) andThen SHtml.makeFormsAjax
    "form" #> ((in: NodeSeq) => ajaxForm(in))
  }


}
