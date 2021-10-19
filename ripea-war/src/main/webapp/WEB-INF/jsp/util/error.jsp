<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="errorTitol"><spring:message code="error.titol.http"/> ${errorObject.statusCode}</c:set>
<c:choose>
	<c:when test="${errorObject.throwable.class.canonicalName == 'es.caib.ripea.core.api.exception.NotFoundException'}">
		<c:set var="errorTitol"><spring:message code="error.titol.not.found"/></c:set>
	</c:when>
	<c:when test="${errorObject.throwable.class.canonicalName == 'es.caib.ripea.core.api.exception.SistemaExternException'}">
		<c:set var="errorTitol"><spring:message code="error.titol.sistema.ext"/></c:set>
	</c:when>
</c:choose>
<html>
<head>
	<title>${errorTitol}</title>
	<meta name="title-icon-class" content="fa fa-warning"/>
	<rip:modalHead/>
<style>
pre {
	overflow: auto;
	word-wrap: normal;
	white-space: pre;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	window.parent.removeLoading();
});
</script>
</head>
<body>
	<table class="table table-bordered" style="width:100%">
	<tbody>
		<c:choose>
			<c:when test="${errorObject.throwableClassName == 'java.lang.SecurityException'}">
				<div class="alert alert-danger" style="margin-top: 20px;" role="alert"><strong>Error accedint a RIPEA!</strong> ${errorObject.exceptionMessage}</div>
			</c:when>
			<c:when test="${errorObject.throwable.class.canonicalName == 'es.caib.ripea.core.api.exception.NotFoundException'}">
				<tr>
					<td width="20%"><strong><spring:message code="error.tipus.element"/></strong></td>
					<td>${errorObject.throwable.objectClass.canonicalName}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="error.id"/></strong></td>
					<td>${errorObject.throwable.objectId}</td>
				</tr>
			</c:when>
			<c:when test="${errorObject.throwable.class.canonicalName == 'es.caib.ripea.core.api.exception.SistemaExternException'}">
				<tr>
					<td><strong><spring:message code="error.sistema.ext"/></strong></td>
					<td><spring:message code="sistema.extern.codi.${errorObject.throwable.sistemaExternCodi}"/></td>
				</tr>
				<tr>
					<td width="20%"><strong><spring:message code="error.missatge"/></strong></td>
					<td>${errorObject.throwable.message}</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td width="20%"><strong><spring:message code="error.recurs"/></strong></td>
					<td>${errorObject.requestUri}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="error.excepcio"/></strong></td>
					<td>${errorObject.throwable.class.canonicalName}</td>
				</tr>
				<c:if test="${not empty errorObject.exceptionMessage}">
					<tr>
						<td><strong><spring:message code="error.missatge"/></strong></td>
						<td>${errorObject.exceptionMessage}</td>
					</tr>
				</c:if>
			</c:otherwise>
		</c:choose>
		<tr>
			<c:if test="${errorObject.throwableClassName != 'java.lang.SecurityException'}">
				<td>
					<strong>
						<button class="btn btn-default" type="button" data-toggle="collapse" data-target="#trasaCollapse" aria-expanded="false" aria-controls="trasaCollapse">
							<spring:message code="error.trasa"/>
						</button>
					</strong>
				</td>
				<td>
					<pre class="collapse" id="trasaCollapse""><code>${errorObject.fullStackTrace}</code></pre>
				</td>
			</c:if>
		</tr>
	</tbody>
	</table>
</body>
</html>
