<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="id" required="false" rtexprvalue="true"%>
<%@ attribute name="className" required="false" rtexprvalue="true"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="modeLlistat" required="true" rtexprvalue="true"%>
<%@ attribute name="mostrarObrir" required="false" rtexprvalue="true"%>
<%@ attribute name="nodeco" required="false" rtexprvalue="true"%>

<c:set var="expedientPareObertOInexistent" value="${empty contingut.expedientPare or contingut.expedientPare.estat == 'OBERT'}"/>
<c:set var="mostrarSeparador" value="${false}"/>
<div <c:if test="${not empty id}">id="${id}" </c:if>class="dropdown<c:if test="${not modeLlistat}"> text-center</c:if><c:if test="${not empty className}"> ${className}</c:if>">
	<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle<c:if test="${not modeLlistat}"> btn-xs</c:if>"><span class="fa fa-cog"></span><c:if test="${modeLlistat}">&nbsp;<spring:message code="comu.boto.accions"/></c:if>&nbsp;<span class="caret caret-white"></span></button>
	<ul class="dropdown-menu">
		<c:if test="${empty mostrarObrir or mostrarObrir}">
			<li><a href="${contingut.id}"><span class="fa fa-folder-open-o"></span>&nbsp;<spring:message code="comu.boto.consultar"/></a></li>
			<c:set var="mostrarSeparador" value="${true}"/>
		</c:if>
		<c:if test="${contingut.expedientPare.agafat and expedientPareObertOInexistent}">
			<c:choose>
				<c:when test="${contingut.expedient and (empty contingut.metaNode or contingut.metaNode.usuariActualWrite)}">
					<li><a href="<c:url value="/expedient/${contingut.id}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
				<c:when test="${contingut.document and (empty contingut.metaNode or contingut.metaNode.usuariActualWrite) and contingut.estat == 'REDACCIO'}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
				<c:when test="${contingut.carpeta}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/carpeta/${contingut.id}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
			</c:choose>
			<c:if test="${not contingut.expedient}">
				<li><a href="<c:url value="/contingut/${contingut.id}/moure"/>" data-toggle="modal"><span class="fa fa-arrows"></span>&nbsp;<spring:message code="comu.boto.moure"/>...</a></li>
				<li><a href="<c:url value="/contingut/${contingut.id}/copiar"/>" data-toggle="modal"><span class="fa fa-copy"></span>&nbsp;<spring:message code="comu.boto.copiar"/>...</a></li>
				<c:if test="${empty contingut.expedientPare}">
					<li><a href="<c:url value="/contingut/${contingut.id}/enviar"/>" data-toggle="modal"><span class="fa fa-send"></span>&nbsp;<spring:message code="comu.boto.enviara"/>...</a></li>
				</c:if>
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>

					<li><a href="<c:url value="/contingut/${contingut.id}/delete"/>" data-confirm="<spring:message code="contingut.confirmacio.esborrar.node"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
					<c:set var="mostrarSeparador" value="${true}"/>

			<c:if test="${contingut.expedient}">
				<c:if test="${mostrarSeparador}">
					<c:set var="mostrarSeparador" value="${false}"/>
					<li role="separator" class="divider"></li>
				</c:if>
				<li><a href="<c:url value="/expedient/${contingut.id}/alliberar"/>"><span class="fa fa-unlock"></span>&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
				<c:if test="${contingut.metaNode.usuariActualWrite or empty contingut.metaNode}">
					<li><a href="<c:url value="/expedient/${contingut.id}/relacionar"/>" data-toggle="modal"><span class="fa fa-link"></span>&nbsp;<spring:message code="comu.boto.relacionar"/>...</a></li>
					<%--li><a href="<c:url value="/expedient/${contingut.id}/acumular"/>" data-toggle="modal"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.boto.acumular"/>...</a></li>
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/expedient/${contingut.id}/disgregar"/>" data-toggle="modal"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.disgregar"/>...</a></li--%>
				</c:if>
				<c:choose>
					<c:when test="${contingut.estat == 'OBERT'}">
						<c:choose>
							<c:when test="${contingut.valid}">
								<li><a href="<c:url value="/expedient/${contingut.id}/tancar"/>" data-toggle="modal"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="<c:url value=" onclick="/>"alert('<spring:message code="contingut.expedient.tancar.error.no.valid"/>');return false;"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<li><a href="<c:url value="/expedient/${contingut.id}/reobrir"/>" data-toggle="modal"><span class="fa fa-undo"></span>&nbsp;<spring:message code="comu.boto.reobrir"/>...</a></li>
					</c:otherwise>
				</c:choose>
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
		</c:if>
		<c:if test="${contingut.document}">
			<c:if test="${mostrarSeparador}">
				<c:set var="mostrarSeparador" value="${false}"/>
				<li role="separator" class="divider"></li>
			</c:if>
			<c:if test="${contingut.documentTipus != 'FISIC'}">
				<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
			<c:if test="${contingut.expedientPare.agafat and expedientPareObertOInexistent}">
				<c:if test="${contingut.estat == 'CUSTODIAT'}">
					<li><a href="<c:url value="/document/${contingut.id}/notificar"/>" data-toggle="modal" data-datatable-id="taulaEnviaments"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="comu.boto.notificar"/>...</a></li>
					<li><a href="<c:url value="/document/${contingut.id}/publicar"/>" data-toggle="modal" data-datatable-id="taulaEnviaments"><span class="fa fa-clipboard"></span>&nbsp;<spring:message code="comu.boto.publicar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${contingut.estat == 'REDACCIO' && contingut.metaNode.firmaPortafirmesActiva && contingut.documentTipus != 'FISIC'}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<li><a href="<c:url value="/document/${contingut.id}/portafirmes/upload"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
						</c:when>
						<c:otherwise>
							<li><a href="<c:url value=" onclick="/>"alert('<spring:message code="contingut.document.firmar.error.no.valid"/>');return false;"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
						</c:otherwise>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${contingut.estat == 'REDACCIO' && contingut.metaNode.firmaPassarelaActiva && contingut.documentTipus != 'FISIC'}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<li><a href="<c:url value="/document/${contingut.id}/firmaPassarela"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
						</c:when>
						<c:otherwise>
							<li><a href="<c:url value=" onclick="/>"alert('<spring:message code="contingut.document.firmar.error.no.valid"/>');return false;"><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
						</c:otherwise>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<%--c:if test="${contingut.estat != 'REDACCIO' && contingut.documentTipus != 'FISIC'}">
					<c:choose>
						<c:when test="${contingut.estat != 'CUSTODIAT'}">
							<li><a href="<c:url value="/document/${contingut.id}/portafirmes/info"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.portafirmes.info"/></a></li>
						</c:when>
						<c:otherwise>
							<li><a href="<c:url value="/document/${contingut.id}/custodia/info"/>" target="_blank"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.custodia"/>&nbsp;<small><i class="fa fa-external-link"></i></small></a></li>
						</c:otherwise>
					</c:choose>
					<c:if test="${contingut.estat != 'CUSTODIAT'}">
						<li><a href="<c:url value="/document/${contingut.id}/portafirmes/info"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.portafirmes.info"/></a></li>
					</c:when>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if--%>
				<c:if test="${contingut.estat != 'REDACCIO' && contingut.documentTipus != 'FISIC' && contingut.estat != 'CUSTODIAT'}">
					<li><a href="<c:url value="/document/${contingut.id}/portafirmes/info"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.portafirmes.info"/></a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
			</c:if>
		</c:if>
		<c:if test="${mostrarSeparador}">
			<c:set var="mostrarSeparador" value="${false}"/>
			<li role="separator" class="divider"></li>
		</c:if>
		<li><a href="<c:url value="/contingut/${contingut.id}/log"/>" data-toggle="modal"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.historial"/></a></li>
		<c:if test="${contingut.expedient or contingut.document}">
			<c:if test="${!empty nodeco}">
				<li><a href="<c:url value="/nodeco/contingut/${contingut.id}/exportar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.exportar.eni"/></a></li>
			</c:if>
			<c:if test="${empty nodeco}">
				<li><a href="<c:url value="/contingut/${contingut.id}/exportar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.exportar.eni"/></a></li>
			</c:if>			

		</c:if>
	</ul>
</div>
