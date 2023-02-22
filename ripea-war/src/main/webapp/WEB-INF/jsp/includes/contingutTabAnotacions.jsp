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


<table 
	id="taulaAnotacions" 
	data-toggle="datatable"
	data-url="<c:url value="/expedientPeticio/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/datatable"/>"
	data-paging-enabled="false" 
	data-default-order="3" 
	data-default-dir="desc"
	class="table table-bordered table-striped" 
	style="width: 100%">
	<thead>
		<tr>
			<th data-col-name="id" data-visible="false"></th>
			<th data-col-name="registre.extracte" data-orderable="false" width="25%"><spring:message code="contingut.anotacions.columna.extracte"/></th>
			<th data-col-name="registre.origenRegistreNumero" data-orderable="false" width="25%"><spring:message code="contingut.anotacions.columna.numero"/></th>
			<th data-col-name="registre.data" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.anotacions.columna.data"/></th>
			<th data-col-name="registre.destiDescripcio" data-orderable="false" width="25%"><spring:message code="contingut.anotacions.columna.destiDescripcio"/></th>

			<th data-col-name="id" data-orderable="false" data-template="#cellAnotacioAccionsTemplate" width="10%">
				<script id="cellAnotacioAccionsTemplate" type="text/x-jsrender">
			<a href="<c:url value="/expedientPeticio/{{:id}}"/>" data-maximized="true" class="btn btn-primary" data-toggle="modal"><span class="fa fa-info"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>
		</script>
			</th>
		</tr>
	</thead>
</table>