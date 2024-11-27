<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ attribute name="id" required="false" rtexprvalue="true"%>
<%@ attribute name="className" required="false" rtexprvalue="true"%>
<%@ attribute name="contingutNavigationId" required="false" rtexprvalue="true"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="modeLlistat" required="true" rtexprvalue="true"%>
<%@ attribute name="mostrarObrir" required="false" rtexprvalue="true"%>
<%@ attribute name="nodeco" required="false" rtexprvalue="true"%>

<c:set var="mostrarSeparador" value="${false}"/>
<c:set var="isTasca" value="${not empty tascaId}"/>
<div <c:if test="${not empty id}">id="${id}" </c:if>class="dropdown<c:if test="${not modeLlistat}"> text-center</c:if><c:if test="${not empty className}"> ${className}</c:if>">

	<button data-toggle="dropdown" class="btn btn-primary dropdown-toggle<c:if test="${not modeLlistat}"> btn-xs</c:if>"><span class="fa fa-cog"></span><c:if test="${modeLlistat}">&nbsp;<spring:message code="comu.boto.accions"/></c:if>&nbsp;<span class="caret caret-white"></span></button>
	<ul class="dropdown-menu">
	
		<c:if test="${contingut.document}">
			<%---- Visualitzar/Descarregar when clicking row ----%>
			<c:choose>
				<c:when test="${(contingut.fitxerExtension!='pdf' && contingut.fitxerExtension!='odt' && contingut.fitxerExtension!='docx')}">
					<li class="hidden"><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregar?tascaId=${tascaId}"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
				</c:when>
				<c:otherwise>
					<li class="hidden"><a href="#" data-nom="${fn:escapeXml(contingut.nom)}" onclick="showViewer(event, ${contingut.id}, this.getAttribute('data-nom'), ${contingut.custodiat})"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
				</c:otherwise>
			</c:choose>							
		</c:if>
		
		<%---- Guardar en arxiu ----%>
		<c:choose>
			<c:when test="${contingut.document && contingut.arxiuUuid==null}">
				<c:set var="primerGuardarExpedientArxiu"><spring:message code="disabled.button.primerGuardarExpedientArxiu"/></c:set>
				<li class="disabledMsg" title="${expedient.arxiuUuid == null ? primerGuardarExpedientArxiu : ''}"><a class="${expedient.arxiuUuid == null ? 'disabled' : ''}" href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/guardarDocumentArxiu?origin=docDetail&tascaId=${tascaId}"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
			</c:when>
			<c:when test="${contingut.document && contingut.gesDocFirmatId != null}">
				<li><a href="<c:url value="/document/${contingut.id}/portafirmes/reintentarGuardarArxiu?tascaId=${tascaId}"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
			</c:when>			
