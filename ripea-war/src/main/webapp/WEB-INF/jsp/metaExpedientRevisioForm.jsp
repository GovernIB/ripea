<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:choose>
	<c:when test="${modificar}"><c:set var="titol"><spring:message code="metaexpedient.revisio.form.titol.canviarEstatRevisio"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="metaexpedient.revisio.form.titol.estatRevisio"/></c:set></c:otherwise>
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
	$(document).ready(function(){

	});
</script>	
	
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedientRevisio"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaExpedientRevisioCommand" role="form" >
	
		<form:hidden path="id"/>
		<rip:inputSelect name="revisioEstat" optionEnum="MetaExpedientRevisioEstatEnumDto" textKey="metaexpedient.revisio.form.camp.estatRevisio" disabled="${!modificar}"/>
		<div style="min-height: 150px;"></div>

		<div id="modal-botons">
			<c:if test="${modificar}"><button type="submit" class="btn btn-success" <span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button></c:if>
			<a href="<c:url value="/entitat"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
