<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>

	<title><spring:message code="metaexpedient.regla.detalls.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>

<style>

body .rmodal {
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}

</style>	
<script>

$(document).ready(function() {

	$('.btn-load').on('click', function() {
		$("body").prepend("<div class='rmodal'></div>")
	});
});
</script>	

</head>
<body>

	<div style="min-height: 50px;">

		<dl class="dl-horizontal">
			<dt><spring:message code="metaexpedient.regla.creat"/></dt>
			<dd>
				<c:choose>
					<c:when test="${not empty regla}">
						<span class="fa fa-check" style="color:green;"></span>
					</c:when>
					<c:otherwise>
						<span class="fa fa-close" style="color:red;"></span> <a href="<c:url value="/modal/metaExpedient/${metaExpedient.id}/reglaCrear"/>" class="btn btn-xs btn-default btn-load" style="margin-left: 10px;"><span class="fa fa-check"></span> <spring:message code="metaexpedient.list.accio.boto.crear.regla"/></a>
					</c:otherwise>
				</c:choose>
			</dd>	
			<c:if test="${not empty regla}">
				<dt><spring:message code="metaexpedient.regla.actiu"/></dt>
				<dd>
					<c:choose>
						<c:when test="${regla.activa}">
							<span class="fa fa-check" style="color:green;"></span>
						</c:when>
						<c:otherwise>
							<span class="fa fa-close" style="color:red;"></span> <a href="<c:url value="/modal/metaExpedient/${metaExpedient.id}/reglaActivar"/>" class="btn btn-xs btn-default btn-load" style="margin-left: 10px;"><span class="fa fa-check"></span> <spring:message code="metaexpedient.list.accio.boto.activar.regla"/></a>
						</c:otherwise>
					</c:choose>
				</dd>			
				<dt><spring:message code="metaexpedient.regla.data.creacio"/></dt>
				<dd>${regla.data}</dd>	
							
				<dt><spring:message code="metaexpedient.regla.nom"/></dt>
				<dd>${regla.nom}</dd>				

			</c:if>
		</dl>
	
	</div>
	<div id="modal-botons">
		<a href="<c:url value="/metaExpedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
