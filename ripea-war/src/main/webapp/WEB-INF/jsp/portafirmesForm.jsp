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
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="portafirmesEnviarCommand" role="form">
		<rip:inputText name="motiu" textKey="contenidor.document.portafirmes.camp.motiu" required="true"/>
		<rip:inputSelect name="prioritat" textKey="contenidor.document.portafirmes.camp.prioritat" optionEnum="PortafirmesPrioritatEnumDto" required="true"/>
		<rip:inputDate name="dataCaducitat" textKey="contenidor.document.portafirmes.camp.data.caducitat" required="true"/>
		<rip:inputText name="portafirmesResponsables" textKey="metadocument.form.camp.portafirmes.responsables" multiple="true"/>
		<rip:inputSelect name="portafirmesFluxTipus" textKey="metadocument.form.camp.portafirmes.fluxtip" optionItems="${metadocumentFluxtipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-send"></span> <spring:message code="contenidor.document.portafirmes.enviar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