<%--			<c:when test="${contingut.expedient && contingut.arxiuUuid==null}">--%>
<%--				<li><a href="<c:url value="/expedient/${contingut.id}/guardarExpedientArxiu?origin=expDetail"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>--%>
<%--			</c:when>--%>
		</c:choose>
		<c:if test="${(empty mostrarObrir or mostrarObrir)}">
			<c:choose>
				<c:when test="${contingut.carpeta}">
					<li><a href="${(not empty contingut.expedientRelacionat) ? contingut.expedientRelacionat.id : contingut.id}"><span class="fa fa-folder-open-o"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
				</c:when>
				<c:otherwise>
					<c:if test="${!isTasca}">
						<%---- Consultar ----%>
						<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/contingut/${contingut.id}"/>" data-toggle="modal" ><span class="fa fa-folder-open-o"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
					</c:if>
				</c:otherwise>
			</c:choose>
			
			<c:set var="mostrarSeparador" value="${true}"/>
		</c:if>
		
		<%---- Assignar... ----%>
		<c:if test="${contingut.expedient && isEntitatUserAdminOrOrgan}">
			<li><a href="<c:url value="/expedient/${contingut.id}/assignar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-user"></span>&nbsp;<spring:message code="comu.boto.assignar"/></a></li>
		</c:if>
		
		<c:if test="${potModificar}">
			<%---- Modificar... ----%>
			<c:set var="isPermesModificarCustodiatsVar" value="${isPermesModificarCustodiats && contingut.document && (contingut.estat == 'CUSTODIAT' || contingut.estat == 'FIRMAT' || contingut.estat == 'FIRMA_PARCIAL' || contingut.estat == 'DEFINITIU')}"/>
			<c:choose>
				<%--- Expedient ---%>
				<c:when test="${contingut.expedient && contingut.estat == 'OBERT'}">
					<li><a href="<c:url value="/expedient/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:when>
				<%--- Document ---%>
				<c:when test="${contingut.document && expedientObert}">
					<c:choose>
						<c:when test="${contingut.estat != 'FIRMA_PENDENT' && (!contingut.arxiuEstatDefinitiu || isPermesModificarCustodiatsVar)}">
							<li class="${(contingut.document && (contingut.arxiuUuid==null || contingut.gesDocFirmatId != null)) ? 'disabled' : ''}"><a href="<c:url value="/contingut/${contingut.pare.id}/document/modificar/${contingut.id}/?tascaId=${tascaId}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
							<c:set var="mostrarSeparador" value="${true}"/>
						</c:when>
						<c:when test="${empty contingut.metaNode and contingut.arxiuEstatDefinitiu and empty tascaId}">
							<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/modificarTipus/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar.tipus"/>...</a></li>
							<c:set var="mostrarSeparador" value="${true}"/>
						</c:when>
					</c:choose>
				</c:when>
				<%--- Carpeta ---%>
				<c:when test="${contingut.carpeta && isCreacioCarpetesActiva}">
					<li><a href="<c:url value="/contingut/${contingut.pare.id}/carpeta/${contingut.id}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
					<li><a href="<c:url value="/contingut/carpeta/${contingut.id}/generarIndex/PDF"/>"><span class="fa fa-list-ol"></span>&nbsp;<spring:message code="carpeta.list.user.recuperar.index.pdf"/>...</a></li>
					<c:set var="mostrarSeparador" value="${true}"/>
					<c:if test="${isExportacioExcelActiva}">
						<li><a href="<c:url value="/contingut/carpeta/${contingut.id}/generarIndex/XLSX"/>"><span class="fa fa-th-list"></span>&nbsp;<spring:message code="carpeta.list.user.recuperar.index.xlsx"/>...</a></li>
						<c:set var="mostrarSeparador" value="${true}"/>
					</c:if>
				</c:when>
			</c:choose>
			<c:if test="${contingut.document and !isTasca or (contingut.carpeta && isCreacioCarpetesActiva)}">
				
				<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/contingut/${contingut.id}/moure"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-arrows"></span>&nbsp;<spring:message code="comu.boto.moure"/>...</a></li>
				<c:if test="${isMostrarCopiar}">
					<li><a href="<c:url value="/contingut/${contingut.id}/copiar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-copy"></span>&nbsp;<spring:message code="comu.boto.copiar"/>...</a></li>
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
		<%---- Agafar/Alliberar ... ----%>
		<c:if test="${contingut.expedient && expedientAgafatPerUsuariActual}">
			<li><a href="<c:url value="/expedient/${contingut.id}/alliberar"/>"><span class="fa fa-unlock"></span>&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
		</c:if>
		<c:if test="${contingut.expedient && not expedientAgafatPerUsuariActual}">
			<li><a href="<c:url value="/expedient/${contingut.id}/agafar"/>"><span class="fa fa-lock"></span>&nbsp;<spring:message code="comu.boto.agafar"/></a></li>
		</c:if>
		<c:if test="${(potModificar) || (contingut.carpeta && isCreacioCarpetesActiva)}">
			<c:if test="${contingut.expedient and !isTasca}">
				<%---- Canviar prioritat... ----%>
