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
		<div id="file-chooser-${campPath}-input" class="input-group">
			<form:hidden path="${campPath}" id="${campPath}" disabled="${disabled}"/>
			<div id="file-chooser-${campPath}-panel" class="panel panel-default">
				<div id="file-chooser-${campPath}-path" class="panel-heading"></div>
				<div id="file-chooser-${campPath}-content" class="panel-body"></div>
			</div>
		</div>
		<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
	</div>
</div>
<style>
#file-chooser-${campPath}-input {
	width: 100%;
}
#file-chooser-${campPath}-panel div.panel-body {
	padding: 0 !important;
}
#file-chooser-${campPath}-panel div.list-group {
	margin: 0 !important;
}
#file-chooser-${campPath}-panel a.list-group-item {
	border: none !important;
}
</style>
<script>
String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
};
function refrescarFileChooser(campPath, contenidorId) {
	$.ajax({
		type: "GET",
		url: '<c:url value="/contenidor/explora/${contenidorBaseId}/"/>' + contenidorId, // returns container with given contenidorId
		async: false,
		timeout: 20000,
		success: function(data) { //returns container of the given element (it returns the folder in which document is situated and if there is no parent folder it returns expedient)
			var ocultarExpedients = <c:choose><c:when test="${ocultarExpedients}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;
			var ocultarCarpetes = <c:choose><c:when test="${ocultarCarpetes}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;
			var ocultarDocuments = <c:choose><c:when test="${ocultarDocuments}">true</c:when><c:otherwise>false</c:otherwise></c:choose>;
			$("input#" + campPath).val(data.id);
			
			data = JSOG.decode(data);
			// SEETING PATH IN THE PANEL HEADER
			var path = "";
			if (data.id == '${contenidorBaseId}') { // if the returned container is the root container
				if (data.expedient) { // if it is an expedient
					path += '<span class="fa fa-desktop"></span> Expedient: ';
					path += data.nom;
				}
			} else {  
				path += data.pathAsStringExploradorAmbNom;
				path = path.replaceAll('#E#', '<span class="fa fa-desktop"></span> Expedient');
				path = path.replaceAll('#X#', '<span class="fa fa-briefcase"></span>');
				path = path.replaceAll('#C#', '<span class="fa fa-folder"></span>');
				path = path.replaceAll('#D#', '<span class="fa fa-file"></span>');
			}
			$("#file-chooser-" + campPath + "-path").html(path);

			// SEETING CONTENT IN THE PANEL BODY
			$("#file-chooser-" + campPath + "-content").html('');
			$('<div class="list-group">').appendTo("#file-chooser-" + campPath + "-content");
			if (data.id != '${contenidorBaseId}')
				$('<a href="' + data.pare.id + '" class="list-group-item"><span class="fa fa-level-up fa-flip-horizontal"></span> ..</a>').appendTo("#file-chooser-" + campPath + "-content");
			for (var i = 0; i < data.fills.length; i++) {
				var ocultar = (data.fills[i].expedient && ocultarExpedients) || (data.fills[i].carpeta && ocultarCarpetes) || (data.fills[i].document && ocultarDocuments);
				if (!ocultar && data.fills[i].id != '${contingutOrigen.id}') {
					var htmlIcona = '';
					if (data.fills[i].expedient)
						htmlIcona += '<span class="fa fa-briefcase"></span> ';
					if (data.fills[i].carpeta)
						htmlIcona += '<span class="fa fa-folder"></span> ';
					else if (data.fills[i].document)
						htmlIcona += '<span class="fa fa-file"></span> ';
					if ((data.fills[i].expedient || data.fills[i].carpeta) && data.fills[i].id != '${contingutOrigen.id}')
						$('<a href="' + data.fills[i].id + '" class="list-group-item">' + htmlIcona + data.fills[i].nom + '</a>').appendTo("#file-chooser-" + campPath + "-content");
					else
						$('<div class="list-group-item text-muted" style="border:none">' + htmlIcona + data.fills[i].nom + '</div>').appendTo("#file-chooser-" + campPath + "-content");
				}
			}
			$('</div>').appendTo("#file-chooser-" + campPath + "-content");

			// SETTING EVENT HANDLER FOR CLICKING FILES OR FOLDERS IN THE PANEL BODY
			$('#file-chooser-' + campPath + '-content a').click(function() {
				refrescarFileChooser(campPath, $(this).attr('href'));
				return false;
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
$(document).ready(function() {
	refrescarFileChooser('${campPath}', '${contenidorInicialId}');
});
</script>