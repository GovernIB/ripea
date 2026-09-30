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

	// Espera entre elements (ms), opcional per a cada procés (atribut esperaEntreIteracionsMs del controlador).
	// La fa el navegador: el servidor respon tan bon punt acaba cada element.
	var esperaEntreIteracionsMs	= parseInt('${esperaEntreIteracionsMs}', 10) || 0;
	var timerEspera				= null;

	// Servei no disponible (servidor aturat o reiniciant-se): el procés es pausa sense comptar l'element com a error
	// i es comprova periòdicament si el servidor torna a respondre. /api/* no està protegit a nivell de contenidor,
	// per tant respon (encara que sigui amb 401/403) sense redirigir al proveïdor d'identitat quan la sessió s'ha perdut.
	var urlComprovacioServei	= '<c:url value="/api/"/>';
	var intervalComprovacioMs	= 15000;
	var timerComprovacio		= null;

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
			aturarEspera();
			aturarComprovacioServei();
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
					if (esErrorServei(jqXHR)) {
						// No és un error de l'element: no s'avança i es torna a intentar quan el servei respongui
						pausarPerServeiNoDisponible(jqXHR);
						return;
					}
					currentIteration++;
					iterationsKo++;
					$("#elementsKo").html(iterationsKo);
					printError(jqXHR.responseText);
				},
				complete: function(jqXHR) {
					if (esErrorServei(jqXHR)) {
						return;
					}
					updateProgress();
					if (currentIteration<elementsAiterar.length) {
						programarSeguentIteracio();
					} else {
						$("#botonera").hide();
						$("#progressInfo").html("<b>L'EXECUCIÓ DEL PROCÉS HA FINALITZAT.</b>");
					}
				}
			});
		}
	}

	// Llança el següent element, després de l'espera configurada si n'hi ha
	function programarSeguentIteracio() {
		if (!procesActiu) {
			return;
		}
		if (esperaEntreIteracionsMs > 0) {
			$("#progressInfo").html("Esperant " + (esperaEntreIteracionsMs / 1000) + " s abans del següent element ("
					+ elementsAiterar[currentIteration] + ")...");
			timerEspera = setTimeout(function() {
				timerEspera = null;
				executaIteracio();
			}, esperaEntreIteracionsMs);
		} else {
			executaIteracio(); //Cridada recursiva per el seguent element
		}
	}

	function aturarEspera() {
		if (timerEspera) {
			clearTimeout(timerEspera);
			timerEspera = null;
		}
	}

	// Respostes que no depenen de l'element: sense connexió (0) o errors del proxy/servidor d'aplicacions (502, 503, 504)
	function esErrorServei(jqXHR) {
		return jqXHR.status === 0 || jqXHR.status === 502 || jqXHR.status === 503 || jqXHR.status === 504;
	}

	function pausarPerServeiNoDisponible(jqXHR) {
		procesActiu = false;
		// El procés ja està pausat i es reprèn sol quan el servei torna a estar disponible: cap dels dos botons té sentit
		$("button[name='BotoReinicia']").prop("disabled", true);
		$("button[name='BotoPausa']").prop("disabled", true);
		var estat = jqXHR.status === 0 ? "sense connexió" : "HTTP " + jqXHR.status;
		printAvis("Servei no disponible (" + estat + ") a l'element " + elementsAiterar[currentIteration]
				+ ": procés pausat. Es comprovarà cada " + (intervalComprovacioMs / 1000) + " segons si torna a estar disponible.", "coral");
		$("#progressInfo").html("<b style=\"color: coral;\">Servei no disponible. Esperant que torni a estar disponible...</b>");
		programarComprovacioServei();
	}

	function programarComprovacioServei() {
		aturarComprovacioServei();
		timerComprovacio = setTimeout(comprovarServei, intervalComprovacioMs);
	}

	function aturarComprovacioServei() {
		if (timerComprovacio) {
			clearTimeout(timerComprovacio);
			timerComprovacio = null;
		}
	}

	// 1r pas: el servidor respon? Qualsevol resposta que no sigui un error de servei (també 401/403) indica que sí.
	function comprovarServei() {
		timerComprovacio = null;
		$.ajax({
			type: 'GET',
			url: urlComprovacioServei,
			cache: false,
			timeout: 10000,
			complete: function(jqXHR) {
				if (esErrorServei(jqXHR)) {
					$("#progressInfo").html("<b style=\"color: coral;\">Servei no disponible (darrera comprovació: " + horaActual() + "). Esperant que torni a estar disponible...</b>");
					programarComprovacioServei();
				} else {
					comprovarSessio();
				}
			}
		});
	}

	// 2n pas: la sessió continua vàlida? Si el servidor s'ha reiniciat, la petició autenticada no arriba (redirecció al
	// proveïdor d'identitat) i cal recarregar la pàgina; la llista d'elements es recalcula i només hi surten els pendents.
	function comprovarSessio() {
		$.ajax({
			type: 'GET',
			dataType: "json",
			url: urlTotalIteracions,
			cache: false,
			success: function(data) {
				printAvis("Servei disponible de nou: es reprèn el procés a l'element " + elementsAiterar[currentIteration] + ".", "mediumseagreen");
				$("button[name='BotoReinicia']").prop("disabled", true);
				$("button[name='BotoPausa']").prop("disabled", false);
				procesActiu = true;
				executaIteracio();
			},
			error: function(jqXHR) {
				printAvis("Servei disponible de nou, però la sessió ha caducat: recarregueu la pàgina per continuar amb els elements pendents.", "mediumseagreen");
				$("#progressInfo").html("<b>Servei disponible de nou. Recarregueu la pàgina per continuar.</b>");
				$("button[name='BotoReinicia']").prop("disabled", true);
				$("button[name='BotoPausa']").prop("disabled", true);
			}
		});
	}

	function printAvis(msg, color) {
		const p = document.createElement("p");
		p.style.color = color;
		p.style.fontWeight = "bold";
		p.textContent = horaActual() + " - " + msg;
		document.getElementById("errorsText").appendChild(p);
	}

	function horaActual() {
		return new Date().toLocaleTimeString();
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