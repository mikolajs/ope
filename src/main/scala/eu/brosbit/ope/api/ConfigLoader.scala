package eu.brosbit.ope.api

import java.io.File
import scala.io.{BufferedSource, Source}
import scala.util.Try

object ConfigLoader {
  var sqlPassw = ""
  var sqlDB = ""
  var mongoDB = ""
  var mongoPort = 27017
  var emailAddr = ""
  var emailPassw = ""
  var emailPort = ""
  var emailSMTP = ""
  var judgeDir = ""
  //println("LOAD OPE")
  val f = new File("/etc/ope/config.cfg")
  private val source: BufferedSource = Source.fromFile(f)
  private val lines = source.getLines().toList
  source.close()
  def init(): Unit = lines.foreach(line => {
    //println(line)
    val opt = line.split('=').map(x => x.trim)
    if (opt.length == 2) opt.head match {
        case "sqlpassword" => sqlPassw = opt.last
        case "sqldatabase" => sqlDB = opt.last
        case "mongodatabase" => mongoDB = opt.last
        case "mongoport" => mongoPort = Try(opt.last.toInt).getOrElse(mongoPort)
        case "emailaddress" => emailAddr = opt.last
        case "emailpassword" => emailPassw = opt.last
        case "emailport" => emailPort = opt.last
        case "emailsmtp" => emailSMTP = opt.last
        case "judgeDir" => judgeDir = opt.last
        case _ =>
      }
    //println(printInfo

  })

  def printInfo = "sqlPass: %s, sqlDB: %s, mongoDB: %s emailSMTP %s, emailPort %s, emailadress %s, emailPass %s"
    .format(sqlPassw, sqlDB, mongoDB, emailSMTP, emailPort, emailAddr, emailPassw)
}

