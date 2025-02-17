<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.enviar.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jstree.min.js"/>"></script>
</head>
<body>
	<form:form action="" class="form-horizontal" modelAttribute="contingutMoureCopiarEnviarCommand">
		<form:hidden path="origenId"/>
		<rip:inputFixed textKey="contingut.enviar.camp.origen">
			<rip:blocIconaContingut contingut="${contingutOrigen}"/>
			${contingutOrigen.nom}
		</rip:inputFixed>
		<rip:inputArbre name="destiId" textKey="contingut.enviar.camp.desti" arbre="${arbreUnitatsOrganitzatives}" required="true" fulles="${busties}" fullesAtributId="id" fullesAtributNom="nom" fullesAtributPare="unitatCodi" fullesIcona="fa fa-inbox fa-lg" isArbreSeleccionable="${false}" isFullesSeleccionable="${true}" isOcultarCounts="${true}"/>
		<rip:inputTextarea name="comentariEnviar" textKey="contingut.enviar.camp.comentari" required="false"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-send"></span> <spring:message code="comu.boto.enviar"/></button>
			<a href="<c:url value="/contingut/${contingutOrigen.pare.id}"/>" class="btn btn-default" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
