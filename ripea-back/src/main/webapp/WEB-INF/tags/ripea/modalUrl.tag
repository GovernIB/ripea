<%@ attribute name="value" required="true"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%
	request.setAttribute(
			"isModal",
			es.caib.ripea.back.helper.ModalHelper.isModal(request));
%><c:choose><c:when test="${isModal}"><c:url value="/modal${value}"/></c:when><c:otherwise><c:url value="${value}"/></c:otherwise></c:choose>