<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty permisCommand.id}"><c:set var="titol"><spring:message code="organgestor.permis.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="organgestor.permis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<script>
	$(document).ready(function() {
		$("#modal-botons button[type='submit']").on('click', function() {
			$("form#permisCommand *:disabled").attr('readonly', 'readonly');
			$("form#permisCommand *:disabled").removeAttr('disabled');
		});
	});
</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/organgestor/permis"/></c:set>
	<c:if test="${not empty permisOrganGestorCommand.organGestorId}">
		<c:set var="formAction"><rip:modalUrl value="/organgestor/${ permisOrganGestorCommand.organGestorId }/permis"/></c:set>
 	</c:if>	
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="permisOrganGestorCommand">
		<form:hidden path="id"/>
		<rip:inputSelect name="organGestorId" textKey="organgestor.form.camp.organ" 
						 disabled="${not empty permisOrganGestorCommand.organGestorId}" emptyOption="true" emptyOptionTextKey="organgestor.form.camp.organ.opcio.cap"
						 optionItems="${ organsGestors }" optionValueAttribute="id" optionTextAttribute="nom"
						 required="true" optionMinimumResultsForSearch="5"/>
		<c:if test="${not empty permisOrganGestorCommand.organGestorId}">
			<form:hidden path="organGestorId"/>
	 	</c:if>					 
		<rip:inputSelect name="principalTipus" textKey="organgestor.form.camp.tipus" disabled="${not empty permisOrganGestorCommand.id}" optionEnum="PrincipalTipusEnumDto"/>
		<rip:inputText name="principalNom" required="true" textKey="organgestor.form.camp.principal" disabled="${not empty permisCommand.id}" placeholderKey="entitat.permis.form.camp.principal"/>
		<rip:inputCheckbox name="administration" textKey="organgestor.form.camp.administracio"/>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="organgestor/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
