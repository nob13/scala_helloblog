package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import play.Logger
import models.Post
import anorm.NotAssigned
import anorm.Pk
import anorm._

object PostController extends Controller {

  implicit def toPk (id : Option[Long]) : Pk[Long] = {
      id match { 
          case Some (i) => Id (i)
          case None => NotAssigned
      }
  }
  
  val editPostForm = Form (
	mapping(
	 "id" -> optional (longNumber),
	 "title" -> nonEmptyText,
	 "content" -> nonEmptyText
	 ) 
	 ((id,title,content) => Post ( 
	         id, title, content) // id benutzt implizite konvertierung
	         ) 
	 ((post : Post) => Some ((post.id.toOption, post.title, post.content)))
	         
  )
  
  def posts = Action { implicit request =>
      val posts = Post.findAllByTime();
      Ok(html.posts (posts))
  }
  
  def addView = Action { implicit request => 
      Ok (html.addPost (editPostForm));
  }
  
  def add = Action { implicit request =>
      editPostForm.bindFromRequest.fold (
    	       formWithErrors => {
    	             Logger.info ("Form with errors=" + formWithErrors)
    	             BadRequest(html.addPost(formWithErrors))},
    	 post =>
    	  {
    	             val p = Post.create (post)
    	             Logger.info ("Created post " + p.id);
    	             Redirect (routes.PostController.posts).flashing ("success" -> "The item has been saved!")
    	  }
      )
  }
  
  def editView (id : Long) = Action { implicit request =>
      Post.findById(id) match { 
      case Some(post) => 
          Ok(html.editPost (id, editPostForm.fill(post)))
      case None =>
          NotFound
      }
  }
  
  def edit (id : Long) = Action {  implicit request =>
  	editPostForm.bindFromRequest.fold (
  			formWithErrors => BadRequest (html.editPost (id, formWithErrors)),
  			post => {
  			    Logger.info ("Edited Post = " + post.id);
  			    post.update;
  			    Redirect (routes.PostController.posts).flashing ("success" -> "Post has been edited!")
  			}
  	)
  }
  
  def delete (id : Long) = Action { implicit request =>
  	Post.findById(id) match {
  	    case None => NotFound
  	    case Some(post) => 
  	        post.delete
  	        Logger.info ("Deleted post " + post.id)
  	        Ok ("Post deleted")
  	}
  }
  
  def deleteView (id : Long, really: Boolean = true) = Action { implicit request =>
      Post.findById(id) match {
          case None => NotFound
          case Some (post) =>
              if (really) {
                  post.delete
                  Redirect (routes.PostController.posts).flashing ("success" -> "The post has been deleted")
              } else {
                  Ok (html.delPost (post))
              }
      }
  }
  
}