<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol">
	<spring:message code="expedient.peticio.form.acceptar.titol" /> - <spring:message code="expedient.peticio.form.acceptar.escollir.metadocs.titol" />
	<c:if test="${size!=0}">
		(${index + 1} / ${size})
	</c:if>
	
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


#titolINom {
	cursor: pointer !important;
}

.disabled-icon .form-control:hover {
    cursor: not-allowed !important;
}

.titolINom_btn {
	cursor: pointer !important;
}

.disabled-icon .titolINom_btn:hover {
    cursor: not-allowed !important;
}

.customIcon {
	font-size: 10px;
	background-color: transparent !important;
} 

.titolINom_btn {
	background-color: #ccc !important;
}
.titolINom_btn:hover {
	background-color: #b6b6b6 !important;
	border-color: #999;
}

</style>
<script>

$(document).ready(function(){

	
	$("button#btnSave").submit(function (e) {
	    e.preventDefault();
	    $("button#btnSave").attr("disabled", true);
	    return true;
	});		
});



var previousAnnex;
function showViewer(event, annexId, observacions, dataCaptura, origen) {
    var resumViewer = $('#annex-viewer');
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
    resumViewer.find('*').not('#container-previs').remove();
    var viewerContent = '<div class="panel-heading">\
							<span class="fa-stack customIcon" style="font-size: 10px;margin-top: -5px;">\
							  <i class="fa fa-file-o fa-stack-2x"></i>\
							  <i class="fa fa-search fa-1x" style="margin-left: 4px;margin-top: 7px;"></i>\
							</span>\
	        				<spring:message code="registre.detalls.pipella.previsualitzacio"/> \
    						<span class="fa fa-close" style="float: right; cursor: pointer;" onClick="closeViewer()"></span>\
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
	$('#container-previs').attr('src', '');
	$('#container-previs').addClass('rmodal_loading');
	showDocument(urlDescarrega, annexId);

	// scroll down
	$([document.documentElement, document.body]).animate({
        scrollTop: $("#annex-viewer").offset().top - 40
    }, 500);
}

function showDocument(arxiuUrl, annexId) {
	// Fa la petició a la url de l'arxiu
	$.ajax({
		type: 'GET',
		url: arxiuUrl,
		responseType: 'arraybuffer',
		success: function(json) {
			
			if (json.error) {
				$('#container-previs').removeClass('rmodal_loading');
				$('#annex-viewer .viewer-padding:last').before('<div class="viewer-padding"><div class="alert alert-danger"><spring:message code="contingut.previsualitzacio.error"/>: ' + json.errorMsg + '</div></div>');
			} else if (json.warning) {
				$('#container-previs-').removeClass('rmodal_loading');
				$('#annex-viewer .viewer-padding:last').before('<div class="viewer-padding"><div class="alert alert-warning"><spring:message code="contingut.previsualitzacio.warning"/>' + '</div></div>');
			} else {
				response = json.data;
				var blob = base64toBlob(response.contingut, response.contentType);
	            var file = new File([blob], response.contentType, {type: response.contentType});
	            link = URL.createObjectURL(file);
	            
	            var viewerUrl = "<c:url value="/webjars/pdf-js/2.5.207/web/viewer.html"/>" + '?file=' + encodeURIComponent(link);
			    $('#container-previs').removeClass('rmodal_loading');
			    $('#container-previs').attr('src', viewerUrl);
			}
		    
		},
		error: function(xhr, ajaxOptions, thrownError) {
			$('#container-previs').removeClass('rmodal_loading');
			alert(thrownError);
		}
	});
}

// Amagar visor
function closeViewer() {
	$('#annex-viewer').slideUp(500, function(){
	});
}



</script>

