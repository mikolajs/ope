package eu.brosbit.osp.snippet.view

import java.util.Date
import _root_.net.liftweb.util._
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import eu.brosbit.osp.lib.{Formater, ZeroObjectId}
import eu.brosbit.osp.model.{AnswerWorkItem, Groups, LessonCourse, Work, WorkAnswer}
import eu.brosbit.osp.snippet.WorkCommon
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JsCmds.Run
import _root_.net.liftweb.http.{S, SHtml}
import _root_.net.liftweb.util.CssSel
import scala.xml.{NodeSeq, Unparsed}

class PerformWorkSn extends  BaseSnippet  with WorkCommon {
  private val id = S.param("id").getOrElse("")
  private val work = Work.find(id).getOrElse(Work.create)
  private val groups = Groups.findAll.filter( gr => gr.students.exists(s => s.id == user.id.get))
    .map(gr => ("_" + gr._id.toString, gr.name))
  if(!groups.exists(gr => gr._1 == work.groupId)  || work.teacherId == 0L) S.redirectTo("/view/showwork?Error")
  private val answer = WorkAnswer.findAll(("work"->work._id.toString)~("authorId"->user.id.get)).headOption
    .getOrElse(WorkAnswer.create)
  if(answer.work.toString == ZeroObjectId.get.toString){
    answer.work = work._id
    answer.authorId = user.id.get
    answer.authorName = user.getFullName
    answer.save
  } else {
    answer.teacherChanged = false
    answer.save
  }
  private val lesson = LessonCourse.find(work.lessonId).getOrElse(LessonCourse.create)

  def showWork():CssSel = {
    WorkAnswer.update("_id"->answer._id.toString, "$set" -> ("teacherChanged" -> false) )
    "#descript *" #> Unparsed(mkDescription(work.description)) &
    "#subject *" #>  work.subjectName &
    "#lesson *" #> work.lessonTitle &
    "#questions" #> createQuestions(lesson.contents, answer)
  }

  def addMessage():CssSel = {
    var link = ""
    var messageStr = ""
    var quizId = ""
    def send():JsCmd = {
      if(link.trim.isEmpty && messageStr.trim.nonEmpty){
        val ansItem = AnswerWorkItem(Formater.formatTime(new Date()), user.getFullName,
          t = false, quizId, messageStr, l = false)
        WorkAnswer.update("_id"->answer._id.toString,
        ("$set" -> ("pupilChanged" -> true)) ~ ("$addToSet" -> ("answers" -> ansItem.toJson)))
      } else if(link.nonEmpty) {
        val ansItem = AnswerWorkItem(Formater.formatTime(new Date()), user.getFullName, t = false, quizId, link, l = true)
        WorkAnswer.update("_id"->answer._id.toString,
          ("$set" -> ("pupilChanged" -> true)) ~ ("$addToSet" -> ("answers" -> ansItem.toJson)))
      }
      Run("performWork.messageSuccess()")

    }
    val ajaxForm = "#date" #> <span></span> &
    "#quizId" #> SHtml.text(quizId , quizId = _) &
    "#comment" #> SHtml.textarea(messageStr, t => messageStr = t.trim) &
    "#link" #> SHtml.text(link, b => link = b.trim) &
    "#send" #> SHtml.ajaxSubmit("Wyślij", send) andThen SHtml.makeFormsAjax
    "form" #> ((in: NodeSeq) => ajaxForm(in))
  }

}
