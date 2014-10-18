<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
/**
 * java script file for userpics
 */

$(".profilepic").click(function(){
	var picid=$(this).parent().find(
					"img").attr("id");

	$.ajax({
		url:"${Globals.root_path}/Profile/Profilepic/"+picid,
		type:"PUT"
	}).done(function(){
		$(".success").html("Profile Picture updated!");
		$(".success").show().delay(1500).fadeOut(500);
	});
});

$(".delete").click(function(){
	var	parent=$(this).parent()
	var picid = parent.find("img").attr("id");
	$.ajax({
		url:"${Globals.root_path}/Image/" + picid,
		type : "DELETE"
	}).done(function(){
		parent.hide();
	})

});
