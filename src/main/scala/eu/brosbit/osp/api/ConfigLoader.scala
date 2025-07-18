package eu.brosbit.osp.api

import java.io.File
import scala.io.Source
import scala.util.Try

object ConfigLoader {
  var sqlPassw = ""
  var sqlDB = ""
  var mongoDB = ""
  var mongoPort = 27017
  var emailAddr= ""
  var emailPassw = ""
  var emailPort = ""
  var emailSMTP = ""
  var judgeDir = ""
  println("LOAD OSP")
  val f = new File("/etc/osp/config.cfg")

  val lines = Source.fromFile(f).getLines().toList

  def init:Unit = lines.map(line => {
    val opt = line.split('=').map(x => x.trim)
    if (opt.length == 2) {
      opt.head match {
        case "sqlpassword" => sqlPassw = opt.last
        case "sqldatabase" => sqlDB = opt.last
        case "mongodatabase" => mongoDB = opt.last
        case "mongoport" => mongoPort = Try(opt.last.toInt).getOrElse(mongoPort)
        case "emailaddress" => emailAddr = opt.last
        case "emailpassword" => emailPassw = opt.last
        case "emailport" => emailPort = opt.last
        case "emailsmtp" => emailSMTP  = opt.last
        case "judgeDir" => judgeDir = opt.last
        case _ =>
      }
    }
   //println(printInfo)
  })

  def printInfo = "sqlPass: %s, sqlDB: %s, mongoDB: %s emailSMTP %s"
    .format(sqlPassw, sqlDB, mongoDB, emailSMTP)
}

