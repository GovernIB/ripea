<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="tamanyDoble" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="tamanyEnorme" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="nomesIconaNom" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<rip:blocIconaContingutNoms/>
<c:if test="${tamanyDoble}"><c:set var="iconaTamany" value="fa-2x"/></c:if>
<c:if test="${tamanyEnorme}"><c:set var="iconaTamany" value="fa-5x"/></c:if>
<c:choose>
	<c:when test="${nomesIconaNom}">
		<c:choose>
			<c:when test="${contingut.expedient and contingut.estat == 'OBERT'}">${iconaExpedientObert} ${iconaTamany}</c:when>
			<c:when test="${contingut.expedient and contingut.estat != 'OBERT'}">${iconaExpedientTancat} ${iconaTamany}</c:when>
			<c:when test="${contingut.document}">${iconaDocument} ${iconaTamany}</c:when>
			<c:when test="${contingut.carpeta}">${iconaCarpeta} ${iconaTamany}</c:when>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${contingut.expedient and contingut.estat == 'OBERT'}">
				<span class="fa ${iconaExpedientObert} ${iconaTamany}" title="<spring:message code="contingut.icona.expedient"/>"></span>
			</c:when>
			<c:when test="${contingut.expedient and contingut.estat != 'OBERT'}">
				<span class="fa ${iconaExpedientTancat} ${iconaTamany}" title="<spring:message code="contingut.icona.expedient"/>"></span>
			</c:when>
			<c:when test="${contingut.document}">
				<span class="fa ${iconaDocument} ${iconaTamany}" title="<spring:message code="contingut.icona.document"/>"></span>
			</c:when>
			<c:when test="${contingut.carpeta}">
				<span class="fa ${iconaCarpeta} ${iconaTamany}" title="<spring:message code="contingut.icona.carpeta"/>"></span>
			</c:when>
		</c:choose>
	</c:otherwise>
</c:choose>
