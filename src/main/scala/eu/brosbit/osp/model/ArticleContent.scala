package eu.brosbit.osp.model
/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */
import _root_.net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import java.util.Date
import org.bson.types.ObjectId

object ArticleContent extends MongoDocumentMeta[ArticleContent] {
  override def collectionName = "articlecontent"
  override def connectionIdentifier: ConnectionIdentifier = bootstrap.liftweb.MongoConnectionIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new ArticleContent(ObjectId.get, "")
}

case class ArticleContent(var _id: ObjectId, var content: String)
  extends MongoDocument[ArticleContent] {
  def meta = ArticleContent
}
