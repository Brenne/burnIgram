/**
 * java script file for userpics
 */
$(".profilepic").click(function(){
	var picid=$(this).parent().find(
					"img").attr("id");

	$.ajax({
		url:"${Globals.root_path}/Profile/Profilepic/"+picid,
		type:"PUT"
	})
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
