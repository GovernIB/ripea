<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${contingut.expedient}"><c:set var="titol"><spring:message code="contingut.errors.titol.expedient"/></c:set></c:when>
	<c:when test="${contingut.document}"><c:set var="titol"><spring:message code="contingut.errors.titol.document"/></c:set></c:when>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">
		$( document ).ready(function() {
			$( document ).ajaxComplete(function() {
				webutilModalAdjustHeight();
			});
			$('#taulaAlertes').on('rowinfo.dataTable', function(e, td, rowData) {
				$(td).append(rowData.error);
				webutilModalAdjustHeight();
			});
		});
	</script>
	<style type="text/css">
		.altura {
			height: 60px;
		}
	</style>
</head>
<body>
	<c:if test="${!contingut.valid}">
		<c:set var="hiHaMetaDades" value="${false}"/>
		<c:set var="hiHaMetaDocuments" value="${false}"/>
		<c:set var="hiHaDocumentsSenseMetaNode" value="${false}"/>
		<c:set var="hiHaNotificacionsNoFinalitzades" value="${false}"/>
		<c:forEach var="error" items="${errors}">
			<c:if test="${error.errorMetaDada}"><c:set var="hiHaMetaDades" value="${true}"/></c:if>
			<c:if test="${error.errorMetaDocument}"><c:set var="hiHaMetaDocuments" value="${true}"/></c:if>
			<c:if test="${error.documentsWithoutMetaDocument}"><c:set var="hiHaDocumentsSenseMetaNode" value="${true}"/></c:if>
			<c:if test="${error.withNotificacionsNoFinalitzades}"><c:set var="hiHaNotificacionsNoFinalitzades" value="${true}"/></c:if>
		</c:forEach>
		<c:if test="${hiHaMetaDades}">
			<h4><span class="fa fa-exclamation-triangle text-warning"></span>&nbsp;<spring:message code="contingut.errors.falten.metadades"/></h4>
			<ul class="list-group">
				<c:forEach var="error" items="${errors}">
					<c:if test="${error.errorMetaDada}">
						<li class="list-group-item">${error.metaDada.nom}</li>
					</c:if>
				</c:forEach>
			</ul>
		</c:if>
		<c:if test="${hiHaMetaDocuments}">
			<h4><span class="fa fa-exclamation-triangle text-warning"></span>&nbsp;<spring:message code="contingut.errors.falten.metadocuments"/></h4>
			<ul class="list-group">
				<c:forEach var="error" items="${errors}">
					<c:if test="${error.errorMetaDocument}">
						<li class="list-group-item">${error.metaDocument.nom}</li>
					</c:if>
				</c:forEach>
			</ul>
		</c:if>
		<c:if test="${hiHaDocumentsSenseMetaNode}">
			<div class="${!hiHaMetaDades && !hiHaMetaDocuments ? 'altura' : ''}">
				<h4><span class="fa fa-exclamation-triangle text-warning"></span>&nbsp;<spring:message code="contingut.errors.documents.sense.metadocuments"/></h4>
			</div>
			<br>
		</c:if>
		
		<c:if test="${hiHaNotificacionsNoFinalitzades}">
			<div class="${!hiHaMetaDades && !hiHaMetaDocuments ? 'altura' : ''}">
				<h4><span class="fa fa-exclamation-triangle text-warning"></span>&nbsp;<spring:message code="contingut.errors.notificacions.nofinalitzades"/></h4>
			</div>
		</c:if>
	</c:if>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contingut/${contingut.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>