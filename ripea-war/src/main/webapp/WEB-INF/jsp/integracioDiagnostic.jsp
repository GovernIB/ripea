<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="titol"><spring:message code="integracio.diag.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<script type="text/javascript">
	
		let baseUrl = '<c:url value="/integracio/diagnostic"/>';
		
		$(document).ready(function() {

			$("button[name=refrescar]").click(function() {
				window.location.href = '<c:url value="/modal/integracio/diagnostic"/>';
			});
			
			<c:set var="tempsDelay">100</c:set>
			<c:forEach var="integracio" items="${integracions}">
				setTimeout(getDiagnostic, ${tempsDelay}, '${integracio.codi}');
				<c:set var="tempsDelay" value="${tempsDelay+500}"/>
			</c:forEach>
		});

		function getDiagnostic(integracioCodi) {
			$.get(baseUrl+'/'+integracioCodi).done(function(data) {
				$("#DD_"+integracioCodi).html("<span class='"+data.texte+"'></span> "+data.codi);
			})
			.fail(function() {
				$("#DD_"+integracioCodi).html("<span class='fa fa-times vermell'></span> "+error.jquery.ajax);
			});
		}
	</script>
</head>
<body>

	<dl class="dl-horizontal">
		<c:forEach var="integracio" items="${integracions}">
			<dt><spring:message code="sistema.extern.codi.${integracio.codi}"/></dt>
			<dd id="DD_${integracio.codi}" style="padding-bottom: 10px;"><span class="fa fa-refresh fa-spin"></span></dd>
		</c:forEach>
	</dl>

	<div id="modal-botons">
		<a href="<c:url value="/integracio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		<button type="button" class="btn btn-warning" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.refresca"/></button>
	</div>

</body>