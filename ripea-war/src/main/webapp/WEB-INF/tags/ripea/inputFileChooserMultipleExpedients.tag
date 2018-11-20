<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="ocultarExpedients" required="false" rtexprvalue="true"%>
<%@ attribute name="ocultarCarpetes" required="false" rtexprvalue="true"%>
<%@ attribute name="ocultarDocuments" required="false" rtexprvalue="true"%>
<%@ attribute name="contingutOrigen" required="true" rtexprvalue="true" type="java.lang.Object"%> <!-- document which we are moving -->
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<!-- setting root container of document we are moving  -->
<c:choose> 
	<c:when test="${not empty contingutOrigen.expedientPare}"><c:set var="contenidorBaseId" value="${contingutOrigen.expedientPare.id}"/></c:when>
	<c:otherwise><c:set var="contenidorBaseId" value="${contingutOrigen.id}"/></c:otherwise>
</c:choose>
<!-- setting parent container of document we are moving  -->
<c:choose> 
	<c:when test="${not empty contingutOrigen.pare}"><c:set var="contenidorInicialId" value="${contingutOrigen.pare.id}"/></c:when>
	<c:otherwise><c:set var="contenidorInicialId" value="${contingutOrigen.id}"/></c:otherwise>
</c:choose>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
	<label class="control-label col-xs-${campLabelSize}" for="${campPath}">
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
		<c:if test="${required}">*</c:if>
	</label>
	<div class="controls col-xs-${campInputSize}">
		<div id="file-chooser-input" class="input-group" style="width: 70%;">
			<form:hidden path="${campPath}" id="${campPath}" disabled="${disabled}"/>
<%-- 			<div id="file-chooser-panel-id" class="panel panel-default"> --%>
<%-- 				<div id="file-chooser-path-id" class="panel-heading"></div> --%>
<%-- 				<div id="file-chooser-content-id" class="panel-body"></div> --%>
<%-- 			</div>		 --%>
		</div>
		<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
	</div>
</div>
<style>
#file-chooser-${campPath}-input {
	width: 100%;
}
div.panel-body {
	padding: 0 !important;
}
div.list-group {
	margin: 0 !important;
}
a.list-group-item {
	border: none !important;
}

.panel-default>.panel-heading:hover {
    background-color: #c3daee;
    border-color: #afcee9;
}
.panel-default:hover>.panel-heading {
    background-color: #c3daee;
    border-color: #afcee9;
}
.panel-default:hover {
    background-color: #c3daee;
    border-color: #afcee9;
}
.panel + .panel {
    border-top: 0;
}

.panel-heading {
    border-bottom: 0px;
}

.panel, .panel>.panel-heading {
    margin-bottom: 0px;
    border-radius: 0px;
}

</style>
<script>
String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
};



function changeSelected(selectedId, campPath) {
	//removing previously visually selected container
	$('.selected').css('border-color', '');
	$('.selected .panel-heading').css('border-color', '');
	$('.selected .panel-heading').css('background-color', '');
	$('.selected').removeClass("selected");
	//selecting visually chosen container
	$('#file-chooser-panel-'+selectedId).css('border-color', '#2e6da4');
	$('#file-chooser-path-'+selectedId).css('background-color', '#337ab7');
	$('#file-chooser-path-'+selectedId).css('border-color', '#2e6da4');



	$('#file-chooser-panel-'+selectedId).addClass("selected");
	$("input#" + campPath).val(selectedId);
	
}


