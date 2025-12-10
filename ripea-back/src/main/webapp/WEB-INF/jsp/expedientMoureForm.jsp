<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.moure.tot.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
	<form:form action="" class="form-horizontal" modelAttribute="expedientMoureCommand">
		<form:hidden path="expedientOrigenId"/>
		
		<div class="alert well-sm alert-info alert-dismissable">
			<p><spring:message code="contingut.moure.tot.alerta"/></p>
		</div>
				
		<rip:inputFixed textKey="contingut.moure.camp.origen">
			<rip:blocIconaContingut contingut="${expedientOrigen}"/> ${expedientOrigen.nom}
		</rip:inputFixed>
		
		<rip:inputSelect name="expedientDestiId" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" optionItems="${expedients}" optionTextAttribute="nom" optionValueAttribute="id" textKey="contingut.moure.camp.expedient.desti" required="true"/>

		
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.moure"/></button>
			<a href="<c:url value="/contenidor/${expedientOrigen.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
