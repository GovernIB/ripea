<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="es.caib.ripea.core.api.dto.HistoricTipusEnumDto" %>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="decorator.menu.accions.estadistiques"/></title>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.2.3/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-select/1.3.1/js/dataTables.select.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/chartjs/2.9.3/Chart.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">
		// COMMON FUNCTIONS
        function getRandomColor() {
            var letters = '0123456789ABCDEF'.split('');
            var color = '#';
            for (var i = 0; i < 6; i++ ) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        }
	
		var nom_metriques = {
				'numExpedientsCreats': "<spring:message code="historic.metriques.enum.EXPEDIENTS_CREATS"/>",
				'numExpedientsCreatsTotal': "<spring:message code="historic.metriques.enum.EXPEDIENTS_CREATS_ACUM"/>",
				'numExpedientsTancats': "<spring:message code="historic.metriques.enum.EXPEDIENTS_TANCATS"/>",
				'numExpedientsTancatsTotal': "<spring:message code="historic.metriques.enum.EXPEDIENTS_TANCATS_ACUM"/>",
				'numExpedientsAmbAlertes': "<spring:message code="historic.metriques.enum.EXPEDIENTS_AMB_ALERTES"/>",
				'numExpedientsAmbErrorsValidacio': "<spring:message code="historic.metriques.enum.EXPEDIENTS_AMB_ERRORS_VALID"/>",
				'numDocsPendentsSignar': "<spring:message code="historic.metriques.enum.DOCUMENTS_PENDENTS_SIGNAR"/>",
				'numDocsSignats': "<spring:message code="historic.metriques.enum.DOCUMENTS_SIGNATS"/>",
				'numDocsPendentsNotificar': "<spring:message code="historic.metriques.enum.DOCUMENTS_PENDENTS_NOTIFICAR"/>",
				'numDocsNotificats': "<spring:message code="historic.metriques.enum.DOCUMENTS_NOTIFICATS"/>"
		}
		var showingTables = ${ historicFiltreCommand.showingTables };
		var showDadesEntitat = ${showDadesEntitat};
		var showDadesOrganGestor = ${showDadesOrganGestor};
		var showDadesUsuari = ${showDadesUsuari};
		var showDadesInteressat = ${showDadesInteressat};
		
		var showingDadesActuals = ${ showingDadesActuals };
		var metriquesSeleccionades = [];
		<c:forEach items="${ metriquesSeleccionades }" var="metrica">
			metriquesSeleccionades.push("${ metrica }");
	    </c:forEach>
		console.log(metriquesSeleccionades);
		
		var usuarisSeleccionats = [];
		<c:forEach items="${ usuarisSeleccionats }" var="codiUsuari">
			usuarisSeleccionats.push("${ codiUsuari }");
	    </c:forEach>
		console.log(usuarisSeleccionats);
		
		function inicialitzarSeccioEntitat() {
			// function to update our chart
		    function buildChartEntitat(chart, url, data) {
		        var data = data || {};
		
		        $.getJSON(url, data).done(function(response) {
		        	response.sort((a, b) => (a.data > b.data))
		        	if (response.length > 0) {
			        	var yLabels = response.map(item => new Date(item.data).toLocaleDateString("es"));
			        	
			        	var i = 0;
			            chart.data.labels = yLabels;
			        	for (var nomSerie in response[0]) {
			        		if (nomSerie.substring(0, 3) == "num"){
			        			chart.data.datasets.push({
			        				'data': response.map(item => item[nomSerie]),
			        				'label': nom_metriques[nomSerie],
			        				'backgroundColor': "rgba(0,0,0,0.0)",
			        				'borderColor': getRandomColor()
			        				});
			        			i = i + 1;
			        		}	
			        	}
		        	}
		        	
		            chart.update(); // finally update our chart
		        });
		    }
			if (!showingTables) {
				var ctx = 'chartEntitat';
				var chartEntitat = new Chart(ctx, {
				    type: 'line',
				    data: {
				        labels: [],
				        datasets: [], 
				        color: [
				            'red',    // color for data at index 0
				            'blue',   // color for data at index 1
				            'green',  // color for data at index 2
				            'black',  // color for data at index 3
				            //...
				        ]
				    },
				    options: {
				        scales: {
				            yAxes: [{
				                ticks: {
				                    beginAtZero: true
				                }
				            }]
				        }
				    }
				});
	
				buildChartEntitat(chartEntitat, "historic/chart/entitat", {});
			}
		}
		
		function inicialitzarSeccioOrgansGestors() {
			var tablesTitles = {};
			var dataTablesOrgansGestors = [];
			function buildTablesMetrics(data){
				console.log(data);
				var tableHeader = '<table class="table table-bordered table-striped table-hover style="width:100%">' +
						'<thead>' +
							'<tr>';
				tableHeader += '<th><spring:message code="historic.taula.header.data"/></th>';
				for (var organGestor in data) {
					tableHeader += '<th>' + organGestor + '</th>';
				}
				tableHeader += '</tr></thead><tbody>';
				
				var dates = [];
				var iSerie = 0;
				while (dates.length == 0 && iSerie < Object.keys(data).length ){
					var serie = data[Object.keys(data)[iSerie]];
					dates = Object.keys(serie);
					iSerie ++;
				}
				dates.sort();
				dates = dates.map(item => new Date(item).toLocaleDateString("es"));
				var tableBody = '';
				dates.forEach(function(date){
					var row = '<tr>';
					row += '<td>' + date + '</td>'
					for (var organGestor in data) {
						var value = data[organGestor][date] != null ? data[organGestor][date] : 0;
						row += '<td>' + value + '</td>';
					}
					row += '</tr>';
					tableBody += row;
				});
									
				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
				
			}
			
			function create_chart_organGestors(metric, data) {
				var dates = [];
				var iSerie = 0;
				while (dates.length == 0 && iSerie < Object.keys(data).length ){
					var serie = data[Object.keys(data)[iSerie]];
					dates = Object.keys(serie);
					iSerie ++;
				}
				dates.sort();
				var datasets = []
				for (var organGestor in data) {
					var dataset_data = [];
					dates.forEach(function(date){
						dataset_data.push(
								data[organGestor][date] != null ? data[organGestor][date] : 0
								);						
					});
					
        			datasets.push({
        				'data': dataset_data,
        				'label': organGestor,
        				'backgroundColor': "rgba(0,0,0,0.0)",
        				'borderColor': getRandomColor()
        				});
				}

				var ctx = 'chart-' + metric;
				var chart = new Chart(ctx, {
				    type: 'line',
				    data: {
				        labels: dates.map(item => new Date(item).toLocaleDateString("es")),
				        datasets: datasets, 
				    },
				    options: {
				        scales: {
				            yAxes: [{
				                ticks: {
				                    beginAtZero: true
				                }
				            }]
				        }
				    }
				});
			}
			
			function create_chart_current(metric, data) {
				var labels = []
				var values = []
				var colors = []
				for (var organGestor in data) {
					labels.push(organGestor);
					values.push(data[organGestor]);
					colors.push(getRandomColor())
				}
				
				console.log(values);
				console.log(labels);
				var ctx = 'chart-current-' + metric;
				var chart = new Chart(ctx, {
				    type: 'doughnut',
				    data: {
				        datasets: [{
				            data: values,
				            backgroundColor: colors
				        }],

				        // These labels appear in the legend and in the tooltips when hovering different arcs
				        labels: labels
				    },
			        options: {
			            title: {
			              display: true,
			              text: tablesTitles[metric]
			            }
			          }
				});
			}
			function updateContentOrganGestors(metrics) {
				if (metrics.length == 0) {
					return;
				}
				if ( showingDadesActuals ) {
					$.ajax({
						type: "POST",
						url: 'historic/actual/organgestors',
						data: {
							metrics: metrics
						},
						success: function(response) {
							for (var metric in response) {
								var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
								$('#div-dades-organgestors').append(canvas);
								create_chart_current(metric, response[metric]);
							}
						}
					});
				} else{
					$.ajax({
						type: "POST",
						url: 'historic/organgestors',
						data: {
							metrics: metrics
						},
						success: function(response) {
// 							dataTablesOrgansGestors.forEach(function (dataTable) {
// 								dataTable.destroy();
// 							});
// 							dataTablesOrgansGestors = [];
							$('#div-dades-organgestors').html("");
							for (var metric in response) {
								var title = '<h2>' + tablesTitles[metric] + '</h2>'
								if (showingTables) {
									var htmlTable = buildTablesMetrics(response[metric]);
									
									$('#div-dades-organgestors').append(title + htmlTable);		
								} else {
									var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
									$('#div-dades-organgestors').append(title + canvas);
									
									create_chart_organGestors(metric, response[metric]);
								}
							}
						}
					});
				}
			}
			
			$(function () {
				$.ajax({
					url: webutilAjaxEnumPath('HistoricMetriquesEnumDto'),
					success: function(resposta) {
						for (var i = 0; i < resposta.length; i++) {
							var enumItem = resposta[i];
							tablesTitles[enumItem['value']] = enumItem['text'];
							var htmlCheckBox = 
								'<div class="checkbox">' +
				  					'<label>' +
				  						'<input class="checkbox-metric" type="checkbox" value="' + enumItem['value'] + '"';
				  			if (metriquesSeleccionades.includes(enumItem['value'])) {
				  				htmlCheckBox += ' checked>';
				  				
				  			} else {
				  				htmlCheckBox += '>';
				  			}
		  					htmlCheckBox += 
			  						enumItem['text'] + 
				  					'</label>' +
								'</div>';
							$('form#form-estadistics').prepend(htmlCheckBox);
						}
					}
				});
						
				updateContentOrganGestors(metriquesSeleccionades);
				$("form#form-estadistics").on('submit', function(){
					var metrics = $(this).find('.checkbox-metric:checked').map(function(){return $(this).val();}).get();
					updateContentOrganGestors(metrics);
					return false;
				});				
			});
		}
		
		function inicialitzarSeccioUsuaris() {
			var dataTables = [];
			function usuari_construirTaula(dades, codiUsuari){
				var tableHeader = '<table id="table-user-' + codiUsuari + '" class="table table-bordered table-striped table-hover style="width:100%" >' +
						'<thead>' +
							'<tr>';
				tableHeader += '<th><spring:message code="historic.taula.header.data"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
	
				tableHeader += '</tr></thead><tbody>';
				
				
				var dates = dades.map(item => new Date(item.data).toLocaleDateString("es"));
				
				var tableBody = '';
				dades.forEach(function(registre){
					var row = '<tr>';
					row += '<td data-sort="' + registre.data + '">' + new Date(registre.data).toLocaleDateString("es") + '</td>'
					row += '<td>' + registre.numExpedientsCreats + '</td>'
					row += '<td>' + registre.numExpedientsCreatsTotal + '</td>'
					row += '<td>' + registre.numExpedientsTancats + '</td>'
					row += '<td>' + registre.numExpedientsTancatsTotal + '</td>'
					row += '<td>' + registre.numTasquesTramitades + '</td>'
					
					row += '</tr>';
					tableBody += row;
				});
									
				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
			}
		
			function updateContentUsuaris(usuaris) {
				if (usuaris.length == 0){
					return;
				}
				if ( showingDadesActuals ) {
// 					$.ajax({
//						type: "POST",
//						url: 'historic/actual/organgestors',
//						data: {
//							metrics: metrics
//						},
//						success: function(response) {
//							for (var metric in response) {
//								var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
//								$('#div-dades-organgestors').append(canvas);
//								create_chart_current(metric, response[metric]);
//							}
//						}
//					});
				} else {
					$.ajax({
						type: "POST",
						url: 'historic/usuaris/dades/',
						data: {
							usuaris: usuaris
						},
						success: function (response) {
							dataTables.forEach(function (dataTable) {
								dataTable.destroy();
							});
							dataTables = [];
							$('#div-dades-usuaris').html("");
							for (usuariCodi in response){
								var htmlTable = usuari_construirTaula(response[usuariCodi], usuariCodi);
								var title = '<h2>' + usuariCodi + '</h2>'
								
								$('#div-dades-usuaris').append(title + htmlTable);
								var language = requestLocale;
								// Només acceptam es i ca com a llengues //
								if (language.startsWith("es")) {
									language = "es";
								} else {
									language = "ca";
								}

								
								var dataTable = $("#table-user-" + usuariCodi).DataTable({
									language: {
										url: webutilContextPath() + '/js/datatables/i18n/datatables.' + language + '.json'
									},
									paging: true,
									pageLength: 10,
									pagingStyle: 'page',
									lengthMenu: [10, 20, 50, 100, 250],
									dom: '<"row"<"col-md-6"i><"col-md-6"<"botons">>>' + 't<"row"<"col-md-3"l><"col-md-9"p>>',
									select: {
										style: 'multi',
										selector: 'td:first-child',
										info: false
									}
								});
								
								dataTables.push(dataTable);
							}								
						}
					});
				}
			}
			updateContentUsuaris(usuarisSeleccionats);
			$("form#form-estadistics-usuaris").on('submit', function(){
				var usuaris = $("#input-usuaris").find(':selected').map(function(){return $(this).val();}).get();;
				var names = $("#input-usuaris").find(':selected').map(function(){return $(this).html();}).get();;
				updateContentUsuaris(usuaris);
				return false;
			});		
		}
		
		$(function () {
			if (showDadesEntitat) {
				inicialitzarSeccioEntitat();
			}
			
			if (showDadesOrganGestor) {
				inicialitzarSeccioOrgansGestors();
			}
			
			if (showDadesUsuari) {
				inicialitzarSeccioUsuaris();
			}
			
			if (showDadesInteressat) {
				
			}
		});
		
	</script>
