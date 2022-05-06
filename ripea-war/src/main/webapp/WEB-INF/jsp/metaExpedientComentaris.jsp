<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="metaexpedient.comentaris.titol"/></c:set>
<html>
<head>
	<title>${titol}: ${metaExpedient.nom}</title>
	<rip:modalHead/>
	
	<style>
		.enviar-comentari {
			width: 84%
		}
		.comentari-bocata {
			width: 85%;
		}
		.comentari-autor {
			color: #999;
		}
		div.comentari-autor {
			margin-bottom: 6px;
		}
		.comentari-propi {
			background-color: #dfeecf;
		}
		#comentaris_content {
		 	overflow: hidden;
		    height: 500px;
		    max-height: 500px;
		    overflow-y: scroll;
		    margin-bottom: 15px;
		}
	</style>
	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('button.enviar-comentari').click(function() {
				var text = $("#comentari_text").val();
				if (text != undefined && text != "") {
					$('.enviar-comentari').prop("disabled", true);
					$("#comentaris_content").empty();
					$("#comentaris_content").append('<div class="datatable-dades-carregant" style="text-align: center; margin-top: 200px;"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>');
					enviarLlistarComentaris(text);
				}
			});
			
			enviarLlistarComentaris("");


			$('textarea').keyup(function(e) {
			    var $lines = $(this).val().split(/\r|\r\n|\n/).length;
			    if ($lines>10) {
			    	$lines = 10;
				}
			    $(this).get(0).rows = $lines;
			    window.scrollTo(0, document.body.scrollHeight || document.documentElement.scrollHeight);
			});			
		});
		
		function enviarLlistarComentaris(text) {
			$('textarea').get(0).rows = 1;
			$.ajax({
				type: 'POST',
				url: "<c:url value="/metaExpedient${isRevisor? 'Revisio': ''}/${metaExpedient.id}/comentaris/publicar"/>",
				data: {
					text: text
				},
				success: function(data) {
					pintarComentaris(data);
					$("#comentari_text").val("");
					webutilModalAdjustHeight();
					$('.enviar-comentari').prop("disabled", false);
				},
				error: function() {
					console.log("error enviant comentari..");
				}
			});
		}
		
		function pintarComentaris(comentaris) {
			$("#comentaris_content").empty();
			for (var comentariIndex in comentaris) {
				var comentari = comentaris[comentariIndex];
				
				var fons = "";
				var pull = "left";
				var propi = comentari.createdBy.codi == "${usuariActual.codi}"; 
				if (propi) {
					pull = "right"
					fons = "comentari-propi";
				}
					
				var comentariHtml = '<div class="well comentari-bocata pull-' + pull + ' ' + fons + '">';
				if (!propi)
					comentariHtml += '<div class="comentari-autor"><strong>' + comentari.createdBy.nom + '</strong></div>';
				
				comentariHtml +='<p style="white-space: pre;">' + comentari.text + '</p>' +
				'<small class="pull-right comentari-autor">' + comentari.createdDateAmbFormat + '</small>' +
				'</div>';
				$("#comentaris_content").append(comentariHtml);
			}
			scrollFinal();
		}
		
		function scrollFinal() {
			var contenidor = $('#comentaris_content');
			var height = contenidor[0].scrollHeight;
			contenidor.scrollTop(height);
		}
	</script>
	
</head>
<body>
	<div id="comentaris_content" class="col-xs-12">
	</div>
	
	<div class="col-xs-10">
		<textarea id="comentari_text" rows="1" style="resize: none;" class="form-control" placeholder="<spring:message code="contingut.comentaris.text.placeholder"/>" maxlength="1024"></textarea>
	</div>
	<div class="col-xs-2">
		<button class="btn btn-success enviar-comentari"><span class="fa fa-paper-plane-o"></span>&nbsp;<spring:message code="comu.boto.enviar"/></button>
	</div>
		
	<div class="col-xs-12" style="height:10px">
	</div>

	<div id="modal-botons" class="well">
		<a href="<c:url value="/metaexpedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
