<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty grupCommand.id}"><c:set var="titol"><spring:message code="grup.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="grup.form.titol.modificar"/></c:set></c:otherwise>
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
	<c:set var="formAction"><rip:modalUrl value="/grup/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="grupCommand">
		<form:hidden path="id"/>
		<!--  form:hidden path="entitatId"/-->
		<rip:inputText name="codi" textKey="grup.form.camp.codi" required="true"/>
		<rip:inputTextarea name="descripcio" textKey="grup.form.camp.descripcio" required="true"/>
		
		<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
		<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
		<rip:inputSuggest 
				name="organGestorId"  
				urlConsultaInicial="${urlConsultaInicial}"
				urlConsultaLlistat="${urlConsultaLlistat}"
				textKey="metaexpedient.relacionar.grup.form.camp.organGestor"
				suggestValue="id"
				suggestText="codiINom"
				required="${esAdminOrgan}" />
		
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>
				<c:choose>
					<c:when test="${empty grupCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
					<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
				</c:choose>
			</button>	
			<a href="<c:url value="/grup"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
