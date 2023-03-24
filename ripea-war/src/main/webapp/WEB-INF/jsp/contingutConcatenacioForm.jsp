<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="concatenacio.form.crear"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/clamp.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jquery.filedrop.js"/>"></script>
	<rip:modalHead/>
<style type="text/css">
.contenidor-documents {
	text-align: center;
}
#contenidor-contingut {
	background-color: #fdfdfd;
	border: 5px solid #fbfbfb;
	display: inline-block;
	padding: 1%;
	width: 100%;
}
#contenidor-contingut li.element-contingut {
	display: -moz-inline-stack;
	display: inline-block;
	vertical-align: top;
	zoom: 1;
	margin-bottom: 1%;
	width: 20%;
}
#contenidor-contingut .thumbnail {
	margin-bottom: 0 !important;
	border: 2px solid #f9f9f9;
}
#contenidor-contingut .thumbnail:hover {
	border: 2px solid #ddd;
	background-color: #f5f5f5;
	cursor: move;
}
#contenidor-contingut .thumbnail h4 {
	margin-top: 4px;
}
#contenidor-contingut .thumbnail a {
	text-decoration: none;
}
#contenidor-contingut .caption p {
	
}
#contenidor-contingut .caption .dropdown-menu {
	text-align: left;
}
#contenidor-contingut .caption .dropdown-menu li {
	width: 100%;
	margin: 0;
	padding: 0;
}
#contenidor-info h3 {
	font-weight: bold;
	margin-top: 0;
	border-bottom: 1px solid #e3e3e3;
	padding-bottom: .6em;
}
#contenidor-info h4 {
	font-weight: bold;
	margin-top: 0;
	border-bottom: 1px solid #f5f5f5;
	padding-bottom: .6em;
}
#contenidor-info dt {
	color: #999;
	font-size: small;
	font-style: italic;
	font-weight: normal;
}
#contenidor-info #botons-accions-info button{
    padding: 1px 6px;
	margin-bottom: 4px;    
}
#contenidor-info dd {
	font-size: medium;
	font-weight: bold;
	margin-bottom: 0.4em;
}
#contingut-botons {
	margin-bottom: .8em;
}
</style>
<script>
$(document).ready(function() {
	webutilModalAdjustHeight();
	
	$('#contenidor-contingut').sortable({
		placeholder: "ui-state-highlight",
        update: function (event, ui) {
        	var ordreId = $(this).sortable('serialize');
        	console.log(ordreId);
            var multipleUrl = '<c:url value="/contingut/${expedientId}/ordre"/>';
            $.ajax({
                type: "GET",
                url: multipleUrl,
                data: ordreId
           });
        }
    });
	$('#contenidor-contingut').disableSelection();
});
</script>
</head>
<body>
<div class="container">
	<div class="container col-md-12">
		<div><spring:message code="contingut.document.form.titol.concatenacio.ordre"/></div>
	</div>
	<hr>
	<div class="contenidor-documents">
		<ul id="contenidor-contingut" class="list-inline row">
			<c:forEach var="document" items="${documents}">
				<li id="ordreId-${document.id}" class="element-contingut element-draggable element-droppable" data-contenidor-id="${document.id}">
		
					<div id="info-document-${document.id}" class="thumbnail element-noclick col-md-12">
						<div class="text-center">
							<rip:blocIconaContingut contingut="${document}" tamanyDoble="true"/> 
						</div>
						<div class="caption">
							<p class="text-center">
								<c:if test="${not document.valid}"><span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.icona.estat.invalid"/>"></span></c:if>
								<span class="fa fa-bookmark" title="<spring:message code="contingut.info.estat.firmat"/>"></span>
								${document.nom}
							</p>
						</div>
					</div>
				</li>
			</c:forEach>
		</ul>
	</div>
	<div class="contenidor-botons col-md-12 text-center">
		<div class="btn-group">
			<div id="descarregar-mult" class="btn-group">
				<a href="<c:url value="/contingut/${expedientId}/doCreateConcatenatedDocument"/>" data-toggle="modal" data-refresh-pagina="true" class="btn btn-default"  data-missatge-loading="<spring:message code="concatenacio.pdf.modal.missatge"/>">
					<span class="fa fa-paperclip"></span>
					<spring:message code="concatenacio.form.boto.concatenar"/>
				</a>
			</div>
		</div>
	</div>
</div>
</body>
</html>
