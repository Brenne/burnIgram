<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
/**
 * javascript source for profile.jsp
 */

$("document").ready(function(){
	$(".success").hide();
	inputListener();
});

function inputListener(){
	$("input[name='email']").change(function(){
		var emailList = "";
		$("input[name='email']").each(function() {
			var emailValue = $(this).val();
			if(emailValue.indexOf("@")!==-1){
				emailList=emailList+$(this).val()+",";
			}
		    
		});
					$.ajax({
						url : "${Globals.root_path}/Profile/Email",
						
						data : {email:emailList},
						type : "PUT"
					}).done(
							function() {
								$(".success").fadeIn(500).delay(2000).fadeOut(500);
								addMailInput()
							})

			
	});
}


function addMailInput(){
	var lastInputMail =$("input[name='email']").last().clone();
	if($(lastInputMail).val()!=""){
		
		$(lastInputMail).insertBefore($("#lastEmail"));
		$("#lastEmail").before("<br/>");
		$("input[name='email']").last().val('');
		inputListener();

		
	}
}