<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ attribute name="contingut" required="true" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="mostrarExpedients" required="true" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="mostrarNoExpedients" required="true" rtexprvalue="true" type="java.lang.Boolean"%>
<%@ attribute name="nodeco" required="false" rtexprvalue="true"%>
<c:choose>
	<c:when test="${mostrarExpedients and mostrarNoExpedients}"><c:set var="fills" value="${contingut.fillsNoRegistres}"/></c:when>
	<c:when test="${mostrarExpedients and not mostrarNoExpedients}"><c:set var="fills" value="${contingut.fillsExpedients}"/></c:when>
	<c:when test="${not mostrarExpedients and mostrarNoExpedients}"><c:set var="fills" value="${contingut.fillsNoExpedients}"/></c:when>
</c:choose>
<c:choose>
	<c:when test="${vistaIcones}">
		<%--------------------- GRID -------------------%>
		<ul id="contenidor-contingut" class="list-inline row">
			<c:forEach var="fill" items="${fills}">
			<%--  && fill.documentTipus != 'VIRTUAL' --%>
				<c:if test="${fill.carpeta or (fill.document && fill.documentTipus != 'VIRTUAL') or empty fill.metaNode or fill.metaNode.usuariActualRead}">
					<li class="col-md-2 element-contingut element-draggable<c:if test="${not fill.document}"> element-droppable</c:if>" data-contenidor-id="${fill.id}">
						<div id="${fill.id}" class="thumbnail element-noclick">
							<div class="text-center">
								<rip:blocIconaContingut contingut="${fill}" tamanyDoble="true"/> 
							</div>
							<div class="caption">
								<p class="text-center">
									<c:if test="${fill.document && fill.documentTipus != 'IMPORTAT' && fill.estat == 'REDACCIO'}"><span class="icona-esborrany fa fa-bold" title="<spring:message code="contingut.info.estat.redaccio"/>"></span></c:if>
									<c:if test="${fill.document && fill.documentTipus == 'IMPORTAT'}"><span class="importat fa fa-info-circle" title="<spring:message code="contingut.info.estat.importat"/>"></span></c:if>
									<c:if test="${fill.node and not fill.valid}"><span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.icona.estat.invalid"/>"></span></c:if>
									<c:if test="${fill.document && (fill.estat == 'CUSTODIAT' || (fill.estat == 'FIRMAT' && fill.errorEnviamentPortafirmes && fill.gesDocFirmatId != null) || fill.estat == 'ADJUNT_FIRMAT')}"><span class="firmat fa fa-pencil-square" title="<spring:message code="contingut.info.estat.firmat"/>"></span></c:if>
									<c:if test="${fill.document && fill.estat == 'FIRMAT' && fill.gesDocFirmatId != null}"><span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentCustodiar"/>"></span></c:if>
									<c:if test="${fill.document && (fill.estat == 'REDACCIO' || fill.estat == 'ADJUNT_FIRMAT') && fill.gesDocAdjuntId != null}"><span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span></c:if>
									<c:if test="${fill.expedient && fill.estat == 'TANCAT'}"><span class="fa fa-check-square text-success" title="<spring:message code="contingut.info.estat.tancat"/>"></span></c:if>
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
										<span class="parcial fa fa-pencil-square" title="<spring:message code="contingut.info.estat.pendentfirma"/>"></span>
									</c:if>
									<c:if test="${fill.document && fill.estat != 'CUSTODIAT' && fill.estat != 'REDACCIO' && fill.errorEnviamentPortafirmes && fill.gesDocFirmatId == null}">
										<span class="error fa fa-pencil-square" title="<spring:message code="contingut.info.estat.pendentfirma"/>"></span>
									</c:if>
									<c:if test="${not fill.carpeta && fill.metaNode == null}">
										<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.info.document.tipusdocument"/>"></span>
									</c:if>
									${fill.nom}
								</p>
								<rip:blocContingutAccions id="accions-fill-${fill.id}" className="botons-accions-element" modeLlistat="false" contingut="${fill}" nodeco="${nodeco}"/>
							</div>
						</div>
						<script>
							$('#${fill.id}').click(function(e) {
								var contenidorContingut = document.getElementById('contenidor-contingut');
								
								if ($(this).hasClass('noclick')) {
									$(this).removeClass('noclick');
								} else {
									if ($('#accions-fill-${fill.id}').has(e.target).length == 0) {
										$('#${fill.id}').tooltip('destroy');
										if ($(contenidorContingut).hasClass('multiple') && ${fill.document}) {
											var index = docsIdx.indexOf(${fill.id});
											var multipleUrl;
											
											if (index > -1) {
												docsIdx.splice(index, 1);
												$(this).removeClass('selectd');
												var multipleUrl = '<c:url value="/contingut/${contingut.id}/deselect"/>';
												$.get(
														multipleUrl, 
														{docsIdx: docsIdx},
														function(data) {
															$(".seleccioCount").html(data);
														}
												);
											} else {
												var multipleUrl = '<c:url value="/contingut/${contingut.id}/select"/>';
												$(this).addClass('selectd');
												docsIdx.push(${fill.id});
												$.get(
														multipleUrl, 
														{docsIdx: docsIdx},
														function(data) {
															$(".seleccioCount").html(data);
														}
												);
											}
											enableDisableButton();
										} else {
											window.location = $('#${fill.id} a:first').attr('href');
										}
									}
								}
							});
							$('#${fill.id} li a').click(function(e) {
								e.stopPropagation();
							});
						</script>
					</li>
				</c:if>
			</c:forEach>
		</ul>
	<c:if test="${empty fills}">
		<h1 style="opacity: .1; text-align: center; margin-bottom: 1em;"><rip:blocIconaContingut contingut="${fill}" tamanyEnorme="false"/><strong><spring:message code="contingut.sense.contingut"/></strong></h1>				
		<!--<h3 style="opacity: .2; text-align: center; margin-bottom: 3em"><strong><spring:message code="contingut.sense.contingut"/></strong></h3>  -->
	</c:if>
	</c:when>
	<c:when test="${vistaLlistat and fn:length(fills) > 0}">
		<%--------------------- TABLE -------------------%>
		<table class="table table-striped table-bordered table-hover" id="table-documents">
			<thead>
				<tr>
					<th><input type="checkbox" id="checkItAll" autocomplete="off"/></th>
					<th><spring:message code="contingut.info.nom"/></th>
					<th><spring:message code="contingut.info.descirpcio"/></th>
					<th><spring:message code="contingut.info.tipus"/></th>
					<th><spring:message code="contingut.info.createl"/></th>
					<th><spring:message code="contingut.info.creatper"/></th>
					<th width="10%">&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="fill" items="${fills}">
				<c:if test="${fill.carpeta or (fill.document && fill.documentTipus != 'VIRTUAL') or empty fill.metaNode or fill.metaNode.usuariActualRead}">
					<tr id="info-fill-${fill.id}" class="element-drag-drop element-draggable ui-draggable <c:if test="${not fill.document}"> element-droppable</c:if>" data-contenidor-id="${fill.id}">
						
						<td>
						<c:if test="${fill.document}">
						<input type="checkbox" class="info-fill-${fill.id}" id="${fill.id}" autocomplete="off"/>
						</c:if>
						</td>
						<td>
							<rip:blocIconaContingut contingut="${fill}"/>
							<c:if test="${fill.document && fill.documentTipus != 'IMPORTAT' && fill.estat == 'REDACCIO'}"><span class="icona-esborrany fa fa-bold" title="<spring:message code="contingut.info.estat.redaccio"/>"></span></c:if>
							<c:if test="${fill.document && fill.documentTipus == 'IMPORTAT'}"><span class="importat fa fa-info-circle" title="<spring:message code="contingut.info.estat.importat"/>"></span></c:if>
							<c:if test="${fill.node and not fill.valid}">&nbsp;<span class="fa fa-exclamation-triangle text-warning"></span></c:if>
							<c:if test="${fill.document && (fill.estat == 'CUSTODIAT' || (fill.estat == 'FIRMAT' && fill.errorEnviamentPortafirmes && fill.gesDocFirmatId != null) || fill.estat == 'ADJUNT_FIRMAT')}"><span class="firmat fa fa-pencil-square" title="<spring:message code="contingut.info.estat.firmat"/>"></span></c:if>
							<c:if test="${fill.document && fill.estat == 'FIRMAT' && fill.gesDocFirmatId != null}"><span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentCustodiar"/>"></span></c:if>
							<c:if test="${fill.document && (fill.estat == 'REDACCIO' || fill.estat == 'ADJUNT_FIRMAT') && fill.gesDocAdjuntId != null}"><span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span></c:if>
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
						</td>
						<td>
						<c:if test="${fill.document}">
							&nbsp;${fill.descripcio}
						</c:if>
						</td>
						<td width="25%">
							<c:choose>
								<c:when test="${not fill.carpeta && fill.metaNode != null}">
									${fill.metaNode.nom}
								</c:when>
								<c:when test="${not fill.carpeta && fill.metaNode == null}">
									<div id="botons-errors-validacio" class="alert well-sm alert-warning alert-dismissable col-md-12 hidden">
										<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.info.document.tipusdocument"/>"></span>
										<spring:message code="contingut.info.document.tipusdocument"/>
									</div>
									<select id="${fill.id}" class="select-tipus-document">
										<option value=""><spring:message code="contingut.document.form.camp.nti.cap"/></option>
										<c:forEach items="${metaDocumentsLeft}" var="metaDocument">
											<option id="${metaDocument.id}">
												${metaDocument.nom}
											</option>
										</c:forEach>
									</select>
								</c:when>
							</c:choose>
						</td>
						<td><fmt:formatDate value="${fill.createdDate}" pattern="dd/MM/yyyy HH:mm"/></td>
						<td>${fill.createdBy.nom}</td>
						<td>
							<rip:blocContingutAccions className="botons-accions-element" modeLlistat="true" contingut="${fill}"  nodeco="${nodeco}"/>
						</td>
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
				</c:forEach>
			</tbody>
		</table>
	</c:when>
</c:choose>

<c:if test="${vistaLlistat and empty fills}">
	<%--------------------- TABLE -------------------%>
	<table class="table table-striped table-bordered table-hover" id="table-documents">
		<thead>
			<tr>
				<th></th>
				<th><spring:message code="contingut.info.nom"/></th>
				<th><spring:message code="contingut.info.tipus"/></th>
				<th><spring:message code="contingut.info.createl"/></th>
				<th><spring:message code="contingut.info.creatper"/></th>
				<th width="10%">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<tr class="odd">
					<td colspan="9" valign="top">
						<h1 style="opacity: .1; text-align: center;"><rip:blocIconaContingut contingut="${fill}" tamanyEnorme="false"/><strong><spring:message code="contingut.sense.contingut"/></strong></h1>
					</td>
				</tr>
			</tbody>
		</table>
		<!--<h3 style="opacity: .2; text-align: center; margin-bottom: 3em"><strong><spring:message code="contingut.sense.contingut"/></strong></h3>  -->
</c:if>
