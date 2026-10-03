package eu.brosbit.ope.snippet.edu

import eu.brosbit.ope._
import model._
import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb._
import common._
import util._
import http.{S, SHtml}
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

trait BaseLesson {

  val user: User = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  val levList: Seq[(String, String)] = List(("1", "podstawowy"), ("2", "średni"), ("3", "rozszerzony"))
  val levMap: Map[String, String] = levList.toMap

  var idPar: String = S.param("id").openOr("0")
  var courseId: String = S.param("c").openOr("0")
  //parametr id jest _id lekcji gdy już była utworzona. Gdy mamy nową lekcję parametr ten jest id kursu
  val lesson: LessonCourse = if (idPar == "0") LessonCourse.create else LessonCourse.find(idPar).getOrElse(LessonCourse.create)
  private val notFoundLesson = (lesson.courseId.toString == "000000000000000000000000" || lesson.courseId.toString.length() < 20)
  val courseOption: Option[Course] = if (idPar == "0") Course.find(courseId)
  else {
    if (notFoundLesson) None else Course.find(lesson.courseId.toString)
  }
  val subjectId: Long = if (courseOption.isEmpty) 0L else courseOption.get.subjectId
  val chapters: Seq[String] = if (courseOption.isEmpty) Nil else courseOption.get.chapters
  val chaptersList: Seq[(String, String)] = chapters.map(ch => (ch, ch))


  def findChapterName(id: Int): String = if (chapters.contains(id.toString)) chapters(id) else "Brak nazwy"

  //println(">>>>>>>>>>>> lessonID + " + lesson._id.toString + " idPar = " + idPar + "  courseId = " + lesson.courseId.toString );
}