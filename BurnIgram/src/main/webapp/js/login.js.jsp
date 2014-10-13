/**
 * login.js for secure login
 */

$(document).ready(function() {

	$('#loginForm').submit(function() {

		var username = $('#username').val();
		var pass = $('#password').val();

		if (username.length === 0 || pass.length == 0) {
			alert("Please insert an username and password!")
			return false;
		} else {
			return $.ajax({
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
					return true;

				},
				failure : function() {
					message('false', 'No connection to server');
					return false;
				}
			});
			//to finally submit the form
			/* return true; */

		}

	});
});