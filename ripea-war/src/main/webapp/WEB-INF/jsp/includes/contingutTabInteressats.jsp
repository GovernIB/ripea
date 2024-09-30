<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>

.disabledMsg:hover {
    cursor: not-allowed;
}

.rowinfo-interessat h5 {
	font-weight: bold;
}
.interessaat-info dl {
	columns: 2;
	margin: 0;
}
.interessaat-info dl div {
	display: flex;
}
.interessaat-info dl div dt {
	text-align: right;
	width: 160px;
}
.interessaat-info dl div dd {
	grid-column: 2;
	margin-left: 10px;
}
table.font-decresed {
	font-size: small;
	margin-bottom: 0px;
}
table.font-decresed td {
	padding: 4px 8px !important;
}
td.prop-col {
	font-weight: bold;
}

</style>


<script>

var interessatTipusText = new Array();
<c:forEach var="option" items="${interessatTipusEnumOptions}">
interessatTipusText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>

//################################################## document ready START ##############################################################
$(document).ready(function() {

	$('#taulaInteressats').on('draw.dt', function (e, settings) {
		let api = new $.fn.dataTable.Api(settings);
		if (api.page.info().recordsTotal>0) {
			$("#exportInteressatBtn").removeAttr('disabled');
			$("#exportInteressatBtn").removeAttr('title');
			$("#exportInteressatBtn").attr('href', '<c:url value="/expedient/${expedientId}/interessat/exportar"/>');
		} else {
			$("#exportInteressatBtn").attr('disabled', 'disabled');
			$("#exportInteressatBtn").attr('title', '<spring:message code="tab.interessats.noExportData"/>');
			$("#exportInteressatBtn").attr('href', '#');
		}
	});

	//=======================  list additonal info on clicking desplegable in interessats table =============================
	$('#taulaInteressats').on('rowinfo.dataTable', function (e, td, rowData) {
		var getUrl = "<c:url value="/expedient/interessat/"/>" + rowData.id + "?dadesExternes=true";
		$.get(getUrl).done(function (data) {
			$(td).empty();
			$(td).css({backgroundColor: '#EFF2F5'});
			const rowinfo_content = generateInteressatTable(data) + generateRepresentantTable(data.representant);
			$(td).append(rowinfo_content);
		});
	});
	
});//################################################## document ready END ##############################################################

function showInteressatFieldInfo(fieldName, fieldValue) {
	if (fieldValue) {
		return '<div>' +
					'<dt>' + fieldName + '</dt>' +
					'<dd>' + fieldValue + '</dd>' +
			   '</div>';
	} else {
		return '';
	}
}

