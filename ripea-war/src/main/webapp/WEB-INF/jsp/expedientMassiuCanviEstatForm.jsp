<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="titol"><spring:message code="expedient.massiu.canviarEstat.titol"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
</head>
<body>


	<c:set var="formAction"><rip:modalUrl value="/massiu/canviEstat/canviarEstat"/></c:set>

	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientMassiuCanviEstatCommand">
		<rip:inputSelect name="expedientEstatId" textKey="expedient.massiu.estat.form.camp"  optionItems="${expedientEstats}" optionValueAttribute="id" optionTextAttribute="nom"/>
		<div class="row" style="margin-bottom: 60px;"></div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
