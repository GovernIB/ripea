<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
//pageContext.setAttribute(
//		"expedientEstatsOptdions",
//		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
//		es.caib.ripea.core.api.dto.ExpedientEstatEnumDto.class,
//		"expedient.estat.enum."));
%>
<html>
<head>
	<title><spring:message code="expedient.list.user.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
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
.rmodal {
    display:    none;
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
body.loading {
    overflow: hidden;   
}
body.loading .rmodal {
    display: block;
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
		

		$("a.fileDownload").on("click", function() {
			$("body").addClass("loading");
			checkLoadingFinished();
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


	
	var metaExpedientId = "";
	var counter = 0;
	$('#metaExpedientId').on('change', function() {
		metaExpedientId = $(this).val();

		if (counter != 0) {
			
			if (metaExpedientId) {
				$.get("<c:url value="/expedient/estatValues/"/>"+metaExpedientId)
				.done(function(data) {
					
					$('#expedientEstatId').select2('val', '', true);
					$('#expedientEstatId option[value!=""]').remove();
					for (var i = 0; i < data.length; i++) {
						$('#expedientEstatId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					}
				})
				.fail(function() {
					alert("<spring:message code="error.jquery.ajax"/>");
				});
			}
		}
		counter++;


		
		//select dominis a partir de metaexpedient
		var dominisRefresh = function(data) {	
			$('#metaExpedientDominiCodi').append("<option value=\"\"></option>");
			for (var i = 0; i < data.length; i++) {

				console.log($('#metaExpedientDominiCodi').val());
				$('#metaExpedientDominiCodi').append('<option value="' + data[i].codi + '">' + data[i].nom + '</option>');
			}
		};
		if (metaExpedientId) {
			var multipleUrl = '<c:url value="/metaExpedient/'  + metaExpedientId + '/metaDadaPermisLectura/domini"/>';
			$.get(multipleUrl)
			.done(dominisRefresh)
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		}
		
	});
	$('#metaExpedientId').trigger('change');

	
			
	$('#metaExpedientDominiCodi').on('change', function() {
		var dominiCodi= $(this).val();
		//get valor domini seleccionat
		var dominisRefresh = function(data) {
			$('#metaExpedientDominiValor').append("<option value=\"\"></option>");
			for (var i = 0; i < data.length; i++) {
				console.log(data[i]);
				$('#metaExpedientDominiValor').append('<option value="' + data[i].id + '">' + data[i].valor + '</option>');
			}
		};
		if (metaExpedientId != "") {
			var multipleUrl = '<c:url value="/metaExpedient/'  + metaExpedientId + '/metaDada/domini/' + dominiCodi + '"/>';
			$.get(multipleUrl)
			.done(dominisRefresh)
			.fail(function() {
			alert("<spring:message code="error.jquery.ajax"/>");
			});
		}
	});
	

	$('#organGestorId').on('change', function() {
		var organGestorId = $(this).val();

		$('#metaExpedientId').val('').trigger('change')

		if (organGestorId) {
			$("#metaExpedientId").data('urlParamAddicional', organGestorId);
		} else {
			$("#metaExpedientId").data('urlParamAddicional', null);

		}

	});

					

	
});

function checkLoadingFinished() {
	var cookieName = "contentLoaded";
	if (getCookie(cookieName) != "") {
		$("body").removeClass("loading");
        removeCookie(cookieName);
		return;
	}
    setTimeout(checkLoadingFinished, 100);
}
	
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
function removeCookie(cname) {
    var expires = new Date(0).toUTCString();
    document.cookie = cname + "=; path=/; expires=" + expires + ";";
}

</script>
</head>
<body>
	<form:form id="expedientFiltreForm" action="" method="post" cssClass="well" commandName="expedientFiltreCommand">
		<div class="row">
			<div class="col-md-3">
			
				<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
 					name="organGestorId"  
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					placeholderKey="metaexpedient.form.camp.organgestor"
 					suggestValue="id"
 					suggestText="nom"
 					inline="true"/>	
			</div>	
			<div class="col-md-3">
				<c:url value="/metaexpedientajax/metaexpedient" var="urlConsultaInicial"/>
				<c:url value="/metaexpedientajax/metaexpedients" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
 					name="metaExpedientId"  
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					placeholderKey="expedient.list.user.placeholder.tipusExpedient"
 					suggestValue="id"
 					suggestText="nom"
 					suggestTextAddicional="classificacioSia"
 					inline="true"
 					urlParamAddicional="${expedientFiltreCommand.organGestorId}"
 					/>				
			</div>		
			<div class="col-md-2">
				<rip:inputText name="numero" inline="true" placeholderKey="expedient.list.user.placeholder.numero"/>
			</div>
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="expedient.list.user.placeholder.titol"/>
			</div>

		</div>
		<div class="row">
			<div class="col-md-3">
				<rip:inputSelect name="expedientEstatId" optionItems="${expedientEstatsOptions}" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="expedient.list.user.placeholder.estat" inline="true"/>
			</div>
			<div class="col-md-3">
				<rip:inputDate name="dataCreacioInici" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.inici"/>
			</div>
			<div class="col-md-3">
				<rip:inputDate name="dataCreacioFi" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.fi"/>
			</div>	
			<div class="col-md-3">
				<rip:inputText name="interessat" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.interessat"/>
			</div>			
		</div>
		
		<div class="row">
			<button type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none;"></button>
			<div class="col-md-2">
				<button id="meusExpedientsBtn" title="<spring:message code="expedient.list.user.meus"/>" class="btn btn-default <c:if test="${meusExpedients}">active</c:if>" data-toggle="button"><span class="fa fa-lock"></span> <spring:message code="expedient.list.user.meus"/></button>
			</div>						
			<rip:inputHidden name="meusExpedients"/>
		
			<div class="col-md-4">
			<!-- rip:inputSelect name="metaExpedientDominiId" optionItems="${metaExpedientDominisOptions}"  emptyOption="true" placeholderKey="expedient.list.user.placeholder.domini" optionValueAttribute="id" optionTextAttribute="nom" inline="true"/-->
				<rip:inputSelect name="metaExpedientDominiCodi" placeholderKey="expedient.list.user.placeholder.domini" emptyOption="true" inline="true"/>
			</div>
			<div class="col-md-3">
				<rip:inputSelect name="metaExpedientDominiValor" placeholderKey="expedient.list.user.placeholder.domini.value" emptyOption="true" inline="true"/>
			</div>

			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<div class="rmodal"></div>
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
						<li><a class="fileDownload" href="expedient/generarIndex"><spring:message code="expedient.list.user.generar.index"/></a></li>
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
		data-default-order="18" 
		data-default-dir="desc"
		data-botons-template="#botonsTemplate"
		data-rowhref-template="#rowhrefTemplate"
		data-selection-enabled="true"
		data-save-state="true"
		data-mantenir-paginacio="${mantenirPaginacio}"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="usuariActualWrite" data-visible="false"></th>
				<th data-col-name="seguidor" data-visible="false"></th>
				<th data-col-name="metaNode.usuariActualWrite" data-visible="false"></th>
				<th data-col-name="metaNode.usuariActualDelete" data-visible="false"></th>
				<th data-col-name="agafat" data-visible="false"></th>
				<th data-col-name="agafatPer.codi" data-visible="false"></th>
				<th data-col-name="expedientEstat" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="valid" data-visible="false"></th>
				<th data-col-name="errorLastEnviament" data-visible="false"></th>
				<th data-col-name="errorLastNotificacio" data-visible="false"></th>
				<th data-col-name="ambEnviamentsPendents" data-visible="false"></th>
				<th data-col-name="ambNotificacionsPendents" data-visible="false"></th>
				<th data-col-name="conteDocumentsFirmats" data-visible="false"></th>
				<th data-col-name="numero"><spring:message code="expedient.list.user.columna.numero"/></th>				
				<th data-col-name="nom" width="30%">
					<spring:message code="expedient.list.user.columna.titol"/>
				</th>
				<th data-col-name="id" data-template="#cellAvisosTemplate" width="5%">
					<spring:message code="expedient.list.user.columna.avisos"/>
					<script id="cellAvisosTemplate" type="text/x-jsrender">
						{{if !valid}}<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.expedient.validacio"/>"></span>{{/if}}
						{{if errorLastEnviament }}<span class="fa fa-pencil-square text-danger" title="<spring:message code="contingut.errors.expedient.enviaments"/>"></span>{{/if}}
						{{if errorLastNotificacio }}<span class="fa fa-envelope-square text-danger" title="<spring:message code="contingut.errors.expedient.notificacions"/>"></span>{{/if}}
						{{if ambEnviamentsPendents }}<span class="fa fa-pencil-square text-primary" title="<spring:message code="contingut.pendents.expedient.enviaments"/>"></span>{{/if}}
						{{if ambNotificacionsPendents }}<span class="fa fa-envelope-square text-primary" title="<spring:message code="contingut.pendents.expedient.notificacions"/>"></span>{{/if}}
						{{if alerta}}<span class="fa fa-exclamation-circle text-danger" title="<spring:message code="contingut.errors.expedient.alertes"/>"></span>{{/if}}			
					</script>
				</th>
				<th data-col-name="tipusStr" width="20%"><spring:message code="expedient.list.user.columna.tipus"/></th>								
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
				<th data-col-name="numSeguidors" data-orderable="false" data-template="#cellSeguidorsTemplate" width="1%">
					<script id="cellSeguidorsTemplate" type="text/x-jsrender">
						{{if numSeguidors > 0}}
							<a href="expedient/{{:id}}/seguidors" data-toggle="modal" data-refresh-tancar="true" data-modal-id="seguidors{{:id}}" class="btn btn-default" title="&nbsp;<spring:message code="comu.boto.followers"/>&nbsp;"><span class="fa fa-lg fa-users"></span>&nbsp;<span class="badge">{{:numSeguidors}}</span></a>
						{{else}}
							<a href="expedient/{{:id}}/seguidors" data-toggle="modal" data-refresh-tancar="true" data-modal-id="seguidors{{:id}}" class="btn btn-default disabled"><span class="fa fa-lg fa-users"></span>&nbsp;<span class="badge">{{:numSeguidors}}</span></a>
						{{/if}}							
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
								{{if metaNode.usuariActualWrite && seguidor}}
									<li><a href="expedient/{{:id}}/unfollow" data-toggle="ajax"><span class="fa fa-user-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.unfollow"/></a></li>
								{{else metaNode.usuariActualWrite && !seguidor}}					
									<li><a href="expedient/{{:id}}/follow" data-toggle="ajax"><span class="fa fa-user-plus"></span>&nbsp;&nbsp;<spring:message code="comu.boto.follow"/></a></li>		
								{{/if}}
								{{if metaNode.usuariActualDelete && estat != 'TANCAT'}}
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