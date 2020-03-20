<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contenidor.document.portafirmes.titol"/></c:set>
<c:set var="isTasca" value="${not empty tascaId}"/>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<style type="text/css">
.modal-lg {
	width: 100%;
}
.modal-dialog {
	width: 95%;
	height: 100%;
	margin: 5% auto;
	padding: 0;
}

.modal-content {
	height: auto;
}

.iframe_container {
	position: relative;
	width: 100%;
	height: 100vh;
	padding-bottom: 40%;
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
.btn-flux {
	color: #fff; 
	background-color: #bfbfbf;
	border-color: #bfbfbf;
}

.btn-flux:hover {
	color: #fff; 
}
</style>

<script type="text/javascript">
$(document).ready(function() {
	let currentHeight = window.frameElement.contentWindow.document.body.scrollHeight;
	localStorage.setItem("currentIframeHeight", currentHeight);
	console.log(localStorage.getItem("currentIframeHeight"));
	
	$(".portafirmesFlux_btn").on('click', function(){		
		var tipusDocumentNom = '${document.fitxerNom}';
		$.ajax({
			type: 'GET',
			dataType: "json",
			data: {tipusDocumentNom: tipusDocumentNom},
			url: "<c:url value="/modal/document/portafirmes/iniciarTransaccio"/>",
			success: function(transaccioResponse) {
				if (transaccioResponse != null) {
					localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
					$('.content').addClass("hidden");
					$('.flux_container').html('<div class="iframe_container"><iframe id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe></div>');	
					
					adjustModalPerFlux();
				}
			},
			error: function(err) {
				console.log("Error recuperant la transacció");
			}
		});
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
}

</script>
</head>
<body>
	<c:if test="${document.fitxerNom != document.fitxerNomEnviamentPortafirmes}">
		<div class="alert well-sm alert-warning alert-dismissable">
			<span class="fa fa-exclamation-triangle"></span>
			<spring:message code="contenidor.document.portafirmes.conversio.avis"/>
			<a data-rdt-link-modal="true" class="btn btn-xs btn-default pull-right" href="../pdf">
				<spring:message code="contenidor.document.portafirmes.conversio.boto.previsualitzar"/>
			</a>
		</div>
	</c:if>
	<c:choose>
		<c:when test="${isTasca}">
			<c:set var="formAction"><rip:modalUrl value="/usuariTasca/${tascaId}/document/${document.id}/portafirmes/upload"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="formAction"><rip:modalUrl value="/document/${document.id}/portafirmes/upload"/></c:set>
		</c:otherwise>
	</c:choose>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal content" commandName="portafirmesEnviarCommand" role="form">
		<rip:inputText name="motiu" textKey="contenidor.document.portafirmes.camp.motiu" required="true"/>
		<rip:inputSelect name="prioritat" textKey="contenidor.document.portafirmes.camp.prioritat" optionEnum="PortafirmesPrioritatEnumDto" required="true"/>
		<rip:inputDate name="dataCaducitat" textKey="contenidor.document.portafirmes.camp.data.caducitat" required="true"/>
		<form:hidden name="portafirmesFluxTipus" path="portafirmesFluxTipus"/>
		<c:choose>
		<c:when test="${fluxTipus == 'SIMPLE'}">
			<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
			<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
			<rip:inputSuggest 
				name="portafirmesResponsables" 
				urlConsultaInicial="${urlConsultaInicial}" 
				urlConsultaLlistat="${urlConsultaLlistat}" 
				textKey="metadocument.form.camp.portafirmes.responsables"
				suggestValue="nif"
				suggestText="nom"
				suggestTextAddicional="nif"
				required="true"/>
						
			<rip:inputSelect name="portafirmesSequenciaTipus" textKey="metadocument.form.camp.portafirmes.seqtip" optionItems="${metadocumentFluxtipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		</c:when>
		<c:when test="${fluxTipus == 'PORTAFIB' && nouFluxDeFirma}">
			<div class="form-group">
				<label class="col-xs-4"></label>
				<div class="col-xs-8">
					<span class='btn btn-flux form-control portafirmesFlux_btn' title="<spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>"><spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>  <i class="fa fa-external-link"></i></span>
				</div>
			</div>
			<!-- 
			<rip:inputText name="portafirmesFluxId" textKey="metadocument.form.camp.portafirmes.flux.id" button="true" icon="fa fa-external-link" buttonMsg="metadocument.form.camp.portafirmes.flux.iniciar"/>
			 -->
		</c:when>
		<c:otherwise>
			<rip:inputText name="portafirmesFluxId" textKey="contenidor.document.portafirmes.camp.flux.id"  readonly="true"/>
			<rip:inputText name="portafirmesFluxNom" textKey="contenidor.document.portafirmes.camp.flux.nom" readonly="true"/>
			<rip:inputText name="portafirmesFluxDescripcio" textKey="contenidor.document.portafirmes.camp.flux.descripcio" readonly="true"/>
		</c:otherwise>
		</c:choose>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-send"></span> <spring:message code="contenidor.document.portafirmes.enviar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	<div class="flux_container"></div>
</body>
</html>