function refrescarOne(campPath, contenidorId, prevContenidorId) {

	$.ajax({
		type: "GET",
		url: '<c:url value="/contenidor/explora/"/>' + contenidorId, // returns container with given contenidorId
		async: false,
		timeout: 20000,
		success: function(data) { //returns container of the given element (it returns the folder in which document is situated and if there is no parent folder it returns expedient)
			var ocultarExpedients = <c:choose><c:when test="${ocultarExpedients}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;
			var ocultarCarpetes = <c:choose><c:when test="${ocultarCarpetes}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;
			var ocultarDocuments = <c:choose><c:when test="${ocultarDocuments}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;

			
 			

// 			$("#file-chooser-input").html('');
			$('#${campPath}').nextAll('.panel').remove();
			
			$("#file-chooser-input").append('<div id="file-chooser-panel-'+data.id+'" class="panel panel-default"></div>');

	
			// SETTING PATH IN THE PANEL HEADER
			var path = "";
			if (data.expedient) { // if it is an expedient
				path += '<span class="fa fa-folder-open"></span>  ';
				path += data.nom;
			
			} else {  
				path += data.pathAsStringExploradorAmbNom;
				path = path.replaceAll('#E#', '<span class="fa fa-folder-open"></span> ');
				path = path.replaceAll('#X#', '<span class="fa fa-folder-open"></span>');
				path = path.replaceAll('#C#', '<span class="fa fa-folder-o"></span>');
				path = path.replaceAll('#D#', '<span class="fa fa-file"></span>');
			}
	
	
			$("#file-chooser-panel-"+data.id).append('<div id="file-chooser-path-'+data.id+'" class="panel-heading"></div>');
			$("#file-chooser-path-"+data.id).html('');
			$("#file-chooser-path-"+data.id).append(path);
			$("#file-chooser-panel-"+data.id).append('<div id="file-chooser-content-'+data.id+'" class="panel-body"></div>');
			
	
			// SETTING CONTENT IN THE PANEL BODY
			$("#file-chooser-content-"+data.id).html('');
			$('<div class="list-group">').appendTo("#file-chooser-content"+data.id);
			if (!data.expedient) {
				$('<a data-id="' + data.pare.id + '" class="list-group-item"><span class="fa fa-level-up fa-flip-horizontal"></span> ..</a>').appendTo("#file-chooser-content-"+data.id);
			} else {
				$('<a data-id="returnAll" class="list-group-item"><span class="fa fa-level-up fa-flip-horizontal"></span> ..</a>').appendTo("#file-chooser-content-"+data.id);
			}
			if(data.fills){
				for (var i = 0; i < data.fills.length; i++) {
					var ocultar = (data.fills[i].expedient && ocultarExpedients) || (data.fills[i].carpeta && ocultarCarpetes) || (data.fills[i].document && ocultarDocuments);
					if (!ocultar && data.fills[i].id != '${contingutOrigen.id}') {
						var htmlIcona = '';
						if (data.fills[i].expedient)
							htmlIcona += '<span class="fa fa-briefcase"></span> ';
						if (data.fills[i].carpeta)
							htmlIcona += '<span class="fa fa-folder-o"></span> ';
						else if (data.fills[i].document)
							htmlIcona += '<span class="fa fa-file"></span> ';
						if ((data.fills[i].expedient || data.fills[i].carpeta) && data.fills[i].id != '${contingutOrigen.id}')
							$('<a data-id="' + data.fills[i].id + '" class="list-group-item">' + htmlIcona + data.fills[i].nom + '</a>').appendTo("#file-chooser-content-"+data.id);
						else
							$('<div class="list-group-item text-muted" style="border:none">' + htmlIcona + data.fills[i].nom + '</div>').appendTo("#file-chooser-content-"+data.id);
					}
				}
			}
			$('</div>').appendTo("#file-chooser-content-"+data.id);

			changeSelected(contenidorId, campPath);

	
			// SETTING EVENT HANDLER FOR CLICKING FILES OR FOLDERS IN THE PANEL BODY
			$("#file-chooser-content-"+data.id + " a").click(function() {

				if($(this).attr('data-id')=='returnAll'){
					loadFileChooser(campPath, data.id)
				} else {
					refrescarOne(campPath, $(this).attr('data-id'), data.id);
				}
				
			});
				
			webutilModalAdjustHeight();
			
		},
		error: function(xhr, textStatus, errorThrown) {
			console.log("<spring:message code="peticio.ajax.error"/>: " + xhr.responseText);
			if (textStatus == 'timeout')
				alert("<spring:message code="peticio.ajax.timeout"/>");
			else
				alert("<spring:message code="peticio.ajax.error"/>: " + errorThrown);
		}
    });
}



function loadFileChooser(campPath, contenidorId) {
	$.ajax({
		type: "GET",
		url: '<c:url value="/contenidor/exploraAllWithSameExpedientType/${contenidorBaseId}/"/>' + contenidorId, // returns container with given contenidorId
		async: false,
		timeout: 20000,
		success: function(dataTable) { //returns container of the given element (it returns the folder in which document is situated and if there is no parent folder it returns expedient)
			var ocultarExpedients = <c:choose><c:when test="${ocultarExpedients}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;
			var ocultarCarpetes = <c:choose><c:when test="${ocultarCarpetes}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;
			var ocultarDocuments = <c:choose><c:when test="${ocultarDocuments}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;

// 		$("#file-chooser-input").html('');
		$('#${campPath}').nextAll('.panel').remove();

		$.each(dataTable, function( key, data ) {

			// SETTING PATH IN THE PANEL HEADER
			var path = "";
			if (data.expedient) { // if it is an expedient
				path += '<span class="fa fa-folder-open"></span> ';
				path += data.nom;
			
			} else {  
				path += data.pathAsStringExploradorAmbNom;
				path = path.replaceAll('#E#', '<span class="fa fa-folder-open"></span> ');
				path = path.replaceAll('#X#', '<span class="fa fa-folder-open"></span>');
				path = path.replaceAll('#C#', '<span class="fa fa-folder-o"></span>');
				path = path.replaceAll('#D#', '<span class="fa fa-file"></span>');
			}


			$("#file-chooser-input").append('<div id="file-chooser-panel-'+data.id+'" class="panel panel-default"></div>');
			$("#file-chooser-panel-"+data.id).append('<div id="file-chooser-path-'+data.id+'" class="panel-heading"></div>');
			$("#file-chooser-path-"+data.id).html('');
			$("#file-chooser-path-"+data.id).append(path);

			


			

			// SETTING EVENT HANDLER FOR CLICKING FILES OR FOLDERS IN THE PANEL BODY
			$(".panel").click(function() {
				
				var idStr = $(this).attr('id');
				var idStrRes = idStr.replace("file-chooser-panel-", "");

				
				refrescarOne(campPath, idStrRes, data.id);	
			});


			// SETTING EVENT HANDLER FOR CLICKING ANY PANEL
			$("#file-chooser-panel-"+data.id).click(function(event) {

				// if element is triggered by "a" element, is processed in refrescarOne() element, not here
				if(!$(event.target).is("a")){
					changeSelected(data.id, campPath);
				}
			});

		});
			
			changeSelected(contenidorId, campPath);
			webutilModalAdjustHeight();
		},
		error: function(xhr, textStatus, errorThrown) {
			console.log("<spring:message code="peticio.ajax.error"/>: " + xhr.responseText);
			if (textStatus == 'timeout')
				alert("<spring:message code="peticio.ajax.timeout"/>");
			else
				alert("<spring:message code="peticio.ajax.error"/>: " + errorThrown);
		}
    });
}
$(document).ready(function() {
	loadFileChooser('${campPath}', '${contenidorInicialId}');
});
</script>