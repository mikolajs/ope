package eu.brosbit.osp.snippet.view

import java.util.Date
import scala.xml.Text
import _root_.net.liftweb._
import http.{S, SHtml}
import util._
import eu.brosbit.osp.model._
import Helpers._
import eu.brosbit.osp.snippet.BaseShowCourseSn
import eu.brosbit.osp.lib.Formater
import net.liftweb.mapper.By
import net.liftweb.common.Full
import json.JsonDSL._

class ShowCourseSn extends BaseShowCourseSn {

  val user = User.currentUser match {
    case Full(user) => user
    case _ => S.redirectTo("/login?r=/view/course/")
  }
  private val groupsIds = Groups.findAll.filter(gr => gr.students.exists(s => s.id == user.id.get)).map("_" + _._id)

  def show(): CssSel = {
    if (!canView) S.redirectTo("/view/courses/")

    if (course.title != "") {
      "#subjectListLinks a" #> createLessonList &
        "#courseInfo" #> <div class="alert alert-success">
          <h2>
            {course.title}
          </h2> <p>
            {course.descript}
          </p>
        </div> &
        ".content *" #> this.showAsDocument(currentLesson, false)
    } else ".main *" #> <h1>Nie ma takiego kursu lub brak lekcji</h1>
  }
/*
  def sendMessage():CssSel = {
    var msg = ""
    def send() {
      if (canView) {
        val message = Message.create
        val body = "<p class=\"msq-body\">" + msg + "</p><p><small class=\"msgSource\">Widomość z kursu " +
          course.getInfo + " lekcja: " + currentLesson.title + "</small></p>"
        val d = new Date()
        val mc = MessageChunk(user.id.get.toString, user.getFullName, Formater.formatTime(d), body)
        message.lastDate = d.getTime
        message.body = List(mc)
        User.find(By(User.id, course.authorId)) match {
          case Full(u) =>
            message.people = u.getFullName
            message.who = List(u.id.get, user.id.get)
            message.people = u.getFullName + " " + user.getFullName
            message.save
          case _ =>
        }
      }
    }

    "#writeMessage" #> SHtml.textarea(msg, msg = _) &
      "#sendMessage" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text("Wyślij"), send)
  }

 */

  private def canView =  course.groupsList.exists(gr => groupsIds.contains(gr))

}