package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.util.Date
import org.apache.commons.lang.StringEscapeUtils;
import eu.henkelmann.actuarius.ActuariusTransformer;
import play.Logger;

object Post {
    
  val simple = {
    get[Pk[Long]]("Post.id") ~
    get[String]("Post.title") ~
    get[String]("Post.content") ~
    get[Date]("Post.date") map {
      case id~title~content~date => Post(id, title, content, date)
    }
  }
    
    def findById (id : Long) : Option[Post] = {
        DB.withConnection { implicit connection => 
            SQL ("select * from post where id={id}")
            .on ('id -> id).as(Post.simple.singleOpt)
        }
    }
    
    def findAll () : Seq [Post] = {
        DB.withConnection { implicit connection =>
            SQL ("select * from post").as(Post.simple.*)
        }
    }
    
    // find all posts ordered by time; newest at first
    def findAllByTime () : Seq [Post] = {
        DB.withConnection { implicit connection => 
            SQL ("select * from post order by date desc").as(Post.simple.*)
        }
    }
    
    def create (post : Post) : Post = {
        DB.withTransaction { implicit con =>
            val id: Long = post.id.getOrElse {
        		SQL("select next value for post_seq").as(scalar[Long].single)
        	}
        	
	       // Insert the project
	       SQL(
	         """
	           insert into post values (
	             {id}, {title}, {content}, {date}
	           )
	         """
	       ).on(
	         'id -> id,
	         'title -> post.title,
	         'content -> post.content,
	         'date -> post.date
	       ).executeUpdate()
	       
	       post.copy (id = Id(id))	             
        }
    }
    
    val transformer = new ActuariusTransformer()
    
}

case class Post (id: Pk[Long], title : String, content : String, date : Date = new Date()){
    def validate : Boolean = {
        !(title.isEmpty() || content.isEmpty())
    }
    
    def update = {
        DB.withConnection { implicit connection =>
            SQL ("update post set title={title}, content={content} where id={id}")
            .on ('title -> title, 'content -> content, 'id -> id)
            .executeUpdate()
        }
    }
    
    def delete = {
    	DB.withConnection { implicit connection =>
    		SQL ("delete from post where id={id}").on ('id -> id).executeUpdate()
    	}
    }
    
    /// Returns id or invalid -1
    def idAsLong : Long = id.getOrElse(-1)
    
    /// Converts content to Html
    def contentAsHtml : String = {
       // StringEscapeUtils.escapeHtml(content).replaceAll("\n", "<br>");
        val transformed = Post.transformer (content);
        // Logger.info(content + " is transformed to " + transformed)
        return transformed
    }
}