<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="titol"><spring:message code="contingut.arxiu.titol"/></c:set>
<html>
<head>
  	<title><spring:message code="expedient.peticio.list.btn.rebutjadaInfo"/></title>
	<rip:modalHead/>
</head>
<body>

	<table class="table table-striped table-bordered">
		<tbody>
			<tr>
				<td><strong><spring:message code="expedient.peticio.rebutjadaInfo.motiu"/></strong></td>
				<td>${expedientPeticio.observacions}</td>
			</tr>
			<tr>
				<td><strong><spring:message code="expedient.peticio.rebutjadaInfo.data"/></strong></td>
				<td>${expedientPeticio.dataActualitzacioStr}</td>
			</tr>
			<tr>
				<td><strong><spring:message code="expedient.peticio.rebutjadaInfo.usuari"/></strong></td>
				<td>${expedientPeticio.usuariActualitzacio}</td>
			</tr>

		</tbody>
	</table>
				
	<div id="modal-botons" class="well">
		<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
