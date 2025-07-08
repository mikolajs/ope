package eu.brosbit.osp.snippet.admin

import net.liftweb.http.{FileParamHolder, S, SHtml}
import net.liftweb.util.Helpers._
import net.liftweb.common.{Box, Empty, Full}
import _root_.net.liftweb.json.JsonDSL._
import bootstrap.liftweb.MongoConnectionIdentifier
import com.mongodb.client.gridfs.{GridFSBucket, GridFSBuckets}
import com.mongodb.gridfs.GridFS
import net.liftweb.util.CssSel
import eu.brosbit.osp.model.MapExtraData
import net.liftweb.db.DB
import net.liftweb.mongodb.MongoDB
import org.bson.types.ObjectId

import java.io.ByteArrayOutputStream
import java.nio.file.{Files, Paths}

/**
 * Created by ms on 04.01.16.
 */
class AdminLogoSn {
  private val aKey = "logopath"
  private val mapED = MapExtraData.getMapData(key = aKey)

  def show(): CssSel = {
    val  pathMedia =  S.hostAndPath.split('/').take(3).mkString("/").split(':').take(2).mkString(":")  + "/img/"
    var fphBoxLogo:Box[FileParamHolder] = Empty
    var fphBoxFav:Box[FileParamHolder] = Empty
    val infoDataM = scala.collection.mutable.Map[String, String]()
    if (mapED.contains("logo")) infoDataM("logo") = mapED.get("logo").getOrElse("")
    if (mapED.contains("favi")) infoDataM("favi") = mapED.get("favi").getOrElse("")
    def save(): Unit = {
      //println("Logo action!!!!!!!!!!!!!!!!!!")
      if(!fphBoxLogo.isEmpty) {
        val fph = fphBoxLogo.openOrThrowException("Niemożliwe")
        //println("LOGO Start " + fph.fileName)
        if(fph.mimeType.toLowerCase == "image/png") {
          val logoLink = saveFile(fph.file, "logo.png", "image/png")
          println("LOGO LINK: " + logoLink)
          infoDataM("logo") = logoLink + ".png"
        }
      }
      if(!fphBoxFav.isEmpty) {
        val fph = fphBoxFav.openOrThrowException("Niemożliwe")
        //println("favicon Start " + fph.fileName)
        if(fph.mimeType.toLowerCase == "image/png") {
          val faviLink = saveFile(fph.file, "favicon.png", "image/png")
          println("Favi LINK: " + faviLink)
          infoDataM("favi") = faviLink + ".png"
        }
      }
      MapExtraData.setMapData(aKey, infoDataM.toMap)
    }

    "#logoImg [src]" #> ("/img/" + mapED.get("logo").getOrElse("")) &
    "#faviconImg [src]" #> ("/img/" + mapED.get("favi").getOrElse("")) &
    "#logo" #> SHtml.fileUpload(fileParamHold => fphBoxLogo = Full(fileParamHold))  &
    "#favicon" #> SHtml.fileUpload(fileParamHold => fphBoxFav = Full(fileParamHold)) &
    "#save" #> SHtml.submit("Zapisz", save)
  }

  private def saveFile(data:Array[Byte], name:String, fileType:String):String = {
    var rName = ""
    MongoDB.use(MongoConnectionIdentifier){
      db => {
        val gfs = new GridFS(db)
        val gfsInpFile = gfs.createFile(data)
        gfsInpFile.setFilename(name)
        gfsInpFile.setContentType(fileType)
        gfsInpFile.save()
        rName = gfsInpFile.getId.asInstanceOf[ObjectId].toHexString
      }
    }
    rName
  }
}
