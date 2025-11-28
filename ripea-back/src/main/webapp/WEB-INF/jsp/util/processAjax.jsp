<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>

<head>
	<title>${titolProces}</title>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	
	<script type="text/javascript">
	
	var urlTotalIteracions 		= '<c:url value="${urlTotalIteracions}"/>';
	var urlInteracioIndividual	= '<c:url value="${urlInteracioIndividual}"/>';
	var procesActiu 		= false;	
	var elementsAiterar;
	var totalIterations		= 100;
	var currentIteration	= 0;
	var iterationsOk		= 0;
	var iterationsKo		= 0;
	
	$(document).ready(function() {
		$.ajax({
			type: 'GET',
			dataType: "json",
			url: urlTotalIteracions,
			success: function(data) {
				debugger;
				if (data && data.length>0) {
					totalIterations = data.length;
					elementsAiterar = data;
					$("#standbyAlert").show();
				} else {
					$("#progressInfo").html("<b>No hi ha elements per iniciar el procés.</b>");
					$("button[name='BotoReinicia']").prop("disabled", true);
				}
			},
			error: function (error) {
				debugger;
				printError(error.responseText);
			}
		});
		
		$("button[name='BotoPausa']").on('click', function(e) {
			procesActiu=false;
	        $("button[name='BotoReinicia']").prop("disabled", false);
	        $("button[name='BotoPausa']").prop("disabled", true);
	        $("#pauseAlert").show();
		});
		
		$("button[name='BotoReinicia']").on('click', function(e) {
	        $("button[name='BotoReinicia']").prop("disabled", true);
	        $("button[name='BotoPausa']").prop("disabled", false);
	        $("#standbyAlert").hide();
	        $("#pauseAlert").hide();
	        procesActiu=true;
			executaIteracio();
		});
	});
	
	function executaIteracio() {
		if (procesActiu) {
			$.ajax({
				type: 'GET',
// 				dataType: "json",
				url: urlInteracioIndividual+"/"+elementsAiterar[currentIteration],
				beforeSend: function() {
					$("#progressInfo").html("Executant acció per l'element "+elementsAiterar[currentIteration]+" ...");
				},
				success: function(data) {
					debugger;
					currentIteration++;
					iterationsOk++;
					$("#elementsOk").html(iterationsOk);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					debugger;
					currentIteration++;
					iterationsKo++;
					$("#elementsKo").html(iterationsKo);
				    if (jqXHR.status === 0) {
				        printError("No se pudo conectar con el servidor (ERR_CONNECTION_REFUSED).");
				    } else {
				        printError(jqXHR.responseText);
				    }
				},
				complete: function() {
					updateProgress();
					if (currentIteration<elementsAiterar.length) {
						executaIteracio(); //Cridada recursiva per el seguent element
					} else {
						$("#botonera").hide();
						$("#progressInfo").html("<b>L'EXECUCIÓ DEL PROCÉS HA FINALITZAT.</b>");
					}
				}
			});
		}
	}
	
	function printError(msgError) {
		const p = document.createElement("p");
		p.textContent = msgError;
		document.getElementById("errorsText").appendChild(p);
	}
	
	function updateProgress() {
		let percent = Math.round((currentIteration / totalIterations) * 100);
		let bar = document.getElementById("progressBar");
		bar.style.width = percent + "%";
		bar.setAttribute("aria-valuenow", percent);
		bar.textContent = percent + "%"; // opcional, mostrar el número		
	}
	
	</script>	
</head>

<body>
<div class="row">
	<div class="col-md-4">
		<p style="padding-top: 6px;">Num. elements OK: <b id="elementsOk">0</b>, num. elements ERROR: <b id="elementsKo">0</b></p>
	</div>
	<div class="col-md-6" id="botonera" style="padding-bottom: 15px;">
		<button type="button" name="BotoReinicia"	class="btn btn-primary"><span class="fa fa-play-circle-o"></span>&nbsp;Iniciar</button>&nbsp;
		<button type="button" name="BotoPausa" 		class="btn btn-default" disabled><span class="fa fa-pause-circle-o"></span>&nbsp;Pausar</button>&nbsp;
		<b id="pauseAlert" style="color: coral; display: none;">Execució pausada</b>
		<b id="standbyAlert" style="color: mediumseagreen; display: none;">Premeu iniciar per començar amb el procés.</b>
	</div>
</div>
<div class="progress">
  <div id="progressBar" 
       class="progress-bar" 
       role="progressbar" 
       style="width: 0%;" 
       aria-valuenow="0" 
       aria-valuemin="0" 
       aria-valuemax="100">0%</div>
</div>
<div class="row">
	<div class="col-md-12">
		<p id="progressInfo"></p>
	</div>
</div>
<div class="row">
	<div class="col-md-12" id="errorsText" style="color: indianred;"></div>
</div>
</body>