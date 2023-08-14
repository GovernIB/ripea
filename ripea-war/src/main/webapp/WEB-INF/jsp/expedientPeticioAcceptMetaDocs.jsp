<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol">
	<spring:message code="expedient.peticio.form.acceptar.titol" /> - <spring:message code="expedient.peticio.form.acceptar.escollir.metadocs.titol" />
</c:set>
<html>
<head>
<title>${titol}</title>
<rip:modalHead />

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
<style type="text/css">
.fa-circle-o-notch {
	position: absolute;
	right: 10px;
	top: 10px;
}
.rmodal_loading {
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
.annex-viewer {
	display: none;
	width: 100%;
}
.viewer-content {
	width: 100%;
	padding-top: 1% !important;
}

.viewer-content > .dl-horizontal, .viewer-firmes-container > .dl-horizontal {
	margin-bottom: 0;
}

.viewer-firmes hr {
	margin-top: 5px !important;
	margin-bottom: 5px !important;
}

.viewer-padding {
	padding: 0% 2% 0% 2%;
	padding-top: 1%;
}


.form-control {
	cursor: pointer !important;
}

.disabled-icon .form-control:hover {
    cursor: not-allowed !important;
}

</style>
<script>

$(document).ready(function(){

var metaDocs = [];
<c:forEach var="metaDoc" items="${metaDocuments}" varStatus="status">
metaDocs.push({'id': ${metaDoc.id}, 'permetMultiple': ${metaDoc.permetMultiple}, 'nom': '${metaDoc.nom}'});
</c:forEach>

	var listOfSelectedNotMultiple = [];
	$("[id$=metaDocumentId]").on('change', function(e) {

		elementWithChange = $(this);
		elementWithChangeId = $(this).val();
		
		listOfSelectedNotMultiple = [];
		$("[id$=metaDocumentId]").each( function() {
			elemeId = $(this).val();
			for (var md in metaDocs) {
				if (elemeId == metaDocs[md].id && metaDocs[md].permetMultiple==false) {
					listOfSelectedNotMultiple.push(metaDocs[md]);
				}
			}
		});
		
		$("[id$=metaDocumentId]").each( function() {
			var other = $(this);
			var otherId = $(this).val();
			if (!other.is(elementWithChange)) {
				var thisList = [];
				for (var md in metaDocs) {
					var addToList = true;
					for (var nm in listOfSelectedNotMultiple) {
						if (listOfSelectedNotMultiple[nm].id == metaDocs[md].id && listOfSelectedNotMultiple[nm].id != otherId) {
							addToList = false;
						}
					}
					if (addToList) {
						thisList.push(metaDocs[md]);
					}
				}
				var idElem = other.attr('id');
				idElem = idElem.replace(/\[/g, '\\[').replace(/\]/g, '\\]').replace(/\./g, '\\.');
				$('#' + idElem + ' option[value!=""]').remove();
				for (var i = 0; i < thisList.length; i++) {
					other.append('<option value="' + thisList[i].id + '">' + thisList[i].nom + '</option>');
				}
				$(other).val(otherId);
				
			}
		});
	});

	$("[id$=metaDocumentId]").trigger( "change" );
	
	$("button#btnSave").submit(function (e) {
	    e.preventDefault();
	    $("button#btnSave").attr("disabled", true);
	    return true;
	});		
});



var previousAnnex;
function showViewer(event, annexId, observacions, dataCaptura, origen) {
    var resumViewer = $('#annex-viewer-' + annexId);
	// Mostrar/amagar visor
	if (!resumViewer.is(':visible')) {
		resumViewer.slideDown(500);
	} else if (previousAnnex == undefined || previousAnnex == annexId) {
		closeViewer(annexId);
		event.srcElement.parentElement.style = "background: #fffff";
		previousAnnex = annexId;
		return;
	}
	event.srcElement.parentElement.style = "background: #f9f9f9";
	previousAnnex = annexId;
	
    // Mostrar contingut capçalera visor
    resumViewer.find('*').not('#container-previs-' + annexId).remove();
    var viewerContent = '<div class="panel-heading"><spring:message code="registre.detalls.pipella.previsualitzacio"/> \
    					 <span class="fa fa-close" style="float: right; cursor: pointer;" onClick="closeViewer(' + annexId + ')"></span>\
    					 </div>\
    					 <div class="viewer-content viewer-padding">\
    						<dl class="dl-horizontal">\
	        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.eni.data.captura"/>: </dt><dd>' + dataCaptura + '</dd>\
	        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.eni.origen"/>: </dt><dd>' + origen + '</dd>\
	        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.observacions"/>: </dt><dd>' + observacions + '</dd>\
        					</dl>\
    					 </div>';
    resumViewer.prepend(viewerContent);
    
    // Recupera i mostrar contingut firmes
    $.get(
			"<c:url value="/expedientPeticio/firmaInfo/"/>" + annexId + "/content",
			function(firmes) {
				if (firmes && firmes.length > 0) {
					var nieList = "", nomList = "";
					var viewerContent = '<div class="viewer-firmes viewer-padding">\
											<hr>\
				    						<div class="viewer-firmes-container">';
				    firmes.forEach(function(firma) {
    					nieList += '[';
    					firma.detalls.forEach(function(firmaDetall, index) {
							if (firmaDetall.responsableNif != undefined && firmaDetall.responsableNif != null)	
								nieList += firmaDetall.responsableNif + (index !== (firma.detalls.length -1) ? ', ' : '');
							if (firmaDetall.responsableNom != undefined && firmaDetall.responsableNom != null)
								nomList += firmaDetall.responsableNom + (index !== (firma.detalls.length -1) ? ', ' : '');
							if (firmaDetall.responsableNif == null && firma.autofirma != null)
								nieList += '<spring:message code="registre.annex.detalls.camp.firma.autoFirma"/> <span class="fa fa-info-circle" title="<spring:message code="registre.annex.detalls.camp.firma.autoFirma.info" />"></span>';
							
						});
    					nieList += ']';
				    });

					viewerContent += '<dl class="dl-horizontal">\
						   				<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.firmants"/>:</dt>\
						   				<dd>' + nieList + (nomList != "" ? ' - ' +  nomList : '') + '</dd>\
						   			  </dl>\
						   			  </div><hr></div>';
						$(viewerContent).insertAfter('.viewer-content');
				}
			}
	);


    // Recuperar i mostrar document al visor
	var urlDescarrega = "<c:url value="/expedientPeticio/annex/"/>" + annexId + "/content";
	$('#container-previs-' + annexId).attr('src', '');
	$('#container-previs-' + annexId).addClass('rmodal_loading');
	showDocument(urlDescarrega, annexId);
}

function showDocument(arxiuUrl, annexId) {
	// Fa la petició a la url de l'arxiu
	$.ajax({
		type: 'GET',
		url: arxiuUrl,
		responseType: 'arraybuffer',
		success: function(json) {
			
			if (json.error) {
				$('#container-previs-' + annexId).removeClass('rmodal_loading');
				$('#annex-viewer-' + annexId  + ' .viewer-padding:last').before('<div class="viewer-padding"><div class="alert alert-danger"><spring:message code="contingut.previsualitzacio.error"/>: ' + json.errorMsg + '</div></div>');
			} else if (json.warning) {
				$('#container-previs-' + annexId).removeClass('rmodal_loading');
				$('#annex-viewer-' + annexId  + ' .viewer-padding:last').before('<div class="viewer-padding"><div class="alert alert-warning"><spring:message code="contingut.previsualitzacio.warning"/>' + '</div></div>');
			} else {
				response = json.data;
				var blob = base64toBlob(response.contingut, response.contentType);
	            var file = new File([blob], response.contentType, {type: response.contentType});
	            link = URL.createObjectURL(file);
	            
	            var viewerUrl = "<c:url value="/webjars/pdf-js/2.5.207/web/viewer.html"/>" + '?file=' + encodeURIComponent(link);
			    $('#container-previs-' + annexId).removeClass('rmodal_loading');
			    $('#container-previs-' + annexId).attr('src', viewerUrl);
			}
		    
		},
		error: function(xhr, ajaxOptions, thrownError) {
			$('#container-previs-' + annexId).removeClass('rmodal_loading');
			alert(thrownError);
		}
	});
}

// Amagar visor
function closeViewer(annexId) {
	$('#annex-viewer-' + annexId).slideUp(500, function(){
	});
}



</script>

</head>
<body>

	<c:set var="formAction">
		<rip:modalUrl value="/expedientPeticio/acceptar/${expedientPeticioId}" />
	</c:set>
	<form:form id="expedientPeticioAcceptarForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientPeticioAcceptarCommand">
		<form:hidden path="id" />
		<form:hidden path="accio"/>
		<form:hidden path="metaExpedientId"/>
		<form:hidden path="expedientId"/>
		<form:hidden path="organGestorId"/>
		<form:hidden path="any"/> 			
		<form:hidden path="associarInteressats"/> 
		<form:hidden path="agafarExpedient"/>
		<form:hidden path="newExpedientTitol"/>
		
		<c:choose>
			<c:when test="${!empty expedientPeticioAcceptarCommand.annexos}">
				<c:forEach items="${expedientPeticioAcceptarCommand.annexos}" varStatus="vs" var="annex">
					<div class="well"> 
						<form:hidden path="annexos[${vs.index}].id" />
						
						<div <c:choose>
								<c:when test="${annex.tipusMime == 'application/pdf' }">
									onclick="showViewer(event, ${annex.id}, '${annex.observacions}', '${annex.ntiFechaCaptura}', '${annex.ntiOrigen}')"
									title="<spring:message code="registre.annex.detalls.previsualitzar"/>" 
								</c:when>
								<c:otherwise>
									title="<spring:message code="registre.annex.detalls.previsualitzar.no"/>"
									class="disabled-icon"
								</c:otherwise>
							 </c:choose>>
							<rip:inputText name="annexos[${vs.index}].titolINom" textKey="expedient.peticio.form.acceptar.camp.annex.nom" required="true" readonly = "true"/>
						</div>
						<rip:inputSelect name="annexos[${vs.index}].metaDocumentId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="${fn:length(metaDocuments) > 1 ? true : false}" emptyOptionTextKey="contingut.document.form.camp.nti.cap" required="true"/>
					</div>
					
					<div class="panel panel-default annex-viewer" id="annex-viewer-${annex.id}">
						<iframe id="container-previs-${annex.id}" class="viewer-padding" width="100%" height="540" frameBorder="0"></iframe>
					</div>  
					
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div class="well"> 
					<spring:message code="registre.annex.buit"/>
				</div>
				<div style="min-height: 50px;"></div>
			</c:otherwise>
		</c:choose>
		
		<c:if test="${!empty expedientPeticioAcceptarCommand.justificant}">
			<c:set var="justificant" value="${expedientPeticioAcceptarCommand.justificant}"></c:set>
			<div class="well"> 
				<form:hidden path="justificant.id" />
				<rip:inputText name="justificant.titolINom" textKey="expedient.peticio.form.acceptar.camp.justificnat.nom" required="true" readonly = "true"/>
				<rip:inputSelect name="justificant.metaDocumentId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="${fn:length(metaDocuments) > 1 ? true : false}" emptyOptionTextKey="contingut.document.form.camp.nti.cap" required="true"/>
			</div>
		</c:if>
		<div id="modal-botons" class="well">
			<button id="btnSave" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar" /></button>
			<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>
</body>
</html>

