package eu.brosbit.osp.snippet.edu

import eu.brosbit.osp.model.Exam
import eu.brosbit.osp.lib.Formater
import net.liftweb.util.Helpers._
import net.liftweb.json.JsonDSL._

import java.util.Date

class ShowAllExamsSn  extends  BaseResourceSn {

  def subjectChoice() = super.subjectChoice("/educontent/exams")

  def showAllUserExams() = {

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
}