</head>
<body>
<c:url value="/organgestorajax/organgestor" var="urlConsultaOrgansInicial"/>
<c:url value="/organgestorajax/organgestor" var="urlConsultaOrgansLlistat"/>
<c:url value="/metaexpedientajax/metaexpedient" var="urlConsultaMetaExpedientsInicial"/>
<c:url value="/metaexpedientajax/metaexpedients" var="urlConsultaMetaExpedientsLlistat"/>
	<form:form action="" method="post" cssClass="well" commandName="historicFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<rip:inputDate name="dataInici" inline="true" placeholderKey="historic.filtre.data.inici"/>
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataFi" inline="true" placeholderKey="historic.filtre.data.fi"/>
			</div>		
			<div class="col-md-4">
				<rip:inputSuggest 
					name="organGestorsIds" 
					urlConsultaInicial="${urlConsultaOrgansInicial}" 
					urlConsultaLlistat="${urlConsultaOrgansLlistat}" 
					inline="true" 
 					placeholderKey="historic.filtre.organsGestors"
 					suggestValue="id"
 					suggestText="nom"/>
			</div>
			<div class="col-md-4">
				<rip:inputSuggest
					name="metaExpedientsIds" 
					urlConsultaInicial="${urlConsultaMetaExpedientsInicial}" 
					urlConsultaLlistat="${urlConsultaMetaExpedientsLlistat}" 
					inline="true"
					placeholderKey="historic.filtre.metaExpedients"
					suggestValue="id"
					suggestText="nom"/>

			</div>
		</div>
		<div class="row">
			<div class="col-md-8">
				<rip:inputSelect name="dadesMostrar" 
								 optionEnum="HistoricDadesMostrarEnum" 
								 multiple="true"
								 emptyOption="false" 
								 placeholderKey="historic.filtre.dadesMostrar" 
								 inline="true"/>
			</div>
			<div class="col-md-1">
