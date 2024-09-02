<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="contingut.document.form.titol.descarregar.seleccionats"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
	<link href="<c:url value="/css/jstree.min.css"/>" rel="stylesheet">
	<rip:modalHead/>
<style type="text/css">
.rmodal {
	display: none;
	position: fixed;
	z-index: 1000;
	top: 0;
	left: 0;
	height: 100%;
	width: 100%;
	background: rgba(255, 255, 255, .8)
		url('<c:url value="/img/loading.gif"/>') 50% 50% no-repeat;
}

body.loading {
	overflow: hidden;
}

body.loading .rmodal {
	display: block;
}
</style>
<script type="text/javascript">
$(document).ready(function() {	
	webutilModalAdjustHeight();
	
	$body = $("body");
	$(document).on({
		ajaxStart: function() { $body.addClass("loading"); },
		ajaxStop: function() { $body.removeClass("loading"); }    
	});
	
	$('#btnSubmit').on('click', function(event) {
		event.preventDefault();

		const data = {
		        estructuraCarpetesJson: $('#estructuraCarpetesJson').val()
		};
		
		$.ajax({
            url: '<c:url value="/contingut/${descarregaCommand.pareId}/descarregarSelectedDocuments"/>',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(base64String, status, xhr) {
                
            	// Obtener el nombre del archivo
                const disposition = xhr.getResponseHeader('Content-Disposition');
                const fileName = disposition ? disposition.split('filename=')[1].replace(/"/g, '') : 'default_filename';
                
                // Decodificar el base64 a un ArrayBuffer
		        const byteCharacters = atob(base64String);
		        const byteNumbers = new Array(byteCharacters.length);
		        for (let i = 0; i < byteCharacters.length; i++) {
		            byteNumbers[i] = byteCharacters.charCodeAt(i);
		        }
		        const byteArray = new Uint8Array(byteNumbers);
		
		        // Crear un blob a partir del ArrayBuffer
		        const blob = new Blob([byteArray], { type: 'application/zip' });
        
                if (blob && blob instanceof Blob) {
	                // Crear un enlace para descargar el archivo
	                const url = window.URL.createObjectURL(blob);
	                const a = document.createElement('a');
	                a.href = url;
	                a.download = fileName; // Establecer el nombre del archivo
	                document.body.appendChild(a);
	                a.click();
	                a.remove();
	                window.URL.revokeObjectURL(url);
                } else {
                	console.error('La respuesta no es un blob válido.');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                var errorDiv = $("<div class='copy alert alert-danger' style='font-weight:bold;' role='alert'><span class='fa fa-exclamation-triangle'></span> <spring:message code='expedient.boto.descarregar.fitxer.comprimit.estructurat.valid'/></div>");

                $('form').prepend(errorDiv);
                
                setTimeout(function(){
	                errorDiv.remove();
	            }, 6000);
            }
        });
	
	});
});

function changedCallback(e, data) {
	var arbre = $('#arbreCarpetes');
	
	var json = arbre.data().jstree.get_json()
	var jsonString = JSON.stringify(json);

	webutilModalAdjustHeight();
	$('#estructuraCarpetesJson').val(jsonString);
}
</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/contingut/${descarregaCommand.pareId}/descarregarSelectedDocuments"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="descarregaCommand">
		<br/>
		<rip:arbreMultiple  name="estructuraCarpetesJson" isArbreSeleccionable="${true}" isFullesSeleccionable="${true}" id="arbreCarpetes" 
			atributId="id" atributNom="nom" arbre="${carpetes}" fulles="${documents}" fullesIcona="fa fa-file-text-o" fullesAtributId="id" 
			fullesAtributNom="nom" fullesAtributPare="pareId" changedCallback="changedCallback" isCheckboxEnabled="${true}"/>
			
		<br/>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-success" id="btnSubmit"><span class="fa fa-save"></span>&nbsp;<spring:message code="expedient.boto.descarregar.fitxer.comprimit.estructurat.boto"/></button>
			<a href="<c:url value="/contingut/${carpetaCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	
	<div class="rmodal"></div>
</body>
</html>
