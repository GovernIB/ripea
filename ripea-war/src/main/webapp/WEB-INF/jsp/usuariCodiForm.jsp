<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<rip:modalHead/>
	<title><spring:message code="decorator.menu.canvi.usuari.codi"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="application/javascript">
		$(document).ready(function() {
			$('#enviarFormCodis').on('click', function () {
				event.preventDefault();
				bloquejarForm();
				const codisNous = $('#usuarisBatch').val();
				$.ajax({
					url: '<c:url value="/usuari/username/validar"/>',
					type: 'POST',
					data: { usuarisBatch: codisNous },
					success: function (validacionsList) {
						if (validacionsList && validacionsList!='') {
							if (confirm(validacionsList)) {
								$('#usuariCodiCommand').submit();
							} else {
								desBloquejarForm();
							}
						} else {
							alert("No s'ha introduit cap dada válida per iniciar el procés.");
							desBloquejarForm();
						}
					},
					error: function (xhr, status, error) {
						console.error("Error en obtenir UsuariDto: ", error);
					}
				});
			});
// 			webutilModalAdjustHeight(parent.iframes[0]);
		});

		function bloquejarForm() {
			$('#usuariCodiCommand').hide();
			$('#loading-overlay').show();
		}

		function desBloquejarForm() {
			$('#usuariCodiCommand').show();
			$('#loading-overlay').hide();
		}
	</script>
	<style>
		/* Capa semitransparent */
		#loading-overlay {
			position: fixed;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background-color: rgba(0, 0, 0, 0.1);
			z-index: 9999; /* Assegura que estigui per sobre de tot */
			display: flex;
			justify-content: center;
			align-items: center;
		}

		#loading-overlay .fa-spinner {
			color: white; /* Color blanc per al spinner */
		}
	</style>	
</head>
<body>
	<c:if test="${usuariCodiCommand.resultat!=null}">
		<c:out value="${usuariCodiCommand.resultat}" escapeXml="false"></c:out>
	</c:if>
	<c:if test="${usuariCodiCommand.resultat==null}">
	<c:url value="/usuari/username" var="formAction"/>
		<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="usuariCodiCommand" role="form">
			<div class="row">
				<div class="col-md-12">
					<rip:inputTextarea name="usuarisBatch" textKey="usuari.form.camp.antic" comment="usuari.codi.form.text"/>
				</div>
			</div>
			<div id="modal-botons" class="well">
				<button type="button" id="enviarFormCodis" class="btn btn-success"><spring:message code="comu.boto.executar"/></button>
				<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
			</div>
		</form:form>
		<div id="loading-overlay" style="display: none;">
			<i class="fa fa-spinner fa-spin fa-3x"></i>
		</div>
	</c:if>
</body>
</html>