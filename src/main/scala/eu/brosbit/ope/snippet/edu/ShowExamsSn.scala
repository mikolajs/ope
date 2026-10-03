package eu.brosbit.ope.snippet.edu

import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.http.S
import eu.brosbit.ope.model.{Quiz, ExamAnswer, Exam}
import eu.brosbit.ope.lib.Formater
import java.util.Date

class ShowExamsSn extends BaseResourceSn {

  private val exId = S.param("id").openOr("")
  if (exId.isEmpty) S.redirectTo("/educontent/exams")
  val exam: Exam = Exam.find(exId).getOrElse(Exam.create)
  private val quizzes = Quiz.findAll("_id" -> ("$in" -> exam.quizzes.map(q => q.toString)))
  private var pointsMaxList: List[Int] = quizzes.map(qz => qz.questions.map(q => q.p).sum)
  if (pointsMaxList.length != exam.quizzes.length) pointsMaxList = (-5 to (exam.quizzes.length - 5)).toList

  def showInfo(): CssSel = {
    "span *" #> exam.description &
      "small *" #> ("od " + Formater.formatTime(new Date(exam.start)) +
        " do " + Formater.formatTime(new Date(exam.end)))
  }

  def showExamAnswers(): CssSel = {
    val exAns = ExamAnswer.findAll("exam" -> exam._id.toString)

    "tr" #> exAns.sortWith(_.authorName < _.authorName).map(ea => {
      val gain = if (ea.answers.nonEmpty) ea.answers.map(_.p).sum else 0
      val percent = if (ea.max == 0) 0.0F else ((100.0F * gain.toFloat) / ea.max.toFloat)
      ".col1 *" #> ea.authorName &
        ".col2 *" #> ea.code &
        ".col3 *" #> (gain.toString + " / " + ea.max.toString + " : " +
          scala.math.round(percent).toString + "%") &
        ".col4 *" #> <a href={"/educontent/checkexam/" + ea._id.toString}
                        class="btn btn-small btn-success">
          <span class="glyphicon glyphicon-check"></span>
          Sprawdź</a>
    })
  }
  def editSubject():CssSel = {
    "a [href]" #> s"/educontent/editexam/0?s=${subjectId}"
  }
}
