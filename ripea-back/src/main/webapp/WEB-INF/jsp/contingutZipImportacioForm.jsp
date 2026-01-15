<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.boto.crear.document.multiple"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value='/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css'/>" rel="stylesheet"/>
	<link href="<c:url value='/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css'/>" rel="stylesheet"/>
	<script src="<c:url value='/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js'/>"></script>
	<script src="<c:url value='/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js'/>"></script>
	<link href="<c:url value='/css/jasny-bootstrap.min.css'/>" rel="stylesheet">
	<script src="<c:url value='/js/jasny-bootstrap.min.js'/>"></script>
	<script src="<c:url value='/webjars/moment/2.15.1/min/moment.min.js'/>"></script>
	<script src="<c:url value='/webjars/moment/2.15.1/min/locales.min.js'/>"></script>
	<script src="<c:url value='/webjars/moment/2.15.1/locale/${requestLocale}.js'/>"></script>
	<link href="<c:url value='/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build/css/bootstrap-datetimepicker.min.css'/>" rel="stylesheet"/>
	<script src="<c:url value='/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build/js/bootstrap-datetimepicker.min.js'/>"></script>
	<script src="<c:url value='/js/webutil.common.js'/>"></script>
	<script src="<c:url value='/webjars/autoNumeric/1.9.30/autoNumeric.js'/>"></script>
	<script src="<c:url value='/js/jquery.maskedinput.min.js'/>"></script>
	<rip:modalHead/>

	<style>
	#command { padding-bottom: 10px; }
	.title-container {
		margin-bottom: 20px;
		text-align: left;
		background-color: #696666;
		padding-left: 5px;
		line-height: 25px;
		height: 25px;
		color: #fff;
	}
	.progressContainer {
		margin-top: 15px;
		text-align: center;
		width: 95%;
		margin-left:auto;
		margin-right:auto;
	}
	.progressText {
		margin-top: 10px; 
	}
	.help-block {
		color: #a94442;
	}
	</style>

	<script>
	var intervalProgres;
	var preparantZipMsg = "<spring:message code='contingut.boto.crear.document.multiple.preparant'/>";
	var tancarModalMsg = "<spring:message code='contingut.boto.crear.document.multiple.tancar'/>";
	var cancelarModalMsg = "<spring:message code='contingut.boto.crear.document.multiple.cancelar'/>";

	$(document).ready(function() {

		$('#cancelarProcessarDocumentsBtn').hide();
		
		clearInterval(intervalProgres);

		mostrarBarraProgresExistent();

		processarZip();
		
		$('button[name=cancelarBtn]').click(tancarModalImportacio);

		$('button.close').click(function(e){
			e.preventDefault();
			tancarModalImportacio();
		});

	});
	
	function processarZip() {
		$(".progressContainer").remove();

		$('#processarDocumentsBtn').on('click', function () {
			
		    $('#processarDocumentsBtn', parent.document).attr('disabled', true);
		    
		    var form = $('form')[0];
		    var data = new FormData(form);

		    $('form, .esborranys').hide();
			$('#cancelarProcessarDocumentsBtn', parent.document).show();
			mostrarBarraProgres();
        	
		    $.ajax({
		        url: form.action,
		        type: 'POST',
		        data: data,
		        processData: false,
		        contentType: false,
		        error: function(jqXHR, textStatus, errorThrown) {
		        	$('form, .esborranys').show();
		        	$('#cancelarProcessarDocumentsBtn', parent.document).hide();
				    $('#processarDocumentsBtn', parent.document).attr('disabled', false);
				    $('#progressZip .progressContainer').remove();
				    $('form').find('.help-block').remove();
		        	$('form .fileinput').after('<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<span>' + jqXHR.responseText + '</span></p>')
		            clearInterval(intervalProgres);
		        }
		    });
		});
	}
	
	function cancelarProcessamentZip(){
		if(confirm(cancelarModalMsg)){
			$.ajax({
				url: "<c:url value='/contingut/${command.pareId}/zip/importacio/cancelar/'/>",
		        type: 'POST',
				success: function () {
					window.top.location.reload();
		        }
		    });
		}
	}
		
	function tancarModalImportacio(){
		if(confirm(tancarModalMsg)){
			window.top.location.reload();
		}
	}
		
	function mostrarBarraProgres(){
		var html = '<div class="progressContainer"> \
		           		<div class="progress"> \
		           			<div class="progress-bar progress-bar-striped active" style="width:0%">0%</div> \
		            	</div> \
		            <div class="progressText">' + preparantZipMsg + '</div> \
		            </div>';
		$('#progressZip').html(html);
		refreshProgres();
	}

	function refreshProgres(){
		intervalProgres = setInterval(consultarProgreso, 100); // Cada 0.5 segons refrescar
	}

	function consultarProgreso(){
		$.ajax({
			url: "<c:url value='/contingut/${command.pareId}/zip/importacio/progres/'/>",
			type: "GET",
			success: function(res){
				if(res){
					var prog = Math.round(res.progres || 0);
					$('#progressZip .progress-bar')
						.css('width', prog + '%')
						.text(prog + '%');

					if(res.error){
						$('#progressZip .progress-bar').addClass('progress-bar-danger');
						$('#progressZip .progressText').text(res.errorMsg || 'Error processant zip...');
					} else if (res.info && res.info.length > 0){
						$('#progressZip .progressText').text(res.info[res.info.length-1].text || 'Procesando...');
					}

					if(res.finished){
						clearInterval(intervalProgres);
						window.top.location.reload();
					}
				}
			},
			error: function(){
				clearInterval(intervalProgres);
				$('#progressZip .progress-bar').addClass('progress-bar-danger');
				$('#progressZip .progressText').text('Error consultant progress...');
			}
		});
	}
		
	function mostrarBarraProgresExistent() {
	    $.ajax({
	        url: "<c:url value='/contingut/${command.pareId}/zip/importacio/progres/'/>",
	        type: "GET",
	        success: function(res) {
	            if (res && !res.finished) {
	            	$('form, .esborranys').hide();
	            	$('#cancelarProcessarDocumentsBtn', parent.document).show();
	            	$('#processarDocumentsBtn', parent.document).attr('disabled', true);
	            	
	                if ($("#progressZip .progressContainer").length === 0) {
	                    var progressContainer = 
	                        '<div class="progressContainer"> \
	                         	<div class="progress"> \
	                         		<div class="progress-bar progress-bar-striped active" style="width:' + res.progres + '%">' + Math.round(res.progres) + '%</div> \
	                         	</div> \
	                        	<div class="progressText">' + (res.info[0]?.text || preparantZipMsg) + '</div> \
	                        </div>';
	                    $("#progressZip").html(progressContainer);
	                }

	                refreshProgres();
	            }
	        }
	    });
	}
	</script>
</head>

<body>
	<c:set var="formAction"><rip:modalUrl value="${action}"/></c:set>

	<div class="esborranys alert well-sm alert-info alert-dismissable">
		<spring:message code="contingut.boto.crear.document.multiple.info"/>
	</div>
	<div id="progressZip"></div>

	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="command" enctype="multipart/form-data">
		<form:hidden path="tascaId"/>
		<form:hidden path="metaExpedientId"/>
		
		<c:if test="${empty command.documents}">
			<rip:inputFile name="arxiuZip" textKey="contingut.document.zip.form.camp.arxiu" required="true"/>
		</c:if>

		<div id="modal-botons" class="well">
			<button type="button" onclick="cancelarProcessamentZip();" id="cancelarProcessarDocumentsBtn"  class="btn btn-danger">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.cancelar"/>
			</button>
			
			<button type="button" id="processarDocumentsBtn" class="btn btn-success">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.processar"/>
			</button>
			
			<button type="button" name="cancelarBtn" class="btn btn-default">
				<spring:message code="comu.boto.tanca"/>
			</button>
		</div>
	</form:form>
</body>
</html>
