<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="charSearch" value='"' />
<c:set var="charReplace" value='\\"' />
<c:set var="maxFileSize"><%=es.caib.ripea.back.config.WebMvcConfig.MAX_UPLOAD_SIZE%></c:set>

<%
	pageContext.setAttribute(
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
pageContext.setAttribute(
		"multiplicitatEnumOptions",
		es.caib.ripea.back.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.service.intf.dto.MultiplicitatEnumDto.class,
		"multiplicitat.enum."));
pageContext.setAttribute(
		"metadocumentSequenciatipEnumOptions",
		es.caib.ripea.back.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto.class,
		"metadocument.seqtip.enum."));
pageContext.setAttribute(
		"metadocumentFluxtipEnumOptions",
		es.caib.ripea.back.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto.class,
		"metadocument.fluxtip.enum."));
%>

<c:choose>
	<c:when test="${empty metaDocumentCommand.id}"><c:set var="titol"><spring:message code="metadocument.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="metadocument.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
	
<style type="text/css">

.rmodal {
    display:    none;
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
body.loading {
    overflow: hidden;   
}
body.loading .rmodal {
    display: block;
}

.ui-dialog {
	z-index: 1000;
}
.modal-dialog {
	width: 100%;
	height: 100%;
	margin: 0;
	padding: 0;
}

.modal-content {
	height: auto;
	min-height: 100%;
}

.iframe_container {
	position: relative;
	width: 100%;
	height: 97vh;
	padding-bottom: 0;
}

.iframe_content {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
}
#fluxModal {
	margin: 1%;
}
.portafirmesFluxId_btn_edicio:hover {
	cursor: pointer;
}
.flux_disabled {
	pointer-events: none;
	cursor: not-allowed;
}
.flux_disabled:hover {
	cursor: not-allowed;
}
</style>	
<script type="text/javascript">

	var maxTamanyFitxerUpload = ${maxFileSize};

	$(document).ready(function() {

		let currentHeight = window.frameElement.contentWindow.document.body.scrollHeight;
		localStorage.setItem("currentIframeHeight", currentHeight);
		
		$('#plantilla').change(function(){
			let tamany = $(this)[0].files[0].size;
			var pare = $(this).closest('.fileinput').parent();
			if (tamany>maxTamanyFitxerUpload) {
				$(pare).find('div.alert.alert-danger').remove();
				$(pare).append('<div class="alert alert-danger" style="padding-top: 5px; padding-bottom: 5px; padding-left: 10px; margin-bottom: 0px;" role="alert"><span><spring:message code="MaxFileUploadSize"/></span></div>');
			} else {
				$(pare).find('div.alert.alert-danger').remove();
			}
		});
		
		$("#biometricaCallbackActiu").on('change', function(){
			if($(this).prop("checked") == true){
				$(".callback").removeClass("hidden");
			} else if($(this).prop("checked") == false){ 
				$(".callback").addClass("hidden");
			}
		});
		
        if($("#firmaPortafirmesActiva").prop("checked") == true){
        	$("label[for='portafirmesDocumentTipus']").append( " *" );
        	$($("label[for='portafirmesResponsables']")[1]).append( " *" );
        }

		$("#firmaPortafirmesActiva").on('change', function(){
	            if($(this).prop("checked") == true){
	            	$("label[for='portafirmesDocumentTipus']").append( " *" );
	            	$($("label[for='portafirmesResponsables']")[1]).append( " *" );
	            }
	            else if($(this).prop("checked") == false){
	            	$("label[for='portafirmesDocumentTipus']").text( $("label[for='portafirmesDocumentTipus']").text().replace(' *', '') );
	            	$($("label[for='portafirmesResponsables']")[1]).text( $($("label[for='portafirmesResponsables']")[1]).text().replace(' *', '') );
	            }			
		});
		
		$("#portafirmesFluxTipus").on('change', function(){
			if($(this).val() == 'SIMPLE') {
				$('.flux_portafib').hide();
				$('.flux_simple').show();
			} else {
				$('.flux_portafib').show();
				$('.flux_simple').hide();
			}
		});
		
		$("#portafirmesFluxTipus").trigger('change');
		
		$(".portafirmesFluxId_btn_edicio").on('click', function() {
			var metaDocumentNom = "${fn:replace(metaDocumentCommand.nom, charSearch, charReplace)}";
			$.ajax({
				type: 'GET',
				dataType: "json",
				data: {nom: metaDocumentNom, plantillaId: $("#portafirmesFluxId").val()},
				url: "<c:url value="/modal/metaExpedient/metaDocument/iniciarTransaccio"/>",
				success: function(transaccioResponse) {
					if (transaccioResponse != null && !transaccioResponse.error) {
						$('#metaDocumentCommand').addClass("hidden");
						$('.flux_container').html('<div class="iframe_container"><iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe></div>');	
						adjustModalPerFlux();
						$body = $("body");
						$body.addClass("loading");
					} else if (transaccioResponse != null && transaccioResponse.error) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + transaccioResponse.errorDescripcio + '</span></div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						webutilModalAdjustHeight();
					}
				},
				error: function(error) {
					if (error != null && error.responseJSON != null) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + error.responseJSON.message + '</span></div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						webutilModalAdjustHeight();
					}
				}
			});
		});
		
		$.ajax({
			type: 'GET',
			dataType: "json",
			url: "<c:url value="/metaExpedient/metaDocument/flux/plantilles"/>",
			success: function(data) {
				var plantillaActual = "${portafirmesFluxSeleccionat}";
				var selPlantilles = $("#portafirmesFluxId");
				selPlantilles.empty();
				selPlantilles.append("<option value=\"\"></option>");
				if (data) {
					var items = [];
					$.each(data, function(i, val) {
						items.push({
							"id": val.fluxId,
							"text": val.nom
						});
						selPlantilles.append("<option value=\"" + val.fluxId + "\">" + val.nom + "</option>");
					});
				}
				var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
				selPlantilles.select2(select2Options);
				if (plantillaActual != '') {
					selPlantilles.val(plantillaActual);
					selPlantilles.change();
					$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.flux.editar"/>");
				}
			},
			error: function (error) {
				var selPlantilles = $("#portafirmesFluxId");
				selPlantilles.empty();
				selPlantilles.append("<option value=\"\"></option>");
				var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
				selPlantilles.select2(select2Options);
			}
		});
		
		$(".portafirmesFluxId_btn_esborrar").on('click', function () {
			var portafirmesFluxId = $("#portafirmesFluxId").val();
			$.ajax({
				type: 'GET',
				dataType: "json",
				url: "<c:url value="/metaExpedient/metaDocument/flux/esborrar/"/>" + portafirmesFluxId,
				success: function(esborrat) {
					if (esborrat) {
						var alertDiv = "<div class='alert alert-success' role='alert'><a class='close' data-dismiss='alert'>×</a><span>" + "<spring:message code="metadocument.form.camp.portafirmes.flux.esborrar.ok"/>" + "</span></div>";
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						$("#portafirmesFluxId option[value='" + portafirmesFluxId + "']").remove();
					} else {
						var alertDiv = "<div class='alert alert-danger' role='alert'><a class='close' data-dismiss='alert'>×</a><span>" + "<spring:message code="metadocument.form.camp.portafirmes.flux.esborrar.ko"/>" + "</span></div>";
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
					}
					webutilModalAdjustHeight();
				},
				error: function (error) {
					var alertDiv = "<div class='alert alert-danger' role='alert'><a class='close' data-dismiss='alert'>×</a><span>" + "<spring:message code="metadocument.form.camp.portafirmes.flux.esborrar.ko"/>" + "</span></div>";
					$('form').prev().find('.alert').remove();
					$('form').prev().prepend(alertDiv);
					webutilModalAdjustHeight();		
				}
			});
		});
		$("#portafirmesFluxId").on('change', function () {
			var portafirmesFluxId = $(this).val();
			if(portafirmesFluxId != null && portafirmesFluxId != '') {
				$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.flux.editar"/>");
				$(".portafirmesFluxId_btn_esborrar").removeClass("flux_disabled");
			} else {
				$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>");
				$(".portafirmesFluxId_btn_esborrar").addClass("flux_disabled");
			}
		});
		
		$("#portafirmesFluxId").trigger('change');
	});
	
	function adjustModalPerFlux() {
		var $iframe = $(window.frameElement);
		$iframe.css('height', '100%');
		$iframe.parent().css('height', '600px');
		$iframe.closest('div.modal-content').css('height',  'auto');
		$iframe.closest('div.modal-dialog').css({
			'height':'auto',
			'height': '100%',
			'margin': '3% auto',
			'padding': '0'
		});
		$iframe.closest('div.modal-lg').css('width', '95%');
		$iframe.parent().next().addClass('hidden');
	}
	
	function removeLoading() {
		$body = $("body");
		$body.removeClass("loading");
	}
