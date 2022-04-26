<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.ripea.war.helper.RolHelper.isRolActualAdministrador(request));
%>

<script type="text/javascript">
var myHelpers = {hlpIsAdministradorEntitat: isRolActualAdministrador};
$.views.helpers(myHelpers);
function isRolActualAdministrador() {
	return ${isRolActualAdministrador};
}

$(document).ready(function() {

	$('#expedient').on('draw.dt', function () {
		$("span[class^='stateColor-']").each(function( index ) {
		    var fullClassNameString = this.className;
		    var colorString = fullClassNameString.substring(11);
			if (colorString != "") {
				$(this).parent().css( "background-color", colorString );
				if (adaptColor(colorString)) {
					$(this).parent().css( "color", "white" );
				}
				$(this).parent().parent().css( "box-shadow", "-6px 0 0 " + colorString );
			}
		});
	});

	
	$( document ).ajaxComplete(function() {
		webutilModalAdjustHeight();
	});
	
});



function adaptColor(hexColor) {
	let adapt = false;

	let rgb = hexToRgb(hexColor);
	if (rgb != null) {
		var hsp = Math.sqrt(
				0.299 * (rgb.r * rgb.r) +
				0.587 * (rgb.g * rgb.g) +
				0.114 * (rgb.b * rgb.b)
		);
	}
	if (hsp < 127.5) {
		adapt = true;
	}
	return adapt;
}

function hexToRgb(hex) {
	// Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
	var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
	hex = hex.replace(shorthandRegex, function(m, r, g, b) {
		return r + r + g + g + b + b;
	});

	var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
	return result ? {
		r: parseInt(result[1], 16),
		g: parseInt(result[2], 16),
		b: parseInt(result[3], 16)
	} : null;
}

</script>

<%--
<c:if test="${not simplifiedView}">
<form:form id="filtre" action="" method="post" cssClass="well" commandName="expedientFiltreCommand">
	<div class="row">
		<div class="col-md-2">
			<rip:inputText name="numero" inline="true" placeholderKey="expedient.list.user.columna.numero"/>
		</div>
		<div class="col-md-3">
			<rip:inputText name="titol" inline="true" placeholderKey="expedient.list.user.columna.titol"/>
		</div>
		<div class="col-md-2 pull-right">
			<div class="pull-right">
				<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>
		</div>
	</div>

</form:form>
</c:if>
--%>
<table
	id="expedient"
	data-toggle="datatable"
	data-url="<c:url value="/expedient/metaExpedient/${metaExpedient.id}/datatable"/>"
	data-search-enabled="false"
	data-default-order="11"
	data-default-dir="desc"
	class="table table-striped table-bordered"
	data-botons-template="#botonsTemplate"
	data-save-state="true"
	data-mantenir-paginacio="true"
	style="width:100%"
	data-filter="#filtre">
	<thead>
		<tr> 
			<th data-col-name="numero" data-template="#cellExpedientLink"><spring:message code="expedient.list.user.columna.numero"/>
				<script id="cellExpedientLink" type="text/x-jsrender">
					<a href="<c:url value="/contingut/{{:id}}"/>" target="_blank">{{:numero}}</a>	
				</script>
			</th>
			<th data-col-name="nom"><spring:message code="expedient.list.user.columna.titol"/></th>
			<th data-col-name="alerta" data-visible="false"></th>
			<th data-col-name="valid" data-visible="false"></th>
			<th data-col-name="errorLastEnviament" data-visible="false"></th>
			<th data-col-name="errorLastNotificacio" data-visible="false"></th>
			<th data-col-name="expedientEstat" data-visible="false"></th>
			<th data-col-name="ambEnviamentsPendents" data-visible="false"></th>
			<th data-col-name="ambNotificacionsPendents" data-visible="false"></th>
			<th data-col-name="arxiuUuid" data-visible="false"></th>	
			<th data-col-name="id" data-template="#cellAvisosTemplate">
					<spring:message code="expedient.list.user.columna.avisos"/>
					<script id="cellAvisosTemplate" type="text/x-jsrender">
						{{if !valid}}<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.expedient.validacio"/>"></span>{{/if}}
						{{if errorLastEnviament }}<span class="fa fa-pencil-square text-danger" title="<spring:message code="contingut.errors.expedient.enviaments"/>"></span>{{/if}}
						{{if errorLastNotificacio }}<span class="fa fa-envelope-square text-danger" title="<spring:message code="contingut.errors.expedient.notificacions"/>"></span>{{/if}}
						{{if ambEnviamentsPendents }}<span class="fa fa-pencil-square text-primary" title="<spring:message code="contingut.pendents.expedient.enviaments"/>"></span>{{/if}}
						{{if ambNotificacionsPendents }}<span class="fa fa-envelope-square text-primary" title="<spring:message code="contingut.pendents.expedient.notificacions"/>"></span>{{/if}}
						{{if alerta}}<span class="fa fa-exclamation-circle text-danger" title="<spring:message code="contingut.errors.expedient.alertes"/>"></span>{{/if}}			
						{{if arxiuUuid == null}}
							<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>
						{{/if}}
					</script>
			</th>
			<th data-col-name="createdDate" data-type="datetime" data-converter="datetime"><spring:message code="expedient.list.user.columna.createl"/></th>
			<th data-col-name="id" data-visible="false"></th>
			<th data-col-name="estat" data-template="#cellEstatTemplate" width="11%">
				<spring:message code="expedient.list.user.columna.estat"/>
				<script id="cellEstatTemplate" type="text/x-jsrender">
					{{if expedientEstat != null && estat != 'TANCAT'}}
						<span class="fa fa-folder-open"></span>&nbsp;{{:expedientEstat.nom}}
					{{else}}
						{{if estat == 'OBERT'}}
							<span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.estat.enum.OBERT"/>
						{{else}}
							<span class="fa fa-folder"></span>&nbsp;<spring:message code="expedient.estat.enum.TANCAT"/>
						{{/if}}
					{{/if}}				
					{{if expedientEstat != null && expedientEstat.color!=null}}
						<span class="stateColor-{{:expedientEstat.color}}"></span>
					{{/if}}
				</script>
			</th>
			<th data-col-name="agafatPer.codiAndNom" data-orderable="false"><spring:message code="expedient.list.user.columna.agafatper"/></th>

		</tr>
	</thead>
</table>

<script id="botonsTemplate" type="text/x-jsrender">
<c:if test="${not simplifiedView}">
	<p style="text-align:right">
		<c:if test="${isRolActualAdministrador}">
			<a id="procediment-boto-cache" class="btn btn-warning" href="${unitatCodiUrlPrefix}procediment/cache/refrescar"><span class="fa fa-trash"></span>&nbsp;<spring:message code="metaexpedient.form.camp.comu"/></a>
			<c:if test="${!isCodiDir3Entitat}">
				<a id="procediment-boto-update"
					class="btn btn-default" href="${unitatCodiUrlPrefix}servei/update/auto"
					data-toggle="modal"
					data-maximized="false">
					<span class="fa fa-refresh"></span>&nbsp;<spring:message code="metaexpedient.form.camp.comu"/>
				</a>
			</c:if>
		</c:if>
		<a id="procediment-boto-nou" class="btn btn-default" href="${unitatCodiUrlPrefix}servei/new" data-toggle="modal" data-maximized="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.form.camp.comu"/></a>
	</p>
</c:if>
</script>