<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>





<div class="btn-group">
	<a href="<c:url value="/metaExpedient/${metaExpedient.id}/metaDocument"/>" class="btn btn-default ${element == 'document' ? 'active' : ''}"><span class="badge">${metaExpedient.metaDocumentsCount}</span>&nbsp;<spring:message code="metaexpedient.list.boto.meta.documents"/></a>
	<a href="<c:url value="/metaExpedient/${metaExpedient.id}/metaDada"/>" class="btn btn-default ${element == 'dada' ? 'active' : ''}"><span class="badge">${metaExpedient.metaDadesCount}</span>&nbsp;<spring:message code="metaexpedient.list.boto.meta.dades"/></a>
	<a href="<c:url value="/expedientEstat/${metaExpedient.id}"/>" class="btn btn-default ${element == 'estat' ? 'active' : ''}"><span class="badge">${metaExpedient.expedientEstatsCount}</span>&nbsp;<spring:message code="metaexpedient.list.boto.estats"/></a>
	<a href="<c:url value="/metaExpedient/${metaExpedient.id}/tasca"/>" class="btn btn-default ${element == 'tasca' ? 'active' : ''}"><span class="badge">${metaExpedient.expedientTasquesCount}</span>&nbsp;<spring:message code="metaexpedient.list.boto.tasques"/></a>
	<a href="<c:url value="/metaExpedient/${metaExpedient.id}/grup"/>" class="btn btn-default ${element == 'grup' ? 'active' : ''}"><span class="badge">${metaExpedient.grupsCount}</span>&nbsp;<spring:message code="metaexpedient.list.boto.grups"/></a>
</div>