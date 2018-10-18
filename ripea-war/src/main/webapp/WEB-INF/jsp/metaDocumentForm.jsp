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
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaDocumentCommand.metaExpedientId}/metaDocument"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaDocumentCommand" enctype="multipart/form-data">
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.dades"/></a></li>
			<li role="presentation"><a href="#firma-portafirmes" aria-controls="firma-portafirmes" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.portafirmes"/></a></li>
			<li role="presentation"><a href="#firma-passarela" aria-controls="firma-passarela" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.passarela"/></a></li>
		</ul>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="metaExpedientId"/>
		<br/>
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane active" id="dades">
				<rip:inputText name="codi" textKey="metadocument.form.camp.codi" required="true"/>
				<rip:inputText name="nom" textKey="metadocument.form.camp.nom" required="true"/>
				<rip:inputTextarea name="descripcio" textKey="metadocument.form.camp.descripcio"/>
				<rip:inputSelect name="multiplicitat" textKey="metadocument.form.camp.multiplicitat" optionItems="${multiplicitatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<rip:inputFile name="plantilla" textKey="metadocument.form.camp.plantilla"/>
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
				<rip:inputText name="portafirmesResponsables" textKey="metadocument.form.camp.portafirmes.responsables" multiple="true"/>
				<rip:inputSelect name="portafirmesFluxTipus" textKey="metadocument.form.camp.portafirmes.fluxtip" optionItems="${metadocumentFluxtipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<rip:inputText name="portafirmesCustodiaTipus" textKey="metadocument.form.camp.portafirmes.custodia"/>				
			</div>
			<div role="tabpanel" class="tab-pane" id="firma-passarela">
				<rip:inputCheckbox name="firmaPassarelaActiva" textKey="metadocument.form.camp.passarela.activa"/>
				<rip:inputText name="firmaPassarelaCustodiaTipus" textKey="metadocument.form.camp.passarela.custodia"/>
			</div>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaDocument"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
