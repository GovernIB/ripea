<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty documentNotificacioCommand.id}"><c:set var="titol"><spring:message code="publicacio.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="publicacio.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/document/${documentPublicacioCommand.documentId}/publicar"/></c:set>
	<c:choose>
		<c:when test="${empty documentPublicacioCommand.id}"><c:set var="formAction"><rip:modalUrl value="/document/${documentPublicacioCommand.documentId}/publicar"/></c:set></c:when>
		<c:otherwise><c:set var="formAction"><rip:modalUrl value="/expedient/${expedientId}/publicacio/${documentPublicacioCommand.id}"/></c:set></c:otherwise>
	</c:choose>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="documentPublicacioCommand" role="form">
		<rip:inputHidden name="id"/>
		<rip:inputHidden name="documentId"/>
		<rip:inputSelect name="tipus" textKey="publicacio.form.camp.tipus" optionItems="${publicacioTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
		<rip:inputSelect name="estat" textKey="publicacio.form.camp.estat" optionItems="${publicacioEstatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
		<rip:inputText name="assumpte" textKey="publicacio.form.camp.assumpte" required="true"/>
		<rip:inputDate name="dataPublicacio" textKey="publicacio.form.camp.data.pub" />
		<rip:inputDate name="enviatData" textKey="notificacio.info.camp.data.enviament" required="true"/>
		<rip:inputTextarea name="observacions" textKey="publicacio.form.camp.observacions"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-floppy-o"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
