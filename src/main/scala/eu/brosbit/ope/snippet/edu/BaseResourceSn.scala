package eu.brosbit.ope.snippet.edu

import eu.brosbit.ope.model._
import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb._
import common._
import util._
import http.{S, SHtml}
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import org.bson.types.ObjectId
import Helpers._

trait BaseResourceSn {

  protected val user: User = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  protected val subjectTeach: List[SubjectTeach] = SubjectTeach.findAll(("authorId" -> user.id.get), ("prior" -> 1))
  if (subjectTeach.isEmpty && S.uri.split("/").last != "options")
    S.redirectTo("/educontent/options")
  protected val subjId: String = S.param("s").openOr(subjectTeach.head.id.toString)
  protected var subjectNow: SubjectTeach = subjectTeach.find(s =>
    s.id.toString == subjId).getOrElse(subjectTeach.head)
  //val levStr = S.param(l").openOr(subjectNow.lev.toString)
  protected var subjectId: Long = subjectNow.id
  val levList: Seq[(String, String)] = List(("1", "podstawowy"), ("2", "rozszerzony"), ("3", "konkursowy"))
  protected val levMap: Map[String, String] = levList.toMap

  protected var departNr: Int = tryo(S.param("d").openOr("0").toInt).openOr(0)
  protected var departName: String = getDepartName(departNr)

  val query: JObject =
    if (departNr < 0)
      ("authorId" -> user.id.get) ~ ("subjectId" -> subjectNow.id)
    else
      ("authorId" -> user.id.get) ~ ("subjectId" -> subjectNow.id) ~ ("department" -> departName)

  protected def changeSubject(subID:Long):Unit = {
      subjectNow = subjectTeach.find(s => s.id == subID).getOrElse(subjectTeach.head)
      subjectId = subjectNow.id
  }
  protected def changeDepartment(depName:String):Unit = {
    departNr = subjectNow.departments.indexOf(depName)
    departName = depName
  }
  private def getDepartName(depID:Int):String = depID match {
    case -2 => ""
    case -1 => if (subjectNow.departments.isEmpty) "" else subjectNow.departments.head
    case nr: Int if (subjectNow.departments.length > nr) => subjectNow.departments(nr)
    case _ => if (subjectNow.departments.isEmpty) "" else subjectNow.departments.head
  }

  def techerSubjects(): CssSel = {
    val subj = subjectTeach.map(s => (s.id, s.name))
    "#subjectSelect" #> subj.map(s =>
      "option" #> <option value={s._1.toString}>
        {s._2}
      </option>)
  }

  def findSubjectName(id: Long): String = {
    for (s <- subjectTeach) {
      if (s.id == id) return s.name
    }
    ""
  }

  def findSubjectId(name: String): Long = {
    val matched = subjectTeach.filter(s => s.name == name)
    if (matched.isEmpty) subjectNow.id else matched.head.id
  }

  def subjectChoice(basePath: String): CssSel = {
    def redirect(str: String): JsCmd = {
      S.redirectTo(basePath + "?s=" + str)
    }

    val subjects = subjectTeach.map(s => (s.id.toString(), s.name))
    "#subjectChoice" #> SHtml.ajaxSelect(subjects, Full(subjectNow.id.toString), (str) => redirect(str))
  }

  def subjectAndDepartmentChoice(basePath: String): CssSel = {
    def redirectPath(sub: String, depNr: String): String = {
      basePath + "?s=" + sub + "&d=" + depNr
    }

    val subjects = subjectTeach.map(s => {
      <optgroup label={s.name}>
        {var n = -1;
      s.departments.map(d => {
        n += 1
        <option value={redirectPath(s.id.toString, n.toString)}>
          {d}
        </option>
      })}
        ++
        <option value={redirectPath(s.id.toString, (-1).toString)}>Wszystkie</option>
      </optgroup>
    })


    "#subjectChoice" #> <select>
      {subjects}
    </select> &
      "h2" #> <h2>
        <span class="label label-info">
          {subjectNow.name}
        </span>
        Dział:
        <big id="subjectNameLabel">
          {if (departName.isEmpty) "Wszystkie" else Unparsed(departName)}
        </big>
        <small style="display:none;">
          {redirectPath(subjectNow.id.toString, departNr.toString)}
        </small>
      </h2>
  }

  def getSeparator = ";#;;#;"

  /*
   def choiceSubjectAndLevel(basePath:String) = {
     val subjects = subjectTeach.map(s => (s.id.toString, s.name))
     var subjectChoice = ""
     var levelChoice = ""
          def makeChoise() {
              S.redirectTo(basePath + "?s=" + subjectChoice+ "&l=" + levelChoice)
          }
         "#subjects" #> SHtml.select(subjects, Full(subjectNow.id.toString), subjectChoice = _) &
         "#levels" #> SHtml.select(levList, Full(levStr), levelChoice = _) &
         "#choise" #> SHtml.submit("Wybierz", makeChoise)
   }
   *
   */
  /*
    def autocompliteScript(in: NodeSeq) = {
      var dataStart = "$(function() {\n" + "var availableTags = ["
      var dataCenter = subjectNow.departments.map(dep => "\"" + dep + "\"").mkString(", ")
      var dataEnd = "];\n $( \"#department\" ).autocomplete({source: availableTags});});"
      <script>{ Unparsed(dataStart + dataCenter + dataEnd) }</script>
    }

    protected def saveNewDepartment(depName: String) = {
      if(depName.length() > 0) {
        if(!subjectNow.departments.exists(d => d == depName)) {
          subjectNow.departments = depName::subjectNow.departments
          subjectNow.save
        }
      }
    }
    */
}
