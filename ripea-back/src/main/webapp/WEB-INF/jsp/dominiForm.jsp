<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty dominiCommand.id}"><c:set var="titol"><spring:message code="domini.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="domini.form.titol.modificar"/></c:set></c:otherwise>
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
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/domini/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="dominiCommand">
		<form:hidden path="id"/>
		<!--  form:hidden path="entitatId"/-->
		<rip:inputText name="codi" textKey="domini.form.camp.codi" required="true"/>
		<rip:inputText name="nom" textKey="domini.form.camp.nom" required="true"/>
		<rip:inputTextarea name="descripcio" textKey="domini.form.camp.descripcio"/>
		<rip:inputTextarea name="consulta" textKey="domini.form.camp.consulta" required="true" exemple="domini.consulta.exemple" exempleLabel="domini.exemple.boto"/>
		<rip:inputTextarea name="cadena" textKey="domini.form.camp.cadena" required="true" exemple="domini.cadena.exemple" exempleLabel="domini.exemple.boto"/>
		<rip:inputText name="contrasenya" textKey="domini.form.camp.contrasenya" required="true"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>
				<c:choose>
					<c:when test="${empty dominiCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
					<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
				</c:choose>
			</button>
			<a href="<c:url value="/domini"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
