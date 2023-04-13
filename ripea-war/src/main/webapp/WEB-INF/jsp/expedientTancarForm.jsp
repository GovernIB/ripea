<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.expedient.tancar.form.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
<style type="text/css">
#loadingwrap {
	position: fixed;
	width: 100%;
	height:100%;
	display: flex;
	align-items: center;
	top: 0;
}
.loading {
  display: flex;
  margin: 0 auto;
}
</style>
	<rip:modalHead/>
</head>
<body>
	<div id="loadingwrap" style="display: none">
		<div class="loading">
			<span class="fa fa-circle-o-notch fa-spin fa-3x"></span>
		</div>
	</div>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="expedientTancarCommand">
		<form:hidden path="id"/>
		<c:if test="${!empty esborranys}">
			<div class="alert well-sm alert-info">
				<span class="fa fa-info-circle"></span>
				<spring:message code="contingut.expedient.tancar.esborranys.info"/>
			</div>
			<table class="table table-bordered">
			<thead>
				<tr>
					<th><spring:message code="contingut.info.nom"/></th>
					<th><spring:message code="contingut.info.tipus"/></th>
					<th><spring:message code="contingut.info.createl"/></th>
					<th><spring:message code="contingut.info.creatper"/></th>
					<th width="10%">&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="esborrany" items="${esborranys}">
					<tr>
						<td><rip:blocIconaContingut contingut="${esborrany}"/>&nbsp;${esborrany.nom}</td>
						<td>${esborrany.metaNode.nom}</td>
						<td><fmt:formatDate value="${esborrany.createdDate}" pattern="dd/MM/yyyy HH:mm"/></td>
						<td>${esborrany.createdBy.nom}</td>
						<td><form:checkbox path="documentsPerFirmar" value="${esborrany.id}" disabled="${esborrany.fitxerExtension != 'pdf' && esborrany.fitxerExtension != 'zip'}"/></td>
					</tr>
				</c:forEach>
			</tbody>
			</table>
		</c:if>
		<rip:inputTextarea name="motiu" textKey="contingut.expedient.tancar.form.camp.motiu" required="true"/>
		<div id="modal-botons" class="well">
			<button type="submit" id="btnSubmit" class="btn btn-success" data-noloading="true"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.tancar"/></button>
			<a href="<c:url value="/contingut/${expedient.pare.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
<script>
setTimeout(() => {
	$(document).ready(function() {
		$('button.btn-success', '#modal-botons').on('click', function(event) {
			let countSeleccionats = $('input[name=documentsPerFirmar]:checked').length;
			let total = $('input[name=documentsPerFirmar]:checkbox').length;
			let countNoSeleccionats = total - countSeleccionats;
			if (countNoSeleccionats) {
				if (confirm("<spring:message code="contingut.expedient.tancar.esborranys.confirm"/>")) {
					window.parent.document.getElementById("btnSubmit").disabled = true;
					$('#loadingwrap').css('display', 'flex');
					$('form').css('visibility', 'hidden');
					return true
				} else {
					return false;
				}
			} else {
				window.parent.document.getElementById("btnSubmit").disabled = true;
				$('#loadingwrap').css('display', 'flex');
				$('form').css('visibility', 'hidden');
			}
		});
	});
}, 100);
</script>
</body>
</html>
