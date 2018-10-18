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
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<style>
table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
	cursor: pointer;
}

.container{width: 1500px}
</style>
<script>
var mostrarMeusExpedients = '${meusExpedients}' === 'true';
var columnaAgafatPer = 12;
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

		$("#taulaDades_wrapper .col-md-3").addClass('col-md-4');
		$("#taulaDades_wrapper .col-md-3").removeClass('col-md-3');
		$("#taulaDades_wrapper .col-md-9").addClass('col-md-8');
		$("#taulaDades_wrapper .col-md-9").removeClass('col-md-9');


	});

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



		 
		        

		 

	$("#taulaDades").on("click", "tr", function(e){
		var idRow = $(this).closest('tr').attr('id');
		var id = idRow.substring(4); 

		

		 $("#frame").attr("src", "<c:url value="/nodeco/contingutDetail/"/>" + id);
	




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


	<div data-toggle="botons-titol">
		<button id="meusExpedientsBtn" class="btn btn-default <c:if test="${meusExpedients}">active</c:if>" data-toggle="button"><span class="fa fa-desktop"></span> <spring:message code="expedient.list.user.meus"/></button>
		<a  style="float: right" href="<c:url value="/expedient"/>"  class="btn btn-primary"> <spring:message code="expedient.list.canviVista"/></a>
	</div>
	<form:form id="expedientFiltreForm" action="" method="post" cssClass="well" commandName="expedientFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<rip:inputText name="numero" inline="true" placeholderKey="expedient.list.user.placeholder.numero"/>
			</div>
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="expedient.list.user.placeholder.titol"/>
			</div>
			<div class="col-md-3">
				<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedientsPermisLectura}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="expedient.list.user.placeholder.tipusExpedient" inline="true"/>
			</div>
			<div class="col-md-3">
				<rip:inputSelect name="estat" optionItems="${expedientEstatEnumOptions}" optionValueAttribute="value" emptyOption="true" optionTextKeyAttribute="text" placeholderKey="expedient.list.user.placeholder.estat" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-9">
				<div class="row">
					<div class="col-md-3">
						<rip:inputDate name="dataCreacioInici" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.inici"/>
					</div>
					<div class="col-md-3">
						<rip:inputDate name="dataCreacioFi" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.fi"/>
					</div>
					<div class="col-md-3">
						<rip:inputDate name="dataTancatInici" inline="true" placeholderKey="expedient.list.user.placeholder.tancat.inici"/>
					</div>
					<div class="col-md-3">
						<rip:inputDate name="dataTancatFi" inline="true" placeholderKey="expedient.list.user.placeholder.tancat.fi"/>
					</div>
				</div>
				<rip:inputHidden name="meusExpedients"/>
			</div>
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none;"></button>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="btn-group pull-right">
			

			<c:if test="${not empty metaExpedientsPermisCreacio}">
				<a href="<c:url value="/expedient/new"/>" data-toggle="modal" data-refresh="true" class="btn btn-default"><span class="fa fa-plus"></span> <spring:message code="expedient.list.user.nou"/></a>
			</c:if>
		</div>
	</script>
	
	
	<div class="row">
		<div class="col-md-4">
			<table id="taulaDades" 
					data-toggle="datatable" 
					data-url="<c:url value="/expedient/datatable"/>" 
					class="table table-bordered table-striped table-hover" 
					data-default-order="8" 
					data-default-dir="desc"
					data-botons-template="#botonsTemplate"
					style="width:100%">
				<thead>
					<tr>



				<th data-col-name="metaNode.usuariActualWrite" data-visible="false"></th>
				<th data-col-name="metaNode.usuariActualDelete" data-visible="false"></th>
				<th data-col-name="agafat" data-visible="false"></th>
				<th data-col-name="agafatPer.codi" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="valid" data-visible="false"></th>
				<th data-col-name="metaNode.nom" width="15%" data-visible="false"><spring:message code="expedient.list.user.columna.tipus"/></th>
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
				<th data-col-name="createdDate" data-type="datetime" data-converter="datetime" width="14%" data-visible="false"><spring:message code="expedient.list.user.columna.createl"/></th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" width="11%" data-visible="false">
					<spring:message code="expedient.list.user.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'OBERT'}}
							<span class="label label-default"><span class="fa fa-folder-open"></span> <spring:message code="expedient.estat.enum.OBERT"/></span>
						{{else}}
							<span class="label label-success"><span class="fa fa-folder"></span> <spring:message code="expedient.estat.enum.TANCAT"/></span>
						{{/if}}
						{{if ambRegistresSenseLlegir}}
							<span class="fa-stack" aria-hidden="true">
          						<i class="fa fa-certificate fa-stack-1x" style="color: darkturquoise; font-size: 20px;"></i>
          						<i class="fa-stack-1x" style="color: white;font-style: normal;font-weight: bold;">N</i>
        					</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="agafatPer.nom" data-orderable="false" width="20%" data-visible="false"><spring:message code="expedient.list.user.columna.agafatper"/></th>
				<th data-col-name="id" data-orderable="false" width="10%" data-visible="false">
			
				</th>
						
						
		
					</tr>
				</thead>
			</table>
		</div>				
		<div class="col-md-8">
		
		     <iframe id="frame" src="" width="100%" height="600px" style="border: none;" ></iframe>

		
		
		
		</div>
			
			
	</div>			
</body>