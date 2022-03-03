<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title>${titolConsulta}</title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<style>
	.linea-exm {
		margin-bottom:8px;
	}
	.in-line-badge{
		margin-left: 0px;
		margin-right: 5px;
	}
	.progress {
		margin-bottom: 10px;
	}
	.one-line {
		display: inline-flex;
	}
	.massiu-dades {
		width: 100%;
	}
	.icona-carrega {
		text-align: center;
	}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		$('.collapsable').on('show.bs.collapse', function () {
			var exm_id = $(this).data("exmid");
			$.ajax({
				type: 'GET',
				url: '<c:url value="/massiu/consultaContingut/' + exm_id + '"/>',
				success: function(data) {
					mostrarContinguts(data, exm_id);
					mostraDetall(data);
				},
				error: function() {
					console.log("error consultant continguts");
				}
			});
		});
		$("button[name=refrescar]").click(function() {
			location.reload();
		});
		$("button[name=nextPage]").click(function() {
			window.location.href = '<c:url value="/modal/massiu/consulta/${pagina + sumador}"/>';
		});
		$("button[name=previousPage]").click(function() {
			window.location.href = '<c:url value="/modal/massiu/consulta/${pagina - 1}"/>';
		});
	});
	function mostrarContinguts(continguts, exm_id) {
		$('#continguts_' + exm_id).empty();
		var html_cont =
			'<table class="table table-striped table-bordered" id="taula_cont_' + exm_id + '">' + 
			'<thead>' +
			'  <tr>' +
			'    <th class="massiu-contingut">Contingut</th>' +
			'    <th class="massiu-estat">Estat</th>' +
			'    <th class="massiu-contingut">Data</th>' +
			'  </tr>' +
			'</thead>' +
			'<tbody>';
		for (var i in continguts) {
			var contingut = continguts[i];
			html_cont += '<tr class="' + (contingut.estat == "ESTAT_ERROR" ? ' danger' : '') + '">';
			html_cont += '<td>' + contingut.documentNom + '</td>';
			var estat = "";
			if (contingut.estat == "ESTAT_CANCELAT"){
				estat = "<span class='fa fa-check-circle'></span><label style='padding-left: 10px'><spring:message code='accio.massiva.estat.ESTAT_CANCELAT'/></label>";
			} else if (contingut.estat == "ESTAT_ERROR"){
				estat = "<span class='fa fa-exclamation-circle'></span><label class='msg-error' data-msg-error='" + contingut.error + "' style='cursor: pointer;padding-left: 10px'><spring:message code='accio.massiva.estat.ESTAT_ERROR'/></label>";					
			} else if (contingut.estat == "ESTAT_FINALITZAT"){
				estat = "<span class='fa fa-check-circle'></span><label style='padding-left: 10px'><spring:message code='accio.massiva.estat.ESTAT_FINALITZAT'/></label>";
			} else if (contingut.estat == "ESTAT_PENDENT"){
				estat = "<span class='fa fa-circle-o-notch fa-spin'></span><label style='padding-left: 10px'><spring:message code='accio.massiva.estat.ESTAT_PENDENT'/>";
			}				
			html_cont += '<td>' + estat + '</td>';
			html_cont += '<td>' + (contingut.dataFiAmbFormat != undefined ? contingut.dataFiAmbFormat : '') + '</td>';
			html_cont += '</tr>';
		}
		html_cont += '</tbody></table>';
		$('#continguts_' + exm_id).html(html_cont);
	}
	var changeTooltipPosition = function(event) {
	 	$('div.tooltip').css({left: 20});
	};
	var showTooltip = function(event) {
		$('div.tooltip').remove();
		$("<div class='tooltip'>" + $(this).data("msg-error") + "</div>").css({
	 		position: "absolute",
			display: "none",
			right: 20,
			top: event.pageY,
			top:event.pageY+4,
			"background-color": "#FFFFCA",
			color: "#000023",
			opacity: 0.90,
			"background-clip": "padding-box",
			border: "1px solid rgba(0, 0, 0, 0.15)",
			"border-radius": "4px",
			"box-shadow": "0 6px 12px rgba(0, 0, 0, 0.176)",
			"font-size": "14px",
			"list-style": "outside none none",
			margin: "0",
			"min-width": "160px",
			padding: "10px",
			"text-align": "left",
		    "word-wrap": "break-word",
		    "z-index": "1000"
		}).appendTo("body").fadeIn(200);
		changeTooltipPosition(event);
		$('div.tooltip').bind({
			mouseleave: hideTooltip
		});							
	};
	var hideTooltip = function(event) {
		var el = document.elementFromPoint(event.pageX, event.pageY);
   		$('div.tooltip').remove();
	};
	function mostraDetall(continguts) {
		$(".msg-error").unbind();
		$(".msg-error").bind({
			   mousemove : changeTooltipPosition,
			   mouseenter : showTooltip
		});
	}