const generateInteressatTable = (interessat) => {
	return 	'<table class="table table-bordered table-striped font-decresed">' +
			'	<thead>' +
			'		<tr>' +
			'			<th colspan="4"><spring:message code="contingut.interessat.info.interessat"/></th>' +
			'		</tr>' +
			'	</thead>' +
			'	<tbody>' +
			'	<tr>' +
			'		<td class="prop-col" style="width: 160px;"><spring:message code="contingut.interessat.columna.identificador"/></td><td>' + getPropertyValue(interessat, 'identificador') + '</td>' +
			'		<td class="prop-col" style="width: 160px;"><spring:message code="contingut.interessat.columna.document"/></td><td>' + getPropertyValue(interessat, 'documentNum') + '</td>' +
			'	</tr>' +
			'	<tr>' +
			'		<td class="prop-col"><spring:message code="contingut.interessat.info.pais"/></td><td>' + getPropertyValue(interessat, 'paisNom') + '</td>' +
			'		<td class="prop-col"><spring:message code="contingut.interessat.info.codipostal"/></td><td>' + getPropertyValue(interessat, 'codiPostal') + '</td>' +
			'	</tr>' +
			'	<tr>' +
			'		<td class="prop-col"><spring:message code="contingut.interessat.info.provincia"/></td><td>' + getPropertyValue(interessat, 'provinciaNom') + '</td>' +
			'		<td class="prop-col"><spring:message code="contingut.interessat.info.email"/></td><td>' + getPropertyValue(interessat, 'email') + '</td>' +
			'	</tr>' +
			'	<tr>' +
			'		<td class="prop-col"><spring:message code="contingut.interessat.info.municipi"/></td><td>' + getPropertyValue(interessat, 'municipiNom') + '</td>' +
			'		<td class="prop-col"><spring:message code="contingut.interessat.info.telefon"/></td><td>' + getPropertyValue(interessat, 'telefon') + '</td>' +
			'	</tr>' +
			'	<tr>' +
			'		<td class="prop-col"><spring:message code="contingut.interessat.info.adresa"/></td><td>' + getPropertyValue(interessat, 'adresa') + '</td>' +
			'		<td class="prop-col"><spring:message code="contingut.interessat.info.observacions"/></td><td>' + getPropertyValue(interessat, 'observacions') + '</td>' +
			'	</tr>' +
			'	</tbody>' +
			'</table>';
}
const generateRepresentantTable = (representant) => {
	if (representant) {
		return 	'<table class="table table-bordered table-striped font-decresed">' +
				'	<thead>' +
				'		<tr>' +
				'			<th colspan="4"><spring:message code="contingut.interessat.info.representant"/></th>' +
				'		</tr>' +
				'	</thead>' +
				'	<tbody>' +
				'	<tr>' +
				'		<td class="prop-col" style="width: 160px;"><spring:message code="contingut.interessat.columna.identificador"/></td><td>' + getPropertyValue(representant, 'identificador') + '</td>' +
				'		<td class="prop-col" style="width: 160px;"><spring:message code="contingut.interessat.columna.document"/></td><td>' + getPropertyValue(representant, 'documentNum') + '</td>' +
				'	</tr>' +
				'	<tr>' +
				'		<td class="prop-col"><spring:message code="contingut.interessat.info.pais"/></td><td>' + getPropertyValue(representant, 'paisNom') + '</td>' +
				'		<td class="prop-col"><spring:message code="contingut.interessat.info.codipostal"/></td><td>' + getPropertyValue(representant, 'codiPostal') + '</td>' +
				'	</tr>' +
				'	<tr>' +
				'		<td class="prop-col"><spring:message code="contingut.interessat.info.provincia"/></td><td>' + getPropertyValue(representant, 'provinciaNom') + '</td>' +
				'		<td class="prop-col"><spring:message code="contingut.interessat.info.email"/></td><td>' + getPropertyValue(representant, 'email') + '</td>' +
				'	</tr>' +
				'	<tr>' +
				'		<td class="prop-col"><spring:message code="contingut.interessat.info.municipi"/></td><td>' + getPropertyValue(representant, 'municipiNom') + '</td>' +
				'		<td class="prop-col"><spring:message code="contingut.interessat.info.telefon"/></td><td>' + getPropertyValue(representant, 'telefon') + '</td>' +
				'	</tr>' +
				'	<tr>' +
				'		<td class="prop-col"><spring:message code="contingut.interessat.info.adresa"/></td><td>' + getPropertyValue(representant, 'adresa') + '</td>' +
				'		<td class="prop-col"><spring:message code="contingut.interessat.info.observacions"/></td><td>' + getPropertyValue(representant, 'observacions') + '</td>' +
				'	</tr>' +
				'	</tbody>' +
				'</table>';
	}
	return "";
}
const getPropertyValue = (obj, prop) => {
	return (obj && obj[prop]) ? obj[prop] : '--';
}


function enableNotificar() {
	$('.btnNotificar').closest('li').removeClass('disabled');
	$('.btnNotificar').closest('li').next().hide();

	let url = '<c:url value="/document/"/>' + $('.btnNotificar').closest('tr').attr("id") + '/notificar';
	$('.btnNotificar').attr("href", url);
}

</script>


