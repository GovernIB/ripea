<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="urlConsultaFiltre" required="true" rtexprvalue="true"%>
<%@ attribute name="urlConsultaInicial" required="true" rtexprvalue="true"%>
<%@ attribute name="codiProperty" required="true" rtexprvalue="true"%>
<%@ attribute name="nomProperty" required="true" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="showDesc" required="false" rtexprvalue="true"%>

<style>
<!--
.lov-principal {
	float: left;
    width: 40%;
}
.lov-complementari {
	float: left;
    padding-left: 10px;
    width: 60%;
}
-->
</style>

<c:set var="campPath" value="${name}"/>
<c:set var="campPathDesc" value="${name}_description"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>

<c:choose>
	<c:when test="${not empty showDesc and showDesc}">
		<c:set var="classWithDesc"  value="lov-principal"/>
	</c:when>
	<c:otherwise>
		<c:set var="classWithDesc" value=""/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty placeholderKey}"><c:set var="placeholderText"><spring:message code="${placeholderKey}"/></c:set></c:when>
	<c:otherwise><c:set var="placeholderText" value="${placeholder}"/></c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not inline}">
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
				<div class="input-group">
			    <form:input path="${campPath}" cssClass="form-control" id="${campPath}" placeholder="${placeholderText}" disabled="${disabled}"/>
			    	<span class="input-group-addon empty-addon"></span>
				    <input class="form-control" id="${campPathDesc}" disabled="disabled"/>
				    <span class="input-group-btn">
				    	<a href="${urlConsultaFiltre}" class="btn btn-default" data-toggle="modal" data-modal-cancel="true"><i class="fa fa-search" aria-hidden="true"></i></a>
				    </span>
				</div>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<div class="input-group ${classWithDesc}">
			    <form:input path="${campPath}" cssClass="form-control" id="${campPath}" placeholder="${placeholderText}" disabled="${disabled}"/>
				<div class="input-group-btn">
			    	<a href="${urlConsultaFiltre}" class="btn btn-default" data-toggle="modal" data-modal-cancel="true" data-maximized="true" data-element-retorn="#${campPath}"><i class="fa fa-search" aria-hidden="true"></i></a>
			    </div>
			</div>
			
			<c:if test="${not empty showDesc and showDesc}">
				<div class="lov-complementari">
					<input class="form-control" id="${campPathDesc}" disabled="disabled"/>
				</div>
			</c:if>
			
			<p class="help-block" style="display:none"><span class="fa fa-exclamation-triangle"></span></p>
		</div>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function() {
	window['loading_${campPath}'] = false;
	
	$("#${campPath}").on("blur", function(e) {
		var valorCodi = this.value;
		if (!window['loading_${campPath}']) {
			$("#${campPathDesc}").val('');
			if (valorCodi != undefined && valorCodi != "") {
				comprovarValorPerCodi(valorCodi, e);
			}
		} else {
			e.preventDefault();
			event.stopImmediatePropagation();
		}
	});
	
	$("#${campPath}").trigger('blur');
});

function comprovarValorPerCodi(valorCodi, e) {
	$("#${campPathDesc}").val('');
	window['loading_${campPath}'] = true;
	
	$.ajax({
		type: 'POST',
		url: "${urlConsultaInicial}",
		dataType: "json",
		data: {codi: valorCodi},
		async: false,
		success: function(data) {
			if (data != undefined && data['${codiProperty}'] != undefined && data['${codiProperty}'] != '') {
				$("#${campPath}").val(data['${codiProperty}']);
				$("#${campPathDesc}").val(data['${nomProperty}']);
			} else {
				$("#${campPath}").focus();
			}
				
		},
		error: function() {
			$("#${campPath}").focus();
		},
		complete: function() {
			$("#${campPathDesc}").attr("placeholder","");
			window['loading_${campPath}'] = false;
		}
	});
}
</script>