</script>
	
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaDocument"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="metaDocumentCommand" enctype="multipart/form-data">
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.dades"/></a></li>
			<li role="presentation"><a href="#dades-nti" aria-controls="dades-nti" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.dadesnti"/></a></li>
			<li role="presentation"><a href="#firma-portafirmes" aria-controls="firma-portafirmes" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.portafirmes"/></a></li>
			<li role="presentation"><a href="#firma-passarela" aria-controls="firma-passarela" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firmasimpleweb"/></a></li>
			<c:if test="${isFirmaBiometrica}">
				<li role="presentation"><a href="#firma-biometrica" aria-controls="firma-biometrica" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.biometrica"/></a></li>
			</c:if>
			<li role="presentation"><a href="#pinbal" aria-controls="pinbal" role="tab" data-toggle="tab"><spring:message code="metadocument.form.tab.pinbal"/></a></li>
		</ul>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="metaExpedientId"/>
		<br/>
		<div class="tab-content content">
			<div role="tabpanel" class="tab-pane active" id="dades">
				<rip:inputText name="codi" textKey="metadocument.form.camp.codi" required="true"/>
				<rip:inputText name="nom" textKey="metadocument.form.camp.nom" required="true"/>
				<rip:inputTextarea name="descripcio" textKey="metadocument.form.camp.descripcio"/>
				<rip:inputSelect name="multiplicitat" textKey="metadocument.form.camp.multiplicitat" optionItems="${multiplicitatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<rip:inputFile 
					name="plantilla"
					textKey="metadocument.form.camp.plantilla"
					comment="contingut.document.MAX_UPLOAD_SIZE"
					fileName="${metaDocumentCommand.plantillaNom}"/>
			</div>
			<div role="tabpanel" class="tab-pane" id="dades-nti">
				<rip:inputSelect name="ntiOrigen" emptyOption="true"
					emptyOptionTextKey="contingut.document.form.camp.nti.cap"
					textKey="contingut.document.form.camp.nti.origen"
					optionItems="${ntiOrigenOptions}" optionValueAttribute="value"
					optionTextKeyAttribute="text" required="true" />
				<rip:inputSelect name="ntiTipoDocumental" emptyOption="true"
					emptyOptionTextKey="contingut.document.form.camp.nti.cap"
					textKey="contingut.document.form.camp.nti.tipdoc"
					optionItems="${ntiTipusDocumentalOptions}"
					optionValueAttribute="codi" 
					optionTextAttribute="codiNom"
					required="true" 
					optionMinimumResultsForSearch="3"/>
				<rip:inputSelect name="ntiEstadoElaboracion" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.estela" optionItems="${ntiEstatElaboracioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
			</div>
			<div role="tabpanel" class="tab-pane" id="firma-portafirmes">
				<rip:inputCheckbox name="firmaPortafirmesActiva" textKey="metadocument.form.camp.firma.portafirmes.activa"/>
				<c:if test="${isPortafirmesDocumentTipusSuportat}">
					<rip:inputSelect name="portafirmesDocumentTipus" textKey="metadocument.form.camp.portafirmes.document.tipus" optionItems="${portafirmesDocumentTipus}" optionValueAttribute="id" optionTextAttribute="codiNom" emptyOption="true" optionMinimumResultsForSearch="0" disabled="${bloquejarCamps}"/>
				</c:if>
				<%--rip:inputText name="portafirmesFluxId" textKey="metadocument.form.camp.portafirmes.flux.id"/--%>
				<%--<rip:inputText name="portafirmesResponsables" textKey="metadocument.form.camp.portafirmes.responsables" multiple="true"/>--%>
				<rip:inputSelect name="portafirmesFluxTipus" textKey="metadocument.form.camp.portafirmes.fluxtip" optionItems="${metadocumentFluxtipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<div class="flux_portafib">
					<rip:inputSelect name="portafirmesFluxId" textKey="metadocument.form.camp.portafirmes.flux.id" emptyOption="true" botons="true" icon="fa fa-external-link" iconAddicional="fa fa-trash-o" buttonMsg="${buttonTitle}"/>
				</div>
				<div class="flux_simple">
					<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
					<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
					<rip:inputSuggest 
						name="portafirmesResponsables" 
						urlConsultaInicial="${urlConsultaInicial}" 
						urlConsultaLlistat="${urlConsultaLlistat}" 
						textKey="metadocument.form.camp.portafirmes.responsables"
						suggestValue="nif"
						suggestText="codiAndNom"
						suggestTextAddicional="nifOfuscat"
						required="true"/>
					<rip:inputSelect name="portafirmesSequenciaTipus" textKey="metadocument.form.camp.portafirmes.seqtip" optionItems="${metadocumentSequenciatipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				</div>									
			</div>
			<div role="tabpanel" class="tab-pane" id="firma-passarela">
				<rip:inputCheckbox name="firmaPassarelaActiva" textKey="metadocument.form.camp.firmaSimpleWebActiva"/>
			</div>
			<c:if test="${isFirmaBiometrica}">
				<div role="tabpanel" class="tab-pane" id="firma-biometrica">
					<rip:inputCheckbox name="firmaBiometricaActiva" textKey="metadocument.form.camp.firma.biometrica.activa"/>
					<rip:inputCheckbox name="biometricaLectura" textKey="metadocument.form.camp.biometrica.lectura"/>
				</div>
			</c:if>
			<div role="tabpanel" class="tab-pane" id="pinbal">
				<rip:inputCheckbox name="pinbalActiu" textKey="metadocument.form.camp.pinbal.actiu"/>
				<rip:inputSelect 
					name="pinbalServei"
					textKey="metadocument.form.camp.pinbal.servei"
					required="true"
					optionItems="${pinbalServeiEnumOptions}"
					optionValueAttribute="codi"
					optionTextAttribute="nom"/>
			</div>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>
				<c:choose>
					<c:when test="${empty metaDocumentCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
					<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
				</c:choose>
			</button>	
			<a href="<c:url value="/metaDocument"/>" class="btn btn-default modal-cancel" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	<div class="flux_container"></div>
	<div class="rmodal"></div>
</body>
</html>
