<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
/**
 * login.js for secure login
 */

$(document).ready(function() {

	$('#loginForm').submit(function() {

		var username = $('#username').val();
		var pass = $('#password').val();
		var returnvalue = false;
		if (username.length === 0 || pass.length == 0) {
			alert("Please insert an username and password!")
			return false;
		} else {
			$.ajax({
				url : '${Globals.root_path}/Login',
				async : false,
				data : {
					saltfor : username
				},
				type : 'POST',
				datatype : 'json',

				success : function(data) {

					var response = $.parseJSON(data);
					if (response.salt == 'false') {
						return false;
					}
					//substitute password with 0s
					var value = Array(pass.length + 1).join("0");
					$('#password').val(value);
					pass = SHA1(pass);
					var hash = SHA1(response.salt + pass);
					$('#hidden').val(hash);
					returnvalue = true;
					return true;

				},
				failure : function() {
					message('false', 'No connection to server');
					return false;
				},
				error : function(data){
					document.write(data.responseText);
					return false;
				}
				
			});
			
			 return returnvalue;

		}

	});
});