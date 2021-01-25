<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="tamanyDoble" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="tamanyEnorme" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="nomesIconaNom" required="false" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<c:set var="isTasca" value="${not empty tascaId}"/>
<rip:blocIconaContingutNoms/>
<c:if test="${tamanyDoble}"><c:set var="iconaTamany" value="fa-2x"/></c:if>
<c:if test="${tamanyEnorme}"><c:set var="iconaTamany" value="fa-5x"/></c:if>
<c:choose>
	<c:when test="${nomesIconaNom}">
		<c:choose>
			<c:when test="${isTasca}">${iconaTask}</c:when>
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
				<c:choose>
					<c:when test="${contingut.fitxerExtension=='pdf' or contingut.estat == 'CUSTODIAT' or contingut.estat == 'FIRMAT' or contingut.estat == 'ADJUNT_FIRMAT'}">
						<span class="fa fa-file-pdf-o ${iconaTamany}" title="<spring:message code="contingut.icona.document"/> ${contingut.fitxerExtensionUpperCase}"></span>
					</c:when>
					<c:when test="${contingut.fitxerExtension=='doc' or contingut.fitxerExtension=='docx' or contingut.fitxerExtension=='odt'}">
						<span class="fa fa-file-word-o ${iconaTamany}" title="<spring:message code="contingut.icona.document"/> ${contingut.fitxerExtensionUpperCase}"></span>
					</c:when>	
					<c:when test="${contingut.fitxerExtension=='xls' or contingut.fitxerExtension=='xlsx' or contingut.fitxerExtension=='ods'}">
						<span class="fa fa-file-excel-o ${iconaTamany}" title="<spring:message code="contingut.icona.document"/> ${contingut.fitxerExtensionUpperCase}"></span>
					</c:when>	
					<c:when test="${contingut.fitxerExtension=='zip'}">
						<span class="fa fa-file-zip-o ${iconaTamany}" title="<spring:message code="contingut.icona.document"/> ${contingut.fitxerExtensionUpperCase}"></span>
					</c:when>		
<%-- 					<c:when test="${contingut.fitxerExtension=='odt'}"> --%>
<%-- 						<span class="fa fa-file-o ${iconaTamany}" title="<spring:message code="contingut.icona.document"/> ${contingut.fitxerExtensionUpperCase}" style="position: relative;left: 10px;"></span> --%>
<%-- 						<span style="font-size: 18px;position: relative;right: 15px;">O</span> --%>
<%-- 					</c:when>				 --%>
					<c:otherwise>
						<span class="fa fa-file-o ${iconaTamany}" title="<spring:message code="contingut.icona.document"/> ${contingut.fitxerExtensionUpperCase}"></span>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="${contingut.carpeta}">
				<span class="fa ${iconaCarpeta} ${iconaTamany}" title="<spring:message code="contingut.icona.carpeta"/>"></span>
			</c:when>
		</c:choose>
	</c:otherwise>
</c:choose>
