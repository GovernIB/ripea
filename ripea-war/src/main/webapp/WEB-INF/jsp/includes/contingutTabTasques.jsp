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
	id="taulaTasques"
	data-toggle="datatable"
	data-url="<c:url value="/expedientTasca/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/datatable"/>"
	data-paging-enabled="false"
	class="table table-bordered table-striped"
	style="width:100%"
	data-botons-template="#taulaTasquesNouBoton">
	<thead>
		<tr>
			<th data-col-name="id" data-visible="false"></th>
			<th data-col-name="metaExpedientTasca.nom" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.metaExpedientTasca"/></th>									
			<th data-col-name="dataInici" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="expedient.tasca.list.columna.dataInici"/></th>
			<th data-col-name="dataFi" data-converter="datetime"data-orderable="false"  width="20%"><spring:message code="expedient.tasca.list.columna.dataFi"/></th>
			<th data-col-name="responsablesStr" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.responsables"/></th>	
			<th data-col-name="responsableActual.codi" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.responsable.actual"/></th>								
			<th data-col-name="estat" data-template="#cellTascaEstatTemplate" data-orderable="false" width="10%">
				<spring:message code="expedient.tasca.list.columna.estat"/>
				<script id="cellTascaEstatTemplate" type="text/x-jsrender">
				{{if estat == 'PENDENT'}}
					<spring:message code="expedient.tasca.estat.enum.PENDENT"/>
				{{else estat == 'INICIADA'}}
					<spring:message code="expedient.tasca.estat.enum.INICIADA"/>
				{{else estat == 'FINALITZADA'}}
					<spring:message code="expedient.tasca.estat.enum.FINALITZADA"/>
				{{else estat == 'CANCELLADA'}}
					<spring:message code="expedient.tasca.estat.enum.CANCELLADA"/>
				{{else estat == 'REBUTJADA'}}
					<spring:message code="expedient.tasca.estat.enum.REBUTJADA"/>
				{{/if}}
			</script>
			</th>
			<th data-col-name="numComentaris" data-orderable="false" data-template="#cellComentarisTemplate" width="1%">
				<script id="cellComentarisTemplate" type="text/x-jsrender">
					<a href='<c:url value="/expedientTasca/{{:id}}/comentaris"/>' data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
				</script>
			</th>
			<th data-col-name="id" data-orderable="false" data-template="#cellExpedientTascaTemplate" width="1%">
				<script id="cellExpedientTascaTemplate" type="text/x-jsrender">
				<div class="dropdown">
					<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu">
						<li><a href="<c:url value="/expedientTasca/{{:id}}/detall"/>" data-maximized="true" data-toggle="modal"><span class="fa fa-info"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
						{{if estat != 'FINALITZADA'}}
							<li><a href="<c:url value="/expedientTasca/{{:id}}/reassignar"/>" data-toggle="modal"><span class="fa fa-user"></span>&nbsp;&nbsp;<spring:message code="comu.boto.reassignar"/></a></li>
						{{/if}}
						<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT')}">
							{{if estat != 'CANCELLADA' && estat != 'FINALITZADA'}}
								<li><a href="<c:url value="/expedientTasca/{{:id}}/cancellar"/>" data-confirm="<spring:message code="expedient.tasca.confirmacio.cancellar"/>"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.cancellar"/></a></li>
							{{/if}}
						</c:if>
					</ul>
				</div>
			</script>
			</th>										
		
		</tr>
	</thead>
</table>
<script id="taulaTasquesNouBoton" type="text/x-jsrender">
	<c:if test="${((expedientAgafatPerUsuariActual && potModificarContingut) || contingut.admin) && (contingut.expedient ? contingut.estat != 'TANCAT' : contingut.expedientPare.estat != 'TANCAT')}">
		<p style="text-align:right"><a href="<c:url value="/expedientTasca/${contingut.expedient ? contingut.id : contingut.expedientPare.id}/new"/>" class="btn btn-default" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="contingut.boto.nova.tasca"/></a></p>
	</c:if>	
</script>