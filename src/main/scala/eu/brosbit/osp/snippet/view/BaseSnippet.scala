package eu.brosbit.osp.snippet.view

import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{S, SHtml}
import mapper.{OrderBy, Descending, By}
import eu.brosbit.osp.model._
import Helpers._

trait BaseSnippet {
  val user = User.currentUser.openOrThrowException("Uczeń musi być zalogowany")

}


