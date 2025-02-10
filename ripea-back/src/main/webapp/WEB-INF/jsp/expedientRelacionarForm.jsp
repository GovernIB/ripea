<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="contingut.expedient.relacionar.form.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.3.1/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.2.3/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	

<style type="text/css">
.select2 {
    width: 100% !important;
}
</style>
<script>

$(document).ready(function() {

	$('#taulaDades').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"relacionarList/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	
	$('#taulaDades').on('draw.dt', function () {

		$("span[class^='stateColor-']").each(function( index ) {

		    var fullClassNameString = this.className;
		    var colorString = fullClassNameString.substring(11);
		    $(this).parent().css( "background-color", colorString );	
		});
		
	});

	$('#metaExpedientId').on('change', function() {
		var tipus = $(this).val();
		$('#expedientEstatId').select2('val', '', true);
		$('#expedientEstatId option[value!=""]').remove();
		var metaNodeRefresh = function(data) {
			for (var i = 0; i < data.length; i++) {
				$('#expedientEstatId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
			}
		};
		if (tipus != "") {
			$.get("<c:url value="/expedient/estatValues/"/>"+tipus)
			.done(metaNodeRefresh)
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		}
	});

});


</script>
</head>
<body>                                    
	<form:form id="expedientFiltreForm" action="" method="post" cssClass="well" commandName="expedientFiltreCommand">
		<div class="row">
			<div class="col-sm-3">
				<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedientsPermisLectura}" optionValueAttribute="id" optionMinimumResultsForSearch="6" optionTextAttribute="nom" emptyOption="true" placeholderKey="expedient.list.user.placeholder.tipusExpedient" inline="true"/>
			</div>		
			<div class="col-sm-3">
				<rip:inputText name="numero" inline="true" placeholderKey="expedient.list.user.placeholder.numero"/>
			</div>
			<div class="col-sm-3">
				<rip:inputText name="nom" inline="true" placeholderKey="expedient.list.user.placeholder.titol"/>
			</div>
			<div class="col-sm-3">
				<rip:inputSelect name="expedientEstatId" optionItems="${expedientEstatsOptions}" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="expedient.list.user.placeholder.estat" inline="true"/>
			</div>
		</div>
		<div class="row">

			
			<div class="col-sm-12 pull-right">
				<div class="pull-right">
					<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<a href="relacionar/" class="btn btn-success"><span id="seleccioCount" class="badge">${fn:length(seleccioRelacionar)}</span> <spring:message code="comu.boto.relacionar"/> <span class="fa fa-link"></span></a>
			</div>
		</div>
	</script>
	<table
		id="taulaDades"
		data-toggle="datatable" 
		data-url="<c:url value="/expedient/${expedientId}/relacio/datatable"/>" 
		class="table table-bordered table-striped table-hover" 
		data-default-order="7" 
		data-default-dir="desc"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-save-state="true"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="agafat" data-visible="false"></th>
				<th data-col-name="agafatPer.codi" data-visible="false"></th>
				<th data-col-name="expedientEstat" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="valid" data-visible="false"></th>
				<th data-col-name="conteDocumentsFirmats" data-visible="false"></th>
				<th data-col-name="metaNode.nom" width="15%" data-orderable="false"><spring:message code="expedient.list.user.columna.tipus"/></th>
				<th data-col-name="numero"><spring:message code="expedient.list.user.columna.numero"/></th>
				<th data-col-name="nom" data-template="#cellNomTemplate" width="30%">
					<spring:message code="expedient.list.user.columna.titol"/>
					<script id="cellNomTemplate" type="text/x-jsrender">
						{{if !valid}}
							{{if alerta}}
								<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.expedient.dual"/>"></span>
							{{else}}
								<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.expedient"/>"></span>
							{{/if}}
						{{else}}
							{{if alerta}}
								<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.expedient.segonpla"/>"></span>
							{{/if}}
						{{/if}}
						{{:nom}}
					</script>
				</th>
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

						{{if ambRegistresSenseLlegir}}
							<span class="fa-stack" aria-hidden="true">
								<i class="fa fa-certificate fa-stack-1x" style="color: darkturquoise; font-size: 20px;"></i>
								<i class="fa-stack-1x" style="color: white;font-style: normal;font-weight: bold;">N</i>
							</span>
						{{/if}}
						{{if expedientEstat != null && expedientEstat.color!=null}}
							<span class="stateColor-{{:expedientEstat.color}}"></span>
						{{/if}}
					</script>
				</th>			
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="relacionar/{{:id}}" class="btn btn-success"><span class="fa fa-link"></span>&nbsp;&nbsp;<spring:message code="comu.boto.relacionar"/></a>
					</script>
				</th>					

			</tr>
		</thead>
	</table>
</body>