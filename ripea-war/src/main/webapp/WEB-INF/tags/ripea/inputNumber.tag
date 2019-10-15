<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="comment" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="multiple" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="separadorDecimal" required="false" rtexprvalue="true"%>
<%@ attribute name="separadorMilers" required="false" rtexprvalue="true"%>
<%@ attribute name="nombreDecimals" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}"> *</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<c:set var="campSeparadorDecimal"><c:choose><c:when test="${not empty separadorDecimal}">${separadorDecimal}</c:when><c:otherwise>,</c:otherwise></c:choose></c:set>
<c:set var="campSeparadorMilers"><c:choose><c:when test="${not empty separadorMilers}">${separadorMilers}</c:when><c:otherwise></c:otherwise></c:choose></c:set>
<c:set var="campNombreDecimal"><c:choose><c:when test="${not empty nombreDecimals}">${nombreDecimals}</c:when><c:otherwise>2</c:otherwise></c:choose></c:set>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>"<c:if test="${multiple}"> data-toggle="multifield"</c:if>>
<c:choose>
	<c:when test="${not inline}">
		<label class="control-label col-xs-${campLabelSize}" for="${campPath}">${campLabelText}</label>
		<div class="col-xs-${campInputSize}">
			<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}" data-toggle="autonumeric" data-a-dec="${campSeparadorDecimal}" data-a-sep="${campSeparadorMilers}" data-m-dec="${campNombreDecimal}"/>
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			<c:if test="${not empty comment}"><p class="comentari col-xs-${12 - labelSize} col-xs-offset-${labelSize}"><spring:message code="${comment}"/></p></c:if>
		</div>
	</c:when>
	<c:otherwise>
   		<label class="sr-only" for="${campPath}">${campLabelText}</label>
   		<form:input path="${campPath}" cssClass="form-control" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}" data-toggle="autonumeric" data-a-dec="${campSeparadorDecimal}" data-a-sep="${campSeparadorMilers}" data-m-dec="${campNombreDecimal}"/>
		<c:if test="${not empty comment}"><p class="comentari col-xs-${12 - labelSize} col-xs-offset-${labelSize}"><spring:message code="${comment}"/></p></c:if>
	</c:otherwise>
</c:choose>
</div>