<%-- 				<li><a href="<c:url value="/expedient/${contingut.id}/canviarPrioritat"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.canviarPrioritat"/>...</a></li> --%>
				<%---- Canviar estat... ----%>
				<c:if test="${contingut.estat == 'OBERT'}">
					<li><a href="<c:url value="/expedient/${contingut.id}/canviarEstat"/>" data-toggle="modal" data-refresh-pagina="true" data-maximized="true"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.canviarEstat"/>...</a></li>
				</c:if>
				<%---- Relacionar... ----%>
				<li><a href="<c:url value="/expedient/${contingut.id}/relacionarList"/>" data-toggle="modal" data-refresh-pagina="true" data-maximized="true"><span class="fa fa-link"></span>&nbsp;<spring:message code="comu.boto.relacionar"/>...</a></li>
				<%---- Tancar... ----%>
				<c:if test="${contingut.estat == 'OBERT'}">
						<c:choose>
							<c:when test="${contingut.potTancar}">
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
									<c:when test="${contingut.conteDocumentsDePortafirmesNoCustodiats}">
										<li class="disabledMsg" title="<spring:message code="disabled.button.msg.conteDocumentsDePortafirmesNoCustodiats"/>"><a class="disabled"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:when>									
									<c:when test="${contingut.conteDocumentsPendentsReintentsArxiu}">
										<li class="disabledMsg" title="<spring:message code="disabled.button.msg.conteDocumentsPendentsReintentsArxiu"/>"><a class="disabled"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:when>		
									<c:when test="${contingut.conteDocumentsDeAnotacionesNoMogutsASerieFinal}">
										<li class="disabledMsg" title="<spring:message code="disabled.button.msg.conteDocumentsDeAnotacionesNoMogutsASerieFinal"/>"><a class="disabled"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:when>																
									<c:otherwise>
										<li class="disabled"><a href="#"/><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/>...</a></li>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
				</c:if>
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
			
			<c:set var="permDeleteExpedient">${contingut.expedient && !contingut.conteDocumentsDefinitius && expedient.usuariActualDelete}</c:set>
			<c:set var="permDeleteCarpeta">${contingut.carpeta && isCreacioCarpetesActiva && expedient.usuariActualWrite}</c:set>
			<c:set var="permDeleteDocument">${(contingut.document && !contingut.documentDeAnotacio && (!contingut.arxiuEstatDefinitiu || (contingut.arxiuEstatDefinitiu && isPermesEsborrarFinals))) && (expedient.usuariActualWrite || isTasca)}</c:set>

			<c:if test="${(permDeleteExpedient || permDeleteCarpeta  || permDeleteDocument) && expedientObert}">
				<li><a href="<c:url value="/contingut/${contingut.id}/delete?contingutNavigationId=${contingutNavigationId}&tascaId=${tascaId}"/>" data-confirm="${esborrarConfirmacioMsg}"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
			</c:if>
			<c:set var="mostrarSeparador" value="${true}"/>
		</c:if>
		<c:if test="${isReobrirPermes && contingut.expedient && expedientTancat && (!isTancamentLogicActiu || (isTancamentLogicActiu && empty contingut.tancatData))}">
			<li><a href="#" onclick="confirmarReobrir();"><span class="fa fa-undo"></span>&nbsp;<spring:message code="comu.boto.reobrir"/>...</a></li>
		</c:if>
		<c:if test="${contingut.document}">
			<c:if test="${mostrarSeparador}">
				<c:set var="mostrarSeparador" value="${false}"/>
				<li role="separator" class="divider"></li>
			</c:if>
			<c:if test="${contingut.documentTipus == 'DIGITAL' or contingut.documentTipus == 'IMPORTAT'}">
				<c:if test="${!isTasca and ((contingut.arxiuEstatDefinitiu or contingut.firmaParcial) or (imprimibleNoFirmats and contingut.pdf))}">
					<c:choose>
						<c:when test="${contingut.fitxerExtension!='xsig'}">
							<li>
							<a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregarImprimible"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregarImprimible"/></a></li>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${contingut.fitxerExtension=='xsig'}">
									<li class="disabledMsg" title="<spring:message code="contingut.document.descarregar.imprimible.desactivat.msg.xml"/>"><a class="disabled"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregarImprimible"/>...</a></li>
								</c:when>																								
								<c:otherwise>
									<li class="disabled"><a href="#"/><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregarImprimible"/>...</a></li>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>			
				</c:if>
				<%---- Descarregar ----%>
				<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregar?tascaId=${tascaId}"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar"/></a></li>
				<%---- Descarregar original de distribucio ----%>
				<c:if test="${contingut.gesDocOriginalId!=null}">
				<li><a href="<c:url value="/contingut/${contingut.pare.id}/document/${contingut.id}/descarregarOriginal?tascaId=${tascaId}"/>"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.descarregar.original"/></a></li>
				</c:if>
				<%---- Visualitzar ----%>
				<li class="${(contingut.fitxerExtension!='pdf' && contingut.fitxerExtension!='odt' && contingut.fitxerExtension!='docx') ? 'disabled' : ''}"><a href="#" data-nom="${fn:escapeXml(contingut.nom)}" onclick="showViewer(event, ${contingut.id}, this.getAttribute('data-nom'), ${contingut.custodiat})"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
				<c:if test="${(contingut.custodiat or contingut.estat == 'DEFINITIU') and isUrlValidacioDefinida}">
					<li><a href="#copy_${contingut.id}"><span class="fa fa-copy"></span>&nbsp;<spring:message code="comu.boto.urlValidacio"/></a></li>
					<script>
					$('a[href="#copy_${contingut.id}"]').click(function(){

					    $.ajax({
					        url: "../document/" + ${contingut.id} + "/urlValidacio",
					        type: "GET",
					        success: function(data) {
					            var dummy = $('<input>').val(data).appendTo('body').select();
					            document.execCommand("copy");
					            $(dummy).remove();
					            var successDiv = $("<div class='copy alert alert-success' style='font-weight:bold;' role='alert'><spring:message code='comu.boto.urlValidacio.copiat'/></div>");

					            toastr.options = {
									"positionClass": "toast-top-right"
					            };
					            
					            toastr.success("<spring:message code='comu.boto.urlValidacio.copiat'/>");
					        },
					        error: function(xhr, status, error) {
							    var errorDiv = $("<div class='copy alert alert-danger' style='font-weight:bold;' role='alert'><spring:message code='comu.boto.urlValidacio.error'/> " + error + "</div>");

					        	var dummy = $('<input>').val("<spring:message code='comu.boto.urlValidacio.error'/>" + " " + error).appendTo('body').select();
					            document.execCommand("copy");
							    $(dummy).remove();
							    
							    toastr.options = {
									"positionClass": "toast-top-right"
					            };
					            
							    toastr.error("<spring:message code='comu.boto.urlValidacio.error'/>");
					        }
					    });
					});
					</script>
				</c:if>		
				<c:set var="mostrarSeparador" value="${true}"/>
			</c:if>
			
			<%--------------- FIRMA RELATED ACCIONS -------------------%>
			<c:if test="${potModificar}">
			
				<%---- Enviar a portafirmes ----%>
				<c:if test="${contingut.metaNode.firmaPortafirmesActiva && (contingut.estat == 'REDACCIO' || contingut.estat == 'FIRMA_PARCIAL') && (contingut.documentTipus == 'DIGITAL' || contingut.documentTipus == 'IMPORTAT') && contingut.fitxerExtension!='zip'}">
					<c:choose>
						<c:when test="${contingut.valid}">
							<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/document/${contingut.id}/portafirmes/upload?tascaId=${tascaId}"/>" data-toggle="modal" data-height="450px" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;<spring:message code="contingut.boto.portafirmes.enviar"/>...</a></li>
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
							<li class="${(contingut.document && contingut.gesDocAdjuntId!=null) ? 'disabled' : ''}"><a href="<c:url value="/document/${contingut.id}/firmaSimpleWeb?tascaId=${tascaId}"/>" data-toggle="modal" data-refresh-pagina="true" data-refresh-tancar="true"><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
						</c:when>
						<c:otherwise>
							<li class="disabled"><a href="#"/><span class="fa fa-edit"></span>&nbsp;<spring:message code="contingut.boto.firma.passarela"/>...</a></li>
						</c:otherwise>
					</c:choose>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
				
				<%---- Enviar a viaFirma ----%>
				<c:if test="${!isTasca and ((contingut.estat == 'REDACCIO' || contingut.estat == 'FIRMA_PARCIAL') && contingut.metaNode.firmaBiometricaActiva && (contingut.documentTipus == 'DIGITAL' || contingut.documentTipus == 'IMPORTAT') && isFirmaBiometrica) && contingut.fitxerExtension!='zip'}">
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
				<c:if test="${(contingut.documentFirmaTipus != 'SENSE_FIRMA' && !empty contingut.arxiuUuid) and !isTasca or contingut.fitxerExtension=='zip'}">
				
					<%---- Notificar ----%>
					<c:choose>
						<c:when test="${expedient.metaExpedient.tipusClassificacio == 'SIA'}">
							<c:set var="notificarMsg"><spring:message code="comu.boto.notificarOComunicar"/></c:set>	
						</c:when>
						<c:otherwise>
							<c:set var="notificarMsg"><spring:message code="comu.boto.comunicar"/></c:set>
						</c:otherwise>
					</c:choose>
					<li>
						<a class="btnNotificar" href="<c:url value="/document/${contingut.id}/notificar"/>" data-missatgeloading="Realitzant enviament..." data-toggle="modal" data-datatable-id="taulaEnviaments" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;${notificarMsg}...</a>
					</li>
					<%--c:choose>
						<c:when test="${!empty expedient.interessatsNotificable}">
							<li>
							<a class="btnNotificar" href="<c:url value="/document/${contingut.id}/notificar"/>" data-missatgeloading="Realitzant enviament..." data-toggle="modal" data-datatable-id="taulaEnviaments" data-maximized="true" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;${notificarMsg}...</a>
							</li>
						</c:when>
						<c:otherwise>
							<li class="disabled"><a class="btnNotificar" href="#" data-missatgeloading="Realitzant enviament..." data-toggle="modal" data-datatable-id="taulaEnviaments" data-maximized="true" data-refresh-pagina="true"><span class="fa fa-envelope-o"></span>&nbsp;${notificarMsg}...</a></li>
							<p style="font-size: 9px;padding: 1px 15px;">&nbsp;<spring:message code="comu.boto.notificar.comentari"/></p>
						</c:otherwise>
					</c:choose-->
					
					<%---- Publicar ----%>
					<c:if test="${isMostrarPublicar}">
						<li><a href="<c:url value="/document/${contingut.id}/publicar"/>" data-toggle="modal" data-datatable-id="taulaEnviaments"><span class="fa fa-clipboard"></span>&nbsp;<spring:message code="comu.boto.publicar"/>...</a></li>
					</c:if>
					<c:set var="mostrarSeparador" value="${true}"/>
				</c:if>
			</c:if>
			<c:if test="${potModificar}">
				<%---- Seguiment portafirmes ----%>
				<c:if test="${contingut.estat == 'FIRMA_PENDENT' && contingut.documentTipus == 'DIGITAL'}">
					<li><a href="<c:url value="/document/${contingut.id}/portafirmes/info?readOnly=false&tascaId=${tascaId}"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="contingut.boto.firma.portafirmes.info"/></a></li>
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
		<%---- URLs instrucci� ----%>
		<c:if test="${!isTasca && contingut.expedient && isGenerarUrlsInstruccioActiu}">
			<li class="dropdown dropdown-submenu">
				<a data-toggle="dropdown" class="dropdown-toggle" id="urlInstruccio" href="#"><span class="fa fa-copy dropdown-item"></span>&nbsp;<spring:message code="comu.boto.url"/><span class="fa fa-caret-right" style="float: right; line-height: 20px;"></span></a>
				<ul class="dropdown-menu dropdown-submenu-right" aria-labelledby="urlInstruccio"></ul>
			</li>
		</c:if>
		<%---- Hist�ric d'accions ----%>
		<c:if test="${!isTasca}">
			<c:if test="${mostrarSeparador}">
				<c:set var="mostrarSeparador" value="${false}"/>
				<li role="separator" class="divider"></li>
			</c:if>		
			<li class="${contingut.document && contingut.gesDocAdjuntId!=null ? 'disabled' : ''}"><a href="<c:url value="/contingut/${contingut.id}/log"/>" data-toggle="modal"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.historial"/></a></li>
		</c:if>
		
		<c:if test="${contingut.expedient && contingut.conteDocuments}">
			<%---- Descarregar fitxer comprimit ----%>
			<%---- <li><a href="<c:url value="/contingut/${contingut.id}/descarregarAllDocumentsOfExpedient?tascaId=${tascaId}"/>" ><span class="fa fa-download"></span>&nbsp;<spring:message code="expedient.boto.descarregar.fitxer.comprimit"/></a></li> ----%>
			<%---- Descarregar fitxer comprimit mantenint estructura carpetes ----%>
			<%---- <li><a href="<c:url value="/contingut/${contingut.id}/descarregarAllDocumentsOfExpedientEstructurat?tascaId=${tascaId}"/>" ><span class="fa fa-download"></span>&nbsp;<spring:message code="expedient.boto.descarregar.fitxer.comprimit.estructurat"/></a></li> ----%>
			<%---- Descarregar contingut seleccionat modal ----%>
			<li><a href="<c:url value="/contingut/${contingut.id}/descarregarSelectedDocuments?tascaId=${tascaId}"/>" data-toggle="modal"><span class="fa fa-download"></span>&nbsp;<spring:message code="expedient.boto.descarregar.fitxer.comprimit"/>...</a></li>
		</c:if>
		
		<c:if test="${(contingut.expedient or contingut.document) and !isTasca}">
			<c:if test="${contingut.expedient}">
				<c:choose>
					<c:when test="${contingut.hasFills}">
						<%---- Exportar índex PDF... ----%>
						<li><a class="fileDownload" href="<c:url value="/expedient/${contingut.id}/generarIndex/PDF"/>"><span class="fa fa-list-ol"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.index.pdf"/>...</a></li>
						<c:if test="${isExportacioExcelActiva}">
							<li><a class="fileDownload" href="<c:url value="/expedient/${contingut.id}/generarIndex/XLSX"/>"><span class="fa fa-th-list"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.index.xlsx"/>...</a></li>
						</c:if>
						<%---- Índex PDF i exportació ENI... ----%>
						<c:choose>
							<c:when test="${contingut.conteDocumentsDefinitius}">
								<li><a class="fileDownload" href="<c:url value="/expedient/${contingut.id}/generarExportarIndex"/>"><span class="fa fa-list-ol"></span>&nbsp;<span class="fa fa-file-code-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportar.index"/>...</a></li>
			 					<li><a class="fileDownload" href="<c:url value="/expedient/${contingut.id}/exportarEni"/>"><span class="fa fa-file-code-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportacio.eni"/>...</a></li>
			 					<c:if test="${isExportacioInsideActiva}">
			 						<li><a class="fileDownload" href="<c:url value="/expedient/${contingut.id}/exportarEni?ambDocuments=true"/>"><span class="fa fa-file-archive-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportacio.eni.inside"/>...</a></li>
			 					</c:if>
			 				</c:when>
			 				<c:otherwise>
			 					<li><a class="disabled" href="#"><span class="fa fa-list-ol"></span>&nbsp;<span class="fa fa-file-code-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportar.index"/>...</a></li>
								<c:if test="${isExportacioInsideActiva}">
									<li><a class="disabled" href="#"><span class="fa fa-file-archive-o"></span>&nbsp;<spring:message code="expedient.list.user.recuperar.exportacio.eni.inside"/>...</a></li>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<li class="disabled"><a href="#"/><span class="fa fa-list-ol"></span>&nbsp;<spring:message code="comu.boto.index"/>...</a></li>
					</c:otherwise>
				</c:choose>	
			</c:if>
			<c:choose>
				<c:when test="${contingut.arxiuUuid != null}">
					<li><a href="<c:url value="/contingut/${contingut.id}/arxiu"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
 				</c:when>
 				<c:otherwise>
 					<li><a class="disabled" href="#"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.arxiu"/></a></li>
				</c:otherwise>
			</c:choose>
			<c:if test="${contingut.expedient}">
				<li><a href="<c:url value="/contingut/${contingut.id}/sincronitzarAmbArxiu"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="expedient.sincronitzar.estat.arxiu"/></a></li>
			</c:if>
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