<table 
	id="taulaInteressats" 
	data-url="<c:url value="/contingut/${expedientId}/interessat/datatable"/>" 
	data-paging-enabled="false"
	data-botons-template="#taulaInteressatsNouBoton" 
	class="table table-striped table-bordered" 
	style="width:100%"
	data-row-info="true">
	<thead>
		<tr>
			<th data-col-name="id" data-visible="false">#</th>
			<th data-col-name="representantId" data-visible="false">#</th>
			<th data-col-name="representant" data-visible="false">#</th>
			<th data-col-name="arxiuPropagat" data-visible="false"></th>
			<th data-col-name="representantArxiuPropagat" data-visible="false"></th>			
			<th data-col-name="expedientArxiuPropagat" data-visible="false"></th>
			<th data-col-name="tipus" data-template="#cellTipusInteressatTemplate" data-orderable="false" width="15%">
				<spring:message code="contingut.interessat.columna.tipus"/>
				<script id="cellTipusInteressatTemplate" type="text/x-jsrender">
					{{:~eval('interessatTipusText["' + tipus + '"]')}}
				</script>
			</th>
			<th data-col-name="documentNum" data-orderable="false" width="15%"><spring:message code="contingut.interessat.columna.document"/></th>
			<th data-col-name="identificador" data-orderable="false" width="35%" data-template="#cellIdentificadorTemplate"><spring:message code="contingut.interessat.columna.identificador"/>
				<script id="cellIdentificadorTemplate" type="text/x-jsrender">
					{{:identificador}} 
					{{if !arxiuPropagat}}
						<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>
					{{/if}}												
				</script>
			</th>
			<th data-col-name="representantIdentificador" data-orderable="false" width="25%" data-template="#cellRepresentantTemplate">
				<spring:message code="contingut.interessat.columna.representant"/>
				<script id="cellRepresentantTemplate" type="text/x-jsrender">
					{{:representantIdentificador}} 
					{{if representantId != null}}
						{{if !representantArxiuPropagat}}
							<span class="fa fa-exclamation-triangle text-danger" title="<spring:message code="contingut.icona.estat.pendentGuardarArxiu"/>"></span>
						{{/if}}	
					{{/if}}													
				</script>										
			</th>
			<c:choose>
				<c:when test="${potModificar}">
					<th data-col-name="id" data-orderable="false" data-template="#cellAccionsInteressatTemplate" width="10%">
						<script id="cellAccionsInteressatTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									{{if (!arxiuPropagat || !representantArxiuPropagat)}}
										{{if !expedientArxiuPropagat}}
											<li class="disabledMsg" title="<spring:message code="disabled.button.primerGuardarExpedientArxiu"/>"><a class="disabled"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
										{{else}}
											<li><a href="<c:url value="/expedient/${expedientId}/guardarInteressatsArxiu?origin=docDetail"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
										{{/if}}
									{{/if}}	
									<li><a href="<c:url value="/expedient/${expedientId}/interessat/{{:id}}?potModificar=true"/>" data-toggle="modal" data-refresh-pagina="false"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									<li><a href="<c:url value="/expedient/${expedientId}/interessat/{{:id}}/delete"/>" data-toggle="ajax" data-refresh-pagina="false" data-confirm="<spring:message code="contingut.confirmacio.esborrar.interessat"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
									{{if tipus != 'ADMINISTRACIO'}}
										<li class="divider" role="separator"></li>
										{{if representantId}}
											<li><a href="<c:url value="/expedient/${expedientId}/interessat/{{:id}}/representant/{{:representantId}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.modificar.prepresentant"/></a></li>
											<li><a href="<c:url value="/expedient/${expedientId}/interessat/{{:id}}/representant/{{:representantId}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="contingut.confirmacio.esborrar.representant"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.borrar.representant"/></a></li>
										{{else}}
											<li><a href="<c:url value="/expedient/${expedientId}/interessat/{{:id}}/representant/new"/>" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.nou.prepresentant"/></a></li>														
										{{/if}}
									{{/if}}
								</ul>
							</div>
						</script>
					</th>				
				</c:when>
				<c:otherwise>
					<th data-col-name="id" data-orderable="false" data-template="#cellAccionsInteressatTemplate" width="10%">
						<script id="cellAccionsInteressatTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">	
									<li><a href="<c:url value="/expedient/${expedientId}/interessat/{{:id}}?potModificar=false"/>" data-toggle="modal" data-refresh-pagina="false"><span class="fa fa-search"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
									{{if tipus != 'ADMINISTRACIO' && representantId}}
										<li class="divider" role="separator"></li>
											<li><a href="<c:url value="/expedient/${expedientId}/interessat/{{:id}}/representant/{{:representantId}}?potModificar=false"/>" data-toggle="modal"><span class="fa fa-search"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.detalls.prepresentant"/></a></li>
									{{/if}}
								</ul>
							</div>
						</script>
					</th>				
				</c:otherwise>
			</c:choose>

			
		</tr>
	</thead>
</table>
<script id="taulaInteressatsNouBoton" type="text/x-jsrender">
	<div style="float: right;">
		<a href="<c:url value="/expedient/${expedientId}/interessat/exportar"/>" id="exportInteressatBtn" class="btn btn-default">
			<span class="fa fa-upload"></span>&nbsp;<spring:message code="contingut.boto.exp.interessat"/>
		</a>
		<c:if test="${potModificar}">
			<a href="<c:url value="/expedient/${expedientId}/interessat/importar"/>" id="importInteressatBtn" class="btn btn-default" data-toggle="modal">
				<span class="fa fa-download"></span>&nbsp;<spring:message code="contingut.boto.imp.interessat"/>
			</a>
			<a href="<c:url value="/expedient/${expedientId}/interessat/new"/>" id="addInteressatBtn" style="margin-left: 45px;" class="btn btn-default" data-toggle="modal" data-func-to-call-on-tancar="enableNotificar">
				<span class="fa fa-plus"></span>&nbsp;<spring:message code="contingut.boto.nou.interessat"/>
			</a>
		</c:if>
	</div>
</script>