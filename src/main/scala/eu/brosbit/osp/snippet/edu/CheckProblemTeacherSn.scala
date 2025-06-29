package eu.brosbit.osp.snippet.edu

import eu.brosbit.osp.lib.{Formater, TestProblemRunner}
import eu.brosbit.osp.model.{TestProblem, TestProblemTry, User}
import eu.brosbit.osp.snippet.CheckProblemSn
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, SHtml}
import net.liftweb.json.JsonDSL._

import java.util.Date
import java.util.zip.DataFormatException
import scala.xml.{Text, Unparsed}

class CheckProblemTeacherSn extends CheckProblemSn {
  override val baseDir = "/educontent"
}
