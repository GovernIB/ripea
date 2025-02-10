<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="rest.bustia.info.titol"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('#executar_enviarContingut').click(function() {
		$.ajax({
			type: 'POST',
			url: $('#form_enviarContingut').attr('action'),
			data: {
				entitat: "TEST1",
				unitatAdministrativa: "A04013501",
				tipus: "REGISTRE_ENTRADA",
				referencia: "L19E2/2016"
			},
			success: function(data) {
				alert(data);
			}
		});
		return false;
	});
});
</script>
</head>
<body>

	<div class="panel panel-default">
		<div class="panel-heading" data-toggle="collapse" data-target="#collapse_enviarContingut" aria-expanded="false">
			<span class="fa fa-cog"></span> enviarContingut <span class="fa fa-chevron-down pull-right"></span>
		</div>
		<div id="collapse_enviarContingut" class="collapse">
			<div class="panel-body">
				<form id="form_enviarContingut" action="bustia/enviarContingut" class="form-horizontal">
					<div class="form-group">
						<label for="entitat" class="control-label col-sm-2"><spring:message code="rest.bustia.info.camp.entitat"/></label>
						<div class="col-sm-10">
							<input id="entitat" type="text" class="form-control"/>
						</div>
					</div>
					<div class="form-group">
						<label for="unitatAdministrativa" class="control-label col-sm-2"><spring:message code="rest.bustia.info.camp.unitat.adm"/></label>
						<div class="col-sm-10">
							<input id="unitatAdministrativa" type="text" class="form-control"/>
						</div>
					</div>
					<div class="form-group">
						<label for="tipus" class="control-label col-sm-2"><spring:message code="rest.bustia.info.camp.tipus"/></label>
						<div class="col-sm-10">
							<input id="tipus" type="text" class="form-control"/>
						</div>
					</div>
					<div class="form-group">
						<label for="referencia" class="control-label col-sm-2"><spring:message code="rest.bustia.info.camp.referencia"/></label>
						<div class="col-sm-10">
							<input id="referencia" type="text" class="form-control"/>
						</div>
					</div>
				</form>
			</div>
			<div class="panel-footer">
				<button id="executar_enviarContingut" class="btn btn-default"><span class="fa fa-bolt"></span> <spring:message code="comu.boto.executar"/></button>
			</div>
		</div>
	</div>

</body>