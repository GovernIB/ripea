<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty documentCommand.id}"><c:set var="titol"><spring:message code="contingut.document.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="contingut.document.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
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
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<script>
$(document).ready(function() {
	$('#documentTipus').val('DIGITAL');
	$('#metaNodeId').on('change', function() {
		if ($(this).val()) {
			$.get("../metaDocument/" +  $(this).val())
			.done(function(data) {
				if (data.plantillaNom) {
					$('#info-plantilla-si').removeClass('hidden');
					$('#info-plantilla-si a').attr('href', '../metaDocument/' + data.id + '/plantilla');
				} else {
					$('#info-plantilla-si').addClass('hidden');
				}
				webutilModalAdjustHeight();
			})
			.fail(function() {
				alert("<spring:message code="contingut.document.form.alert.plantilla"/>");
			});
		} else {
			$('#info-plantilla-si').addClass('hidden');
		}
	});
	$('input[type=radio][name=origen]').on('change', function() {
		if ($(this).val() == 'DISC') {
			$('#input-origen-arxiu').removeClass('hidden');
			$('#input-origen-escaner').addClass('hidden');
		} else {
			$('#input-origen-escaner').removeClass('hidden');
			$('#input-origen-arxiu').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});
	$(document).on('submit','form#documentCommand', function() {
		var action = $(this).attr('action');
		var lastSlashIndex = action.lastIndexOf('/');
		var actionProcessed = action.substring(0, lastSlashIndex);
		var $btn = $(this).find("button[type=submit]:focus");
		if ($btn.length == 0) {
			if (action.endsWith("new")) {
				actionProcessed += '/digital/new';
			} else {
				actionProcessed += '/digital/update';
			}
		} else {
			actionProcessed += '/escaneig/inici';
		}
		$(this).attr('action', actionProcessed);
		return true;
	});
	$('#metaNodeId').trigger('change');
	$('input[type=radio][name=origen][value=${documentCommand.origen}]').trigger('change');
});
</script>
</head>
<body>
	<c:choose>
		<c:when test="${empty documentCommand.id}"><c:set var="formAction"><rip:modalUrl value="/contingut/${documentCommand.pareId}/document/new"/></c:set></c:when>
		<c:otherwise><c:set var="formAction"><rip:modalUrl value="/contingut/${documentCommand.pareId}/document/update"/></c:set></c:otherwise>
	</c:choose>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="documentCommand" enctype="multipart/form-data">
		<div id="info-plantilla-si" class="alert well-sm alert-info hidden">
			<span class="fa fa-info-circle"></span>
			<spring:message code="contingut.document.form.info.plantilla.si"/>
			<a href="#" class="btn btn-xs btn-default pull-right"><spring:message code="comu.boto.descarregar"/></a>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<form:hidden path="documentTipus"/>
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#dades_doc" aria-controls="dades_doc" role="tab" data-toggle="tab"><spring:message code="contingut.document.form.tab.dades.doc"/></a></li>
			<li role="presentation"><a href="#dades_nti" aria-controls="dades_nti" role="tab" data-toggle="tab"><spring:message code="contingut.document.form.tab.dades.nti"/></a></li>
		</ul>
		<br/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dades_doc">
				<rip:inputText name="nom" textKey="contingut.document.form.camp.nom" required="true"/>
				<rip:inputDate name="data" textKey="contingut.document.form.camp.data" required="true"/>
				<rip:inputSelect name="metaNodeId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom"/>

					<rip:inputRadio name="origen" textKey="contingut.document.form.camp.origen" botons="true" optionItems="${digitalOrigenOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
					<div id="input-origen-arxiu" class="hidden">
						<rip:inputFile name="arxiu" textKey="contingut.document.form.camp.arxiu" required="${empty documentCommand.id}"/>
					</div>
					<div id="input-origen-escaner" class="hidden">
						<rip:inputFixed name="escanejatTempId" padding="false" textKey="contingut.document.form.camp.escaneig">
							<rip:inputHidden name="escanejatTempId"/>
							<div class="input-group">
								<input type="text" class="form-control" disabled="disabled" value="${escanejat.nom}"/>
								<span class="input-group-btn">
									<button class="btn btn-default" name="hola" type="submit"><span class="fa fa-print"></span> <spring:message code="contingut.document.form.boto.escaneig"/></button>
								</span>
		    				</div>
						</rip:inputFixed>
					</div>
			
			</div>
			<div role="tabpanel" class="tab-pane" id="dades_nti">
				<rip:inputDate name="dataCaptura" textKey="contingut.document.form.camp.nti.datacap" required="true"/>
				<rip:inputText name="ntiOrgano" textKey="contingut.document.form.camp.nti.organo" required="true"/>
				<rip:inputSelect name="ntiOrigen" textKey="contingut.document.form.camp.nti.origen" required="true" optionItems="${ntiOrigenOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<rip:inputSelect name="ntiEstadoElaboracion" textKey="contingut.document.form.camp.nti.estela" required="true" optionItems="${ntiEstatElaboracioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<rip:inputSelect name="ntiTipoDocumental" textKey="contingut.document.form.camp.nti.tipdoc" required="true" optionItems="${ntiTipusDocumentalOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/contingut/${documentCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
