<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
	pageContext.setAttribute(
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
pageContext.setAttribute(
		"multiplicitatEnumOptions",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.MultiplicitatEnumDto.class,
		"multiplicitat.enum."));
pageContext.setAttribute(
		"metadocumentSequenciatipEnumOptions",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto.class,
		"metadocument.seqtip.enum."));
pageContext.setAttribute(
		"metadocumentFluxtipEnumOptions",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto.class,
		"metadocument.fluxtip.enum."));
%>

<c:choose>
	<c:when test="${empty metaExpedientCommand.id}"><c:set var="titol"><spring:message code="metadocument.form.titol.crear"/></c:set></c:when>
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
.portafirmesFluxId_btn:hover {
	cursor: pointer;
}
</style>	
<script type="text/javascript">
	$(document).ready(function() {
		let currentHeight = window.frameElement.contentWindow.document.body.scrollHeight;
		localStorage.setItem("currentIframeHeight", currentHeight);
		console.log(localStorage.getItem("currentIframeHeight"));
		
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
		
		$(".portafirmesFluxId_btn").on('click', function(){
			var metaDocumentNom = '${metaDocumentCommand.nom}';
			$.ajax({
				type: 'GET',
				dataType: "json",
				data: {nom: metaDocumentNom},
				url: "<c:url value="/modal/metaExpedient/metaDocument/iniciarTransaccio"/>",
				success: function(transaccioResponse) {
					if (transaccioResponse != null) {
						localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
						//$("#fluxModal").modal('show');
						//$("#fluxModal").find(".modal-body").html('<div class="iframe_container"><iframe class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe></div>');	
						$('#metaDocumentCommand').addClass("hidden");
						$('.flux_container').html('<div class="iframe_container"><iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe></div>');	
						
						adjustModalPerFlux();
						$body = $("body");
						$body.addClass("loading");
					}
				},
				error: function(err) {
					console.log("Error recuperant la transacció");
				}
			});
		});
		
		$('#fluxIframe').ready(function () {
		    $(this).addClass("loading");
		});
		$('#fluxIframe').load(function () {
		    $(this).removeClass("loading");
		});
		
		$('.modal-cancel').on('click', function(){
			localStorage.removeItem('transaccioId');
		});
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
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaDocumentCommand.metaExpedientId}/metaDocument"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaDocumentCommand" enctype="multipart/form-data">
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.dades"/></a></li>
			<li role="presentation"><a href="#dades-nti" aria-controls="dades-nti" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.dadesnti"/></a></li>
			<li role="presentation"><a href="#firma-portafirmes" aria-controls="firma-portafirmes" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.portafirmes"/></a></li>
			<li role="presentation"><a href="#firma-passarela" aria-controls="firma-passarela" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.passarela"/></a></li>
			<c:if test="${isFirmaBiometrica}">
				<li role="presentation"><a href="#firma-biometrica" aria-controls="firma-biometrica" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.biometrica"/></a></li>
			</c:if>
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
				<rip:inputFile name="plantilla" textKey="metadocument.form.camp.plantilla"/>
			</div>
			<div role="tabpanel" class="tab-pane" id="dades-nti">
				<rip:inputSelect name="ntiOrigen" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.origen" optionItems="${ntiOrigenOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
				<rip:inputSelect name="ntiTipoDocumental" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.tipdoc" optionItems="${ntiTipusDocumentalOptions}" optionValueAttribute="codi" optionTextAttribute="nom" required="true"/>
				<rip:inputSelect name="ntiEstadoElaboracion" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.estela" optionItems="${ntiEstatElaboracioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
			</div>
			<div role="tabpanel" class="tab-pane" id="firma-portafirmes">
				<rip:inputCheckbox name="firmaPortafirmesActiva" textKey="metadocument.form.camp.firma.portafirmes.activa"/>
				<c:choose>
					<c:when test="${isPortafirmesDocumentTipusSuportat}">
						<rip:inputSelect name="portafirmesDocumentTipus" textKey="metadocument.form.camp.portafirmes.document.tipus" optionItems="${portafirmesDocumentTipus}" optionValueAttribute="id" optionTextAttribute="codiNom" emptyOption="true" optionMinimumResultsForSearch="0"/>
					</c:when>
					<c:otherwise>
						<rip:inputText name="portafirmesDocumentTipus" textKey="metadocument.form.camp.portafirmes.document.tipus"/>
					</c:otherwise>
				</c:choose>
				<%--rip:inputText name="portafirmesFluxId" textKey="metadocument.form.camp.portafirmes.flux.id"/--%>
				<%--<rip:inputText name="portafirmesResponsables" textKey="metadocument.form.camp.portafirmes.responsables" multiple="true"/>--%>
				<rip:inputSelect name="portafirmesFluxTipus" textKey="metadocument.form.camp.portafirmes.fluxtip" optionItems="${metadocumentFluxtipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<div class="flux_portafib">
					<rip:inputText name="portafirmesFluxId" textKey="metadocument.form.camp.portafirmes.flux.id" button="true" icon="fa fa-external-link" buttonMsg="metadocument.form.camp.portafirmes.flux.iniciar"/>
				</div>
				<div class="flux_simple">
					<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
					<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
					<rip:inputSuggest 
						name="portafirmesResponsables" 
						urlConsultaInicial="${urlConsultaInicial}" 
						urlConsultaLlistat="${urlConsultaLlistat}" 
						textKey="metadocument.form.camp.portafirmes.responsables"
						suggestValue="codi"
						suggestText="nom"
						suggestTextAddicional="nif"
						required="true"/>
				
					<rip:inputSelect name="portafirmesSequenciaTipus" textKey="metadocument.form.camp.portafirmes.seqtip" optionItems="${metadocumentSequenciatipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				</div>					
				<rip:inputText name="portafirmesCustodiaTipus" textKey="metadocument.form.camp.portafirmes.custodia"/>				
			</div>
			<div role="tabpanel" class="tab-pane" id="firma-passarela">
				<rip:inputCheckbox name="firmaPassarelaActiva" textKey="metadocument.form.camp.passarela.activa"/>
				<rip:inputText name="firmaPassarelaCustodiaTipus" textKey="metadocument.form.camp.passarela.custodia"/>
			</div>
			<c:if test="${isFirmaBiometrica}">
				<div role="tabpanel" class="tab-pane" id="firma-biometrica">
					<rip:inputCheckbox name="firmaBiometricaActiva" textKey="metadocument.form.camp.firma.biometrica.activa"/>
					<rip:inputCheckbox name="biometricaLectura" textKey="metadocument.form.camp.biometrica.lectura"/>
				</div>
			</c:if>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaDocument"/>" class="btn btn-default modal-cancel" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	<div class="flux_container"></div>
	<div class="rmodal"></div>
</body>
</html>
