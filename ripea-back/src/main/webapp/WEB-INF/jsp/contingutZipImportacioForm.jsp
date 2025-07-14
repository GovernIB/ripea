<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.boto.crear.document.multiple"/></c:set>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<%--
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	--%>
	<script src="<c:url value="/webjars/moment/2.15.1/min/moment.min.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/min/locales.min.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/locale/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build//js/bootstrap-datetimepicker.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.min.js"/>"></script>
	<rip:modalHead/>
<style type="text/css">

#command {
	padding-bottom: 10px;
}

.title-container {
	margin-bottom: 20px;
	text-align: left;
  	background-color: #696666;
  	padding-left: 5px;
  	line-height: 25px;
  	height: 25px;
  	color: #fff;
}

</style>

<script>
var intervalProgres;
var content="<spring:message code="contingut.boto.crear.document.multiple.cancelar"/>";

$(document).ready(function() {

	clearInterval(intervalProgres);
	
	let currentIframe = window.frameElement;
	if (currentIframe) {
		var target = $(currentIframe.parentElement).find(".progressContainer");
        if (target) {
            target.remove();
        }
	}
	
	$('button[name=processarDocumentsBtn]').click( function(e) {
        $('.datatable-dades-carregant', parent.document).css("display", "none");
        
        mostrarBarraProgres();
    });
	
	setTimeout(function () {
        $('[id^="ntiEstadoElaboracion_"]').each(function() {
            $(this).trigger('change');
        });
    }, 50);
	
	$('[id^="ntiEstadoElaboracion_"]').change(function() {
        var index = $(this).attr('id').split('_')[1];  // Obtenir índex del camp seleccionat
        var estatElaboracioSeleccionat = $(this).val();
        
       	var estatsElaboracioIdentificadorEniObligat = "${estatsElaboracioIdentificadorEniObligat}".replace(/\s/g, '').split(',');
		if (estatElaboracioSeleccionat && (estatsElaboracioIdentificadorEniObligat.includes(estatElaboracioSeleccionat))) {
			$('#ntiIdDocumentoOrigenDiv_' + index).show();
		} else {
			$('#ntiIdDocumentoOrigenDiv_' + index).hide();
		}
	});

	// En tancar modal
	$('button[name=cancelarBtn]').click( function(e) {
		tancarModalImportacio();
    });
	
	$('button.close', currentIframe.parentElement.parentElement).click(function (e) {
		e.stopPropagation();
		e.preventDefault();
		
		tancarModalImportacio();
	});
	
	function mostrarBarraProgres() {
		let rootIframe = window.frameElement;
		
		var progressContainer = '<div class="progressContainer"> \
									<div class="progress"> \
										<div id="bar" class="progress-bar" role="progressbar progress-bar-striped active" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0%</div> \
									</div> \
									<div class="progressText"> \
										<p >...</p> \
									</div> \
								</div>';
							
		$(progressContainer).insertAfter(parent.$('.datatable-dades-carregant'));
		
		$(rootIframe.parentElement).find(".progressContainer").css({
			"width": "95%",
			"margin": "auto",
			"margin-top": "15px",
			"text-align": "center"
		});
		
		$(rootIframe.parentElement).find(".progressText").css({
			"margin-top": "10px"
		});
		
		refreshProgres();
	}
	
	function refreshProgres() {
		intervalProgres =  setInterval(function(){ consultarProgreso(); }, 50);
	}

    function consultarProgreso() {
    	$.ajax({
            url: "<c:url value="/contingut/${command.pareId}/zip/importacio/progres/"/>",
            type: "GET",
            success: function(response) {
            	let rootIframe = window.frameElement;
            	
            	if (! response.finished) {
            		$(rootIframe.parentElement).find(".progress-bar").css("width", response.progres + "%").text(Math.round(response.progres) + "%");
            		
            		$(response.info).each(function(index, info) {
                		$(rootIframe.parentElement).find(".progressText").text(info.text || "Procesando...");
            		});
                }
            	
            }
        });
    }
    
    function tancarModalImportacio() {
    	if (confirm(content)) {
    		window.top.location.reload();
    	}
    }
	  
});
</script>

</head>

