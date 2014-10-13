/**
 * javascript source for imageinfo.jsp
 */

$(".brightness").click(
		function() {
			var value = this.id;

			$.ajax({
				url : "${Globals.root_path}/Image/${Pic.SUUID}",
				dataType : "text",
				data : "brightness," + value,
				type : "PUT"
			}).done(
					function() {
						var d = new Date();
						//the date is added as parameter to prevent browser from fetching image from cache
						$("#thumb").attr(
								"src",
								"${Globals.root_path}/Thumb/${Pic.SUUID}?"
										+ d.getTime());
					})

		});

$(".rotate").click(
		function() {
			var direction = this.id;
			console.log(direction);
			$.ajax({
				url : "${Globals.root_path}/Image/${Pic.SUUID}",
				data : "rotate," + direction,
				dataType : "text",
				type : "PUT"
			}).done(
					function() {
						var d = new Date();
						//the date is added as parameter to prevent browser from fetching image from cache
						$("#thumb").attr(
								"src",
								"${Globals.root_path}/Thumb/${Pic.SUUID}?"
										+ d.getTime());
					})

		});