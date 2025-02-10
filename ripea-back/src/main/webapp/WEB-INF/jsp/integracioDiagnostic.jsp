<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol"><spring:message code="integracio.diag.titol"/></c:set>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title>${titol}</title>
    <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
    <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
    <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
    <script src="<c:url value="/js/webutil.common.js"/>"></script>	
	<rip:modalHead/>
	<script type="text/javascript">
	
		let baseUrl = '<c:url value="/integracio/diagnostic"/>';
		
		$(document).ready(function() {

			$("button[name=refrescar]").click(function() {
				window.location.href = '<c:url value="/modal/integracio/diagnostic"/>';
			});
			
			<c:set var="tempsDelay">100</c:set>
			<c:forEach var="integracio" items="${integracions}">
				setTimeout(getDiagnostic, ${tempsDelay}, '${integracio.codi}');
				<c:set var="tempsDelay" value="${tempsDelay+500}"/>
			</c:forEach>
		});

		function getDiagnostic(integracioCodi) {
			$.get(baseUrl+'/'+integracioCodi).done(function(data) {
				$("#DD_"+integracioCodi).html("<span class='"+data.texte+"'></span> "+data.codi);
			})
			.fail(function() {
				$("#DD_"+integracioCodi).html("<span class='fa fa-times vermell'></span> "+error.jquery.ajax);
			});
		}
	</script>
</head>
<body>

	<form:form action="" method="post" cssClass="well" commandName="diagnosticFiltreDto">
		<div class="row">
	        <div class="col-xs-6">
				<rip:inputSelect
	            	name="entitatCodi"
	            	optionItems="${entitatsDiagnostic}"
	            	optionValueAttribute="codi"
	            	optionTextAttribute="nom"
	            	optionMinimumResultsForSearch="3"
	            	emptyOption="true"
	            	placeholderKey="accio.massiva.list.filtre.tipusexpedient"
	            	inline="true"/>
	        </div>
	        <div class="col-xs-6">
				<rip:inputSelect
	            	name="organCodi"
	            	optionItems="${organsDiagnostic}"
	            	optionValueAttribute="codi"
	            	optionTextAttribute="nom"
	            	optionMinimumResultsForSearch="3"
	            	emptyOption="true"
	            	placeholderKey="accio.massiva.list.filtre.tipusexpedient"
	            	inline="true"/>	        
	        </div>	        
	     </div>
	     <div class="row">
	        <div class="col-xs-12 pull-right">
	            <div class="pull-right">
	                <button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
	                <button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
	                <button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="integracio.boto.diag"/></button>
	            </div>
	        </div>
        </div>    
	</form:form>

	<dl class="dl-horizontal">
		<c:forEach var="integracio" items="${integracions}">
			<dt><spring:message code="sistema.extern.codi.${integracio.codi}"/></dt>
			<dd id="DD_${integracio.codi}" style="padding-bottom: 10px;"><span class="fa fa-refresh fa-spin"></span></dd>
		</c:forEach>
	</dl>

	<div id="modal-botons">
		<a href="<c:url value="/integracio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		<%-- button type="button" class="btn btn-warning" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.refresca"/></button --%>
	</div>

</body>