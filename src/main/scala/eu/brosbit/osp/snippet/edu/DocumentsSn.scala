package eu.brosbit.osp.snippet.edu

import java.util.Date
import scala.xml.{Text, Unparsed}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import mapper.{Descending, OrderBy}
import eu.brosbit.osp.model.Document
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

//for show list of all doc for teacher
class DocumentsSn extends BaseResourceSn {
  //edocuments => show edocuments list

  def docList() = {

    val documents = Document.findAll(query)

    "tbody tr" #> documents.map(doc => ".titleTd *" #> <strong>
      {doc.title}
    </strong> &
      ".descriptTd *" #> Text(doc.descript) &
      ".departTd *" #> Text(doc.department) &
      ".levelTd *" #> Text(levMap(doc.lev.toString)) &
      ".editTd *" #> <a class="btn btn-success" href={"/educontent/editdocument/" + doc._id.toString}>
        <span class="glyphicon glyphicon-edit"></span>
      </a>
    )
  }

  def subjectForNew(): CssSel = {
    "a [href]" #> ("/educontent/editdocument/0?s=" + subjectNow.id.toString +
      "&d=" + departNr)
  }

  def subjectChoice(): CssSel = {
    super.subjectChoice("/educontent/documents")
  }

  def subjectAndDepartmentChoice(): CssSel = {
    super.subjectAndDepartmentChoice("/educontent/documents")
  }
}
