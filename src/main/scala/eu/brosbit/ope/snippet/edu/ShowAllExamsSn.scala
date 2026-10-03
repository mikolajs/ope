package eu.brosbit.ope.snippet.edu

import eu.brosbit.ope.model.{Exam, Groups}
import eu.brosbit.ope.lib.Formater
import net.liftweb.util.Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.util.CssSel

import java.util.Date

class ShowAllExamsSn  extends  BaseResourceSn {
  mkBlocking()

  def subjectChoice(): CssSel = super.subjectChoice("/educontent/exams")

  def showAllUserExams(): CssSel = {

    "tr" #> Exam.findAll(("authorId" -> user.id.get) ~ ("subjectId" -> subjectNow.id))
      .map(ex => {
      <tr>
        <td>{Formater.formatTimeForSort(new Date(ex.start))}</td>
        <td>{Formater.formatTimeForSort(new Date(ex.end))}</td>
        <td>{ex.description}</td>
        <td>{ex.groupName}</td>
        <td>
          <a class="btn btn-success" href={"/educontent/showexams/" + ex._id.toString}>
            <span class="glyphicon glyphicon-edit"></span></a>
        </td>
        <td>
          <a class="btn btn-success" href={"/educontent/editexam/" + ex._id.toString}>
            <span class="glyphicon glyphicon-pencil"></span></a>
        </td>
        <td>
          <a class="btn btn-warning" href={"/educontent/editexam?c=" + ex._id.toString }>
            <span class="glyphicon glyphicon-open"></span></a>
        </td>
      </tr>
    })
  }

  def blockingGroups() : CssSel = {
    "#blocked *" #> Groups.findAll("authorId" -> user.id.get).filter(gr => gr.blocked).map(gr => gr.name).mkString(" ")
  }

  private def mkBlocking(): Unit = {
    val now = new Date().getTime
    println(new Date().toString)
    val myExams = Exam.findAll("authorId" -> user.id.get)
    val grWorksIds = myExams.filter(ex => (ex.start <= now) && (ex.end >= now)).map(_.groupId).distinct
    grWorksIds.foreach(g => println(s"group $g"))
    Groups.findAll("authorId" -> user.id.get).foreach( gr => {
      val ifWork = grWorksIds.find(p => {
        println(s"${p.drop(1)} =?= ${gr._id.toString}")
        p.drop(1) == gr._id.toString}) //underscore first
      if(ifWork.nonEmpty && !gr.blocked) {
        gr.blocked = true
        gr.save
      } else if(ifWork.isEmpty && gr.blocked) {
        gr.blocked = false
        gr.save
      }
    })
  }
  def editSubject():CssSel = {
    "a [href]" #> s"/educontent/editexam/-1?s=${subjectId}"
  }

}
