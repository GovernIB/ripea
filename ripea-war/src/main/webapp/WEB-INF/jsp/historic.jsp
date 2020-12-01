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

		var metricsDefinition = {
			'EXPEDIENTS_CREATS': {
				'attrname' : 'numExpedientsCreats',
				'text': "<spring:message code="historic.metriques.enum.EXPEDIENTS_CREATS"/>"
			},
			'EXPEDIENTS_CREATS_ACUM': {
				'attrname' : 'numExpedientsCreatsTotal',
				'text': "<spring:message code="historic.metriques.enum.EXPEDIENTS_CREATS_ACUM"/>"
			},
			'EXPEDIENTS_TANCATS': {
				'attrname' : 'numExpedientsTancats',
				'text': "<spring:message code="historic.metriques.enum.EXPEDIENTS_TANCATS"/>"
			},
			'EXPEDIENTS_TANCATS_ACUM': {
				'attrname' : 'numExpedientsTancatsTotal',
				'text': "<spring:message code="historic.metriques.enum.EXPEDIENTS_TANCATS_ACUM"/>"
			},
			'DOCUMENTS_SIGNATS': {
				'attrname' : 'numDocsSignats',
				'text': "<spring:message code="historic.metriques.enum.DOCUMENTS_SIGNATS"/>"
			},
			'DOCUMENTS_NOTIFICATS': {
				'attrname' : 'numDocsNotificats',
				'text': "<spring:message code="historic.metriques.enum.DOCUMENTS_NOTIFICATS"/>"
			},
			'TASQUES_TRAMITADES': {
				'attrname' : 'numDocsNotificats',
				'text': "<spring:message code="historic.metriques.enum.TASQUES_TRAMITADES"/>"
			}
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
		
		var interessatsSeleccionats = [];
		<c:forEach items="${ interessatsSeleccionats }" var="docNum">
		interessatsSeleccionats.push("${ docNum }");
	    </c:forEach>
		console.log(interessatsSeleccionats);
		
		var language = requestLocale;
		// Només acceptam es i ca com a llengues //
		if (language.startsWith("es")) {
			language = "es";
		} else {
			language = "ca";
		}
		
		function dataTableHistoric (selector) {
			return $(selector).DataTable({
				language: {
					url: webutilContextPath() + '/js/datatables/i18n/datatables.' + language + '.json'
				},
				paging: true,
				pageLength: 10,
				order: [[ 0, "desc" ]],
				pagingStyle: 'page',
				lengthMenu: [10, 20, 50, 100, 250],
				dom: '<"row"<"col-md-6"i><"col-md-6"<"botons">>>' + 't<"row"<"col-md-3"l><"col-md-9"p>>',
				select: {
					style: 'multi',
					selector: 'td:first-child',
					info: false
				}
			});
		}
		
		function chartPie(canvas, data, labels, backgroundColors, title) {
			return new Chart(canvas, {
			    type: 'doughnut',
			    data: {
			        datasets: [{
			            data: data,
			            backgroundColor: backgroundColors
			        }],

			        // These labels appear in the legend and in the tooltips when hovering different arcs
			        labels: labels
			    },
		        options: {
		            title: {
						display: true,
						text: title
		            }
		          }
			});
		}
		
		function Taules () {
			
			this.buildTableActualsPerMetaExpedient = function(data) {
				
				var tableHeader = '<table id="table-per-metaexpedients-' + this.taules.length + '" class="table table-bordered table-striped table-hover style="width:100%" >' +
				'<thead>' +
					'<tr>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
		
				tableHeader += '</tr></thead><tbody>';
				
				
				var tableBody = '';
				data.forEach(function(serie){
					var row = '<tr>';
					row += '<td>' + serie.metaExpedient.nom + '</td>';
					row += '<td>' + serie.EXPEDIENTS_CREATS + '</td>';
					row += '<td>' + serie.EXPEDIENTS_CREATS_ACUM + '</td>';
					row += '<td>' + serie.EXPEDIENTS_TANCATS + '</td>';
					row += '<td>' + serie.EXPEDIENTS_TANCATS_ACUM + '</td>';
					
					row += '</tr>';
					tableBody += row;
				});

				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;

			}
			
			this.buildTableActualsPerOrganGestor = function(data) {
				var mapOrgansGestors = {};
				
				data.forEach(function(serie){
					var organGestor = serie.metaExpedient.organGestor;
					var organGestorId = organGestor != null ? organGestor.id : 'Comu';
					if (organGestorId in mapOrgansGestors) {
						mapOrgansGestors[organGestorId].dades.push(serie);
						
					} else {
						mapOrgansGestors[organGestorId] = {
								organGestor: organGestor,
								dades: [serie]
						}
					}
				});
				
				var tableHeader = '<table id="table-per-organs-' + this.taules.length+ '" class="table table-bordered table-striped table-hover style="width:100%" >' +
				'<thead>' +
					'<tr>';
				tableHeader += '<th><spring:message code="historic.taula.header.organgestor"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
		
				tableHeader += '</tr></thead><tbody>';
				
				function sumListAttr(list, attrname) {
					var sum = 0;
					list.forEach(function(serie){
						sum += serie[attrname];
					});
					return sum;
				}
				var tableBody = '';
				for (var organGestorId in mapOrgansGestors) {
					var dataOrganGestor = mapOrgansGestors[organGestorId];
					var organGestorNom = dataOrganGestor.organGestor != null ? dataOrganGestor.organGestor.nom : 'Comú';
					var row = '<tr>';
					row += '<td>' + organGestorNom + '</td>';
					row += '<td>' + sumListAttr(dataOrganGestor.dades, 'EXPEDIENTS_CREATS') + '</td>';
					row += '<td>' + sumListAttr(dataOrganGestor.dades, 'EXPEDIENTS_CREATS_ACUM') + '</td>';
					row += '<td>' + sumListAttr(dataOrganGestor.dades, 'EXPEDIENTS_TANCATS') + '</td>';
					row += '<td>' + sumListAttr(dataOrganGestor.dades, 'EXPEDIENTS_TANCATS_ACUM') + '</td>';
					
					row += '</tr>';
					tableBody += row;
				}

				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
		
			}
			
			this.taules = [];
			this.cleanTaules = function() {
				this.taules.forEach(function (dataTable) {
					dataTable.destroy();
				});
				this.taules = [];
			}
			
			this.addTaula = function (dataTable) {
				this.taules.push(dataTable);
			}
			
			this.addTaulaAcualsPerMetaExpedient = function (data, selectorDiv) {
				var htmlTable = this.buildTableActualsPerMetaExpedient(data);
				$(selectorDiv).append(htmlTable);
				var dataTable = dataTableHistoric("#table-per-metaexpedients-" + this.taules.length);
				this.addTaula(dataTable);
			}
			
			this.addTaulaAcualsPerOrganGestor = function (data, selectorDiv) {
				var title = '<h2><spring:message code="historic.taula.titol.perorgan"/></h2>'
				var htmlTable = this.buildTableActualsPerOrganGestor(data);
				$(selectorDiv).append(title + htmlTable);	
				
				dataTable = dataTableHistoric("#table-per-organs-" + this.taules.length);
				this.addTaula(dataTable);
			}
		}
		
		function chartLine(canvas, labels, datasets, title) {
			return new Chart(canvas, {
			    type: 'line',
			    data: {
			        labels: labels,
			        datasets: datasets, 
			    },
			    options: {
			        scales: {
			            yAxes: [{
			                ticks: {
			                    beginAtZero: true
			                }
			            }]
			        },
		            title: {
						display: true,
						text: title
		            }
			    }
			});
		}

		
		function modalLoading() {
			var modalId = "modal-loading";
			this.show = function () {
				$("#" + modalId).modal('show');
			};
			
			this.hide = function () {
				$("#" + modalId).modal('hide');
			};
			
			$('body').append(
					'	<div id="' + modalId + '" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
					'		<div class="modal-dialog modal-sm">' +
					'			<div class="modal-content">' +
// 					'				<div class="modal-header">' +
// 					'					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
// 					'					<h4 class="modal-title"></h4>' +
// 					'				</div>' +
					'				<div class="modal-body" style="padding:0">' +
					'					<iframe frameborder="0" height="100" width="100%"></iframe>' +
					'					<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px;">' +
					'						<span class="fa fa-circle-o-notch fa-spin fa-3x"></span>' + 
// 					(plugin.settings.missatgeLoading != null ? '<p>' + plugin.settings.missatgeLoading + '</p>' : '') +	
					'					</div>' +
					'				</div>' +
// 					'				<div class="modal-footer"></div>' +
					'			</div>' +
					'		</div>' +
					'	</div>');
			console.log("uep! com anam?")
		}
		
		
		
		/**
		* 	CODI SECCIÓ ENTITAT
		*/
		function seccioEntitat () {
			var taules = new Taules();
			var loading = new modalLoading();
			
			// function to update our chart
		    function buildChartEntitat(chart, url, data) {
		        var data = data || {};
		
		        $.getJSON(url, data).done(function(response) {
		        	console.log(response);
		        	response.sort((a, b) => (a.data > b.data))
		        	if (response.length > 0) {
			        	var yLabels = response.map(item => new Date(item.data).toLocaleDateString("es"));
			        	
			        	var i = 0;
			            chart.data.labels = yLabels;
			        	for (var nomSerie in response[0]) {
			        		if (nomSerie.includes("_")){
			        			chart.data.datasets.push({
			        				'data': response.map(item => item[nomSerie]),
			        				'label': metricsDefinition[nomSerie]["text"],
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

			
		    if ( showingDadesActuals && showingTables) {
		    	loading.show();
				$.ajax({
					type: "GET",
					url: 'historic/entitat/actual',
					success: function(response) {
						taules.cleanTaules();
						$('#div-dades-entitat').html("");
						loading.hide();
						var title = '<h2><spring:message code="historic.taula.titol.permetaexp"/></h2>';
						$('#div-dades-entitat').append(title);
						taules.addTaulaAcualsPerMetaExpedient(response, '#div-dades-entitat');
						taules.addTaulaAcualsPerOrganGestor(response, '#div-dades-entitat');
					}
				});
			} else if (!showingTables) {
				var ctx = 'chartEntitat';
				var chartEntitat = new Chart(ctx, {
				    type: 'line',
				    data: {
				        labels: [],
				        datasets: [], 
				        color: []
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
		
		/**
		* 	CODI SECCIÓ ORGANS GESTORS
		*/
		function seccioOrgansGestors() {
			var taules = new Taules();
			var loading = new modalLoading();
			function buildTablesMetrics(data, metric){
				var organsGestors = [];
				var iSerie = 0;
				while (organsGestors.length == 0 && iSerie < Object.keys(data).length ){
					var serie = data[Object.keys(data)[iSerie]];
					organsGestors = Object.keys(serie);
					iSerie ++;
				}
				var idTable = "table-organgestor-" + metric;
				var tableHeader = '<table id="' + idTable + '" class="table table-bordered table-striped table-hover style="width:100%">' +
						'<thead>' +
							'<tr>';
				tableHeader += '<th><spring:message code="historic.taula.header.data"/></th>';
				
				organsGestors.forEach(function(organGestor){
					tableHeader += '<th>' + organGestor + '</th>';
				});
				
				tableHeader += '</tr></thead><tbody>';
				
				var tableBody = '';
				for (var date in data) {
					var serie = data[date];
					console.log(serie);
					var row = '<tr>';
					row += '<td data-sort="' + date + '">' +  new Date(date).toLocaleDateString("es") + '</td>'
					organsGestors.forEach(function(organGestor){
						var value = serie[organGestor][metric] != null ? serie[organGestor][metric] : 0;
						row += '<td>' + value + '</td>';
					});
					row += '</tr>';
					tableBody += row;
				}
									
				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
				
			}
			
			function create_chart_organGestors(data, metric) {
				var dates = [];
				for (var organGestor in data) {
					data[organGestor].sort((a, b) => (a.data > b.data) ? 1 : -1);
					if (data[organGestor].length > dates.length) {
						dates = data[organGestor].map(item => item.data);
					}
				}
				
				var organsGestors = Object.keys(data);
				var datasets = []
				for (var organGestor in data) {
					
					var dataset_data = data[organGestor].map(item => item[metric]);
        			datasets.push({
        				'data': dataset_data,
        				'label': organGestor,
        				'backgroundColor': "rgba(0,0,0,0.0)",
        				'borderColor': getRandomColor()
        				});
				}

				var ctx = 'chart-' + metric;
				var labels = dates.map(item => new Date(item).toLocaleDateString("es"))
				var chart = chartLine(ctx, labels, datasets, metricsDefinition[metric]["text"]);
				
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
				var chart = chartPie(ctx, values, labels, colors, metricsDefinition[metric]["text"]);
			}
			
			function updateContentOrganGestors(metrics) {
				
				if ( showingDadesActuals && showingTables) {
					loading.show();
					$.ajax({
						type: "GET",
						url: 'historic/entitat/actual',
						success: function(response) {
							taules.cleanTaules();
							loading.hide();
							taules.addTaulaAcualsPerOrganGestor(response, '#div-dades-organ');
						}
					});
					
				} else if ( showingDadesActuals && !showingTables && metrics.length != 0){		
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/organgestors/actual',
						data: {
							metrics: metrics
						},
						success: function(response) {
							loading.hide();
							for (var metric in response) {
								var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
								$('#div-dades-organ').append(canvas);
								create_chart_current(metric, response[metric]);
							}
						}
					});						

				}  else if ( !showingDadesActuals && showingTables && metrics.length != 0) {
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/organgestors',
						data: {
							metrics: metrics
						},
						success: function(response) {
							taules.cleanTaules();
							$('#div-dades-organ').html("");
							loading.hide();
							metrics.forEach(function(metric){
								console.log(metric);
								var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
								var htmlTable = buildTablesMetrics(response, metric);
								
								$('#div-dades-organ').append(title + htmlTable);	
								
								var dataTable = dataTableHistoric("#table-organgestor-" + metric);								
								taules.addTaula(dataTable);
							});
						}
					});
				}  else if ( !showingDadesActuals && !showingTables && metrics.length != 0) {
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/organgestors/grouped',
						data: {
							metrics: metrics
						},
						success: function(response) {
							$('#div-dades-organ').html("");
							loading.hide();
							metrics.forEach(function(metric){
								var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
								var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
								$('#div-dades-organ').append(title + canvas);
								create_chart_organGestors(response, metric);
							});
						}
					});
				}
			}
					
			updateContentOrganGestors(Object.keys(metricsDefinition));

		}
		
		/**
		* 	CODI SECCIÓ USUARIS
		*/
		function seccioUsuaris() {
			var taules = new Taules();
			var loading = new modalLoading();
			function usuari_construirTaula(dades, codiUsuari){
				var tableHeader = '<table id="table-user-' + codiUsuari + '" class="table table-bordered table-striped table-hover style="width:100%" >' +
						'<thead>' +
							'<tr>';
				tableHeader += '<th><spring:message code="historic.taula.header.data"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numTasquesTramitades"/></th>';
	
				tableHeader += '</tr></thead><tbody>';
				
				var tableBody = '';
				dades.forEach(function(registre){
					var row = '<tr>';
					row += '<td data-sort="' + registre.data + '">' + new Date(registre.data).toLocaleDateString("es") + '</td>';
					row += '<td>' + registre['EXPEDIENTS_CREATS'] + '</td>';
					row += '<td>' + registre['EXPEDIENTS_CREATS_ACUM'] + '</td>';
					row += '<td>' + registre['EXPEDIENTS_TANCATS'] + '</td>';
					row += '<td>' + registre['EXPEDIENTS_TANCATS_ACUM'] + '</td>';
					row += '<td>' + registre['TASQUES_TRAMITADES'] + '</td>';
					
					row += '</tr>';
					tableBody += row;
				});
									
				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
			}

			function buildChartUser(data, user) {
				data.sort((a, b) => (a.data > b.data) ? 1 : -1)
				
				var datasets = []
				for (var nomSerie in data[0]) {
					if (nomSerie.includes("_")){
	        			datasets.push({
	        				'data': data.map(item => item[nomSerie]),
	        				'label': metricsDefinition[nomSerie]["text"],
	        				'backgroundColor': "rgba(0,0,0,0.0)",
	        				'borderColor': getRandomColor()
	        				});
	        		}        			
				}
				var labels = data.map(item => new Date(item.data).toLocaleDateString("es"));
				var ctx = 'chart-' + user;
				var chart = chartLine(ctx, labels, datasets, user);
				
			}
			
			function create_chart_current(metric, data) {
				var labels = []
				var values = []
				var colors = []
				for (var user in data) {
					labels.push(organGestor);
					values.push(data[organGestor]);
					colors.push(getRandomColor())
				}
				
				console.log(values);
				console.log(labels);
				var ctx = 'chart-current-' + metric;
				var chart = chartPie(ctx, values, labels, colors, metricsDefinition[metric]["text"]);
			}
			
			function buildTableUsuarisActuals (data) {
				var tableHeader = '<table id="table-usuaris-actuals" class="table table-bordered table-striped table-hover style="width:100%" >' +
				'<thead>' +
					'<tr>';
				tableHeader += '<th>Usuari</th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numTasquesTramitades"/></th>';
		
				tableHeader += '</tr></thead><tbody>';
				
				function sumListAttr(list, attrname) {
					var sum = 0;
					list.forEach(function(serie){
						sum += serie[attrname];
					});
					return sum;
				}
				var tableBody = '';
				for (var usuari in data) {
					var row = '<tr>';
					row += '<td>' + usuari + '</td>';
					row += '<td>' + sumListAttr(data[usuari], 'EXPEDIENTS_CREATS') + '</td>';
					row += '<td>' + sumListAttr(data[usuari], 'EXPEDIENTS_CREATS_ACUM') + '</td>';
					row += '<td>' + sumListAttr(data[usuari], 'EXPEDIENTS_TANCATS') + '</td>';
					row += '<td>' + sumListAttr(data[usuari], 'EXPEDIENTS_TANCATS_ACUM') + '</td>';
					row += '<td>' + sumListAttr(data[usuari], 'TASQUES_TRAMITADES') + '</td>';
					
					row += '</tr>';
					tableBody += row;
				}

				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
			}
			
			function updateContentUsuaris(usuaris) {
				if (usuaris.length == 0){
					return;
				}
				loading.show();
				if ( showingDadesActuals ) {
					taules.cleanTaules();
					$('#div-dades-usuaris').html("");
					
					$.ajax({
						type: "POST",
						url: 'historic/usuaris/actual',
						data: {
							usuaris: usuaris
						},
						success: function(response) {
							console.log(response);
							var htmlTable = buildTableUsuarisActuals(response);
							var title = '<h2><spring:message code="historic.taula.titol.perusuari"/></h2>'
							$('#div-dades-usuaris').append(title + htmlTable);
							var dataTable = dataTableHistoric("#table-usuaris-actuals");
							
							for (var usuari in response) {
								var title = '<h2><spring:message code="historic.taula.titol.perusuari.usuari"/> ' + usuari + '</h2>';
								$('#div-dades-usuaris').append(title);
								taules.addTaulaAcualsPerMetaExpedient(response[usuari], '#div-dades-usuaris');
							}
							
							taules.addTaula(dataTable);
							
							loading.hide();
						}
					});
				} else {
					
					$.ajax({
						type: "POST",
						url: 'historic/usuaris/dades/',
						data: {
							usuaris: usuaris
						},
						success: function (response) {
							taules.cleanTaules();
							$('#div-dades-usuaris').html("");
							console.log(response);
							for (usuariCodi in response){
								var title = '<h2>' + usuariCodi + '</h2>'
								$('#div-dades-usuaris').append(title);
								if (showingTables) {
									var htmlTable = usuari_construirTaula(response[usuariCodi], usuariCodi);
									var title = '<h2>' + usuariCodi + '</h2>'
									
									$('#div-dades-usuaris').append(htmlTable);
									var dataTable = dataTableHistoric("#table-user-" + usuariCodi);
									taules.addTaula(dataTable);
									
								} else {								
									var canvas = '<canvas id="chart-' + usuariCodi + '" width="400" height="100"></canvas>';
									$('#div-dades-usuaris').append(canvas);
									buildChartUser(response[usuariCodi], usuariCodi);
									
								}
								
							}	
							loading.hide();
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
		
		function seccioInteressats() {
			var taules = new Taules();
			var loading = new modalLoading();
			function interessatConstruirTaula(dades, interessatDoc) {
				var tableHeader = '<table id="table-interessat-' + interessatDoc + '" class="table table-bordered table-striped table-hover style="width:100%" >' +
						'<thead>' +
							'<tr>';
				tableHeader += '<th><spring:message code="historic.taula.header.data"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
	
				tableHeader += '</tr></thead><tbody>';
				
				
				var dates = dades.map(item => new Date(item.data).toLocaleDateString("es"));
				
				var tableBody = '';
				dades.forEach(function(registre){
					var row = '<tr>';
					row += '<td data-sort="' + registre.data + '">' + new Date(registre.data).toLocaleDateString("es") + '</td>';
					row += '<td>' + registre['EXPEDIENTS_CREATS'] + '</td>';
					row += '<td>' + registre['EXPEDIENTS_CREATS_ACUM'] + '</td>';
					row += '<td>' + registre['EXPEDIENTS_TANCATS'] + '</td>';
					row += '<td>' + registre['EXPEDIENTS_TANCATS_ACUM'] + '</td>';
					
					row += '</tr>';
					tableBody += row;
				});
									
				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
			}
			
			function buildTableInteressatsActuals (data) {
				var tableHeader = '<table id="table-interessats-actuals" class="table table-bordered table-striped table-hover style="width:100%" >' +
				'<thead>' +
					'<tr>';
				tableHeader += '<th>Interessat</th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
		
				tableHeader += '</tr></thead><tbody>';
				
				function sumListAttr(list, attrname) {
					var sum = 0;
					list.forEach(function(serie){
						sum += serie[attrname];
					});
					return sum;
				}
				var tableBody = '';
				for (var interessatDoc in data) {
					var row = '<tr>';
					row += '<td>' + interessatDoc + '</td>';
					row += '<td>' + sumListAttr(data[interessatDoc], 'EXPEDIENTS_CREATS') + '</td>';
					row += '<td>' + sumListAttr(data[interessatDoc], 'EXPEDIENTS_CREATS_ACUM') + '</td>';
					row += '<td>' + sumListAttr(data[interessatDoc], 'EXPEDIENTS_TANCATS') + '</td>';
					row += '<td>' + sumListAttr(data[interessatDoc], 'EXPEDIENTS_TANCATS_ACUM') + '</td>';
					
					row += '</tr>';
					tableBody += row;
				}

				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;
			}
			
			function buildChartInteressat(data, interessatDoc) {
				data.sort((a, b) => (a.data > b.data) ? 1 : -1)
				
				var datasets = []
				for (var nomSerie in data[0]) {
					if (nomSerie.includes("_")){
	        			datasets.push({
	        				'data': data.map(item => item[nomSerie]),
	        				'label': metricsDefinition[nomSerie]["text"],
	        				'backgroundColor': "rgba(0,0,0,0.0)",
	        				'borderColor': getRandomColor()
	        				});
	        		}        			
				}
				var labels = data.map(item => new Date(item.data).toLocaleDateString("es"));
				var ctx = 'chart-' + interessatDoc;
				var chart = chartLine(ctx, labels, datasets, interessatDoc);
				
			}
			
			function updateContentInteressats(interessats) {
				if (interessats.length == 0){
					return;
				}
				loading.show();
				if ( showingDadesActuals ) {
					$.ajax({
						type: "POST",
						url: 'historic/interessats/actual',
						data: {
							interessats: interessats
						},
						success: function(response) {
							taules.cleanTaules();
							$('#div-dades-interessats').html("");
							
							console.log(response);
							var htmlTable = buildTableInteressatsActuals(response);
							var title = '<h2><spring:message code="historic.taula.titol.perinteressat"/></h2>'
							$('#div-dades-interessats').append(title + htmlTable);
							var dataTable = dataTableHistoric("#table-interessats-actuals");
							
							for (var interessatDoc in response) {
								var title = '<h2><spring:message code="historic.taula.titol.perinteressat.interessat"/> ' + interessatDoc + '</h2>';
								$('#div-dades-interessats').append(title);
								taules.addTaulaAcualsPerMetaExpedient(response[interessatDoc], '#div-dades-interessats');
							}
							
							taules.addTaula(dataTable);
							loading.hide();
						}
					});

				} else {
					$.ajax({
						type: "POST",
						url: 'historic/interessats/dades/',
						data: {
							interessats: interessats
						},
						success: function (response) {
							console.log(response);
							taules.cleanTaules();
							$('#div-dades-interessats').html("");
							
							for (interessatDoc in response) {
								var title = '<h2>' + interessatDoc + '</h2>'
								if (showingTables) {
									var htmlTable = interessatConstruirTaula(response[interessatDoc], interessatDoc);
									var title = '<h2>' + interessatDoc + '</h2>'
									
									$('#div-dades-interessats').append(title + htmlTable);
							
									var dataTable = dataTableHistoric("#table-interessat-" + interessatDoc);								
									taules.addTaula(dataTable);
									
								} else {								
									var canvas = '<canvas id="chart-' + interessatDoc + '" width="400" height="100"></canvas>';
									$('#div-dades-interessats').append(title + canvas);
									buildChartInteressat(response[interessatDoc], interessatDoc);
									
								}

							}
							loading.hide();
						}
					});
				}
			}
			
			updateContentInteressats(interessatsSeleccionats);
			$("form#form-estadistics-interessats").on('submit', function(){
				var interessats = $("#inputinteressats").find(':selected').map(function(){return $(this).val();}).get();;
				var names = $("#input-interessats").find(':selected').map(function(){return $(this).html();}).get();;
				console.log(interessats);
				console.log(names);
				updateContentInteressats(names);
				return false;
			});	
		}
		
		function checkBoxExpedientsComunsLogic($selectOrgansGestors) {
			var valors = $selectOrgansGestors.val(); 
			if (valors != null || showDadesOrganGestor) {
				$("#incorporarExpedientsComuns").parent().show();
			} else {
				$("#incorporarExpedientsComuns").parent().hide();
			}
		}
		
		$(function () {			
			if (showDadesEntitat) {
				seccioEntitat();
			}
			
			if (showDadesOrganGestor) {
				seccioOrgansGestors();
			}
			
			if (showDadesUsuari) {
				seccioUsuaris();
			}
			
			if (showDadesInteressat) {
				seccioInteressats();				
			}
			
			var $selectOrgansGestors = $("#organGestorsIds");
			checkBoxExpedientsComunsLogic($selectOrgansGestors);
			$selectOrgansGestors.on('select2:select', function (e) {
				checkBoxExpedientsComunsLogic($selectOrgansGestors);
			});
			$selectOrgansGestors.on('select2:unselect', function (e) {
				checkBoxExpedientsComunsLogic($selectOrgansGestors);
			});
			
			$(".form-filtre-visualitzacio").on('change', 'input:radio', function (event) {
				$("#historicFiltreCommand").submit();
			});
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
			<div class="col-md-4">
				<rip:inputSelect name="dadesMostrar" 
								 optionEnum="HistoricDadesMostrarEnum" 
								 netejar="false"
								 emptyOption="false" 
								 inline="true"/>
			</div>
			<div class="col-md-4">
				<rip:inputCheckbox name="incorporarExpedientsComuns" inline="true" textKey="historic.filtre.expedientsComuns"/>
			</div>
			<div class="col-md-4 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
		<div class="row form-filtre-visualitzacio">
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
		<div class="row">
			<div id="div-dades-entitat" class="col-md-12">
				<c:if test="${historicFiltreCommand.showingTables and !showingDadesActuals }">
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
			</div>
		</div>
	</c:if>
	<c:if test="${showDadesOrganGestor}">
		<h1><spring:message code="historic.titol.seccio.organGestor"/></h1>
		<div class="row">
			<div id="div-dades-organ" class="col-md-12">
			</div>
		</div>

	</c:if>
	<c:if test="${showDadesUsuari}">
		<h1><spring:message code="historic.titol.seccio.usuari"/></h1>
		<form id="form-estadistics-usuaris" class="well">
			<div class="row">
				<div class="col-md-8">
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
				<div class="col-offset-2 col-md-2 pull-right">
					<div class="pull-right">
						<button type="submit" name="accio" value="filtrar" class="btn btn-primary">
							<span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/>
						</button>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div id="div-dades-usuaris" class="col-md-12">
			</div>
		</div>
	</c:if>
	<c:if test="${showDadesInteressat}">
		<h1><spring:message code="historic.titol.seccio.interessat"/></h1>
		<form id="form-estadistics-interessats" class="well">
			<div class="row">
				<div class="col-md-8">
					<c:url value="/interessatajax/interessat" var="urlConsultaInicial"/>
					<c:url value="/interessatajax/interessat" var="urlConsultaLlistat"/>
					<c:set var="placeholderText"><spring:message code="historic.filtre.select.interessats"/></c:set>
					<select name="interessats" cssClass="form-control" id="input-interessats"
								style="width:100%" data-toggle="suggest"
								data-placeholder="${placeholderText}"
								data-minimum-input-length="3"
								data-url-llistat="${urlConsultaLlistat}" 
								data-url-inicial="${urlConsultaInicial}"
							    multiple="true"
							    data-placeholder="${placeholderText}"
								data-current-value="${fn:join(interessatsSeleccionats, ",")}" 
								data-suggest-value="documentNum"
								data-suggest-text="documentNum"
								data-url-param-addicional=""> </select>
				</div>
				<div class="col-md-4 pull-right">
					<div class="pull-right">
						<button type="submit" name="accio" value="filtrar" class="btn btn-primary">
							<span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/>
						</button>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div id="div-dades-interessats" class="col-md-12">
			</div>
		</div>
	</c:if>
	<form action="historic/exportar" method="post" class="well">
		<div class="row">
			<div class="col-md-8"></div>
			<div class="col-md-2">
				<select name="format" class="form-control" style="width:100%"
						data-minimumresults="-1"
						data-toggle="select2">
							<option value="json">json</option>
							<option value="xlsx">xlsx</option>
							<option value="odf">odf</option>
							<option value="xml">xml</option>
				</select>
			</div>
			<div class="col-md-2">
				<div class="pull-right">
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary">
						<span class="fa fa-download"></span>&nbsp; <spring:message code="historic.exportacio.boto.exportar"/>
					</button>
				</div>
			</div>
		</div>
	</form>

</body>
</html>