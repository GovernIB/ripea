<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>


</style>


<script>

//################################################## document ready START ##############################################################
$(document).ready(function() {

	
	//======================= enviament list on clicking desplegable in notificacio table =============================
	$('#taulaNotificacions').on('rowinfo.dataTable', function(e, td, rowData) {

		$(td).append('<div class="datatable-dades-carregant" style="text-align: center; margin-top: 10px; margin-bottom: 10px;"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>');

		
		var getUrl = "<c:url value="/expedient/${contingut.id}"/>" + "/enviaments/" + rowData.id;
	    $.get(getUrl).done(function(json) {

	    	$(td).empty();
			if (json.error) {
				$(td).append('<div class="viewer-padding"><div class="alert alert-danger"> ' + json.errorMsg + '</div></div>');

			} else {
				var notificacio = json.data;
			    var enviaments = notificacio.documentEnviamentInteressats;
		    	
		    	$(td).append(
		    			'<table class="table teble-striped table-bordered">' +
		    			'<caption><spring:message code="notificacio.list.enviament.list.titol"/></caption>' +
		    			'<thead>' +
		    			'<tr>' +
						'<th><spring:message code="notificacio.list.enviament.list.titular"/></th>' + 
		    			'<th><spring:message code="notificacio.list.enviament.list.representants"/></th>' +
		    			'<th><spring:message code="notificacio.list.enviament.list.estat"/></th>' +
		    			'<th></th>' +
		    			'</tr>' +
						'</thead><tbody></tbody></table>');

		    	var tableBody = '';
				for (i = 0; i < enviaments.length; i++) {
					tableBody += '<tr>';
					
					//====== titular/interessat ========
					var nomTitular = '',
					    llinatge1 = '',
					    llinatge2 = '',
					    nif = '';
					if (enviaments[i].interessat.nom != null) {
					    nomTitular = enviaments[i].interessat.nom;
					} else if (enviaments[i].interessat.raoSocial != null) {
					    nomTitular = enviaments[i].interessat.raoSocial;
					} else {
					    nomTitular = enviaments[i].interessat.organNom;
					}
					if (enviaments[i].interessat.llinatge1 != null) {
					    llinatge1 = enviaments[i].interessat.llinatge1;
					}
					if (enviaments[i].interessat.llinatge2 != null) {
					    llinatge2 = enviaments[i].interessat.llinatge2;
					}
					if (enviaments[i].interessat.nif != null) {
					    nif = enviaments[i].interessat.nif;
					} else if (enviaments[i].interessat.dir3Codi != null) {
					    nif = enviaments[i].interessat.dir3Codi;
					} else {
					    nif = enviaments[i].interessat.documentNum;
					}
					tableBody += '<td>' + nomTitular + ' ' + llinatge1 + ' ' + llinatge2 + '('+ nif +') </td>';


					//====== destinatari/representant ========
					var representants = '';
					var representant = enviaments[i].interessat.representant;
					if (representant) {
					    var nomDest = '',
					        llinatge1Dest = '',
					        llinatge2Dest = '',
					        nifDest = '';
					    if (representant.nom != null) {
					        nomDest = representant.nom;
					    } else if (representant.raoSocial != null) {
					        nomDest = representant.raoSocial;
					    } else {
					        nomDest = representant.organNom;
					    }
					    if (representant.llinatge1 != null) {
					        llinatge1Dest = representant.llinatge1;
					    }
					    if (representant.llinatge2 != null) {
					        llinatge2Dest = representant.llinatge2;
					    }
					    if (representant.nif != null) {
					        nifDest = representant.nif;
					    } else if (representant.dir3Codi != null) {
					        nifDest = representant.dir3Codi;
					    } else {
					        nifDest = representant.documentNum;
					    }
					    representants += nomDest + ' ' + llinatge1Dest + ' ' + llinatge2Dest + ' (' + nifDest + '), ';
					}
					if (representants != ''){
						//Remove last white space
						representants = representants.substr(0, representants.length-1);
						//Remove last comma
						representants = representants.substr(0, representants.length-1);
					} else {
						representants = '<spring:message code="notificacio.list.enviament.list.senserepresentants"/>';
					}
					tableBody += '<td>' + representants + '</td>';

					
					//============== estat/errors =========================
					tableBody += '<td>';
					tableBody += (enviaments[i].enviamentDatatEstat) ? notificacioEnviamentEstats[enviaments[i].enviamentDatatEstat] : '';
					if (notificacio.error) {
						var errorTitle = '';
						if (notificacio.errorDescripcio) {
							errorTitle = notificacio.errorDescripcio;
						}
						var escaped = notificacio.errorDescripcio.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
						tableBody += ' <span class="fa fa-warning text-danger" title="' + escaped + '"></span>';
					}

					tableBody += '</td>';

					
					//============== buttons =========================
					tableBody += '<td width="114px">';
//	 				if (enviaments[i].notificaCertificacioData != null) {
//	 					tableBody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + enviaments[i].id + '/certificacioDescarregar"/>" class="btn btn-default btn-sm fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.accio.descarregar.certificacio"/>"><span class="fa fa-download"></span></a>';
//	 				} else if (enviaments[i].notificacio.estat == 'REGISTRADA' &&
//	 						(enviaments[i].registreEstat == 'DISTRIBUIT' || enviaments[i].registreEstat == 'OFICI_EXTERN' || enviaments[i].registreEstat == 'OFICI_SIR')) {
//	 					tableBody += '<a href="<c:url value="/notificacio/' + rowData.id + '/enviament/' + enviaments[i].id + '/justificantDescarregar"/>" class="btn btn-default btn-sm fileDownloadSimpleRichExperience" title="<spring:message code="enviament.info.accio.descarregar.justificant"/>"><span class="fa fa-download"></span></a>';
//	 				}
	 				tableBody += '<a href="<c:url value="/expedient/${contingut.id}/enviamentDetails/' + rowData.id + '/enviamentInfo/' + enviaments[i].id + '"/>" data-toggle="modal" class="btn btn-default btn-sm"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>';
					tableBody += '</td>';
					tableBody += '</tr>';
				}
			}

		    

			
			$('table tbody', td).append(tableBody);
			$('table tbody td').webutilModalEval();
		});
	});
	
});//################################################## document ready END ##############################################################




