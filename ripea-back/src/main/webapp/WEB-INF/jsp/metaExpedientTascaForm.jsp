<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty metaExpedientTascaCommand.id}">
		<c:set var="titol"><spring:message code="metaexpedient.tasca.form.titol.crear"/></c:set>
		<c:url var="datatableValidacionsURL" value="/metaExpedient/${metaExpedientTascaCommand.metaExpedientId}/validacions/0/datatable"/>
		<c:url var="tascaSaveURL" value="/metaExpedient/${metaExpedientTascaCommand.metaExpedientId}/validacions/0/newValidacio"/>
		<c:url var="validacioAccioURL" value="/metaExpedient/${metaExpedientTascaCommand.metaExpedientId}/tasca/0/validacioAccio"/>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${consultar}">
				<c:set var="titol"><spring:message code="metaexpedient.tasca.form.titol.consultar"/></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="metaexpedient.tasca.form.titol.modificar"/></c:set>
			</c:otherwise>
		</c:choose>
		<c:url var="datatableValidacionsURL" value="/metaExpedient/${metaExpedientTascaCommand.metaExpedientId}/validacions/${metaExpedientTascaCommand.id}/datatable"/>
		<c:url var="tascaSaveURL" value="/metaExpedient/${metaExpedientTascaCommand.metaExpedientId}/validacions/${metaExpedientTascaCommand.id}/newValidacio"/>
		<c:url var="validacioAccioURL" value="/metaExpedient/${metaExpedientTascaCommand.metaExpedientId}/tasca/${metaExpedientTascaCommand.id}/validacioAccio"/>
	</c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<rip:modalHead/>
	<script type="application/javascript">
		var colorsEstats = {};
		<c:forEach items="${expedientEstats}" var="estat">
			colorsEstats['${estat.id}'] = '${estat.color}';
		</c:forEach>

		function showColor(element) {
			const id = element.id;
			const color = colorsEstats[id];
			if (!color) {
				return $('<span class="no-icon"></span><span>' + element.text + '</span>');
			}
			return $('<span class="color-icon" style="background-color: ' + color + '"></span><span>' + element.text + '</span>');
		}
	</script>
	<script type="text/javascript">

		// Guardar todas las opciones en un array
		var todasOpciones = null;

		$(document).ready(function() {

			$('#taulavalidacions').on('draw.dt', function () {
				$('.dataTables_info').hide();
			});

			todasOpciones = Array.from(document.getElementById('tipusValidacio').options);
			
			$('#itemValidacio').on('change', function () {
				$.ajax({
					url: '<c:url value="/metaExpedient/${metaExpedient.id}/getElements/"/>'+$(this).val(),
					type: "GET",
					contentType: "application/json",
					success: function (data) {
						listGenericDtoToSelect2(data, "itemId");
					}
				});
				validacionesPorTipoElemento($(this).val());
			});
			
			$('#guardaValidacio').on('click', function () {
				
				let tipo = $('#itemValidacio').val();
				let elem = $('#itemId').val();
				let validacion = $('#tipusValidacio').val();
				let elemText = $( "#itemId option:selected" ).text();

				if(esNuloBlancoOIndefinido(tipo) || 
				   esNuloBlancoOIndefinido(elem) ||
				   esNuloBlancoOIndefinido(validacion)) {
					alert('<spring:message code="metaexpedient.tasca.validacio"/>');
				} else {
					$.ajax({
				        type: 'GET',
				        url: '${tascaSaveURL}',
				        data: {tipus: tipo, itemId: elem, itemNom: elemText, validacio: validacion},
				        complete: function (resultat) {
				        	webutilRefreshMissatges();
				        	$('#taulavalidacions').dataTable().fnDraw();
						}
				    });
				}				
			});

			validacionesPorTipoElemento($("#itemValidacio").val());
		});

		function validacionesPorTipoElemento(tipoElem) {

			let select = document.getElementById('tipusValidacio');
			
			// Eliminar todas las opciones
			while (select.options.length > 0) {
				select.remove(0);
			}

			if ("DADA"==tipoElem) {
				//Solo opcion aportado
				select.appendChild(todasOpciones[1]);
			} else {
				select.appendChild(todasOpciones[0]);
				select.appendChild(todasOpciones[1]);
				select.appendChild(todasOpciones[2]);
				select.appendChild(todasOpciones[3]);
				select.appendChild(todasOpciones[4]);
			}
			select.selectedIndex = 0;
		}
		
		function accioValidacio(validacioId, accio) {
			$.ajax({
		        type: 'GET',
		        url: '${validacioAccioURL}',
		        data: {validacioId: validacioId, accioRealitzar: accio},
		        complete: function (resultat) {
		        	webutilRefreshMissatges();
		        	$('#taulavalidacions').dataTable().fnDraw();
				}
		    });	
		}

	</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaExpedient.id}/tasca/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="metaExpedientTascaCommand">
		
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="metaExpedientId"/>
		
		<ul class="nav nav-tabs">
			<li class="active">
				<a href="#info" data-toggle="tab"><spring:message code="contingut.tab.infoTasca"/></a>
			</li>
			<li>
				<a href="#validacions" data-toggle="tab"><spring:message code="contingut.tab.validacions"/></a>
			</li>
		</ul>
		
		<div class="tab-content" style="padding-top: 15px;">
		
			<div class="tab-pane active in" id="info">
				<rip:inputText name="codi" textKey="metaexpedient.tasca.form.camp.codi" required="true" readonly="${bloquejarCamps}"/>
				<rip:inputText name="nom" textKey="metaexpedient.tasca.form.camp.nom" required="true" readonly="${bloquejarCamps}"/>
				<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
				<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
					name="responsable" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					textKey="metaexpedient.tasca.form.camp.responsable"
					suggestValue="codi"
					suggestText="nom"
					placeholderKey="metaexpedient.tasca.form.camp.responsable"
					disabled="${bloquejarCamps}"/>	
				<rip:inputText name="duracio" textKey="tasca.list.column.duracio" comment="tasca.list.column.duracio.tip" tooltip="true" tooltipMsg="tasca.list.column.duracio.tip" readonly="${bloquejarCamps}"/>
				<rip:inputTextarea name="descripcio" textKey="metaexpedient.tasca.form.camp.descripcio" required="true" disabled="${bloquejarCamps}"/>
				<rip:inputSelect id="prioritat" name="prioritat" optionEnum="PrioritatEnumDto" emptyOption="false" textKey="contingut.expedient.form.camp.prioritat" templateResultFunction="showColorPriritats"/>
				<rip:inputSelect id="estatIdCrearTasca" name="estatIdCrearTasca" textKey="metaexpedient.tasca.form.camp.estat.crearTasca" emptyOption="true" optionItems="${expedientEstats}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${bloquejarCamps}" templateResultFunction="showColor" />
				<rip:inputSelect id="estatIdFinalitzarTasca" name="estatIdFinalitzarTasca" textKey="metaexpedient.tasca.form.camp.estat.finalitzarTasca" emptyOption="true" optionItems="${expedientEstats}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${bloquejarCamps}" templateResultFunction="showColor" />
			</div>
			
			<div class="tab-pane" id="validacions">
				
				<fieldset>
					<legend><spring:message code="metaexpedient.tasca.nova.validacio"/></legend>
				    <rip:inputSelect id="itemValidacio"
				   		name="itemValidacio"
				    	emptyOption="false"
				    	textKey="metaexpedient.tasca.validacio.itemValidacio"
				    	required="true"
				    	optionItems="${itemValidacioOptions}"
				    	optionValueAttribute="value"
				    	optionTextKeyAttribute="text"/>
				    <rip:inputSelect id="itemId"
				    	name="itemId"
				    	emptyOption="true"
				    	textKey="metaexpedient.tasca.validacio.itemNom"
				    	required="true"				    	
				    	optionItems="${itemsOptions}"
				    	optionValueAttribute="id"
				    	optionTextAttribute="nom"/>
				    <rip:inputSelect id="tipusValidacio"
				    	name="tipusValidacio"
				    	emptyOption="true"
				    	textKey="metaexpedient.tasca.validacio.tipusValidacio"
				    	required="true"
				    	optionItems="${tipusValidacioOptions}"
				    	optionValueAttribute="value"
				    	optionTextKeyAttribute="text"/>
				    <button type="button" id="guardaValidacio" class="btn btn-primary pull-right">
				    	<span class="fa fa-plus"></span> <spring:message code="comu.boto.afegir"/>
				    </button>
				</fieldset>
				
				<fieldset>
					<legend><spring:message code="metaexpedient.tasca.validacions"/></legend>
					<table id="taulavalidacions"
						data-toggle="datatable"
						data-url="${datatableValidacionsURL}"
						data-paging-enabled="false"
						data-default-order="0"
						data-default-dir="asc"
						class="table table-bordered table-striped"
						style="width:100%">
						<thead>
							<tr>
							<th data-col-name="id" data-visible="false"></th>
							<th data-col-name="itemValidacio" data-orderable="false" data-template="#cellValidacioTemplate" width="20%">
								<spring:message code="metaexpedient.tasca.validacio.itemValidacio"/>
								<script id="cellValidacioTemplate" type="text/x-jsrender">
								{{if itemValidacio == 'DADA'}}
									<spring:message code="metaexpedient.tasca.validacio.tipus.DADA"/>
								{{else itemValidacio == 'DOCUMENT'}}
									<spring:message code="metaexpedient.tasca.validacio.tipus.DOCUMENT"/>
								{{/if}}
								</script>
							</th>
							<th data-col-name="itemNom" data-orderable="false" width="40%"><spring:message code="metaexpedient.tasca.validacio.itemNom"/></th>
							<th data-col-name="tipusValidacio" data-orderable="false" data-template="#cellTipusValidacioTemplate" width="30%">
								<spring:message code="metaexpedient.tasca.validacio.tipusValidacio"/>
								<script id="cellTipusValidacioTemplate" type="text/x-jsrender">
								{{if tipusValidacio == 'AP'}}
									<spring:message code="metaexpedient.tasca.validacio.enum.AP"/>
								{{else tipusValidacio == 'AP_FI'}}
									<spring:message code="metaexpedient.tasca.validacio.enum.AP_FI"/>
								{{else tipusValidacio == 'AP_FI_NI'}}
									<spring:message code="metaexpedient.tasca.validacio.enum.AP_FI_NI"/>
								{{else tipusValidacio == 'AP_FI_NF'}}
									<spring:message code="metaexpedient.tasca.validacio.enum.AP_FI_NF"/>
								{{/if}}
								</script>
							</th>
							<th data-col-name="activa" data-orderable="false" data-template="#cellActivaTemplate" width="5%">
								<spring:message code="metaexpedient.tasca.validacio.activa"/>							
								<script id="cellActivaTemplate" type="text/x-jsrender">
								{{if activa}}<span class="fa fa-check"></span>{{/if}}
								</script>
							</th>
							<th data-col-name="id" data-orderable="false" data-template="#cellValidacionsAccionsTemplate" width="5%">
								<script id="cellValidacionsAccionsTemplate" type="text/x-jsrender">
								<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									{{if activa}}
										<li><a href="Javascript:accioValidacio('{{:id}}', 'DESACTIVAR');"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></li>
									{{else !activa}}
										<li><a href="Javascript:accioValidacio('{{:id}}', 'ACTIVAR');"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
									{{/if}}
									<li><a href="Javascript:accioValidacio('{{:id}}', 'ELIMINAR');"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</ul>
								</div>
							</script>
							</th>
							</tr>
						</thead>
					</table>
				</fieldset>
			</div>
			
		</div>
		
		<div id="modal-botons" class="well">
			<c:if test="${!consultar}">
				<button type="submit" class="btn btn-success" <c:if test="${bloquejarCamps}">disabled</c:if>><span class="fa fa-save"></span>
					<c:choose>
						<c:when test="${empty metaExpedientTascaCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
						<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
					</c:choose>
				</button>	
			</c:if>	
			<a href="<c:url value="/metaExpedient/${metaExpedient.id}/tasca"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
