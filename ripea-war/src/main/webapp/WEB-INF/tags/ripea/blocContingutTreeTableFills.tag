<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>


<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="mostrarFillsFlat" required="true" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="nodeco" required="false" rtexprvalue="true"%>
<c:set var="expedientPare" value="${contingut.expedientPare}"/>
<c:if test="${empty expedientPare and contingut.expedient}"><c:set var="expedientPare" value="${contingut}"/></c:if>
<c:set var="expedientPareAgafatPerUsuariActual" value="${false}"/>
<c:if test="${expedientPare.agafatPer.codi == pageContext.request.userPrincipal.name}"><c:set var="expedientPareAgafatPerUsuariActual" value="${true}"/></c:if>

<c:set var="fills" value="${contingut.fillsHierarchical}"/>

<c:forEach var="fill" items="${fills}">
	<c:if test="${fill.carpeta or (fill.document && fill.documentTipus != 'VIRTUAL') or empty fill.metaNode or fill.metaNode.usuariActualRead}">
		<c:set var="firmat" value="true"/>
		<c:set var="isPdf" value="true"/> 
		<c:set var="isDocAdjuntPendentGuardarArxiu" value="false"/> 
		<c:set var="isCarpetaPendentArxiu" value="${(fill.carpeta && !isCreacioCarpetesLogica && fill.arxiuUuid == null)}"/>
		<script>
			<c:if test="${fill.document}">
				<c:if test="${(fill.estat != 'FIRMAT' || fill.estat == 'CUSTODIAT') && (fill.estat == 'FIRMAT' || fill.estat != 'CUSTODIAT') && fill.estat != 'DEFINITIU'}">
					<c:set var="firmat" value="false"/> 
				</c:if>
				<c:if test="${fill.fitxerContentType != '' && fill.fitxerContentType != 'application/pdf'}">
					<c:set var="isPdf" value="false"/> 
				</c:if>
				<c:if test="${fill.gesDocAdjuntId != null}">
					<c:set var="isDocAdjuntPendentGuardarArxiu" value="true"/> 
				</c:if>								
			</c:if>
		</script>
		
		<tr id="info-fill-${fill.id}" class="element-drag-drop element-draggable ui-draggable <c:if test="${not fill.document}"> element-droppable</c:if><c:if test="${fill.document && firmat}"> firmat</c:if><c:if test="${fill.document && isPdf}"> isPdf</c:if> <c:if test="${fill.document && isDocAdjuntPendentGuardarArxiu}"> docAdjuntPendentGuardarArxiu</c:if>" data-contenidor-id="${fill.id}" data-node="treetable-${fill.id}" data-pnode="treetable-${contingut.id}">
			
			<td>
			<c:if test="${fill.document}">
			<input type="checkbox" class="info-fill-${fill.id}" id="${fill.id}" autocomplete="off"/>
			</c:if>
			</td>
			<td>
				<rip:blocIconaContingut contingut="${fill}"/>
