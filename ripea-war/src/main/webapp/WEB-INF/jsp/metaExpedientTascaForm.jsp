<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty metaExpedientTascaCommand.id}"><c:set var="titol"><spring:message code="metaexpedient.tasca.form.titol.crear"/></c:set></c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${consultar}">
				<c:set var="titol"><spring:message code="metaexpedient.tasca.form.titol.consultar"/></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="metaexpedient.tasca.form.titol.modificar"/></c:set>
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
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<rip:modalHead/>
	<script type="application/javascript">
		var colorsEstats = {};
		<c:forEach items="${expedientEstats}" var="estat">
			colorsEstats['${estat.id}'] = '${estat.color}';
		</c:forEach>

		function showColor(element) {
			const id = element.id;
			const color = colorsEstats[id];
			if (!color) {
				return $('<span class="no-icon"></span><span>' + element.text + '</span>');
			}
			return $('<span class="color-icon" style="background-color: ' + color + '"></span><span>' + element.text + '</span>');
		}
	</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaExpedient.id}/tasca/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaExpedientTascaCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="metaExpedientId"/>
		
		<rip:inputText name="codi" textKey="metaexpedient.tasca.form.camp.codi" required="true" readonly="${bloquejarCamps}"/>
		<rip:inputText name="nom" textKey="metaexpedient.tasca.form.camp.nom" required="true" readonly="${bloquejarCamps}"/>
		<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
		<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
		<rip:inputSuggest 
			name="responsable" 
			urlConsultaInicial="${urlConsultaInicial}" 
			urlConsultaLlistat="${urlConsultaLlistat}" 
			textKey="metaexpedient.tasca.form.camp.responsable"
			suggestValue="codi"
			suggestText="nom"
			placeholderKey="metaexpedient.tasca.form.camp.responsable"
			disabled="${bloquejarCamps}"/>	
		<rip:inputText name="duracio" textKey="tasca.list.column.duracio" comment="tasca.list.column.duracio.tip" tooltip="true" tooltipMsg="tasca.list.column.duracio.tip" readonly="${bloquejarCamps}"/>
		<rip:inputTextarea name="descripcio" textKey="metaexpedient.tasca.form.camp.descripcio" required="true" disabled="${bloquejarCamps}"/>
		<rip:inputSelect id="prioritat" name="prioritat" optionEnum="PrioritatEnumDto" emptyOption="false" textKey="contingut.expedient.form.camp.prioritat" templateResultFunction="showColorPriritats"/>
		<rip:inputSelect id="estatIdCrearTasca" name="estatIdCrearTasca" textKey="metaexpedient.tasca.form.camp.estat.crearTasca" emptyOption="true" optionItems="${expedientEstats}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${bloquejarCamps}" templateResultFunction="showColor" />
		<rip:inputSelect id="estatIdFinalitzarTasca" name="estatIdFinalitzarTasca" textKey="metaexpedient.tasca.form.camp.estat.finalitzarTasca" emptyOption="true" optionItems="${expedientEstats}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${bloquejarCamps}" templateResultFunction="showColor" />
		<div id="modal-botons" class="well">
			<c:if test="${!consultar}">
				<button type="submit" class="btn btn-success" <c:if test="${bloquejarCamps}">disabled</c:if>><span class="fa fa-save"></span>
					<c:choose>
						<c:when test="${empty metaExpedientTascaCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
						<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
					</c:choose>
				</button>	
			</c:if>	
			<a href="<c:url value="/metaExpedient/${metaExpedient.id}/tasca"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
