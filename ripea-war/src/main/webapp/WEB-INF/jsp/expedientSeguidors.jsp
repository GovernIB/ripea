<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="expedient.followers.titol"/></c:set>
<html>
<head>
	<title>${titol}: ${expedient.nom}</title>
	<rip:modalHead/>
<style type="text/css">

#comentaris_content {
 	overflow: hidden;
    height: auto;
    overflow-y: scroll;
    margin-bottom: 15px;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	var $iframe = $(window.frameElement);
	$iframe.closest('div.modal-lg').css('width', '550px');
});
</script>
</head>
<body>
	<div id="comentaris_content" class="col-xs-12">
		<ul class="list-group">
			<c:forEach items="${followers}" var="follower">
				<li class="list-group-item ${follower.codi == usuariActual.codi ? 'active' : ''}"><span class="fa fa-user"></span> ${follower.nom} (${follower.nif})</li>
			</c:forEach>
		</ul>
	</div>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