</head>
<body>


	<c:set var="formAction">
	<c:choose>
		<c:when test="${lastOne}">
			<rip:modalUrl value="/expedientPeticio/acceptar/${expedientPeticioId}" />
		</c:when>
		<c:otherwise>
			<rip:modalUrl value="/expedientPeticio/acceptar/${expedientPeticioId}/getNextAnnex" />
		</c:otherwise>
	</c:choose>
		
	</c:set>
	<form:form id="annexForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="registreAnnexCommand">

		<c:choose>
			<c:when test="${!empty registreAnnexCommand}">
					<div class="well"> 
						<form:hidden path="id" />
						<form:hidden path="tipusMime" />
						
						<c:set var="customIcon">
							<span class="fa-stack customIcon">
							  <i class="fa fa-file-o fa-stack-2x"></i>
							  <i class="fa fa-search fa-1x" style="padding-right: 1px;margin-top: 8px;"></i>
							</span>						
						</c:set>
						
						<div <c:choose>
								<c:when test="${registreAnnexCommand.tipusMime == 'application/pdf'}">
									onclick="showViewer(event, ${registreAnnexCommand.id}, '${registreAnnexCommand.observacions}', '${registreAnnexCommand.ntiFechaCaptura}', '${registreAnnexCommand.ntiOrigen}')"
									title="<spring:message code="registre.annex.detalls.previsualitzar"/>" 
								</c:when>
								<c:otherwise>
									title="<spring:message code="registre.annex.detalls.previsualitzar.no"/>"
									class="disabled-icon"
								</c:otherwise>
							 </c:choose>>
						<rip:inputText 
							name="titolINom"
							textKey="${registreAnnexCommand.id == -1 ? 'expedient.peticio.form.acceptar.camp.justificnat.nom' : 'expedient.peticio.form.acceptar.camp.annex.nom'}"
							readonly="true" 
							button="true" 
							buttonMsg="${registreAnnexCommand.tipusMime == 'application/pdf' ? 'registre.annex.detalls.previsualitzar' : 'registre.annex.detalls.previsualitzar.no'}"
							customIcon="${customIcon}" />
					</div>
						<rip:inputSelect name="metaDocumentId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="${fn:length(metaDocuments) > 1 ? true : false}" emptyOptionTextKey="contingut.document.form.camp.nti.cap" required="true"/>
						<rip:inputDate name="ntiFechaCaptura" textKey="registre.annex.detalls.camp.eni.data.captura" readonly="true" required="true"/>
						<rip:inputText name="ntiOrigen" textKey="registre.annex.detalls.camp.eni.origen" readonly="true"/>
						<rip:inputSelect name="ntiTipoDocumental" textKey="registre.annex.detalls.camp.eni.tipus.documental" disabled="true" optionEnum="NtiTipoDocumentoEnumDto"/>
						<rip:inputSelect name="sicresTipoDocumento" textKey="registre.annex.detalls.camp.sicres.tipus.document" disabled="true" optionEnum="SicresTipoDocumentoEnumDto"/>
						<rip:inputText name="observacions" textKey="registre.annex.detalls.camp.observacions" readonly = "true"/>
						<rip:inputSelect name="annexArxiuEstat" textKey="registre.annex.detalls.camp.estat.arxiu" disabled="true" optionEnum="ArxiuEstatEnumDto"/>

					</div>
					
					<div class="panel panel-default annex-viewer" id="annex-viewer">
						<iframe id="container-previs" class="viewer-padding" width="100%" height="540" frameBorder="0"></iframe>
					</div>  
					
			</c:when>
			<c:otherwise>
				<div class="well"> 
					<spring:message code="registre.annex.buit"/>
				</div>
				<div style="min-height: 50px;"></div>
			</c:otherwise>
		</c:choose>
		
		<div id="modal-botons" class="well">
			<button id="btnSave" type="submit" class="btn btn-success">
			<c:choose>
				<c:when test="${lastOne}">
					<span class="fa fa-save"></span> <spring:message code="comu.boto.guardar" />
				</c:when>
				<c:otherwise>
					<span class="fa fa-arrow-right"></span> <spring:message code="comu.boto.next" />
				</c:otherwise>
			</c:choose>			
			</button>
			<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>
</body>
</html>

