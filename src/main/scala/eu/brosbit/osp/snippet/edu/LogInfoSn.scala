package eu.brosbit.osp.snippet.edu

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{S, SHtml}
import eu.brosbit.osp.model._
import Helpers._

class LogInfoSn {
  def show(): CssSel = {
    User.currentUser match {
      case Full(user) =>
        "a" #> <a title="WYLOGUJ!" href="/user_mgt/logout">
          <button type="button" class="btn btn-info">
            <span class="glyphicon glyphicon-log-out"></span>{user.getFullName}
          </button>
        </a>
      case _ => S.redirectTo("/")
    }
  }
}