<body>

	<c:set var="formAction"><rip:modalUrl value="${action}"/></c:set>
		
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="command" enctype="multipart/form-data">
		<form:hidden path="tascaId"/>
		<form:hidden path="metaExpedientId"/>
		
		<c:if test="${empty command.documents}">
			<rip:inputFile name="arxiuZip" textKey="contingut.document.zip.form.camp.arxiu" required="true"/>
		</c:if>
		
		<c:if test="${not empty command.documents}">			
			<c:forEach items="${command.documents}" varStatus="status" var="documentCommand">
				<div class="title-container">
					<label>Document ${status.index + 1}: ${documentCommand.fitxerNom}</label>
				</div>
				<rip:inputHidden name="documents[${status.index}].validacioFirmaCorrecte"/>
				<rip:inputHidden name="documents[${status.index}].validacioFirmaErrorMsg"/>
				<rip:inputHidden name="documents[${status.index}].tipusFirma"/>
				
				<rip:inputSelect 
					 name="documents[${status.index}].metaNodeId" 
					 textKey="contingut.document.form.camp.metanode" 
					 optionItems="${metaDocuments}" 
					 optionValueAttribute="id" 
					 optionTextAttribute="nom" 
					 emptyOption="${fn:length(metaDocuments) > 1 ? true : false}" 
					 emptyOptionTextKey="contingut.document.form.camp.nti.cap" 
					 required="true"/>
					 
				<rip:inputText
					 name="documents[${status.index}].nom"
					 textKey="contingut.document.form.camp.nom"
					 required="true"
					 tooltip="true"
					 tooltipMsg="contingut.document.form.camp.nom.caracters"
					 maxlength="250"/>
					 
				<rip:inputTextarea 
					 name="documents[${status.index}].descripcio" 
					 textKey="contingut.document.form.camp.descripcio" 
					 showsize="true" 
					 maxlength="510"/>
					 
				<rip:inputDateTime 
					 name="documents[${status.index}].dataTime" 
					 textKey="contingut.document.form.camp.data" 
					 required="true" 
					 readonly="true"/>
				
				<rip:inputSelect 
					 name="documents[${status.index}].ntiOrigen" 
					 emptyOption="true" 
					 emptyOptionTextKey="contingut.document.form.camp.nti.cap" 
					 textKey="contingut.document.form.camp.nti.origen" 
					 optionEnum="NtiOrigenEnumDto" 
					 required="true"/>
				
				<rip:inputSelect 
					 id="ntiEstadoElaboracion_${status.index}"
					 name="documents[${status.index}].ntiEstadoElaboracion" 
					 emptyOption="true" 
					 emptyOptionTextKey="contingut.document.form.camp.nti.cap" 
					 textKey="contingut.document.form.camp.nti.estela" 
					 required="true" 
					 optionItems="${ntiEstatElaboracioOptions}" 
					 optionValueAttribute="value" 
					 optionTextKeyAttribute="text"/>

				<div id="ntiIdDocumentoOrigenDiv_${status.index}">
					<rip:inputText 
						name="documents[${status.index}].ntiIdDocumentoOrigen"
						textKey="contingut.document.form.camp.id.doc.origen"
						required="true"
						comment="contingut.document.form.camp.id.doc.origen.comtentari" />
				</div>

				<rip:inputText
					 name="documents[${status.index}].fitxerNom" 
					 textKey="contingut.document.form.camp.arxiu.nom"
					 readonly="true"/>
					 
				
				<rip:inputCheckbox 
					 name="documents[${status.index}].ambFirma" 
					 textKey="contingut.document.form.camp.amb.firma" 
					 disabled="true"/>
				
				<c:if test="${!documentCommand.validacioFirmaCorrecte}">
					<div class="alert alert-danger" style="padding-top: 5px; padding-bottom: 5px; padding-left: 10px; margin-top: -20px; margin-bottom: 0px;" role="alert"><span>${documentCommand.validacioFirmaErrorMsg}</span></div>
				</c:if>
			</c:forEach>
		</c:if>

		<div id="modal-botons" class="well">
			<div class="text-right col-md-12">
				<a href="<c:url value="/contingut/zip/importacio/plantilla"/>"
					class="btn btn-info"
					style="float: left;"
					data-element-no-tancar="true"
					title="<spring:message code="contingut.document.zip.form.plantilla.boto"/>">
					<span class="fa fa-download"></span>
					<spring:message code="contingut.document.zip.form.plantilla.boto"/>
				</a>
			</div>
			<button type="submit" name="${not empty command.documents ? 'crearDocumentsBtn' : 'processarDocumentsBtn'}" class="btn btn-success"><span class="fa fa-save"></span>
				<spring:message code="${not empty command.documents ? 'comu.boto.crear' : 'comu.boto.processar'}"/>		
			</button>
			
			<button type="button" name="cancelarBtn" class="btn btn-default">
				<spring:message code="comu.boto.cancelar"/>	
			</button>
		</div>
	</form:form>
</body>
</html>
