<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="id" required="false" rtexprvalue="true"%>
<%@ attribute name="className" required="false" rtexprvalue="true"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="modeLlistat" required="true" rtexprvalue="true"%>
<%@ attribute name="mostrarObrir" required="false" rtexprvalue="true"%>
<%@ attribute name="nodeco" required="false" rtexprvalue="true"%>
<c:set var="expedientPare" value="${contingut.expedientPare}"/>
<c:if test="${empty expedientPare and contingut.expedient}"><c:set var="expedientPare" value="${contingut}"/></c:if>
<c:set var="expedientPareAgafatPerUsuariActual" value="${false}"/>
<c:if test="${expedientPare.agafatPer.codi == pageContext.request.userPrincipal.name}"><c:set var="expedientPareAgafatPerUsuariActual" value="${true}"/></c:if>
<c:set var="expedientPareObert" value="${empty expedientPare or expedientPare.estat == 'OBERT'}"/>
<c:set var="potModificarExpedientPare" value="${false}"/>
<c:if test="${expedientPareAgafatPerUsuariActual and expedientPareObert and (not contingut.node or expedientPare.metaNode.usuariActualWrite) or expedientPare.usuariActualWrite}"><c:set var="potModificarExpedientPare" value="${true}"/></c:if>
<c:set var="mostrarSeparador" value="${false}"/>
<div <c:if test="${not empty id}">id="${id}" </c:if>class="dropdown<c:if test="${not modeLlistat}"> text-center</c:if><c:if test="${not empty className}"> ${className}</c:if>">
	<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle<c:if test="${not modeLlistat}"> btn-xs</c:if>"><span class="fa fa-cog"></span><c:if test="${modeLlistat}">&nbsp;<spring:message code="comu.boto.accions"/></c:if>&nbsp;<span class="caret caret-white"></span></button>
	<ul class="dropdown-menu">
		<c:if test="${empty mostrarObrir or mostrarObrir}">
			<c:choose>
			<c:when test="${!contingut.carpeta}">
				<li class="hidden"><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
				<li><a href="${contingut.id}" data-toggle="modal" data-maximized="true"><span class="fa fa-folder-open-o"></span>&nbsp;<spring:message code="comu.boto.consultar"/></a></li>			
			</c:when>
			<c:otherwise>
				<li><a href="${contingut.id}"><span class="fa fa-folder-open-o"></span>&nbsp;<spring:message code="comu.boto.consultar"/></a></li>
			</c:otherwise>
			</c:choose>
			<c:set var="mostrarSeparador" value="${true}"/>
		</c:if>
		<c:if test="${potModificarExpedientPare || (contingut.expedient && contingut.usuariActualWrite) }">
			<c:choose>
				<c:when test="${contingut.expedient}">
					<li><a href="<c:url value="/expedient/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
				<c:when test="${contingut.document and contingut.estat == 'REDACCIO'}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
				<c:when test="${contingut.carpeta}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/carpeta/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
			</c:choose>
			<c:if test="${not contingut.expedient}">
				<c:if test="${isMostrarCopiarMoure}">
					<li><a href="<c:url value="/contingut/${contingut.id}/moure"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-arrows"></span>&nbsp;<spring:message code="comu.boto.moure"/>...</a></li>
					<li><a href="<c:url value="/contingut/${contingut.id}/copiar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-copy"></span>&nbsp;<spring:message code="comu.boto.copiar"/>...</a></li>
				</c:if>
				<c:if test="${empty expedientPare}">
					<li><a href="<c:url value="/contingut/${contingut.id}/enviar"/>" data-toggle="modal"><span class="fa fa-send"></span>&nbsp;<spring:message code="comu.boto.enviara"/>...</a></li>
				</c:if>
				<c:if test="${isMostrarVincular}">
					<li><a href="<c:url value="/contingut/${contingut.id}/vincular"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-link"></span>&nbsp;<spring:message code="comu.boto.vincular"/>...</a></li>
				</c:if>
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
			<c:if test="${contingut.expedient}">
				<c:if test="${mostrarSeparador}">
					<c:set var="mostrarSeparador" value="${false}"/>
					<li role="separator" class="divider"></li>
				</c:if>
				<li><a href="<c:url value="/expedient/${contingut.id}/alliberar"/>"><span class="fa fa-unlock"></span>&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
				<li><a href="<c:url value="/expedient/${contingut.id}/canviarEstat"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.canviarEstat"/>...</a></li>
				<li><a href="<c:url value="/expedient/${contingut.id}/relacionarList"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-link"></span>&nbsp;<spring:message code="comu.boto.relacionar"/>...</a></li>
				
				<%--li><a href="<c:url value="/expedient/${contingut.id}/acumular"/>" data-toggle="modal"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.boto.acumular"/>...</a></li>
				<li><a href="<c:url value="/contingut/${contingut.pare.id}/expedient/${contingut.id}/disgregar"/>" data-toggle="modal"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.disgregar"/>...</a></li--%>
				<c:choose>
					<c:when test="${contingut.estat == 'OBERT'}">
						<c:choose>
							<c:when test="${contingut.valid && contingut.conteDocumentsFirmats}">
								<li><a href="<c:url value="/expedient/${contingut.id}/tancar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
							</c:when>
							<c:otherwise>
								<li class="disabled"><a href="#"/><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<li><a href="<c:url value="/expedient/${contingut.id}/reobrir"/>" data-toggle="modal"><span class="fa fa-undo"></span>&nbsp;<spring:message code="comu.boto.reobrir"/>...</a></li>
					</c:otherwise>
				</c:choose>
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
			
			<c:choose>
				<c:when test="${contingut.expedient && contingut.conteDocumentsFirmats || contingut.document && contingut.estat != 'REDACCIO'}">
					<li class="disabled"><a><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
				</c:when>
				<c:otherwise>
					<li><a href="<c:url value="/contingut/${contingut.id}/delete"/>" data-confirm="<spring:message code="contingut.confirmacio.esborrar.node"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
				</c:otherwise>
			</c:choose>			
			
			<c:set var="mostrarSeparador" value="${true}"/>
		</c:if>
		<c:if test="${contingut.document}">
			<c:if test="${mostrarSeparador}">
				<c:set var="mostrarSeparador" value="${false}"/>
				<li role="separator" class="divider"></li>
			</c:if>
			<c:if test="${contingut.documentTipus != 'FISIC'}">
				<c:if test="${contingut.custodiat}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregarImprimible"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregarImprimible"/></a></li>
				</c:if>
				<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
			<c:if test="${potModificarExpedientPare}">
				<c:if test="${contingut.metaNode.firmaPortafirmesActiva && contingut.estat == 'REDACCIO' && contingut.documentTipus != 'FISIC'}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<li><a href="<c:url value="/document/${contingut.id}/portafirmes/upload"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
						</c:when>
						<c:otherwise>
							<li class="disabled"><a href="#"/><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
						</c:otherwise>
						<%--c:otherwise>
							<li><a href="<c:url value=" onclick="/>"alert('<spring:message code="contingut.document.firmar.error.no.valid"/>');return false;"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
						</c:otherwise--%>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${contingut.metaNode.firmaPassarelaActiva && contingut.estat == 'REDACCIO' && contingut.documentTipus != 'FISIC'}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<li><a href="<c:url value="/document/${contingut.id}/firmaPassarela"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
						</c:when>
						<c:otherwise>
							<li class="disabled"><a href="#"/><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
						</c:otherwise>
						<%--c:otherwise>
							<li><a href="<c:url value=" onclick="/>"alert('<spring:message code="contingut.document.firmar.error.no.valid"/>');return false;"><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
						</c:otherwise--%>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${contingut.estat == 'REDACCIO' && contingut.metaNode.firmaBiometricaActiva && contingut.documentTipus != 'FISIC' && isFirmaBiometrica}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<li><a href="<c:url value="/document/${contingut.id}/viafirma/upload"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-envelope-square"></span>&nbsp;<spring:message code="contingut.boto.viafirma.enviar"/>...</a></li>
						</c:when>
						<c:otherwise>
							<li class="disabled"><a href="#"/><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.viafirma.enviar"/>...</a></li>
						</c:otherwise>
						<%--c:otherwise>
							<li><a href="<c:url value=" onclick="/>"alert('<spring:message code="contingut.document.firmar.error.no.valid"/>');return false;"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
						</c:otherwise--%>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${contingut.estat == 'CUSTODIAT'}">
					<li><a href="<c:url value="/document/${contingut.id}/notificar"/>" data-toggle="modal" data-datatable-id="taulaEnviaments" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="comu.boto.notificar"/>...</a></li>
					<li><a href="<c:url value="/document/${contingut.id}/publicar"/>" data-toggle="modal" data-datatable-id="taulaEnviaments"><span class="fa fa-clipboard"></span>&nbsp;<spring:message code="comu.boto.publicar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${contingut.estat != 'REDACCIO' && contingut.estat != 'CUSTODIAT' && contingut.estat != 'FIRMA_PENDENT_VIAFIRMA' && contingut.documentTipus != 'FISIC'}">
					<li><a href="<c:url value="/document/${contingut.id}/portafirmes/info"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.portafirmes.info"/></a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${contingut.estat != 'REDACCIO' && contingut.estat != 'CUSTODIAT' && contingut.estat == 'FIRMA_PENDENT_VIAFIRMA' && contingut.documentTipus != 'FISIC'}">
					<li><a href="<c:url value="/document/${contingut.id}/viafirma/info"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.viafirma.info"/></a></li>
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
			<c:choose>
				<c:when test="${!empty nodeco}">
					<c:set var="exportarUrl"><c:url value="/nodeco/contingut/${contingut.id}/exportar"/></c:set>	
				</c:when>
				<c:otherwise>
					<c:set var="exportarUrl"><c:url value="/contingut/${contingut.id}/exportar"/></c:set>
				</c:otherwise>
			</c:choose>		
			<c:if test="${contingut.expedient && pluginArxiuActiu}"> 
				<li><a href="<c:url value="/contingut/${contingut.id}/arxiu"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
			</c:if>
			<c:set var="contingutEstat">${contingut.estat}</c:set>	
			<c:choose>
				<c:when test="${contingut.document && contingutEstat != 'CUSTODIAT' || contingut.expedient && !contingut.conteDocumentsFirmats}">
					<li class="disabled"><a href="#"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.exportar.eni"/></a></li>
				</c:when>
				<c:otherwise>
					<li><a href="${exportarUrl}"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.exportar.eni"/></a></li>
				</c:otherwise>
			</c:choose>				
		</c:if>
	</ul>
</div>
