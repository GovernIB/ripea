<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="expedient.list.user.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
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
<style>
#expedientFiltreForm {
	margin-bottom: 15px;
}
table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
	cursor: pointer;
}
.datepicker{
	padding-left: 12px;
	padding-right: 12px;
}
</style>
<script>
var mostrarMeusExpedients = '${meusExpedients}' === 'true';
var columnaAgafatPer = 15;
$(document).ready(function() {
	$('#taulaDades').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"expedient/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	$('#taulaDades').on('draw.dt', function () {
		$('#seleccioAll').on('click', function() {
			$.get(
					"expedient/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"expedient/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('select-none');
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#taulaDades').DataTable().column(columnaAgafatPer).visible(!mostrarMeusExpedients);
		$("span[class^='stateColor-']").each(function( index ) {

		    var fullClassNameString = this.className;
		    var colorString = fullClassNameString.substring(11);
		    $(this).parent().css( "background-color", colorString );	
		});
	});
	if (mostrarMeusExpedients) {
		$('#taulaDades').DataTable().column(columnaAgafatPer).visible(false);
	}
	$('#meusExpedientsBtn').click(function() {
		mostrarMeusExpedients = !$(this).hasClass('active');
		// Modifica el formulari
		$('#meusExpedients').val(mostrarMeusExpedients);
		$(this).blur();
		// Estableix el valor de la cookie
		setCookie("${nomCookieMeusExpedients}", mostrarMeusExpedients);
		// Amaga la columna i refresca la taula
		$('#taulaDades').webutilDatatable('refresh');
	})
	$(".email-user").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		alert("Button Clicked");
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
function setCookie(cname,cvalue) {
	var exdays = 30;
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
</script>
</head>
<body>
	<form:form id="expedientFiltreForm" action="" method="post" cssClass="well" commandName="expedientFiltreCommand">
		<div class="row">
			<div class="col-md-3">
				<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedientsPermisLectura}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="expedient.list.user.placeholder.tipusExpedient" inline="true"/>
			</div>		
			<div class="col-md-2">
				<rip:inputText name="numero" inline="true" placeholderKey="expedient.list.user.placeholder.numero"/>
			</div>
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="expedient.list.user.placeholder.titol"/>
			</div>
			<div class="col-md-3">
				<rip:inputSelect name="expedientEstatId" optionItems="${expedientEstatsOptions}" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="expedient.list.user.placeholder.estat" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-9">
				<div class="row">
					<div class="col-md-4">
						<rip:inputDate name="dataCreacioInici" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.inici"/>
					</div>
					<div class="col-md-4">
						<rip:inputDate name="dataCreacioFi" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.fi"/>
					</div>	
					<div class="col-md-3">
						<rip:inputText name="interessat" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.interessat"/>
					</div>	
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none;"></button>
					<div class="col-md-1">
						<button id="meusExpedientsBtn" title="<spring:message code="expedient.list.user.meus"/>" class="btn btn-default <c:if test="${meusExpedients}">active</c:if>" data-toggle="button"><span class="fa fa-lock"></span> <spring:message code="expedient.list.user.meus"/></button>
					</div>					
				</div>
				<rip:inputHidden name="meusExpedients"/>
			</div>
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-3">
				<rip:inputSelect name="metaExpedientDominiId" optionItems="${metaExpedientDominisOptions}"  emptyOption="true" placeholderKey="expedient.list.user.placeholder.domini" optionValueAttribute="id" optionTextAttribute="nom" inline="true"/>
			</div>	
		</div>
	</form:form>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="seleccioAll"<c:if test="${empty expedientFiltreCommand.metaExpedientId}"> disabled="disabled"</c:if> title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone"<c:if test="${empty expedientFiltreCommand.metaExpedientId}"> disabled="disabled"</c:if> title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				<div class="btn-group">
					<button<c:if test="${empty expedientFiltreCommand.metaExpedientId}"> disabled="disabled"</c:if> class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
  						<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="expedient.list.user.exportar"/> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu">
						<li><a href="expedient/export/ODS"><spring:message code="expedient.list.user.exportar.ODS"/></a></li>
						<li><a href="expedient/export/CSV"><spring:message code="expedient.list.user.exportar.CSV"/></a></li>
					</ul>
				</div>
			</div>
			<c:if test="${not empty metaExpedientsPermisCreacio}">
				<a href="<c:url value="/expedient/new"/>" data-toggle="modal" data-maximized="true" class="btn btn-default"><span class="fa fa-plus"></span> <spring:message code="expedient.list.user.nou"/></a>
			</c:if>
		</div>
	</script>
	<script id="rowhrefTemplate" type="text/x-jsrender">contingut/{{:id}}</script>
	<table
		id="taulaDades"
		data-toggle="datatable" 
		data-url="<c:url value="/expedient/datatable"/>" 
		class="table table-bordered table-striped table-hover" 
		data-default-order="12" 
		data-default-dir="desc"
		data-botons-template="#botonsTemplate"
		data-rowhref-template="#rowhrefTemplate"
		data-selection-enabled="true"
		data-save-state="true"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="usuariActualWrite" data-visible="false"></th>
				<th data-col-name="metaNode.usuariActualWrite" data-visible="false"></th>
				<th data-col-name="metaNode.usuariActualDelete" data-visible="false"></th>
				<th data-col-name="agafat" data-visible="false"></th>
				<th data-col-name="agafatPer.codi" data-visible="false"></th>
				<th data-col-name="expedientEstat" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="valid" data-visible="false"></th>
				<th data-col-name="conteDocumentsFirmats" data-visible="false"></th>
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
				<th data-col-name="metaNode.nom" width="15%"><spring:message code="expedient.list.user.columna.tipus"/></th>								
				<th data-col-name="createdDate" data-type="datetime" data-converter="datetime" nowrap><spring:message code="expedient.list.user.columna.createl"/></th>
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
				<th data-col-name="agafatPer.nom" data-orderable="false" width="10%"><spring:message code="expedient.list.user.columna.agafatper"/></th>
<%-- 				<th data-col-name="interessatsResum" data-orderable="false" width="10%"><spring:message code="expedient.list.user.columna.interessats"/></th>	 --%>
				<th data-col-name="numComentaris" data-orderable="false" data-template="#cellPermisosTemplate" width="1%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
							<a href="expedient/{{:id}}/comentaris" data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
					</script>
				</th>				
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="contingut/{{:id}}"><span class="fa fa-folder-open-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.gestionar"/></a></li>
								{{if metaNode.usuariActualWrite || usuariActualWrite}}
									{{if !agafat}}
										<li><a href="expedient/{{:id}}/agafar" data-toggle="ajax"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.agafar"/></a></li>
									{{else}}
										{{if agafatPer.codi != '${pageContext.request.userPrincipal.name}'}}
											<li><a href="expedient/{{:id}}/agafar" data-confirm="<spring:message code="expedient.list.user.agafar.confirm.1"/> {{:nomPropietariEscriptoriPare}}. <spring:message code="expedient.list.user.agafar.confirm.2"/>" data-toggle="ajax"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.agafar"/></a></li>
										{{else}}
											<li><a href="expedient/{{:id}}/alliberar" data-toggle="ajax"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
										{{/if}}
									{{/if}}
								{{/if}}
								{{if metaNode.usuariActualDelete}}
									<li><a href="contingut/{{:id}}/delete" data-confirm="<spring:message code="contingut.confirmacio.esborrar.node"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>