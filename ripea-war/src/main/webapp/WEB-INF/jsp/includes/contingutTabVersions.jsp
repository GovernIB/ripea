<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style>



</style>


<script>

//################################################## document ready START ##############################################################
$(document).ready(function() {


	
});//################################################## document ready END ##############################################################


</script>




<div id="document-versions" class="panel-group" id="accordion">
	<table class="table table-bordered table-striped">
	<thead>
		<tr>
			<th>Id</th>
			<th>Data</th>
			<c:if test="${contingut.documentTipus != 'FISIC'}">
				<th width="1%"></th>
			</c:if>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="versio" items="${contingut.versions}">
		<tr>
			<td>${versio.id}</td>
			<td><fmt:formatDate value="${versio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
			<c:if test="${contingut.documentTipus != 'FISIC'}">
				<td>
					<a href="<c:url value="/contingut/${contingut.id}/document/${contingut.id}/versio/${versio.id}/descarregar"/>" class="btn btn-default">
						<span class="fa fa-download"></span>&nbsp;
						<spring:message code="comu.boto.descarregar"/>
					</a>
				</td>
			</c:if>
		</tr>
	</c:forEach>
	</tbody>
	</table>
</div>