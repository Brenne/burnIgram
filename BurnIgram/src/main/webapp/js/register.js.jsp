<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
/**
 * javascrpit for register.jsp
 */
/* from http://stackoverflow.com/questions/1909441/jquery-keyup-delay */
		//begin
		var delay = (function() {
			var timer = 0;
			return function(callback, ms) {
				clearTimeout(timer);
				timer = setTimeout(callback, ms);
			};
		})();

		//end

		$(document).ready(function() {
			$(".userexists").hide();
		});
		var usernameExists = false;
		$("#username").keyup(function() {
			var username = $(this).val().toLowerCase();
			/*we use the delay function to avoid an ajax call after each keyup.
			  Instead we trigger the ajax function if there was no keyup event for 400ms.
			  We reduce traffic and prevent errors due to
			  a too large amount of requests /responses */
			delay(function() {

				$.ajax({
					url : '${Globals.root_path}/Username/' + username,
					async : true,
					type : 'GET',
					datatype : 'text',
					failure : function() {
						/* in case of failure we don't know if username exists
						   so hide error
						*/
						$(".userexists").hide();
						usernameExists = false;
					},

				}).done(function(data) {
					if (data == "true") {
						$(".userexists").show();
						usernameExists = true;
					} else {
						$(".userexists").hide();
						usernameExists = false;
					}

				});
			}, 400);

		});
		$("#formRegister").submit(function(){
			if(usernameExists){
				return false;
			}else{
				if(! $("#password").val()){
					alert("Please enter a password");	
					return false;
				}
				else if($("#password").val()!=$("#password1").val()){
					alert("Your passwords must match!");
					return false;
				}
				return true;
			}
		});