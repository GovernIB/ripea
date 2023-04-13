<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="exemple" required="false" rtexprvalue="true"%>
<%@ attribute name="exempleLabel" required="false" rtexprvalue="true"%>
<%@ attribute name="maxlength" required="false" rtexprvalue="true"%>
<%@ attribute name="showsize" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<script>
$(document).ready(function() {
	//Comptador
	if ('${showsize}' && '${maxlength}') {
		var field = '${name}';
		var fieldId = $('#' + field);
		var fieldSize = 'inputCurrentLength_${name}';
		var fieldSizeClass = $(document.getElementsByClassName(fieldSize)[0]);
		if (fieldId.val() != undefined && fieldId.val().length != 0) {
			var size = fieldId.val().length;
			$(fieldSizeClass).text(size);
		} else {
			$(fieldSizeClass).text(0);
		};
		
		$(document.getElementById(field)).bind("change paste keyup", function() {
			var size = $(this).val().length;
			$(fieldSizeClass).text(size);
		});
	}
});
</script>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
	<label class="control-label col-xs-${campLabelSize}" for="${campPath}">
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
		<c:if test="${required}">*</c:if>
	</label>
	<div class="controls col-xs-${campInputSize}">
		<form:textarea path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}" rows="6" maxlength="${maxlength}"/>
		<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
		<c:if test="${showsize && not empty maxlength}">
			<p class="info-length text-success" style="font-size: 12px; margin: 2px 0 0 0 !important;">
				<span class="glyphicon glyphicon-info-sign"></span>
				<span class="inputCurrentLength_${name}">0</span>
					<spring:message code="comu.camp.logitud.count"/>
				<span> ${maxlength}</span>
			</p>
		</c:if>
		<c:if test="${not empty exemple}">
			<a class="btn btn-default btn-xs exemple_boto"  onclick="webutilMostrarExemple(this)"><spring:message code="${exempleLabel}"/></a>
			<div class="exemple">
				<pre><spring:message code="${exemple}"/></pre>
			</div>
		</c:if>
	</div>
</div>