</script>
</head>
<body>

<div class="panel panel-default">
    <div class="panel-heading" role="tab">
    	<div class="row">
			<div class="col-md-3"><strong><spring:message code="accio.massiva.header.nom"/></strong></div>
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.execucio"/></strong></div>
			<div class="col-md-1"><strong><spring:message code="accio.massiva.header.error"/></strong></div>
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.dataInici"/></strong></div>
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.dataFi"/></strong></div>
			<div class="col-md-2"><strong><spring:message code="accio.massiva.header.usuari"/></strong></div>
		</div>
	</div>
</div>

 <c:forEach var="exm" items="${execucionsMassives}">
 
 <c:choose>
 	<c:when test="${exm.errors > 0}">
 		<c:set var="modebg" value="danger"/>
 	</c:when>
 	<c:otherwise>
 		<c:set var="modebg" value="default"/>
 	</c:otherwise>
 </c:choose>
 	
  <div class="panel panel-${modebg} linea-exm">
    <div class="panel-heading collapsable" role="tab" id="heading_${exm.id}">
        <a role="button" data-toggle="collapse" href="#collapse_${exm.id}" aria-expanded="true" aria-controls="collapse_${exm.id}">
        	<div class="row">
	        	<div class="col-xs-3">
		          	<spring:message code="accio.massiva.tipus.${exm.tipus}"/>
	          	</div>
	          	<div class="col-xs-2 one-line" id="barra_${exm.id}">
		          	<div><span class="mass-badge badge in-line-badge">${fn:length(exm.contingutIds)}</span></div> 
		          	<div class="massiu-dades" id="pbar_${exm.id}">
		          		<div class="progress">
	    					<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${exm.executades}" aria-valuemin="0" aria-valuemax="100" style="width: ${exm.executades}%">
	    	  					<span><div class="value">${exm.executades}%</div></span>
	    	  				</div>
		          		</div>
		          	</div>
	          	</div>
	          	<div class="col-xs-1" id="errors_${exm.id}">
		          	${exm.errors}
	          	</div>
	          	<div class="col-xs-2">
		          	<fmt:formatDate value="${exm.dataInici}" pattern="dd/MM/yyyy HH:mm:ss"/>
	          	</div>
	          	<div class="col-xs-2" id="dataFi_${exm.id}">
		          	<fmt:formatDate value="${exm.dataFi}" pattern="dd/MM/yyyy HH:mm:ss"/>
	          	</div>
	          	<div class="col-xs-2">
		          	${exm.createdBy.nom}
	          	</div>
          	</div>
        </a>
    </div>
    <div id="collapse_${exm.id}" data-exmid="${exm.id}" class="panel-collapse collapse collapsable" role="tabpanel" aria-labelledby="heading_${exm.id}">
      <div class="panel-body" id="continguts_${exm.id}">
      	<div class="icona-carrega">
      		<i class="fa fa-circle-o-notch fa-3 fa-spin" aria-hidden="true"></i>
      	</div>
      </div>
    </div>
  </div>
 </c:forEach>
<div id="modal-botons" class="well">
	<a href="<c:url value="/massiu/portafirmes"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
	<button type="button" class="btn btn-primary" name="previousPage" value="previousPage"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.previous"/></button>
		<button type="button" class="btn btn-primary" name="nextPage" value="nextPage"><spring:message code="comu.boto.next"/>&nbsp;<span class="fa fa-arrow-right"></span></button>
	<button type="button" class="btn btn-primary" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.refrescar"/></button>
</div>
</body>
</html>

