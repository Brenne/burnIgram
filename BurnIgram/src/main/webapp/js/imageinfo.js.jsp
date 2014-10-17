<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
/**
 * javascript source for imageinfo.jsp
 */

$(".brightness, .rotate").click(
		function() {
			//value is e.g. "right","left","dark","bright"
			var value = this.id;
			//manipulation is e.g. "rotate" or "brightness"
			var manipulation = this.className;	
			$.ajax({
				url : "${Globals.root_path}/Image/"+picid,
				dataType : "text",
				data : manipulation+"," + value,
				type : "PUT"
			}).done(
					function() {
						var d = new Date();
						//the date is added as parameter to prevent browser from fetching image from cache
						$("#thumb").attr(
								"src",
								"${Globals.root_path}/Thumb/"+picid+"?"
										+ d.getTime());
					})

		});
