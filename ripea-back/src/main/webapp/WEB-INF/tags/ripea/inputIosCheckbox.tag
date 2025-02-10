<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="titleKey" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="inputSize" required="false" rtexprvalue="true"%>
<%@ attribute name="icon" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="disabled"><c:choose><c:when test="${not empty disabled}">${disabled}</c:when><c:otherwise>false</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize"><c:choose><c:when test="${not empty inputSize}">${inputSize}</c:when><c:otherwise>2</c:otherwise></c:choose></c:set>
<c:set var="campOffsetSize">${12 - campLabelSize - campInputSize}</c:set>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>"<c:if test="${not empty titleKey}"> title="<spring:message code="${titleKey}"/>"</c:if>>
	<label class="control-label ios-label col-xs-${campLabelSize} col-xs-offset-${campOffsetSize} check-label" for="${campPath}">
		<c:if test="${not empty icon}"><span class="fa ${icon}"></span></c:if>
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
	</label>
	<div class="controls col-xs-${campInputSize}">
		<div class="checkbox checkbox-primary">
			<label class="form-switch">
				<form:checkbox path="${campPath}" cssClass="span12" id="${campPath}" disabled="${disabled}" autocomplete="off"/>
				<i></i>
			</label>
		</div>
	</div>
</div>