<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${contingut.expedient}"><c:set var="titol"><spring:message code="contingut.alertes.titol.expedient"/></c:set></c:when>
	<c:when test="${contingut.document}"><c:set var="titol"><spring:message code="contingut.alertes.titol.document"/></c:set></c:when>
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
	$('.collapse').on('hide.bs.collapse', function() {
		let buttonId = '#collapse-btn-' + this.id.substring('collapse-'.length);
		$(buttonId + ' span').attr('class', 'fa fa-chevron-down');
	});
	$('.collapse').on('hidden.bs.collapse', function() {
		webutilModalAdjustHeight();
	});
	$('.collapse').on('show.bs.collapse', function() {
		let buttonId = '#collapse-btn-' + this.id.substring('collapse-'.length);
		$(buttonId + ' span').attr('class', 'fa fa-chevron-up');
	});
	$('.collapse').on('shown.bs.collapse', function() {
		webutilModalAdjustHeight();
	});
});
</script>
</head>
<body>
	<c:forEach var="alerta" items="${alertes}">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="heading-${alerta.id}">
				<span class="fa fa-exclamation-circle text-danger"></span>&nbsp;
				${alerta.text}&nbsp;
				<a href="<c:url value="/contingut/${contingut.id}/alertes/${alerta.id}/llegir"/>" class="btn btn-primary btn-sm" title="<spring:message code="contingut.alertes.marcar.llegida"/>" style="float:right"><span class="fa fa-envelope-open-o"></span></a>
				<c:if test="${not empty alerta.error}">
					<a id="collapse-btn-${alerta.id}" href="#collapse-${alerta.id}" class="btn btn-default btn-sm" data-toggle="collapse" aria-expanded="false" aria-controls="collapse-${alerta.id}" style="float:right; margin-right:1em"><span class="fa fa-chevron-down"></span></a>
				</c:if>
				<br/><small><fmt:formatDate value="${alerta.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></small>
			</div>
			<c:if test="${not empty alerta.error}">
				<div id="collapse-${alerta.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading-${alerta.id}">
					<div class="panel-body">
						<pre>${alerta.error}</pre>
					</div>
				</div>
			</c:if>
		</div>
	</c:forEach>
	<c:if test="${empty alertes}">
		<br/>
		<div class="alert well-sm alert-info alert-dismissable">
			<span class="fa fa-info-circle"></span>&nbsp;
			<spring:message code="contingut.alertes.empty"/>
		</div>
		<br/>
	</c:if>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contingut/${contingut.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>