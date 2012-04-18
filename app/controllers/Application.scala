package controllers

import play.api._
import play.api.mvc._

import models.Post;
object Application extends Controller {
  
  def index = Action {
      Redirect ("/posts")
  }
  
}