<%--							<c:if test="${fill.document && fill.documentTipus != 'IMPORTAT' && fill.estat == 'REDACCIO'}"><span class="icona-esborrany fa fa-bold" title="<spring:message code="contingut.info.estat.redaccio"/>"></span></c:if>--%>
				<c:if test="${fill.document && fill.estat == 'REDACCIO'}"><span class="icona-esborrany fa fa-bold" title="<spring:message code="contingut.info.estat.redaccio"/>"></span></c:if>
				<c:if test="${fill.document && fill.documentTipus == 'IMPORTAT'}"><span class="importat fa fa-info-circle" title="<spring:message code="contingut.info.estat.importat"/>"></span></c:if>
				<c:if test="${fill.node and not fill.valid}">&nbsp;<span class="fa fa-exclamation-triangle text-warning"></span></c:if>
				<c:if test="${fill.document && (fill.estat == 'CUSTODIAT' || fill.estat == 'FIRMAT' || fill.estat == 'ADJUNT_FIRMAT')}"><span class="firmat fa fa-pencil-square" title="<spring:message code="contingut.info.estat.firmat"/>"></span></c:if>
				<c:if test="${fill.document && fill.estat == 'FIRMAT' && fill.gesDocFirmatId != null}"><span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentCustodiar"/>"></span></c:if>
				<c:if test="${fill.document && fill.pendentMoverArxiu}"><span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentMoverArxiu"/>"></span></c:if>
				<c:if test="${fill.document && !fill.validacioFirmaCorrecte}"><span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.invalid.origen" arguments="${fill.validacioFirmaErrorMsg}"/>"></span></c:if>
				<c:if test="${fill.document && fill.estat == 'DEFINITIU'}"><span class="definitiu fa fa-check-square" title="<spring:message code="contingut.info.estat.defintiu"/>"></span></c:if>
				<c:if test="${fill.document && fill.ambNotificacions}">
					<c:choose>
						<c:when test="${!fill.errorDarreraNotificacio && (fill.estatDarreraNotificacio == 'PENDENT' or fill.estatDarreraNotificacio == 'REGISTRADA')}">
							<c:set var="envelope" value="pendent fa fa-envelope-square"/>
						</c:when>
						<c:when  test="${!fill.errorDarreraNotificacio && fill.estatDarreraNotificacio == 'ENVIADA'}">
							<c:set var="envelope" value="enviada fa fa-envelope-square"/>
						</c:when>
						<c:when  test="${!fill.errorDarreraNotificacio && (fill.estatDarreraNotificacio == 'PROCESSADA' or fill.estatDarreraNotificacio == 'FINALITZADA')}">
							<c:set var="envelope" value="processada fa fa-envelope-square"/>
						</c:when>
						<c:when  test="${fill.errorDarreraNotificacio}">
							<c:set var="envelope" value="error fa fa-envelope-square"/>
						</c:when>
						<c:otherwise>
							<c:set var="envelope" value="fa fa-envelope-square"/>
						</c:otherwise>
					</c:choose>
					<span class="${envelope} popover-${fill.id}" id="${fill.id}" data-toggle="popover" title="<spring:message code="contingut.info.notificacions"/>"></span>
				</c:if>
				<c:if test="${fill.document && fill.estat != 'CUSTODIAT' && fill.estat != 'REDACCIO' && (fill.estat == 'FIRMA_PENDENT_VIAFIRMA' || fill.estat == 'FIRMA_PENDENT')}">
					<span class="pendent fa fa-pencil-square" title="<spring:message code="contingut.info.estat.pendentfirma"/>"></span>
				</c:if>
				<c:if test="${fill.document && fill.estat == 'FIRMA_PARCIAL'}">
					<span class="parcial fa fa-pencil-square" title="<spring:message code="contingut.info.estat.firmaparcial"/>"></span>
				</c:if>
				<c:if test="${fill.document && fill.estat != 'CUSTODIAT' && fill.estat != 'REDACCIO' && fill.errorEnviamentPortafirmes && fill.gesDocFirmatId == null}">
					<span class="error fa fa-pencil-square" title="<spring:message code="contingut.info.estat.pendentfirma"/>"></span>
				</c:if>
				&nbsp;${fill.nom}
				<c:if test="${isCarpetaPendentArxiu || (!fill.carpeta && fill.arxiuUuid == null)}">
					<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>
				</c:if>
			</td>
			<td>
			<c:if test="${fill.document}">
				&nbsp;${fill.descripcio}
			</c:if>
			</td>
			<td width="25%">
				<c:if test="${not fill.carpeta}">
					${fill.metaNode.nom}
				</c:if>
			</td>
			
			<td><fmt:formatDate value="${fill.createdDate}" pattern="dd/MM/yyyy HH:mm"/></td>
			<td>${fill.createdBy.codiAndNom}</td>
			<td>
				<rip:blocContingutAccions className="botons-accions-element" modeLlistat="true" contingut="${fill}"  nodeco="${nodeco}"/>
			</td>
			<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
				<td class="ordre-col" title="<spring:message code="contingut.sort.titol"/>">
					<span class="fa fa-sort"></span>
				</td>
			</c:if>
		</tr>
		<script>
		$('.info-fill-${fill.id}').change(function() {
			//Remove if duplicate
			var index = docsIdx.indexOf(${fill.id});
			var multipleUrl;
			
			if (index > -1) {
				docsIdx.splice(index, 1);
				var multipleUrl = '<c:url value="/contingut/${contingut.id}/deselect"/>';
				$.get(
						multipleUrl, 
						{docsIdx: docsIdx},
						function(data) {
							$(".seleccioCount").html(data);
						}
				);
			}
			if(this.checked) {
				var multipleUrl = '<c:url value="/contingut/${contingut.id}/select"/>';
				docsIdx.push(${fill.id});
				$.get(
						multipleUrl, 
						{docsIdx: docsIdx},
						function(data) {
							$(".seleccioCount").html(data);
						}
				);
			} else {
				var multipleUrl = '<c:url value="/contingut/${contingut.id}/deselect"/>';
				$.get(
						multipleUrl, 
						{docsIdx: docsIdx},
						function(data) {
							$(".seleccioCount").html(data);
						}
				);
			}
			enableDisableButton();
		});
		</script>
	</c:if>
	
	<rip:blocContingutTreeTableFills contingut="${fill}" mostrarFillsFlat="${!isMostrarCarpetesPerAnotacions}"/>	
	
</c:forEach>
