<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
</head>
<body>
<c:url value="/organgestorajax/organgestor" var="urlConsultaOrgansInicial"/>
<c:url value="/organgestorajax/organgestor" var="urlConsultaOrgansLlistat"/>
<c:url value="/metaexpedientajax/metaexpedients" var="urlConsultaMetaExpedientsInicial"/>
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
				<rip:inputSelect name="dadesMostrar" optionEnum="HistoricDadesMostrarEnum" 
								 multiple="true"
								 emptyOption="true" 
								 placeholderKey="historic.filtre.dadesMostrar" 
								 inline="true"/>
<%-- 				<spring:message code="estadistiques.filtre.dades.mostrar" /> --%>
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
				Gràfic / Taula
			</div>
			<div class="col-md-2">
				<rip:inputSelect name="tipusAgrupament" optionEnum="HistoricTipusEnumDto" 
								 multiple="true"
								 emptyOption="false" 
								 placeholderKey="historic.filtre.dadesMostrar" 
								 inline="true"/>
			</div>		
		</div>
	</form:form>
	<h1>Dades entitat</h1>
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
				<th data-col-name="numExpedientsOberts" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsOberts"/></th>
				<th data-col-name="numExpedientsObertsTotal" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsObertsTotal"/></th>
				<th data-col-name="numExpedientsTancats" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsTancats"/></th>
				<th data-col-name="numExpedientsTancatsTotal" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsTancatsTotal"/></th>
				<th data-col-name="numExpedientsAmbAlertes" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsAmbAlertes"/></th>
				<th data-col-name="numExpedientsAmbErrorsValidacio" data-orderable="false"><spring:message code="historic.taula.header.numExpedientsAmbErrorsValidacio"/></th>
				<th data-col-name="numDocsPendentsSignar" data-orderable="false"><spring:message code="historic.taula.header.numDocsPendentsSignar"/></th>
				<th data-col-name="numDocsSignats" data-orderable="false"><spring:message code="historic.taula.header.numDocsSignats"/></th>
				<th data-col-name="numDocsPendentsNotificar" data-orderable="false"><spring:message code="historic.taula.header.numDocsPendentsNotificar"/></th>
				<th data-col-name="numDocsNotificats" data-orderable="false"><spring:message code="historic.taula.header.numDocsNotificats"/></th>
			</tr>
		</thead>
	</table>
	<canvas id="chartEntitat" width="400" height="100"></canvas>
	<script type="text/javascript">
	$(function () {
		var ctx = 'chartEntitat';
		var chartEntitat = new Chart(ctx, {
		    type: 'line',
		    data: {
		        labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
		        datasets: [], 
// 		        [{
// 		            label: 'N. Expedients creats',
// 		            data: [12, 19, 3, 5, 2, 3],
// 		            backgroundColor: "rgba(0,0,0,0.0)",
//                     borderColor: "rgba(75,192,192,1)",
// 		                'rgba(255, 99, 132, 0.2)',
// 		                'rgba(54, 162, 235, 0.2)',
// 		                'rgba(255, 206, 86, 0.2)',
// 		                'rgba(75, 192, 192, 0.2)',
// 		                'rgba(153, 102, 255, 0.2)',
// 		                'rgba(255, 159, 64, 0.2)'
// 		            ],
// 		            borderColor: [
// 		                'rgba(255, 99, 132, 1)',
// 		                'rgba(54, 162, 235, 1)',
// 		                'rgba(255, 206, 86, 1)',
// 		                'rgba(75, 192, 192, 1)',
// 		                'rgba(153, 102, 255, 1)',
// 		                'rgba(255, 159, 64, 1)'
// 		            ],
// 		            borderWidth: 1
// 		        },
// 		        {
// 		            label: 'N. Expedients oberts',
// 		            data: [8, 8, 10, 10, 5, 1],
// 		            backgroundColor: "rgba(0,0,0,0.0)",
// 		            borderColor: "rgba(255,0,0,1)",
// 		            backgroundColor: [
// 		                'rgba(255, 99, 132, 0.2)',
// 		                'rgba(54, 162, 235, 0.2)',
// 		                'rgba(255, 206, 86, 0.2)',
// 		                'rgba(75, 192, 192, 0.2)',
// 		                'rgba(153, 102, 255, 0.2)',
// 		                'rgba(255, 159, 64, 0.2)'
// 		            ],
// 		            borderColor: [
// 		                'rgba(255, 99, 132, 1)',
// 		                'rgba(54, 162, 235, 1)',
// 		                'rgba(255, 206, 86, 1)',
// 		                'rgba(75, 192, 192, 1)',
// 		                'rgba(153, 102, 255, 1)',
// 		                'rgba(255, 159, 64, 1)'
// 		            ],
// 		            borderWidth: 1
// 		        }]
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
// 		    options: {
// 		        scales: {
// 		            yAxes: [{
// 		                stacked: true
// 		            }]
// 		        }
// 		    }
		});
        function getRandomColor() {
            var letters = '0123456789ABCDEF'.split('');
            var color = '#';
            for (var i = 0; i < 6; i++ ) {
                color += letters[Math.floor(Math.random() * 16)];
            }
            return color;
        }
		ajax_chart(chartEntitat, "historic/chart/entitat", {});
		
	   	// function to update our chart
	    function ajax_chart(chart, url, data) {
	        var data = data || {};
	
	        $.getJSON(url, data).done(function(response) {
	        	console.log(response);
	        	response.sort((a, b) => (a.data > b.data))
	        	if (response.length > 0) {
		        	var yLabels = response.map(item => new Date(item.data).toLocaleDateString("es"));
		        	
		        	var i = 0;
		            chart.data.labels = yLabels;
		        	for (var nomSerie in response[0]) {
		        		if (nomSerie.substring(0, 3) == "num"){
		        			console.log(response.map(item => item[nomSerie]));
		        			chart.data.datasets.push({
		        				'data': response.map(item => item[nomSerie]),
		        				'label': nomSerie,
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

	});

	</script>
	<h1>Dades per òrgan gestor</h1>
	<div class="row">
		<div class="col-md-2">
		<form id="form-estadistics" class="well" action="">
		
			<div class="pull-right">
				<button type="submit" name="accio" value="filtrar" class="btn btn-primary">
					<span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/>
				</button>
			</div>
		</form>
		</div>
		<div class="col-md-10">
		Hola
		</div>
	</div>
	<script type="text/javascript">
		$(function () {
			$.ajax({
				url: webutilAjaxEnumPath('HistoricEstadisticsEnumDto'),
		//			async: true,
				success: function(resposta) {
					for (var i = 0; i < resposta.length; i++) {
						var enumItem = resposta[i];
						$('form#form-estadistics').prepend(
								'<div class="checkbox">' +
				  					'<label>' +
				  					'<input type="checkbox" value="' + enumItem['value'] + '" selected>' +
				  					enumItem['text'] + 
				  					'</label>' +
								'</div>');
						
						// TODO: si esta seleccionat crear taula
		//							$('<input>', {
		//								type: 'checkbox',
		//								value: ,
		//								text: ,
		//// 								selected: enumValue == enumItem['value']
		//							}));
					}
				}
			});
		});
	</script>
	<h1>Dades per usuari</h1>
	
	<h1>Dades per interessat</h1>
</body>
</html>