<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${organConfigCommand.crear}"><c:set var="titol"><spring:message code="config.propietats.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="config.propietats.form.titol.mofificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script type="text/javascript">

</script>
</head>
<body>
<c:choose>
	<c:when test="${organConfigCommand.crear}"><c:set var="formAction"><rip:modalUrl value="/config/organ/new"/></c:set></c:when>
	<c:otherwise><c:set var="formAction"><rip:modalUrl value="/config/organ/update"/></c:set></c:otherwise>
</c:choose>
<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="organConfigCommand">

	<form:hidden path="key"/>
	<form:hidden path="jbossProperty"/>

	<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
	<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>

	<c:if test="${!organConfigCommand.crear}"><form:hidden path="organGestorId"/></c:if>
	<rip:inputSuggest 
		name="organGestorId" 
		urlConsultaInicial="${urlConsultaInicial}"
		urlConsultaLlistat="${urlConsultaLlistat}"
		minimumInputLength="3"
		textKey="metaexpedient.form.camp.organgestor" 
		suggestValue="id" 
		suggestText="codiINomIEntitat"
		required="true"
		disabled="${!organConfigCommand.crear}"/>
		
	<c:set var="campErrors"><form:errors path="value"/></c:set>
	<c:if test="${!config.jbossProperty}">
		<div class="form-group <c:if test="${not empty campErrors}">has-error</c:if>">
			<label class="col-sm-4 control-label" style="word-wrap: break-word;"><spring:message code="config.propietats.form.camp.value"/> *</label>
			<div class="col-sm-8">
		
				<c:choose>
					<c:when test="${config.typeCode == 'INT'}">
					    <form:input cssClass="form-control" path="value" type="number" maxlength="2048"/>
					</c:when>
					<c:when test="${config.typeCode == 'FLOAT'}">
					    <form:input  cssClass="form-control" path="value" type="number" step="0.01" maxlength="2048"/>
					</c:when>
					<c:when test="${config.typeCode == 'CREDENTIALS'}">
					    <form:input cssClass="form-control" path="value" type="password" maxlength="2048"/>
					</c:when>
					<c:when test="${config.typeCode == 'BOOL'}">
						<div class="checkbox checkbox-primary">
							<label><form:checkbox path="booleanValue" cssClass="visualitzar"/></label>
						</div>
					</c:when>
					<c:when test="${config.validValues != null and fn:length(config.validValues) > 2}">
					    <form:select path="value" cssClass="form-control" style="width:100%" data-toggle="select2">
							<option value=""></option>					    
					        <c:forEach var="opt" items="${config.validValues}">
					            <form:option value="${opt}"/>
					        </c:forEach>
					    </form:select>
					</c:when>
					<c:when test="${config.validValues != null and fn:length(config.validValues) == 2}">
					    <label  class="radio-inline">
					        <form:radiobutton path="value" value="${config.validValues[0]}"/> ${config.validValues[0]}
					    </label>
					    <label  class="radio-inline">
					        <form:radiobutton path="value" value="${config.validValues[1]}"/> ${config.validValues[1]}
					    </label>
					</c:when>
					<c:otherwise>
					    <form:input cssClass="form-control" path="value" type="text" maxlength="2048" />
					</c:otherwise>
				</c:choose>
				
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="value"/></p></c:if>				
			</div>
		</div>	
	</c:if>
	<rip:inputCheckbox name="configurableOrgansDescendents" textKey="config.propietats.form.camp.organs.descendents" />

	<div style="min-height: 20px;"></div>
	

	<div id="modal-botons" class="well">
		<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
		<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
	</div>
</form:form>
</body>

</html>