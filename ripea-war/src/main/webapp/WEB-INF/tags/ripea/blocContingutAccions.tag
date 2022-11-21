<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
<c:if test="${expedientPareAgafatPerUsuariActual and expedientPareObert and (not contingut.node or expedientPare.metaNode.usuariActualWrite) and expedientPare.usuariActualWrite}">
	<c:set var="potModificarExpedientPare" value="${true}"/>
</c:if>
<c:set var="mostrarSeparador" value="${false}"/>
<c:set var="isTasca" value="${not empty tascaId}"/>
<div <c:if test="${not empty id}">id="${id}" </c:if>class="dropdown<c:if test="${not modeLlistat}"> text-center</c:if><c:if test="${not empty className}"> ${className}</c:if>">

	<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle<c:if test="${not modeLlistat}"> btn-xs</c:if>"><span class="fa fa-cog"></span><c:if test="${modeLlistat}">&nbsp;<spring:message code="comu.boto.accions"/></c:if>&nbsp;<span class="caret caret-white"></span></button>
	<ul class="dropdown-menu">
	
		<c:if test="${contingut.document}">
			<c:choose>
				<%---- Visualitzar/Descarregar when clicking row ----%>
				<c:when test="${isTasca}">
					<c:choose>
						<c:when test="${contingut.gesDocAdjuntId!=null || (contingut.fitxerExtension!='pdf' && contingut.fitxerExtension!='odt' && contingut.fitxerExtension!='docx')}">
							<li class="hidden"><a href="<c:url value="/usuariTasca/${tascaId}/pare/${contingut.pare.id}/document/${contingut.id}/descarregar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
						</c:when>
						<c:otherwise>
							<li class="hidden"><a href="#" onclick="showViewer(event, ${contingut.id}, '${fn:escapeXml(contingut.nom)}, ${tascaId}', ${contingut.custodiat}"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${(contingut.fitxerExtension!='pdf' && contingut.fitxerExtension!='odt' && contingut.fitxerExtension!='docx')}">
							<li class="hidden"><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
						</c:when>
						<c:otherwise>
							<li class="hidden"><a href="#" onclick="showViewer(event, ${contingut.id}, '${fn:escapeXml(contingut.nom)}', ${contingut.custodiat})"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
						</c:otherwise>
					</c:choose>							
				</c:otherwise>
			</c:choose>	
		</c:if>
		
		
		<c:choose>
			<c:when test="${contingut.document && contingut.arxiuUuid==null}">
				<c:set var="primerGuardarExpedientArxiu"><spring:message code="disabled.button.primerGuardarExpedientArxiu"/></c:set>
				<li class="disabledMsg" title="${expedientPare.arxiuUuid == null ? primerGuardarExpedientArxiu : ''}"><a class="${expedientPare.arxiuUuid == null ? 'disabled' : ''}" href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/guardarDocumentArxiu?origin=docDetail"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
			</c:when>
			<c:when test="${contingut.document && contingut.gesDocFirmatId != null}">
				<li><a href="<c:url value="/document/${contingut.id}/portafirmes/reintentarGuardarArxiu"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
			</c:when>			
			<c:when test="${contingut.expedient && contingut.arxiuUuid==null}">
				<li><a href="<c:url value="/expedient/${contingut.id}/guardarExpedientArxiu?origin=expDetail"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
			</c:when>
		</c:choose>
		<c:if test="${(empty mostrarObrir or mostrarObrir)}">
			<c:choose>
				<c:when test="${contingut.carpeta}">
					<li><a href="${(not empty contingut.expedientRelacionat) ? contingut.expedientRelacionat.id : contingut.id}"><span class="fa fa-folder-open-o"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
				</c:when>
				<c:otherwise>
					<c:if test="${!isTasca}">
						<%---- Consultar ----%>
						<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/contingut/${contingut.id}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-folder-open-o"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
					</c:if>
				</c:otherwise>
			</c:choose>
			
			<c:set var="mostrarSeparador" value="${true}"/>
		</c:if>
		
		
		<c:if test="${contingut.expedient && isEntitatUserAdminOrOrgan}">
			<li><a href="<c:url value="/expedient/${contingut.id}/assignar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-user"></span>&nbsp;<spring:message code="comu.boto.assignar"/></a></li>
		</c:if>
		
		<c:if test="${isTasca || potModificarExpedientPare || contingut.admin}">
			<%---- Modificar... ----%>
			<c:set var="isPermesModificarCustodiatsVar" value="${isPermesModificarCustodiats && contingut.document && (contingut.estat == 'CUSTODIAT' || contingut.estat == 'FIRMAT' || contingut.estat == 'FIRMA_PARCIAL' || contingut.estat == 'DEFINITIU')}"/>
			<c:set var="isPermesEsborrarFinalsVar" value="${contingut.document && ((isPermesEsborrarFinals && (contingut.estat == 'CUSTODIAT' || contingut.estat == 'FIRMAT' || contingut.estat == 'DEFINITIU' || contingut.documentTipus == 'IMPORTAT')) || (contingut.estat == 'REDACCIO' || contingut.estat == 'FIRMA_PENDENT' || contingut.estat == 'FIRMA_PARCIAL'))}"/>
			<c:choose>
				<c:when test="${contingut.expedient && contingut.estat == 'OBERT'}">
					<li><a href="<c:url value="/expedient/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
				
				<c:when test="${contingut.document && (!contingut.arxiuEstatDefinitu || isPermesModificarCustodiatsVar) && expedientPareObert}">
					<c:choose>
						<c:when test="${isTasca}">
							<li><a href="<c:url value="/usuariTasca/${tascaId}/pare/${contingut.pare.id}/document/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
						</c:when>
						<c:otherwise>
							<li class="${(contingut.document && (contingut.arxiuUuid==null || contingut.gesDocFirmatId != null)) ? 'disabled' : ''}"><a href="<c:url value="/contingut/${contingut.pare.id}/document/modificar/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
						</c:otherwise>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
				<c:when test="${contingut.carpeta && isCreacioCarpetesActiva}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/carpeta/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
			</c:choose>
			<c:if test="${contingut.document and !isTasca or (contingut.carpeta && isCreacioCarpetesActiva)}">
				
				<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/contingut/${contingut.id}/moure"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-arrows"></span>&nbsp;<spring:message code="comu.boto.moure"/>...</a></li>
				<c:if test="${isMostrarCopiar}">
					<li><a href="<c:url value="/contingut/${contingut.id}/copiar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-copy"></span>&nbsp;<spring:message code="comu.boto.copiar"/>...</a></li>
				</c:if>
				<c:if test="${empty expedientPare}">
					<li><a href="<c:url value="/contingut/${contingut.id}/enviar"/>" data-toggle="modal"><span class="fa fa-send"></span>&nbsp;<spring:message code="comu.boto.enviara"/>...</a></li>
				</c:if>
				<c:if test="${contingut.document and !isTasca and isMostrarVincular}">
					<li><a href="<c:url value="/contingut/${contingut.id}/vincular"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-link"></span>&nbsp;<spring:message code="comu.boto.vincular"/>...</a></li>
				</c:if>
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
			<c:if test="${contingut.expedient and !isTasca}">
				<c:set var="mostrarSeparador" value="${false}"/>
				<li role="separator" class="divider"></li>
			</c:if>
		</c:if>
		<c:if test="${contingut.expedient && expedientPareAgafatPerUsuariActual}">
			<li><a href="<c:url value="/expedient/${contingut.id}/alliberar"/>"><span class="fa fa-unlock"></span>&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
		</c:if>
		<c:if test="${contingut.expedient && not expedientPareAgafatPerUsuariActual}">
			<li><a href="<c:url value="/expedient/${contingut.id}/agafar"/>"><span class="fa fa-lock"></span>&nbsp;<spring:message code="comu.boto.agafar"/></a></li>
		</c:if>
		<c:if test="${(isTasca || potModificarExpedientPare || contingut.admin) || (contingut.carpeta && isCreacioCarpetesActiva)}">
			<c:if test="${contingut.expedient and !isTasca}">
				<c:if test="${contingut.estat == 'OBERT'}">
					<li><a href="<c:url value="/expedient/${contingut.id}/canviarEstat"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.canviarEstat"/>...</a></li>
				</c:if>
				<li><a href="<c:url value="/expedient/${contingut.id}/relacionarList"/>" data-toggle="modal" data-refresh-pagina="true" data-maximized="true"><span class="fa fa-link"></span>&nbsp;<spring:message code="comu.boto.relacionar"/>...</a></li>
				<%--li><a href="<c:url value="/expedient/${contingut.id}/acumular"/>" data-toggle="modal"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.boto.acumular"/>...</a></li>
				<li><a href="<c:url value="/contingut/${contingut.pare.id}/expedient/${contingut.id}/disgregar"/>" data-toggle="modal"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.disgregar"/>...</a></li--%>
				<c:choose>
					<c:when test="${contingut.estat == 'OBERT'}">
						<c:choose>
							<c:when test="${contingut.valid && contingut.conteDocuments && !contingut.conteDocumentsEnProcessDeFirma && !contingut.conteDocumentsPendentsReintentsArxiu}">
								<li><a href="<c:url value="/expedient/${contingut.id}/tancar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${!contingut.valid}">
										<li class="disabledMsg" title="<spring:message code="contingut.errors.expedient.validacio"/>"><a class="disabled"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:when>								
									<c:when test="${!contingut.conteDocuments}">
										<li class="disabledMsg" title="<spring:message code="disabled.button.msg.noConteCapDocument"/>"><a class="disabled"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:when>
									<c:when test="${contingut.conteDocumentsEnProcessDeFirma}">
										<li class="disabledMsg" title="<spring:message code="disabled.button.msg.conteDocumentsEnProcessDeFirma"/>"><a class="disabled"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:when>
									<c:when test="${contingut.conteDocumentsPendentsReintentsArxiu}">
										<li class="disabledMsg" title="<spring:message code="disabled.button.msg.conteDocumentsPendentsReintentsArxiu"/>"><a class="disabled"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:when>									
									<c:otherwise>
										<li class="disabled"><a href="#"/><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${contingut.estat == 'TANCAT' && isReobrirPermes}">
						<li><a href="<c:url value="/expedient/${contingut.id}/reobrir"/>" data-toggle="modal"><span class="fa fa-undo"></span>&nbsp;<spring:message code="comu.boto.reobrir"/>...</a></li>
					</c:when>
				</c:choose>
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
			
			<%---- Esborrar ----%>
			<c:choose>
				<c:when test="${contingut.document && contingut.estat == 'FIRMA_PENDENT'}">
					<c:set var="esborrarConfirmacioMsg"><spring:message code="contingut.confirmacio.esborrar.firmat.pendent"/> </c:set>
				</c:when>
				<c:when test="${contingut.document && contingut.arxiuEstat == 'DEFINITIU'}">
					<c:set var="esborrarConfirmacioMsg"><spring:message code="contingut.confirmacio.esborrar.firmat"/> </c:set>
				</c:when>
				<c:otherwise>
					<c:set var="esborrarConfirmacioMsg"><spring:message code="contingut.confirmacio.esborrar.node"/></c:set>
				</c:otherwise>
			</c:choose>
			<c:if test="${(!contingut.document && !contingut.conteDocumentsDefinitius) || isPermesEsborrarFinalsVar || contingut.carpeta}">
				<c:choose>
					<c:when test="${isTasca}">
						<li><a href="<c:url value="/usuariTasca/${tascaId}/contingut/${contingut.id}/delete"/>" data-confirm="${esborrarConfirmacioMsg}"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
					</c:when>
					<c:otherwise>
						<c:if test="${((contingut.expedient && expedientPare.usuariActualDelete) || (contingut.document && expedientPare.usuariActualWrite) || (contingut.carpeta && isCreacioCarpetesActiva && expedientPare.usuariActualDelete )) && expedientPareObert}">
							<li><a href="<c:url value="/contingut/${contingut.id}/delete"/>" data-confirm="${esborrarConfirmacioMsg}"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:if>
			<c:set var="mostrarSeparador" value="${true}"/>
		</c:if>
		<c:if test="${contingut.document}">
			<c:if test="${mostrarSeparador}">
				<c:set var="mostrarSeparador" value="${false}"/>
				<li role="separator" class="divider"></li>
			</c:if>
			<c:if test="${contingut.documentTipus == 'DIGITAL' or contingut.documentTipus == 'IMPORTAT'}">
				<c:if test="${(contingut.custodiat or contingut.firmaParcial) and !isTasca}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregarImprimible"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregarImprimible"/></a></li>
				</c:if>
				<c:if test="${!contingut.custodiat and !isTasca and contingut.pdf and imprimibleNoFirmats}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregarImprimible"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregarImprimible"/></a></li>
				</c:if>
				<%---- Descarregar ----%>
				<c:choose>
					<c:when test="${isTasca}">
						<li><a href="<c:url value="/usuariTasca/${tascaId}/pare/${contingut.pare.id}/document/${contingut.id}/descarregar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
					</c:when>
					<c:otherwise>
						<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregar"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
					</c:otherwise>
				</c:choose>
				<%---- Visualitzar ----%>
				<li class="${(contingut.fitxerExtension!='pdf' && contingut.fitxerExtension!='odt' && contingut.fitxerExtension!='docx') ? 'disabled' : ''}"><a href="#" onclick="showViewer(event, ${contingut.id}, '${fn:escapeXml(contingut.nom)}', ${contingut.custodiat})"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
				<c:if test="${(contingut.custodiat or contingut.estat == 'DEFINITIU') and isUrlValidacioDefinida}">
					<li><a href="#copy_${contingut.id}"><span class="fa fa-copy"></span>&nbsp;<spring:message code="comu.boto.urlValidacio"/></a></li>
					<script>
						$('a[href = "#copy_${contingut.id}"]').click(function(){
							$.get("../document/" + ${contingut.id} + "/urlValidacio", function(data) {
								var dummy = $('<input>').val(data).appendTo('body').select();
								document.execCommand("copy");
								$(dummy).remove();				
							});
							$('.copy').remove();
							$('.panel-body').prepend("<div class='copy alert alert-success' style='font-weight:bold;' role='alert'><spring:message code='comu.boto.urlValidacio.copiat'/></div>");
							setTimeout(function(){	
								$('.copy').remove();
							}, 2000);
						}); 
					</script>
				</c:if>		
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
			
			<%--------------- FIRMA RELATED ACCIONS -------------------%>
			<c:if test="${isTasca || potModificarExpedientPare || contingut.admin}">
			
				<%---- Enviar a portafirmes ----%>
				<c:if test="${contingut.metaNode.firmaPortafirmesActiva && (contingut.estat == 'REDACCIO' || contingut.estat == 'FIRMA_PARCIAL') && (contingut.documentTipus == 'DIGITAL' || contingut.documentTipus == 'IMPORTAT') && contingut.fitxerExtension!='zip'}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<c:choose>
								<c:when test="${isTasca}">
									<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/usuariTasca/${tascaId}/document/${contingut.id}/portafirmes/upload"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
								</c:when>
								<c:otherwise>
									<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/document/${contingut.id}/portafirmes/upload"/>" data-toggle="modal" data-height="450px" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
								</c:otherwise>								
							</c:choose>
						</c:when>
						<c:otherwise>
							<li class="disabled"><a href="#"/><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
						</c:otherwise>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				
				<%---- Firmar al navegador ----%>
				<c:if test="${contingut.metaNode.firmaPassarelaActiva && (contingut.estat == 'REDACCIO' || contingut.estat == 'FIRMA_PARCIAL') && (contingut.documentTipus == 'DIGITAL' || contingut.documentTipus == 'IMPORTAT') && contingut.fitxerExtension!='zip'}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<c:choose>
								<c:when test="${isTasca}">
									<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/usuariTasca/${tascaId}/document/${contingut.id}/firmaPassarela"/>" data-toggle="modal" data-refresh-pagina="true" data-refresh-tancar="true"><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
								</c:when>
								<c:otherwise>
									<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/document/${contingut.id}/firmaPassarela"/>" data-toggle="modal" data-refresh-pagina="true" data-refresh-tancar="true"><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
								</c:otherwise>								
							</c:choose>						
						</c:when>
						<c:otherwise>
							<li class="disabled"><a href="#"/><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
						</c:otherwise>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				
				<%---- Enviar a viaFirma ----%>
				<c:if test="${!isTasca and (contingut.estat == 'REDACCIO' && contingut.metaNode.firmaBiometricaActiva && (contingut.documentTipus == 'DIGITAL' || contingut.documentTipus == 'IMPORTAT') && isFirmaBiometrica) && contingut.fitxerExtension!='zip'}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/document/${contingut.id}/viafirma/upload"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-envelope-square"></span>&nbsp;<spring:message code="contingut.boto.viafirma.enviar"/>...</a></li>
						</c:when>
						<c:otherwise>
							<li class="disabled"><a href="#"/><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.viafirma.enviar"/>...</a></li>
						</c:otherwise>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${(contingut.estat == 'CUSTODIAT' or contingut.estat == 'DEFINITIU') and !isTasca or contingut.fitxerExtension=='zip'}">
				
					<%---- Notificar ----%>
					<c:if test="${expedientPare.metaNode.notificacioActiva}"> 
						<c:choose>
							<c:when test="${!empty expedientPare.interessatsNotificable}">
								<li>
								<a href="<c:url value="/document/${contingut.id}/notificar"/>" data-missatgeloading="Realitzant enviament..." data-toggle="modal" data-datatable-id="taulaEnviaments" data-maximized="true" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="comu.boto.notificar"/>...</a>
								</li>
							</c:when>
							<c:otherwise>
								<li class="disabled"><a href="#"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="comu.boto.notificar"/>...</a></li>
								<p style="font-size: 9px;padding: 1px 15px;">&nbsp;<spring:message code="comu.boto.notificar.comentari"/></p>
							</c:otherwise>
						</c:choose>
					</c:if>
					
					<%---- Publicar ----%>
					<c:if test="${isMostrarPublicar}">
						<li><a href="<c:url value="/document/${contingut.id}/publicar"/>" data-toggle="modal" data-datatable-id="taulaEnviaments"><span class="fa fa-clipboard"></span>&nbsp;<spring:message code="comu.boto.publicar"/>...</a></li>
					</c:if>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
			</c:if>
			<c:if test="${isTasca || potModificarExpedientPare || contingut.admin}">
				<%---- Seguiment portafirmes ----%>
				<c:if test="${(contingut.estat == 'FIRMA_PENDENT' || contingut.estat == 'FIRMAT') && contingut.documentTipus == 'DIGITAL'}">
					<c:choose>
						<c:when test="${isTasca}">
							<li><a href="<c:url value="/usuariTasca/${tascaId}/document/${contingut.id}/portafirmes/info"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.portafirmes.info"/></a></li>
						</c:when>
						<c:otherwise>
							<li><a href="<c:url value="/document/${contingut.id}/portafirmes/info"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.portafirmes.info"/></a></li>
						</c:otherwise>
					</c:choose>
					
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<%---- Seguiment via firma ----%>
				<c:if test="${contingut.estat == 'FIRMA_PENDENT_VIAFIRMA' && contingut.documentTipus == 'DIGITAL'}">
					<li><a href="<c:url value="/document/${contingut.id}/viafirma/info"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.viafirma.info"/></a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				<c:if test="${contingut.document && (contingut.estat == 'REDACCIO' || contingut.estat == 'FIRMA_PARCIAL') && contingut.documentTipus == 'DIGITAL' && convertirDefinitiu}">	
					<c:set var="definitiuConfirmacioMsg"><spring:message code="contingut.confirmacio.definitiu"/></c:set>
					<li role="separator" class="divider"></li>			
					<li><a href="<c:url value="/document/${contingut.id}/convertir"/>" data-refresh-pagina="true" data-confirm="${definitiuConfirmacioMsg}"><span class="fa fa-check-square"></span>&nbsp;<spring:message code="contingut.boto.definitiu"/></a></li>
				</c:if>
			</c:if>
		</c:if>
		<%---- Hist�ric d'accions ----%>
		<c:if test="${!isTasca}">
			<c:if test="${mostrarSeparador}">
				<c:set var="mostrarSeparador" value="${false}"/>
				<li role="separator" class="divider"></li>
			</c:if>		
			<li class="${contingut.document && contingut.gesDocAdjuntId!=null ? 'disabled' : ''}"><a href="<c:url value="/contingut/${contingut.id}/log"/>" data-toggle="modal"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.historial"/></a></li>
		</c:if>
		<c:if test="${(contingut.expedient or contingut.document) and !isTasca}">
			<c:if test="${contingut.expedient}">
				<c:choose>
					<c:when test="${contingut.hasFills}">
						<%---- Exportar índex PDF... ----%>
						<li><a class="fileDownload" href="<c:url value="/expedient/${contingut.id}/generarIndex"/>"><span class="fa fa-list-ol"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.index.pdf"/>...</a></li>
						<%---- Índex PDF i exportació ENI... ----%>
						<c:choose>
							<c:when test="${contingut.conteDocumentsDefinitius}">
								<li><a class="fileDownload" href="<c:url value="/expedient/${contingut.id}/generarExportarIndex"/>"><span class="fa fa-list-ol"></span>&nbsp;<span class="fa fa-file-code-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportar.index"/>...</a></li>
			 				</c:when>
			 				<c:otherwise>
			 					<li><a class="disabled" href="#"><span class="fa fa-list-ol"></span>&nbsp;<span class="fa fa-file-code-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportar.index"/>...</a></li>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<li class="disabled"><a href="#"/><span class="fa fa-list-ol"></span>&nbsp;<spring:message code="comu.boto.index"/>...</a></li>
					</c:otherwise>
				</c:choose>	
			</c:if>
			<li><a href="<c:url value="/contingut/${contingut.id}/arxiu"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
			
			<%---- Exportació ENI ----%>
			<c:if test="${contingut.document && contingut.fitxerExtension!='zip'}">
				<c:choose>
					<c:when test="${contingut.conteDocumentsDefinitius}">
						<c:choose>
							<c:when test="${!empty nodeco}">
								<c:set var="exportarUrl"><c:url value="/nodeco/contingut/${contingut.id}/exportar"/></c:set>	
							</c:when>
							<c:otherwise>
								<c:set var="exportarUrl"><c:url value="/contingut/${contingut.id}/exportar"/></c:set>
							</c:otherwise>
						</c:choose>	
						<li><a href="${exportarUrl}"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.exportar.eni"/></a></li>
					</c:when>
					<c:otherwise>
						<li class="disabled"><a href="#"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.exportar.eni"/></a></li>
					</c:otherwise>
				</c:choose>		
			</c:if>		
		</c:if>
	</ul>
</div>
