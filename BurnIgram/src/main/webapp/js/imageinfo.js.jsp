<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
/**
 * javascript source for imageinfo.jsp
 */


$(".brightness, .rotate, .original, .magic").click(
		function() {
			//manipulation is e.g. "rotate" or "brightness"
			var manipulationKey = this.className;	
			//value is e.g. "right","left","dark","bright"
			var manipulationValue = this.id;
			
			sendAjaxRequest(manipulationKey, manipulationValue);
		});

function sendAjaxRequest(manipulationKey,manipulationValue){
	$.ajax({
		url : "${Globals.root_path}/Image/"+picid,
		dataType : "text",
		data : manipulationKey+"," + manipulationValue,
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
}