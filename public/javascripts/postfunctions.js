function addPost () {
	window.location.href = "/posts/add";
}

function editPost (id) {
	window.location.href  = "/posts/edit?id="+id
}

function reallyDeletePost (id) {
	$.ajax ({
			url:"/posts/"+id,
			type:"DELETE",
			success:function () {
				// post verschwinden lassen
				$("#post_"+id).hide("fade", {}, 1000);
				// var dialog =$("<div></div>").html("Succeded").dialog();
				// window.location.href = "/posts";
			}
	}
	);
	// window.location.href = "/posts/delete?really=1&id="+id;
}

function deletePost (id) {
	
	var dialog = $('<div></div>').
	html('Do you really wont to delete this Posting?').
	dialog({
		"modal":true, "title": "Basic Dialog",
		"title": "Delete Post",
		"buttons": 
		{ 
			"Yes" : function () {
				$(this).dialog("close");
				reallyDeletePost (id);
			},
			"Cancel" : function () {
				$(this).dialog("close");
			}
		}
		}
	);
	
	/*
	// alert ("Delete, da fehlt noch eine Nachfrage " + id)
	window.location.href = "/posts/delete?id="+id
	*/
}
