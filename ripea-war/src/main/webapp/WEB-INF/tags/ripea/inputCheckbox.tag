<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="comment" required="false" rtexprvalue="true"%>
<%@ attribute name="faClassInfoIcon" required="false" rtexprvalue="true"%>
<%@ attribute name="inputClass" required="false" rtexprvalue="true"%>
<%@ attribute name="labelClass" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="infoIcon" value="${(not empty comment) and (not empty faClassInfoIcon)}"/>
<c:set var="showComment" value="${(not empty comment) and (empty faClassInfoIcon)}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="disabled"><c:choose><c:when test="${not empty disabled}">${disabled}</c:when><c:otherwise>false</c:otherwise></c:choose></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<c:set var="commentMessage"><c:if test="${not empty comment}"><spring:message code="${comment}"/></c:if></c:set>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
<c:choose>
	<c:when test="${not inline}">
		<label class="control-label col-xs-${campLabelSize} ${labelClass}"
			<c:if test="${infoIcon}"> title="${commentMessage}" </c:if>
			for="${campPath}">
				<c:choose>
					<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
					<c:when test="${not empty text}">${text}</c:when>
					<c:otherwise>${campPath}</c:otherwise>
				</c:choose>
				<c:if test="${not empty infoIcon}"><span class="fa ${faClassInfoIcon}" style="color: cornflowerblue;"></span></c:if>
		</label>
		<div class="controls col-xs-${campInputSize}">
			<div class="checkbox ${inputClass}">
	  			<label>
					<form:checkbox path="${campPath}" cssClass="span12" id="${campPath}" disabled="${disabled}"/>
				</label>
			</div>
			<c:if test="${showComment}"><p class="comentari">${commentMessage}</p></c:if>
			<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
		</div>
	</c:when>
	<c:otherwise>
		<label class="checkbox-inline">
			<form:checkbox path="${campPath}" id="${campPath}" disabled="${disabled}"/>
			<c:choose>
				<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
				<c:when test="${not empty text}">${text}</c:when>
				<c:otherwise>${campPath}</c:otherwise>
			</c:choose>
			<c:if test="${not empty comment}"><p class="comentari">${commentMessage}</p></c:if>
		</label>
	</c:otherwise>
</c:choose>
</div>
		