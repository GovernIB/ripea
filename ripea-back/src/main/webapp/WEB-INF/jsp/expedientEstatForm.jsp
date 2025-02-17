<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty expedientEstatCommand.id}"><c:set var="titol"><spring:message code="expedient.estat.form.titol.crear"/></c:set></c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${consultar}">
				<c:set var="titol"><spring:message code="expedient.estat.form.titol.consultar"/></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="expedient.estat.form.titol.modificar"/></c:set>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#ambColor").change(function() {
				$("#color").prop("disabled", !$("#ambColor").prop('checked'));
			});
		});
	</script>
	<rip:modalHead/>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/expedientEstat/${metaExpedientId}/save"/></c:set>
	<!-- Es redimensiona l'altura de la modal perquè quan «Usuari responsable» ofereix molts resultats, no deixa veure el camp d'escriptura. -->
	<div style="height: 350px;">
		<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="expedientEstatCommand">
			<form:hidden path="id"/>
			<form:hidden path="metaExpedientId"/>
			<form:hidden path="comu"/>
			<rip:inputText name="codi" textKey="expedient.estat.form.camp.codi" required="true" readonly="${bloquejarCamps}"/>
			<rip:inputText name="nom" textKey="expedient.estat.form.camp.nom" required="true" readonly="${bloquejarCamps}"/>
			
			<div class="row">
				<div class="col-xs-4" style="text-align: right;">
					<label for="color">Color</label>
				</div>
				<div class="col-xs-8">
					<form:checkbox id="ambColor" path="ambColor" disabled="${bloquejarCamps}" />
					<form:input type="color" path="color" id="color" name="color" value="" disabled="${bloquejarCamps or not expedientEstatCommand.ambColor}" style="width: calc(100% - 36px); float: right;" />
				</div>
			</div>
			<rip:inputCheckbox name="inicial" textKey="expedient.estat.form.camp.inicial" disabled="${bloquejarCamps}"/>
			
			<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
			<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
			<rip:inputSuggest 
				name="responsableCodi" 
				urlConsultaInicial="${urlConsultaInicial}" 
				urlConsultaLlistat="${urlConsultaLlistat}" 
				textKey="expedient.estat.form.camp.responsable"
				suggestValue="codi"
				suggestText="nom"
				disabled="${bloquejarCamps}"
				placeholderKey="expedient.estat.form.camp.responsable"/>
				
			<div id="modal-botons">
				<c:if test="${!consultar}">
					<button type="submit" class="btn btn-success" <c:if test="${bloquejarCamps}">disabled</c:if>><span class="fa fa-save"></span>
						<c:choose>
							<c:when test="${empty expedientEstatCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
							<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
						</c:choose>
					</button>
				</c:if>			
				<a href="<c:url value="/metaDada"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
			</div>
		</form:form>
	</div>
</body>
</html>