package eu.brosbit.osp.snippet.edu

import eu.brosbit.osp._
import _root_.net.liftweb._
import common._
import model._

trait RoleChecker {

  def isTeacher: Boolean = {
    User.currentUser match {
      case Full(user) => {
        var r = user.role.get
        (r == "t" || r == "m" || r == "a")
      }
      case _ => {
        false
      }
    }
  }

  def isAdmin = User.currentUser match {
    case Full(user) => user.role.get == "a"
    case _ => false
  }
}