</script>


<table
	id="taulaNotificacions"
	data-url="<c:url value="/expedient/${expedientId}/notificacio/datatable"/>"
	data-paging-enabled="false"
	class="table table-bordered table-striped"
	style="width:100%"
	data-row-info="true">
	<thead>
		<tr>
			<th data-col-name="error" data-visible="false"></th>
			<th data-col-name="notificacio" data-visible="false"></th>
			<th data-col-name="publicacio" data-visible="false"></th>
			<th data-col-name="notificacioIdentificador" data-visible="false"></th>
			<th data-col-name="tipus" data-orderable="false" data-template="#cellNotficicacioTipusTemplate" width="15%">
				<spring:message code="contingut.enviament.columna.tipus"/>
				<script id="cellNotficicacioTipusTemplate" type="text/x-jsrender">
					{{if tipus == 'MANUAL'}}
						<spring:message code="contingut.enviament.notificacio.man"/>
					{{else tipus == 'COMUNICACIO'}}
						<spring:message code="contingut.enviament.comunicacio"/>
					{{else}}
						<spring:message code="contingut.enviament.notificacio.elec"/>
					{{/if}}
				</script>
			</th>
			<th data-col-name="createdDate" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.enviament.columna.creadael"/></th>
			<th data-col-name="dataEnviada" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.enviament.columna.enviadael"/></th>
			<th data-col-name="dataFinalitzada" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.enviament.columna.dataFinalitzada"/></th>
			<th data-col-name="assumpte" data-orderable="false" width="25%"><spring:message code="contingut.enviament.columna.concepte"/></th>
			<th data-col-name="destinatari" data-orderable="false" data-visible="false" width="20%">
				<spring:message code="contingut.enviament.columna.destinatari"/>
			</th>
			<th data-col-name="documentId" data-visible="false"/>
			<th data-col-name="documentNom" data-orderable="false" width="25%"><spring:message code="contingut.enviament.columna.document"/></th>
			
			<th data-col-name="notificacioEstat" data-visible="false"></th>
			<th data-col-name="estat" data-template="#cellNotificacioEstatTemplate" data-orderable="false" width="10%">
				<spring:message code="contingut.enviament.columna.estat"/>
				<script id="cellNotificacioEstatTemplate" type="text/x-jsrender">
					{{if notificacioEstat == 'PENDENT'}}
						<span class="label label-warning"><span class="fa fa-clock-o"></span> <spring:message code="notificacio.notificacioEstat.enum.PENDENT"/></span>
						{{if error}} <span class="fa fa-warning text-danger" title="<spring:message code="contingut.enviament.error"/>"></span> {{/if}}
					{{else notificacioEstat == 'ENVIADA'}}
						{{if error}}
							<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.ENVIADA"/></span>
						{{else}}
							<span class="label label-info"><span class="fa fa-envelope-o"></span> <spring:message code="notificacio.notificacioEstat.enum.ENVIADA"/></span>
						{{/if}}
					{{else notificacioEstat == 'REGISTRADA'}}
						{{if error}}
							<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.REGISTRADA"/></span>
						{{else}}
							<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.REGISTRADA"/></span>
						{{/if}}
					{{else notificacioEstat == 'FINALITZADA'}}
						{{if error}}
								<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.FINALITZADA"/></span>
							{{else}}
								<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.FINALITZADA"/></span>
						{{/if}}
					{{else notificacioEstat == 'PROCESSADA'}}
						{{if error}}
								<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.PROCESSADA"/></span>
							{{else}}
								<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.PROCESSADA"/></span>
						{{/if}}
					{{/if}}
					{{if notificacioEstat == 'PROCESSADA' || notificacioEstat == 'FINALITZADA'}}
					{{:~recuperarEstatEnviament(id)}}
					<p class="estat_{{:id}}"  style="display:inline"></p>
					{{/if}}
										</script>
			</th>
			<th data-col-name="id" data-orderable="false" data-template="#cellNotifiacioAccionsTemplate" width="10%">
				<script id="cellNotifiacioAccionsTemplate" type="text/x-jsrender">
					<div class="dropdown">
						<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
						<ul class="dropdown-menu">
							<li><a href="<c:url value="/document/{{:documentId}}/notificacio/{{:id}}/info?contingutNavigationId=${contingut.id}"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
							{{if notificacioEstat != 'PROCESSADA'}}
								<li><a href="<c:url value="/document/notificacio/actualitzarEstat/{{:notificacioIdentificador}}?contingutNavigationId=${contingut.id}"/>"><span class="fa fa-refresh"></span>&nbsp;&nbsp;<spring:message code="enviament.info.accio.ectualitzar.estat"/></a></li>
							{{/if}}
							{{if tipus == 'MANUAL'}}
								<li><a href="<c:url value="/expedient/${expedientId}/notificacio/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="<c:url value="/expedient/${expedientId}/notificacio/{{:id}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="contingut.confirmacio.esborrar.notificacio"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							{{/if}}
						</ul>
					</div>
				</script>
			</th>
		</tr>
	</thead>
</table>