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

</style>


<script>

var interessatTipusText = new Array();
<c:forEach var="option" items="${interessatTipusEnumOptions}">
interessatTipusText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>

//################################################## document ready START ##############################################################
$(document).ready(function() {


	//=======================  list additonal info on clicking desplegable in interessats table =============================
	$('#taulaInteressats').on('rowinfo.dataTable', function(e, td, rowData) {
		var getUrl = "<c:url value="/expedient/interessat/"/>" + rowData.id + "?dadesExternes=true";
		$.get(getUrl).done(function(data) {
			$(td).empty();
			var rowinfo_content = 
				    			'<div class="rowinfo-interessat">' + 
								'<h5><spring:message code="contingut.interessat.info.interessat"/>:</h5>' + 
				    			'<div class="interessaat-info">' +
				    				'<dl>' +
				    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.pais"/>', data.paisNom) +
				    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.email"/>', data.email) +
				    				'</dl>' +
					    			'<dl>' +
					    				showInteressatFieldInfo('<spring:message code="contingut.interessat.info.provincia"/>', data.provinciaNom) +
					    				showInteressatFieldInfo('<spring:message code="contingut.interessat.info.telefon"/>', data.telefon) +
								    '</dl>'+
								    '<dl>' +
								    	showInteressatFieldInfo('<spring:message code="contingut.interessat.info.municipi"/>', data.municipiNom) +
				    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.observacions"/>', data.observacions) +
							    	'</dl>'+
							    	'<dl>' +
							    		showInteressatFieldInfo('<spring:message code="contingut.interessat.info.adresa"/>', data.adresa) +
									'</dl>'+
									'<dl>' +
										showInteressatFieldInfo('<spring:message code="contingut.interessat.info.codipostal"/>', data.codiPostal) +
									'</dl>'+
								'</div>';
								if (data.representant) {
									rowinfo_content += '<h5><spring:message code="contingut.interessat.info.representant"/>:</h5>' +
														'<div class="interessaat-info">' +
										    				'<dl>' +
										    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.pais"/>', data.representant.paisNom) +
										    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.email"/>', data.representant.email) +
										    				'</dl>' +
											    			'<dl>' +
											    				showInteressatFieldInfo('<spring:message code="contingut.interessat.info.provincia"/>', data.representant.provinciaNom) +
											    				showInteressatFieldInfo('<spring:message code="contingut.interessat.info.telefon"/>', data.representant.telefon) +
														    '</dl>'+
														    '<dl>' +
														    	showInteressatFieldInfo('<spring:message code="contingut.interessat.info.municipi"/>', data.representant.municipiNom) +
										    					showInteressatFieldInfo('<spring:message code="contingut.interessat.info.observacions"/>', data.representant.observacions) +
													    	'</dl>'+
													    	'<dl>' +
													    		showInteressatFieldInfo('<spring:message code="contingut.interessat.info.adresa"/>', data.representant.adresa) +
															'</dl>'+
															'<dl>' +
																showInteressatFieldInfo('<spring:message code="contingut.interessat.info.codipostal"/>', data.representant.codiPostal) +
															'</dl>'+
														'</div>';
								}
								rowinfo_content += '</div>';
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


function enableNotificar() {
	$('.btnNotificar').closest('li').removeClass('disabled');
	$('.btnNotificar').closest('li').next().hide();

	let url = '<c:url value="/document/"/>' + $('.btnNotificar').closest('tr').attr("id") + '/notificar';
	$('.btnNotificar').attr("href", url);
}


</script>


<table 
	id="taulaInteressats" 
	data-url="<c:url value="/contingut/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/datatable"/>" 
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
			<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT')}">
			<th data-col-name="id" data-orderable="false" data-template="#cellAccionsInteressatTemplate" width="10%">
				<script id="cellAccionsInteressatTemplate" type="text/x-jsrender">
					<div class="dropdown">
						<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
						<ul class="dropdown-menu">
							{{if (!arxiuPropagat || !representantArxiuPropagat)}}
								{{if !expedientArxiuPropagat}}
									<li class="disabledMsg" title="<spring:message code="disabled.button.primerGuardarExpedientArxiu"/>"><a class="disabled"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
								{{else}}
									<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/guardarInteressatsArxiu?origin=docDetail"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.guardarArxiu"/></a></li>
								{{/if}}
							{{/if}}	
							<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}"/>" data-toggle="modal" data-refresh-pagina="false" class="btnModificarInteressat"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
							<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}/delete"/>" data-toggle="ajax" data-refresh-pagina="false" data-confirm="<spring:message code="contingut.confirmacio.esborrar.interessat"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							{{if tipus != '<%=es.caib.ripea.core.api.dto.InteressatTipusEnumDto.ADMINISTRACIO%>'}}
								<li class="divider" role="separator"></li>
								{{if representantId}}
									<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}/representant/{{:representantId}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.modificar.prepresentant"/></a></li>
									<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}/representant/{{:representantId}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="contingut.confirmacio.esborrar.representant"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.borrar.representant"/></a></li>
								{{else}}
									<li><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/{{:id}}/representant/new"/>" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;&nbsp;<spring:message code="contingut.interessat.nou.prepresentant"/></a></li>														
								{{/if}}
							{{/if}}
						</ul>
					</div>
				</script>
			</th>
			</c:if>
		</tr>
	</thead>
</table>
<script id="taulaInteressatsNouBoton" type="text/x-jsrender">
	<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && (contingut.expedient ? (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT') : contingut.expedientPare.estat != 'TANCAT')}">
		<p style="text-align:right"><a href="<c:url value="/expedient/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/interessat/new"/>" id="addInteressatBtn" class="btn btn-default" data-toggle="modal" data-func-to-call-on-tancar="enableNotificar" ><span class="fa fa-plus"></span>&nbsp;<spring:message code="contingut.boto.nou.interessat"/></a></p>
	</c:if>
</script>