<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>


<c:if test="${contingut.document && contingut.estat == 'REDACCIO'}">
	<span class="icona-esborrany fa fa-bold" title="<spring:message code="contingut.info.estat.redaccio"/>"></span>
</c:if> 
<c:if test="${contingut.document && contingut.documentTipus == 'IMPORTAT'}">
	<span class="importat fa fa-info-circle" title="<spring:message code="contingut.info.estat.importat"/>"></span>
</c:if> 
<c:if test="${contingut.node and not contingut.valid}">&nbsp;<span class="fa fa-exclamation-triangle text-warning"></span>
</c:if> 
<c:if test="${contingut.document && (contingut.estat == 'CUSTODIAT' || contingut.estat == 'FIRMAT' || contingut.estat == 'ADJUNT_FIRMAT')}">
	<span class="firmat fa fa-pencil-square" title="<spring:message code="contingut.info.estat.firmat"/>"></span>
</c:if> 
<c:if test="${contingut.document && contingut.estat == 'FIRMAT' && contingut.gesDocFirmatId != null}">
	<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentCustodiar"/>"></span>
</c:if> 
<c:if test="${contingut.document && contingut.pendentMoverArxiu}">
	<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentMoverArxiu"/>"></span>
</c:if> 
<c:if test="${contingut.document && !contingut.validacioFirmaCorrecte}">
	<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.invalid.origen" arguments="${contingut.validacioFirmaErrorMsg}"/>"></span>
</c:if> 
<c:if test="${contingut.document && contingut.estat == 'DEFINITIU'}">
	<span class="definitiu fa fa-check-square" title="<spring:message code="contingut.info.estat.defintiu"/>"></span>
</c:if> 
<c:if test="${contingut.document && contingut.ambNotificacions}">
	<c:choose>
		<c:when
			test="${!contingut.errorDarreraNotificacio && (contingut.estatDarreraNotificacio == 'PENDENT' or contingut.estatDarreraNotificacio == 'REGISTRADA')}">
			<c:set var="envelope" value="pendent fa fa-envelope-square" />
		</c:when>
		<c:when
			test="${!contingut.errorDarreraNotificacio && contingut.estatDarreraNotificacio == 'ENVIADA'}">
			<c:set var="envelope" value="enviada fa fa-envelope-square" />
		</c:when>
		<c:when
			test="${!contingut.errorDarreraNotificacio && (contingut.estatDarreraNotificacio == 'PROCESSADA' or contingut.estatDarreraNotificacio == 'FINALITZADA')}">
			<c:set var="envelope" value="processada fa fa-envelope-square" />
		</c:when>
		<c:when test="${contingut.errorDarreraNotificacio}">
			<c:set var="envelope" value="error fa fa-envelope-square" />
		</c:when>
		<c:otherwise>
			<c:set var="envelope" value="fa fa-envelope-square" />
		</c:otherwise>
	</c:choose>
	<span class="${envelope} popover-${contingut.id}" id="${contingut.id}" data-toggle="popover" title="<spring:message code="contingut.info.notificacions"/>"></span>
</c:if> 
<c:if test="${contingut.document && contingut.estat != 'CUSTODIAT' && contingut.estat != 'REDACCIO' && (contingut.estat == 'FIRMA_PENDENT_VIAFIRMA' || contingut.estat == 'FIRMA_PENDENT')}">
	<span class="pendent fa fa-pencil-square" title="<spring:message code="contingut.info.estat.pendentfirma"/>"></span>
</c:if> 
<c:if test="${contingut.document && contingut.estat == 'FIRMA_PARCIAL'}">
	<span class="parcial fa fa-pencil-square" title="<spring:message code="contingut.info.estat.firmaparcial"/>"></span>
</c:if> 
<c:if test="${contingut.document && contingut.estat != 'CUSTODIAT' && contingut.estat != 'REDACCIO' && contingut.errorEnviamentPortafirmes && contingut.gesDocFirmatId == null}">
	<span class="error fa fa-pencil-square" title="<spring:message code="contingut.info.estat.pendentfirma"/>"></span>
</c:if> 
<c:if test="${not contingut.carpeta && contingut.metaNode == null}">
	<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.info.document.tipusdocument"/>"></span>
</c:if>			
									
&nbsp;${contingut.nom} 

<c:if test="${contingut.arxiuUuid == null && !isCreacioCarpetesLogica}">
	<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>
</c:if>