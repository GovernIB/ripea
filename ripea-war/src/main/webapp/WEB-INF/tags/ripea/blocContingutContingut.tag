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
		<%--              --%>
		<%-- Vista icones --%>
		<%--              --%>
		<ul id="contenidor-contingut" class="list-inline row">
			<c:forEach var="fill" items="${fills}">
				<c:if test="${fill.carpeta or fill.document or empty fill.metaNode or fill.metaNode.usuariActualRead}">
					<li class="col-md-2 element-contingut element-draggable<c:if test="${not fill.document}"> element-droppable</c:if>" data-contenidor-id="${fill.id}">
						<div id="info-fill-${fill.id}" class="thumbnail element-noclick">
							<div class="text-center">
								<rip:blocIconaContingut contingut="${fill}" tamanyDoble="true"/> 
							</div>
							<div class="caption">
								<p class="text-center">
									<c:if test="${fill.node and not fill.valid}"><span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.icona.estat.invalid"/>"></span></c:if>
									<c:if test="${fill.document && fill.estat == 'CUSTODIAT'}"><span class="fa fa-bookmark" title="<spring:message code="contingut.info.estat.firmat"/>"></span></c:if>
									<c:if test="${fill.expedient && fill.estat == 'TANCAT'}"><span class="fa fa-check-square text-success" title="<spring:message code="contingut.info.estat.tancat"/>"></span></c:if>
									${fill.nom}
								</p>
								<rip:blocContingutAccions id="accions-fill-${fill.id}" className="botons-accions-element" modeLlistat="false" contingut="${fill}" nodeco="${nodeco}"/>
							</div>
						</div>
						<script>
							$('#info-fill-${fill.id}').click(function(e) {
								if ($(this).hasClass('noclick')) {
									$(this).removeClass('noclick');
								} else {
									if ($('#accions-fill-${fill.id}').has(e.target).length == 0) {
										$('#info-fill-${fill.id}').tooltip('destroy');
										window.location = $('#info-fill-${fill.id} a:first').attr('href');
									}
								}
							});
							$('#info-fill-${fill.id} li a').click(function(e) {
								e.stopPropagation();
							});
						</script>
					</li>
				</c:if>
			</c:forEach>
		</ul>
		<%--               --%>
		<%-- /Vista icones --%>
		<%--               --%>
	</c:when>
	<c:when test="${vistaLlistat and fn:length(fills) > 0}">
		<%--               --%>
		<%-- Vista llistat --%>
		<%--               --%>
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th><spring:message code="contingut.info.nom"/></th>
					<th><spring:message code="contingut.info.tipus"/></th>
					<th><spring:message code="contingut.info.createl"/></th>
					<th><spring:message code="contingut.info.creatper"/></th>
					<th width="10%">&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="fill" items="${fills}">
					<tr id="info-fill-${fill.id}" class="element-drag-drop">
						<td>
							<rip:blocIconaContingut contingut="${fill}"/>
							<c:if test="${fill.node and not fill.valid}">&nbsp;<span class="fa fa-exclamation-triangle text-warning"></span></c:if>
							&nbsp;${fill.nom}
						</td>
						<td>
							<c:if test="${not fill.carpeta}">${fill.metaNode.nom}</c:if>
						</td>
						<td><fmt:formatDate value="${fill.createdDate}" pattern="dd/MM/yyyy HH:mm"/></td>
						<td>${fill.createdBy.nom}</td>
						<td>
							<rip:blocContingutAccions className="botons-accions-element" modeLlistat="true" contingut="${fill}"  nodeco="${nodeco}"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<%--                --%>
		<%-- /Vista llistat --%>
		<%--                --%>
	</c:when>
</c:choose>
<c:if test="${empty fills}">
	<h1 style="opacity: .1; text-align: center; margin-top: 1em"><rip:blocIconaContingut contingut="${fill}" tamanyEnorme="true"/></h1>
	<h3 style="opacity: .2; text-align: center; margin-bottom: 3em"><strong><spring:message code="contingut.sense.contingut"/></strong></h3>
</c:if>