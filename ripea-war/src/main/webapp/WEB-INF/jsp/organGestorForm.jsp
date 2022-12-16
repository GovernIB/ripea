<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty organGestorCommand.id}"><c:set var="titol"><spring:message code="organgestor.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="organgestor.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/webjars/jstree/3.2.1/dist/themes/default/style.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
	<rip:modalHead/>
	
	
</head>
<body>

	<c:set var="formAction"><rip:modalUrl value="/organgestor"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="organGestorCommand" role="form">
		<form:hidden path="id"/>
		<rip:inputText name="codi" textKey="organgestor.form.camp.codi" required="true" labelSize="2"/>
		<rip:inputText name="nom" textKey="organgestor.form.camp.nom" required="true" labelSize="2"/>
		<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
		<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
		<rip:inputSuggest 
				name="pareId"  
				urlConsultaInicial="${urlConsultaInicial}"
				urlConsultaLlistat="${urlConsultaLlistat}"
				textKey="organgestor.form.camp.pare"
				suggestValue="id"
				suggestText="codiINom"
				labelSize="2"/>
				
		<rip:inputText name="cif" textKey="entitat.list.columna.cif" labelSize="2"/>
				
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="organGestor"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>

</body>
</html>
