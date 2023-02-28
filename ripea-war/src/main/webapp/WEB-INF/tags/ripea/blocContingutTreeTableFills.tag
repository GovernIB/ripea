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
	<c:if test="${fill.carpeta or (fill.document && fill.documentTipus != 'VIRTUAL')}">
	
		<c:set var="firmat" value="true"/>
		<c:if test="${fill.document}">
			<c:if test="${(fill.estat != 'FIRMAT' || fill.estat == 'CUSTODIAT') && (fill.estat == 'FIRMAT' || fill.estat != 'CUSTODIAT') && fill.estat != 'DEFINITIU'}"><!-- TODO: revise, condition never true -->
				<c:set var="firmat" value="false"/> 
			</c:if>
		</c:if>

		<tr id="${fill.id}"
			class="element-draggable <c:if test="${not fill.document}"> element-droppable</c:if><c:if test="${fill.document}"> isDocument</c:if><c:if test="${fill.document && firmat}"> firmat</c:if><c:if test="${fill.document && fill.pdf}"> isPdf</c:if> <c:if test="${fill.document && fill.arxiuUuid == null}"> docAdjuntPendentGuardarArxiu</c:if>"
			data-expedient-id="${expedientPare.id}" 
			data-node="treetable-${fill.id}"
			data-pnode="treetable-${contingut.id}">
			

			<%------------ checkbox ----------%>
			<td><c:if test="${fill.document}">
				<input type="checkbox" class="checkbox" autocomplete="off" />
			</c:if></td>

			<%------------ Nom ----------%>
			<td>
				<rip:blocIconaContingut contingut="${fill}" /> 
				<rip:blocContingutNomAmbIcons contingut="${fill}" /> 
			</td>


			<%------------ Descripció ----------%>
			<td><c:if test="${fill.document}">
				&nbsp;${fill.descripcio}
			</c:if></td>

			<%------------ Tipus -----------%>
			<td width="25%"><c:if test="${not fill.carpeta}">
					${fill.metaNode.nom}
				</c:if></td>

			<%------------ Creat el -----------%>
			<td><fmt:formatDate value="${fill.createdDate}" pattern="dd/MM/yyyy HH:mm" /></td>

			<%------------ Creat per -----------%>
			<td>${fill.createdBy.codiAndNom}</td>

			<%------------ Accions -----------%>
			<td><rip:blocContingutAccions className="botons-accions-element"
					modeLlistat="true" contingut="${fill}" nodeco="${nodeco}" /></td>

			<%------------ sort ----------%>
			<c:if test="${expedientPareAgafatPerUsuariActual && isOrdenacioPermesa}">
				<td class="ordre-col" title="<spring:message code="contingut.sort.titol"/>"><span
					class="fa fa-sort"></span></td>
			</c:if>
		</tr>

	</c:if>
	
	<rip:blocContingutTreeTableFills contingut="${fill}" mostrarFillsFlat="${!isMostrarCarpetesPerAnotacions}"/>	
	
</c:forEach>
