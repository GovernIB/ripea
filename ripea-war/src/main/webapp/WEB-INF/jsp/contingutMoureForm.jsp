<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.moure.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/JSOG.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
	<form:form action="" class="form-horizontal" commandName="contingutMoureCopiarEnviarCommand">
		<form:hidden path="origenIds"/>
		<form:hidden path="origenId"/>
		<rip:inputFixed textKey="contingut.moure.camp.origen">
		<c:choose>
			<c:when test="${not empty documentsOrigen}">
				<c:forEach items="${documentsOrigen}" var="document" varStatus="status">
					<rip:blocIconaContingut contingut="${document}"/>
					${document.nom}<c:if test="${fn:length(documentsOrigen)  > 1 && !status.last}">,</c:if>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<rip:blocIconaContingut contingut="${contingutOrigen}"/>
				${contingutOrigen.nom}
			</c:otherwise>
		</c:choose>
			
		</rip:inputFixed>
		<rip:inputFileChooserMultipleExpedients name="destiId" contingutOrigen="${contingutOrigen}" documentsOrigen="${documentsOrigen}" ocultarDocuments="true" textKey="contingut.moure.camp.desti" required="true"/>
		
<%-- 		<rip:inputFileChooser name="destiId" contingutOrigen="${contingutOrigen}" textKey="contingut.moure.camp.desti" required="true"/> --%>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.moure"/></button>
			<a href="<c:url value="/contenidor/${contingutOrigen.pare.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