<%-- 				<spring:message code="estadistiques.filtre.format.exportacio" /> --%>
			</div>
			<div class="col-md-3 pull-right">
				<div class="pull-right">
<!-- 					<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button> -->
<%-- 					<a href="${unitatCodiUrlPrefix}bustiaAdmin/excelUsuarisPerBustia" class="btn btn-success"> --%>
<%-- 						<span class="fa fa-download"></span>&nbsp;<spring:message code="comu.boto.exportar" /> --%>
<!-- 					</a> -->
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<div class="btn-group" data-toggle="buttons">
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.showingTables == false}">active</c:if>"> 
						<form:radiobutton path="showingTables" value="false"/>
						<i class="fa fa-bar-chart" aria-hidden="true"></i> <spring:message code="historic.filtre.mostraGrafics"/>
					</label> 
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.showingTables == true}">active</c:if>"> 
						<form:radiobutton path="showingTables" value="true"/>
						<i class="fa fa-table" aria-hidden="true"></i> <spring:message code="historic.filtre.mostraTaules"/>
					</label> 
			
				</div>
			</div>
			<div class="col-md-3">
				<div class="btn-group" data-toggle="buttons">
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.tipusAgrupament == null}">active</c:if>"> 
						<form:radiobutton path="tipusAgrupament"/>
						<i class="fa fa-clock-o"></i> <spring:message code="historic.filtre.mostraDadesActuals"/>
					</label> 
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.tipusAgrupament == 'DIARI'}">active</c:if>"> 
						<form:radiobutton path="tipusAgrupament" value="DIARI"/>
						<i class="fa fa-calendar"></i> <spring:message code="historic.filtre.mostraDadesPerDia"/>
					</label> 
					<label class="btn btn-default form-check-label <c:if test="${historicFiltreCommand.tipusAgrupament == 'MENSUAL'}">active</c:if>"> 
						<form:radiobutton path="tipusAgrupament" value="MENSUAL"/>						 
						<i class="fa fa-calendar-o"></i> <spring:message code="historic.filtre.mostraDadesPerMes"/>
					</label>
			
				</div>
			</div>		
		</div>
	</form:form>
	<c:if test="${showDadesEntitat}">
		<h1><spring:message code="historic.titol.seccio.entitat"/></h1>
		<c:if test="${historicFiltreCommand.showingTables}">
			<table
				id="taulaDades"
				data-toggle="datatable" 
				data-url="<c:url value="/historic/expedient/datatable"/>" 
				class="table table-bordered table-striped table-hover" 
				data-default-order="0" 
				data-default-dir="desc"
				data-selection-enabled="true"
				data-save-state="true"
				data-mantenir-paginacio="${mantenirPaginacio}"
				style="width:100%">
				<thead>
					<tr>
						<th data-col-name="data" data-type="date" data-converter="date" nowrap><spring:message code="historic.taula.header.data"/></th>
						<th data-col-name="numExpedientsCreats" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsCreats"/></th>
						<th data-col-name="numExpedientsCreatsTotal" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>
	<%-- 					<th data-col-name="numExpedientsOberts" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsOberts"/></th> --%>
	<%-- 					<th data-col-name="numExpedientsObertsTotal" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsObertsTotal"/></th> --%>
						<th data-col-name="numExpedientsTancats" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsTancats"/></th>
						<th data-col-name="numExpedientsTancatsTotal" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>
	<%-- 					<th data-col-name="numExpedientsAmbAlertes" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsAmbAlertes"/></th> --%>
	<%-- 					<th data-col-name="numExpedientsAmbErrorsValidacio" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsAmbErrorsValidacio"/></th> --%>
	<%-- 					<th data-col-name="numDocsPendentsSignar" data-orderable="false"><spring:message code="historic.taula.header.numDocsPendentsSignar"/></th> --%>
						<th data-col-name="numDocsSignats" data-orderable="false"><spring:message code="historic.taula.header.numDocsSignats"/></th>
	<%-- 					<th data-col-name="numDocsPendentsNotificar" data-orderable="false"><spring:message code="historic.taula.header.numDocsPendentsNotificar"/></th> --%>
						<th data-col-name="numDocsNotificats" data-orderable="false"><spring:message code="historic.taula.header.numDocsNotificats"/></th>
					</tr>
				</thead>
			</table>
		</c:if>
		<c:if test="${not historicFiltreCommand.showingTables}">
			<canvas id="chartEntitat" width="400" height="100"></canvas>
		</c:if>
	</c:if>
	<c:if test="${showDadesOrganGestor}">
		<h1><spring:message code="historic.titol.seccio.organGestor"/></h1>
		<div class="row">
			<div class="col-md-2">
			<form id="form-estadistics" class="well">
				<div class="row">
					<div class="col-md-12">
						<div class="pull-right">
							<button type="submit" name="accio" value="filtrar" class="btn btn-primary">
								<span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/>
							</button>
						</div>
					</div>
				</div>
			</form>
			</div>
			<div id="div-dades-organgestors" class="col-md-10">
			</div>
		</div>

	</c:if>
	<c:if test="${showDadesUsuari}">
		<h1><spring:message code="historic.titol.seccio.usuari"/></h1>
		<div class="row">
			<div class="col-md-2">
				<form id="form-estadistics-usuaris" class="well">
					<div class="row">
						<div class="col-md-12">
							<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
							<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
							<c:set var="placeholderText"><spring:message code="historic.filtre.select.usuari"/></c:set>
							<select name="usuaris" cssClass="form-control" id="input-usuaris"
										style="width:100%" data-toggle="suggest"
										data-placeholder="${placeholderText}"
										data-minimum-input-length="3"
										data-url-llistat="${urlConsultaLlistat}" 
										data-url-inicial="${urlConsultaInicial}"
									    multiple="true"
									    data-placeholder="${placeholderText}"
										data-current-value="${fn:join(usuarisSeleccionats, ",")}" 
										data-suggest-value="codi"
										data-suggest-text="nom"
										data-suggest-text-addicional="nif" 
										data-url-param-addicional=""> </select>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="col-md-12">
							<div class="pull-right">
								<button type="submit" name="accio" value="filtrar" class="btn btn-primary">
									<span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/>
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div id="div-dades-usuaris" class="col-md-10">
			</div>
		</div>
	</c:if>
	<c:if test="${showDadesInteressat}">
		<h1><spring:message code="historic.titol.seccio.interessat"/></h1>
	</c:if>
</body>
</html>