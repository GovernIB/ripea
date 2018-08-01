<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<rip:blocIconaContingutNoms/>
<c:if test="${fn:length(contingut.path) gt 0}">
	<ol class="breadcrumb">
		<c:forEach var="contingutPath" items="${contingut.path}" varStatus="status">
			<li>
				<c:choose>
					<c:when test="${contingutPath.expedient}"><a href="<c:url value="/contingut/${contingutPath.id}"/>"><span class="fa ${iconaExpedientObert}" title="<spring:message code="contingut.icona.expedient"/>"></span> ${contingutPath.nom}</a></c:when>
					<c:when test="${contingutPath.document}"><a href="<c:url value="/contingut/${contingutPath.id}"/>"><span class="fa ${iconaDocument}" title="<spring:message code="contingut.icona.document"/>"></span> ${contingutPath.nom}</a></c:when>
					<c:when test="${contingutPath.carpeta}"><a href="<c:url value="/contingut/${contingutPath.id}"/>"><span class="fa ${iconaCarpeta}" title="<spring:message code="contingut.icona.carpeta"/>"></span> ${contingutPath.nom}</a></c:when>
				</c:choose>
			</li>
		</c:forEach>
		<li class="active">
			<c:choose>
				<c:when test="${contingut.expedient}"><span class="fa ${iconaExpedientObert}" title="<spring:message code="contingut.icona.expedient"/>"></span></c:when>
				<c:when test="${contingut.document}"><span class="fa ${iconaDocument}" title="<spring:message code="contingut.icona.document"/>"></span></c:when>
				<c:when test="${contingut.carpeta}"><span class="fa ${iconaCarpeta}" title="<spring:message code="contingut.icona.carpeta"/>"></span></c:when>
			</c:choose>
			${contingut.nom}
		</li>
	</ol>
</c:if>
