<%@page import="es.caib.ripea.war.helper.EnumHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
java.util.List<EnumHelper.HtmlOption> estatExpedientEnum = EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.ExpedientEstatEnumDto.class,
		"expedient.estat.enum.");
pageContext.setAttribute(
		"estatExpedientEnum",
		estatExpedientEnum);
%>

<c:set var="titol"><spring:message code="bustia.pendent.contingut.addexp.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
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
	<rip:modalHead/>
	
	<style>
		.no-padding-left {
   			padding-left: 0px;
		}
		.no-padding-right {
			padding-right: 0px;
		}
		.no-margin-bottom .form-group {
			margin-bottom: 0px !important;
		}
		.select2 {
			width: auto !important;
		}
		.well {
			overflow: auto;
		}
	</style>

	<script type="text/javascript">
		
		$( document ).ready(function() {
		    
			$( document ).ajaxComplete(function() {
				webutilModalAdjustHeight();
			});
			
			$('.in').on('hidden.bs.collapse', function () {
				$("#collapse-button").html('<span class="fa fa-chevron-up"></span>');
				webutilModalAdjustHeight();
				$("#search").val("");
			});
			$('.in').on('show.bs.collapse', function () {
				$("#collapse-button").html('<span class="fa fa-chevron-down"></span>');
				webutilModalAdjustHeight();
				$("#numero").val("");
				$("#nom").val("");
				$("#arxiuId").prop('selectedIndex', 0).change();
				$("#estat").prop('selectedIndex', 0).change();
				$("#tipusId").prop('selectedIndex', 0).change();
			});
			
			var expedientId;
			var table = $('#taulaExpedients').DataTable();
		    $('#taulaExpedients tbody').on( 'click', 'tr', function () {
		        if ( $(this).hasClass('selected') ) {
		            $(this).removeClass('selected');
		            expedientId = "";
		        }
		        else {
		            table.$('tr.selected').removeClass('selected');
		            $(this).addClass('selected');
		            expedientId = table.row(this).data().id;
		        }
		    } );
		    $("#addExpForm").submit(function( event ) {
		   		if(expedientId == null) {
		   			alert($('#mfns').val());
			    	event.preventDefault();	
		   		} else {
		   			var url = $('#addExpForm').attr('action');
		   			url = url + expedientId;
		   			$('#addExpForm').attr('action', url);
		   		}
	    	});

			
		});
	
	</script>
	
	<style>
		.selected td {
    		background-color: LightYellow !important;
    		color: black !important;
 		}
	</style>
</head>
<body>
<c:set var="mfns"><spring:message code="bustia.pendent.addexp.missatge.fila.no.seleccionada"/></c:set>
<input id="mfns" type="hidden" value="${mfns}"/>
	<form:form action="" method="post" cssClass="well" commandName="expedientFiltreCommand">
		<div class="no-margin-bottom no-padding-left no-padding-right col-sm-9 collapse in">
			<rip:inputText name="search" inline="true" placeholderKey="expedient.list.user.placeholder.expedient"/>
		</div>
		<div class="no-padding-left col-sm-4 collapse">
			<rip:inputText name="numero" inline="true" placeholderKey="expedient.list.user.placeholder.numero"/>
		</div>
		<div class="col-sm-4 collapse">
			<rip:inputText name="nom" inline="true" placeholderKey="expedient.list.user.placeholder.titol"/>
		</div>
		<div class="no-padding-right col-sm-4 collapse">
			<rip:inputSelect name="tipusId" optionItems="${expedientTipus}" optionValueAttribute="id" optionTextAttribute="identificador" emptyOption="true" placeholderKey="expedient.list.user.placeholder.tipus" inline="true"/>
		</div>
		<div class="no-margin-bottom  no-padding-left col-sm-4 collapse">
			<rip:inputSelect name="arxiuId" optionItems="${arxius}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="expedient.list.user.placeholder.arxiu" inline="true"/>
		</div>
		<div class="no-margin-bottom col-sm-4 collapse">
			<rip:inputSelect name="estat" optionItems="${estatExpedientEnum}" optionTextKeyAttribute="text" optionValueAttribute="value" emptyOption="true" placeholderKey="expedient.list.user.placeholder.estat" inline="true"/>
		</div>
		<div class="col-sm-1 collapse"></div>
		<div class="no-padding-left no-padding-right col-sm-3 pull-right">
			<div class="pull-right">
				<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none"></button>
				<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				<button id="collapse-button" type="button" class="btn btn-default" data-toggle="collapse" data-target=".collapse"><span class="fa fa-chevron-down"></span></button>
			</div>
		</div>
	</form:form>

	<table id="taulaExpedients" 
			data-toggle="datatable" 
			data-url="<c:url value="/bustiaUser/${bustiaId}/pendent/${contingutTipus}/${contingutId}/addexp/datatable"/>" 
			data-filter="#expedientFiltreCommand"
			class="table table-bordered table-striped"
			style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="numero" data-orderable="false" width="10%"><spring:message code="bustia.pendent.addexp.columna.numero"/></th>
				<th data-col-name="nom" data-orderable="false" width="30%"><spring:message code="bustia.pendent.addexp.columna.titol"/></th>
				<th data-col-name="metaNode.codi" data-orderable="false" width="15%"><spring:message code="bustia.pendent.addexp.columna.tipus"/></th>
				<th data-col-name="arxiu.nom" data-orderable="false" width="15%"><spring:message code="bustia.pendent.addexp.columna.arxiu"/></th>
				<th data-col-name="estat" data-orderable="false" data-template="#cellEstatTemplate" width="10%">
					<spring:message code="bustia.pendent.addexp.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'OBERT'}}
							<span class="label label-default"><span class="fa fa-folder-open"></span> <spring:message code="expedient.estat.enum.OBERT"/></span>
						{{else}}
							<span class="label label-success"><span class="fa fa-folder"></span> <spring:message code="expedient.estat.enum.TANCAT"/></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="agafatPer.nom" data-orderable="false" width="20%"><spring:message code="bustia.pendent.addexp.columna.agafatper"/></th>
			</tr>
		</thead>
	</table>
	<c:set var="addExpedient"><c:url value="/bustiaUser/${bustiaId}/pendent/${contingutTipus}/${contingutId}/addexp/"/></c:set>
	<form:form id="addExpForm" action="${addExpedient}">
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-sign-in"></span> <spring:message code="comu.boto.afegir"/></button>
			<a href="<c:url value="/bustiaUser/${bustiaId}/pendent/${contingutTipus}/${contingutId}/addexp"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>
</html>