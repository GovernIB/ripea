<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.moure.tot.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
	<form:form action="" class="form-horizontal" modelAttribute="expedientMoureCommand">
		<form:hidden path="expedientOrigenId"/>
		
		<div class="alert well-sm alert-info alert-dismissable">
			<p><spring:message code="contingut.moure.tot.alerta"/></p>
		</div>
				
		<rip:inputFixed textKey="contingut.moure.camp.origen">
			<rip:blocIconaContingut contingut="${expedientOrigen}"/> ${expedientOrigen.nom}
		</rip:inputFixed>
		
		<c:url value="/expedientajax/expedient/permisEscriptura" var="urlExpedientsPermisEscriptura"/>
		<c:set var="expedientDestiErrors"><form:errors path="expedientDestiId"/></c:set>
		<div class="form-group<c:if test="${not empty expedientDestiErrors}"> has-error</c:if>">
			<label class="control-label col-xs-4" for="expedientDestiId"><spring:message code="contingut.moure.camp.expedient.desti"/> *</label>
			<div class="col-xs-8">
				<form:select path="expedientDestiId" id="expedientDestiId" cssClass="form-control" style="width:100%">
					<option value=""><spring:message code="contingut.document.form.camp.nti.cap"/></option>
				</form:select>
				<c:if test="${not empty expedientDestiErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="expedientDestiId"/></p></c:if>
			</div>
		</div>

		
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.moure"/></button>
			<a href="<c:url value="/contenidor/${expedientOrigen.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
<script>
$(document).ready(function() {
	var pageSizeExpedients = 20;
	var selExpedientDesti = $("#expedientDestiId");

	selExpedientDesti.select2({
		language: "${requestLocale}",
		theme: 'bootstrap',
		allowClear: true,
		placeholder: '<spring:message code="contingut.document.form.camp.nti.cap"/>',
		width: '100%',
		minimumInputLength: 0,
		ajax: {
			url: '${urlExpedientsPermisEscriptura}',
			dataType: 'json',
			delay: 250,
			global: false,
			data: function (params) {
				params.page = params.page || 1;
				return {
					filter: params.term ? params.term : '',
					pageSize: pageSizeExpedients,
					page: params.page,
					expedientExclosId: ${expedientOrigen.id}
				};
			},
			processResults: function (data, params) {
				params.page = params.page || 1;
				var expedients = [];
				for (var i = 0; i < data.contingut.length; i++) {
					var expedient = data.contingut[i];
					expedients.push({
						id: expedient.id,
						text: expedient.numero ? expedient.numero + ' - ' + expedient.nom : expedient.nom
					});
				}
				return {
					results: expedients,
					pagination: {
						more: (params.page * pageSizeExpedients) < data.elementsTotal
					}
				};
			},
			cache: true
		}
	});

	selExpedientDesti.on('select2:open select2:close', function() {
		webutilModalAdjustHeight();
	});
});
</script>
</body>
</html>
