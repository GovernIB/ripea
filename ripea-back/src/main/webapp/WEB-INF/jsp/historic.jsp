<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto" %>
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
	<script src="<c:url value="/webjars/moment/2.15.1/min/moment.min.js"/>"></script>
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
				'attrname' : 'num_expedients_creats',
				'text': "<spring:message code="historic.metriques.enum.EXPEDIENTS_CREATS"/>"
			},
			'EXPEDIENTS_CREATS_ACUM': {
				'attrname' : 'num_expedients_creats_total',
				'text': "<spring:message code="historic.metriques.enum.EXPEDIENTS_CREATS_ACUM"/>"
			},
			'EXPEDIENTS_TANCATS': {
				'attrname' : 'num_expedients_tancats',
				'text': "<spring:message code="historic.metriques.enum.EXPEDIENTS_TANCATS"/>"
			},
			'EXPEDIENTS_TANCATS_ACUM': {
				'attrname' : 'num_expedients_tancats_total',
				'text': "<spring:message code="historic.metriques.enum.EXPEDIENTS_TANCATS_ACUM"/>"
			},
			'DOCUMENTS_SIGNATS': {
				'attrname' : 'num_docs_signats',
				'text': "<spring:message code="historic.metriques.enum.DOCUMENTS_SIGNATS"/>"
			},
			'DOCUMENTS_NOTIFICATS': {
				'attrname' : 'num_docs_notificats',
				'text': "<spring:message code="historic.metriques.enum.DOCUMENTS_NOTIFICATS"/>"
			},
			'TASQUES_TRAMITADES': {
				'attrname' : 'num_tasques_tramitades',
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
		
		<c:if test="${empty historicFiltreCommand.organGestorsIds}">
		var isAnyOrganSelected = false; 
		</c:if>;
		<c:if test="${not empty historicFiltreCommand.organGestorsIds}">
			var isAnyOrganSelected = true; 
		</c:if>;
	
		var language = requestLocale;
		// Només acceptam es i ca com a llengues //
		if (language.startsWith("es")) {
			language = "es";
		} else {
			language = "ca";
		}

		function sumListAttr(list, attrname) {
			var sum = 0;
			list.forEach(function(serie){
				sum += serie[attrname];
			});
			return sum;
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
				tableHeader += "<th><spring:message code="historic.taula.header.tipusexpedient"/></th>";
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsCreatsTotal"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancats"/></th>';
				tableHeader += '<th><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>';
		
				tableHeader += '</tr></thead><tbody>';
				
				
				var tableBody = '';
				data.forEach(function(serie){
					var row = '<tr>';
					row += '<td>' + serie.metaExpedient + '</td>';
					row += '<td>' + serie.num_expedients_creats + '</td>';
					row += '<td>' + serie.num_expedients_creats_total + '</td>';
					row += '<td>' + serie.num_expedients_tancats + '</td>';
					row += '<td>' + serie.num_expedients_tancats_total + '</td>';
					
					row += '</tr>';
					tableBody += row;
				});

				var tableFooter = '</tbody></table>';
				
				return tableHeader + tableBody + tableFooter;

			}
			
			this.buildTableActualsPerOrganGestor = function(data) {
				var mapOrgansGestors = {};
				console.log(data);
				data.forEach(function(serie){
					var organGestor = serie['organ_gestor'] != "" ? serie['organ_gestor'] : 'Comu';
					
					if (organGestor in mapOrgansGestors) {
						mapOrgansGestors[organGestor].push(serie);
						
					} else {
						mapOrgansGestors[organGestor] = [serie];
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
				for (var organGestor in mapOrgansGestors) {
					var dataOrganGestor = mapOrgansGestors[organGestor];
					var row = '<tr>';
					row += '<td>' + organGestor + '</td>';
					row += '<td>' + sumListAttr(dataOrganGestor, 'num_expedients_creats') + '</td>';
					row += '<td>' + sumListAttr(dataOrganGestor, 'num_expedients_creats_total') + '</td>';
					row += '<td>' + sumListAttr(dataOrganGestor, 'num_expedients_tancats') + '</td>';
					row += '<td>' + sumListAttr(dataOrganGestor, 'num_expedients_tancats_total') + '</td>';
					
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
					'				<div class="modal-body" style="padding:0">' +
					'					<iframe frameborder="0" height="100" width="100%"></iframe>' +
					'					<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px;">' +
					'						<span class="fa fa-circle-o-notch fa-spin fa-3x"></span>' + 
					'					</div>' +
					'				</div>' +
					'			</div>' +
					'		</div>' +
					'	</div>');
		}
		
		
		function buildTableMetric(data, metric, idTable) {
			var columns = Object.keys(data);
			var tableHeader = '<table id="' + idTable + '" class="table table-bordered table-striped table-hover style="width:100%">' +
					'<thead>' +
						'<tr>';
			var tableFooter = '</tbody></table>';

			<c:choose>
				<c:when test="${historicFiltreCommand.tipusAgrupament=='DIARI'}">
					tableHeader += '<th><spring:message code="historic.taula.header.data"/></th>';
				</c:when>
				<c:otherwise>
					tableHeader += '<th><spring:message code="historic.taula.header.any"/></th>';				
					tableHeader += '<th><spring:message code="historic.taula.header.mes"/></th>';
				</c:otherwise>
			</c:choose>
			
			
			columns.forEach(function(c){
				tableHeader += '<th>' + c + '</th>';
			});
			
			tableHeader += '</tr></thead><tbody>';
			if (columns.length == 0) {
				return tableHeader + tableFooter;
			}
			var dates = [];
			columns.forEach(function(c){
				data[c] = data[c].sort((a, b) => (moment(a.data,'DD-MM-YYYY') > moment(b.data,'DD-MM-YYYY')));
				if (data[c].length > dates.length) {
					dates = data[c].map(item => item.data);	
				}
			});
						
			var tableBody = '';
			for (var i = 0; i < dates.length; i++ ){
				var date = dates[i];
				var row = '<tr>';
				<c:if test="${historicFiltreCommand.tipusAgrupament=='MENSUAL'}">
					row += '<td data-sort="' + moment(date, 'DD-MM-YYYY') + '">' +  getAny(date) + '</td>'
				</c:if>
				row += '<td data-sort="' + moment(date, 'DD-MM-YYYY') + '">' +  getDate(date, false) + '</td>'
				columns.forEach(function(c){
					var attrname = metricsDefinition[metric]['attrname'];
					var value = data[c][i][attrname] != null ? data[c][i][attrname] : 0;
					row += '<td>' + value + '</td>';
				});
				row += '</tr>';
				tableBody += row;
			}
			
			return tableHeader + tableBody + tableFooter;
		}

		function getDate(date, ambAny){
			<c:choose>
				<c:when test="${historicFiltreCommand.tipusAgrupament=='DIARI'}">
					return date;
				</c:when>
				<c:otherwise>

					let mes = date.substring(3, 5);
					mes = mes.replace(/^0+/, '');
					let mesString;
					
					if (mes == 1) mesString = '<spring:message code="mes.1"/>';
					else if (mes == 2) mesString = '<spring:message code="mes.2"/>';
					else if (mes == 3) mesString = '<spring:message code="mes.3"/>';
					else if (mes == 4) mesString = '<spring:message code="mes.4"/>';
					else if (mes == 5) mesString = '<spring:message code="mes.5"/>';
					else if (mes == 6) mesString = '<spring:message code="mes.6"/>';
					else if (mes == 7) mesString = '<spring:message code="mes.7"/>';
					else if (mes == 8) mesString = '<spring:message code="mes.8"/>';
					else if (mes == 9) mesString = '<spring:message code="mes.9"/>';
					else if (mes == 10) mesString = '<spring:message code="mes.10"/>';
					else if (mes == 11) mesString = '<spring:message code="mes.11"/>';
					else if (mes == 12) mesString = '<spring:message code="mes.12"/>';

					if (ambAny) {
						let any = date.substring(6, 10);
						mesString += ' ' + any;
					}

					return mesString;
				</c:otherwise>
			</c:choose>
		}
		function getAny(date){
			return date.substring(6, 10);
		}
		
		
		function createChartMetric(data, metric, colors) {
			var columns = Object.keys(data);
			
			columns.forEach(function(c){
				data[c] = data[c].sort((a, b) => (moment(a.data,'DD-MM-YYYY') > moment(b.data,'DD-MM-YYYY')));
			});
			
			var dates = data[columns[0]].map(item => getDate(item.data, true));
			
			var datasets = []
			columns.forEach(function(c){
				var attrname = metricsDefinition[metric]['attrname'];
				var dataset = data[c].map(item => item[attrname] != null ? item[attrname] : 0)
				var color = (colors == null || colors[c] == null) ? getRandomColor() : colors[c]
				datasets.push({
    				'data': dataset,
    				'label': c,
    				'backgroundColor': "rgba(0,0,0,0.0)",
    				'borderColor': color
    				});	
			});

			var ctx = 'chart-' + metric;
			var labels = dates
			var chart = chartLine(ctx, labels, datasets, metricsDefinition[metric]["text"]);
		}
		
		
		function buildTableCurrent(data, metriques, firstColumnHeader, getRegistreName) {

			var idTable = "table-organgestors";
			var tableHeader = '<table id="' + idTable + '" class="table table-bordered table-striped table-hover style="width:100%">' +
					'<thead>' +
						'<tr>';
			tableHeader += '<th>' + firstColumnHeader + '</th>';
			metriques.forEach(function(metrica){
				tableHeader += '<th>' + metricsDefinition[metrica]['text']+ '</th>';
			});
			tableHeader += '</tr></thead><tbody>';
			
			var tableBody = '';
			data.forEach(function(registre){
				var row = '<tr>';
				row += '<td>' +  getRegistreName(registre) + '</td>'
				metriques.forEach(function(metrica){
					var attrname = metricsDefinition[metrica]['attrname'];
					var value = registre[attrname] != null ? registre[attrname] : 0;
					row += '<td>' + value + '</td>';
				});
				row += '</tr>';
				tableBody += row;
			});
			var tableFooter = '</tbody></table>';
			return tableHeader + tableBody + tableFooter;
		}

		function createChartCurrent(data, metric, getRegistreName) {
			var labels = []
			var values = []
			var colors = []
			data.forEach(function(registre){
				labels.push(getRegistreName(registre));
				
				var attrname = metricsDefinition[metric]['attrname'];
				var value = registre[attrname] != null ? registre[attrname] : 0;
				values.push(value);
				
				colors.push(getRandomColor());
			});
			
			var ctx = 'chart-current-' + metric;
			var chart = chartPie(ctx, values, labels, colors, metricsDefinition[metric]["text"]);
		}
		
		/**
		* 	CODI SECCIÓ ENTITAT
		*/
		function seccioEntitat () {
			var taules = new Taules();
			var loading = new modalLoading();
			var metriques = [
				'EXPEDIENTS_CREATS',
				'EXPEDIENTS_CREATS_ACUM',
				'EXPEDIENTS_TANCATS',
				'EXPEDIENTS_TANCATS_ACUM'	
			];
			// function to update our chart
		    function buildChartEntitat(chart, url, data) {
		        var data = data || {};
		
		        $.getJSON(url, data).done(function(response) {
		        	console.log(response);
		        	response.sort((a, b) => (moment(a.data,'DD-MM-YYYY') > moment(b.data,'DD-MM-YYYY')))
		        	if (response.length > 0) {
			        	var yLabels = response.map(item => getDate(item.data, true));
			        	
			            chart.data.labels = yLabels;
			            metriques.forEach(function(metrica){
		        			chart.data.datasets.push({
		        				'data': response.map(item => item[metricsDefinition[metrica]["attrname"]]),
		        				'label': metricsDefinition[metrica]["text"],
		        				'backgroundColor': "rgba(0,0,0,0.0)",
		        				'borderColor': getRandomColor()
		        				});			            	
			            });
		        	}
		        	
		            chart.update(); // finally update our chart
		        });
		    }

			
		    if ( showingDadesActuals) {
		    	loading.show();
				$.ajax({
					type: "GET",
					url: 'historic/entitat/actual',
					success: function(response) {
						taules.cleanTaules();
						console.log(response);
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
			var metriques = [
				'EXPEDIENTS_CREATS',
				'EXPEDIENTS_CREATS_ACUM',
				'EXPEDIENTS_TANCATS',
				'EXPEDIENTS_TANCATS_ACUM'	
			];
			
			var selectorContainer = '#div-dades-organ';
			var $container = $(selectorContainer);
			
			var getColumnName = function (registre){
				return registre['organ_gestor']
			};
			
			function viewHistoric(data) {
				taules.cleanTaules();
				$container.html("");

				if ( showingTables ) {
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var htmlTable = buildTableMetric(data, metric, "table-dades-" + metric);
						
						$container.append(title + htmlTable);	
						
						var dataTable = dataTableHistoric("#table-dades-" + metric);								
						taules.addTaula(dataTable);
					});
				} else {
					var colors = {};
					for (var column in data) {
						colors[column] = getRandomColor();
					}
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
						$container.append(title + canvas);
						createChartMetric(data, metric, colors);
					});
				}
			}

			function updateContentOrganGestors() {
				
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
					
				} else if ( showingDadesActuals && !showingTables){	
					if (!isAnyOrganSelected){
						alert("Es necessari seleccionar algún òrgan gestor.");
						return;
					}
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/organgestors/actual',
						success: function(response) {
							loading.hide();
							console.log(response);
							var data = [];
							for (var oGestor in response) {
								var registre = response[oGestor];
								registre["organ_gestor"] = oGestor;
								data.push(registre);
							}
							metriques.forEach(function(metric){
								var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
								$container.append(canvas);
								createChartCurrent(data, metric, getColumnName);
							});
						}
					});						

				}  else if ( !showingDadesActuals ) {
					if (!isAnyOrganSelected){
						alert("Es necessari seleccionar algún òrgan gestor.");
						return;
					}
					loading.show();
					$.ajax({
						type: "POST",
						url: 'historic/organgestors',
						data: {
							metrics: metriques
						},
						success: function(response) {
							loading.hide();
							viewHistoric(response);			
						}
					});
				}
			}
					
			updateContentOrganGestors();

		}
		
		/**
		* 	CODI SECCIÓ USUARIS
		*/
		function seccioUsuaris() {
			var taules = new Taules();
			var loading = new modalLoading();
			var metriques = [
				'EXPEDIENTS_CREATS',
				'EXPEDIENTS_CREATS_ACUM',
				'EXPEDIENTS_TANCATS',
				'EXPEDIENTS_TANCATS_ACUM',
				'TASQUES_TRAMITADES'
			];
			var columnHeader = '<spring:message code="historic.taula.header.usuari"/>';
			var selectorContainer = '#div-dades-usuaris';
			var $container = $(selectorContainer);
			
			var getColumnName = function (registre){
				return registre['user']
			};
			
			function viewHistoric(data) {
				taules.cleanTaules();
				$container.html("");
				console.log(data)
				if ( showingTables ) {
					var dataTable = dataTableHistoric("#table-estats");								
					taules.addTaula(dataTable);
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var htmlTable = buildTableMetric(data, metric, "table-usuaris-" + metric);
						
						$container.append(title + htmlTable);	
						
						var dataTable = dataTableHistoric("#table-usuaris-" + metric);								
						taules.addTaula(dataTable);
					});
				} else {
					var colors = {};
					for (var column in data) {
						colors[column] = getRandomColor();
					}
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
						$container.append(title + canvas);
						createChartMetric(data, metric, colors);
					});
				}
			}
			
			function viewActuals(data) {
				taules.cleanTaules();
				loading.hide();
				var listData = [];
				for (var user in data) {
					var registres = data[user];
					var registre = {};
					registre["user"] = user;
					metriques.forEach(function(metric){
						var metrName = metricsDefinition[metric]["attrname"];
						registre[metrName] = sumListAttr(registres, metrName);
					});
					listData.push(registre);
				}
				console.log(listData);
				
				if ( showingTables ) {
					var title = '<h2></h2>'
					var htmlTable = buildTableCurrent(listData, metriques, columnHeader, getColumnName);
					$container.append(title + htmlTable);
					
				} else {
					metriques.forEach(function(metric){
						var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
						$container.append(canvas);
						createChartCurrent(listData, metric, getColumnName);
					});
				}
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
							loading.hide();
							viewActuals(response);
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
							loading.hide();
							viewHistoric(response);	
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
			var metriques = [
				'EXPEDIENTS_CREATS',
				'EXPEDIENTS_CREATS_ACUM',
				'EXPEDIENTS_TANCATS',
				'EXPEDIENTS_TANCATS_ACUM'
			];
			var columnHeader = '<spring:message code="historic.taula.header.interessat"/>';
			
			var selectorContainer = '#div-dades-interessats';
			var $container = $(selectorContainer);
			
			var getColumnName = function (registre){
				return registre['interessat']
			};

			function viewHistoric(data) {
				taules.cleanTaules();
				$container.html("");
				console.log(data)
				if ( showingTables ) {
					var dataTable = dataTableHistoric("#table-estats");								
					taules.addTaula(dataTable);
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var htmlTable = buildTableMetric(data, metric, "table-usuaris-" + metric);
						
						$container.append(title + htmlTable);	
						
						var dataTable = dataTableHistoric("#table-usuaris-" + metric);								
						taules.addTaula(dataTable);
					});
				} else {
					var colors = {};
					for (var column in data) {
						colors[column] = getRandomColor();
					}
					metriques.forEach(function(metric){
						var title = '<h2>' + metricsDefinition[metric]["text"] + '</h2>'
						var canvas = '<canvas id="chart-' + metric + '" width="400" height="100"></canvas>';
						$container.append(title + canvas);
						createChartMetric(data, metric, colors);
					});
				}
			}
			
			function viewActuals(data) {
				taules.cleanTaules();
				loading.hide();
				var listData = [];
				for (var interessat in data) {
					var registres = data[interessat];
					var registre = {};
					registre["interessat"] = interessat;
					metriques.forEach(function(metric){
						var metrName = metricsDefinition[metric]["attrname"];
						registre[metrName] = sumListAttr(registres, metrName);
					});
					listData.push(registre);
				}
				console.log(listData);
				
				if ( showingTables ) {
					var title = '<h2></h2>'
					var htmlTable = buildTableCurrent(listData, metriques, columnHeader, getColumnName);
					$container.append(title + htmlTable);
					
				} else {
					metriques.forEach(function(metric){
						var canvas = '<div class="col-md-4"><canvas id="chart-current-' + metric + '" width="50" height="50"></canvas></div>';
						$container.append(canvas);
						createChartCurrent(listData, metric, getColumnName);
					});
				}
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
							loading.hide();
							viewActuals(response);
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
							loading.hide();
							viewHistoric(response);	
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

		
		$(document).ready(function() {
			$("#dadesMostrar").on('change', function() {
				var select2Options = {
						theme: 'bootstrap', 
						width: 'auto', 
						minimumResultsForSearch: "0"};
				if($(this).val()=='ENTITAT') {
					$('#exportFormat option[value="csv"]').prop('disabled', false);
					$('#exportFormat').select2(select2Options);
				} else {
					$('#exportFormat option[value="csv"]').prop('disabled', true);
					$('#exportFormat').select2(select2Options);
				}
			});

			$('#dadesMostrar').trigger('change');
		});
		
		
	</script>
</head>
<body>
<c:url value="/organgestorajax/organgestor" var="urlConsultaOrgansInicial"/>
<c:url value="/organgestorajax/organgestor" var="urlConsultaOrgansLlistat"/>
<c:url value="/metaexpedientajax/metaexpedient" var="urlConsultaMetaExpedientsInicial"/>
<c:url value="/metaexpedientajax/metaexpedients/estadistiques" var="urlConsultaMetaExpedientsLlistat"/>
	<form:form action="" method="post" cssClass="well" modelAttribute="historicFiltreCommand">
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
 					suggestText="codiINom"/>
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
						style="width:100%">
						<thead>
							<tr>
								<c:choose>
									<c:when test="${historicFiltreCommand.tipusAgrupament=='DIARI'}">
										<th data-col-name="data" data-type="date" data-converter="date" nowrap><spring:message code="historic.taula.header.data"/></th>
									</c:when>
									<c:otherwise>
										<th data-col-name="any"><spring:message code="historic.taula.header.any"/></th>									
										<th data-col-name="mes" data-type="date" data-converter="date" data-template="#cellMesTemplate" nowrap><spring:message code="historic.taula.header.mes"/>
											<script id="cellMesTemplate" type="text/x-jsrender">
												{{if mes == 1}}
													<spring:message code="mes.1"/>
												{{else mes == 2}}
													<spring:message code="mes.2"/>
												{{else mes == 3}}
													<spring:message code="mes.3"/>
												{{else mes == 4}}
													<spring:message code="mes.4"/>
												{{else mes == 5}}
													<spring:message code="mes.5"/>
												{{else mes == 6}}
													<spring:message code="mes.6"/>
												{{else mes == 7}}
													<spring:message code="mes.7"/>
												{{else mes == 8}}
													<spring:message code="mes.8"/>
												{{else mes == 9}}
													<spring:message code="mes.9"/>
												{{else mes == 10}}
													<spring:message code="mes.10"/>
												{{else mes == 11}}
													<spring:message code="mes.11"/>
												{{else mes == 12}}
													<spring:message code="mes.12"/>
												{{/if}}
										</script>
										</th>
									</c:otherwise>
								</c:choose>
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
					<c:url value="/userajax/usuari" var="urlConsultaInicial"/>
					<c:url value="/userajax/usuaris" var="urlConsultaLlistat"/>
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
				<select id="exportFormat"  name="format" class="form-control" style="width:100%"
						data-minimumresults="-1"
						data-toggle="select2">
							<option value="json">json</option>
							<option value="xlsx">xlsx</option>
							<option value="odf">odf</option>
							<option value="xml">xml</option>
							<option value="csv">csv</option>
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