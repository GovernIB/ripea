<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style></style>

<script>
//################################################## document ready START ##############################################################
$(document).ready(function() { });
//################################################## document ready END ##############################################################
</script>

<table
	id="taulaTasques"
	data-toggle="datatable"
	data-url="<c:url value="/expedientTasca/${expedientId}/datatable"/>"
	data-paging-enabled="false"
	class="table table-bordered table-striped"
	style="width:100%"
	data-botons-template="#taulaTasquesNouBoton">
	<thead>
		<tr>
			<th data-col-name="id" data-visible="false"></th>
			<th data-col-name="shouldNotifyAboutDeadline" data-visible="false"></th>
			<th data-col-name="dataLimitExpirada" data-visible="false"></th>
			<th data-col-name="duracioFormat" data-visible="false"></th>
			<th data-col-name="metaExpedientTasca.nom" width="15%"><spring:message code="expedient.tasca.list.columna.metaExpedientTasca"/></th>
			<th data-col-name="dataInici" data-converter="datetime" width="15%"><spring:message code="expedient.tasca.list.columna.dataInici"/></th>
			<th data-col-name="dataLimitString" width="15%" data-template="#cellTascaDeadlineTemplate" >
				<spring:message code="expedient.tasca.list.columna.dataLimit"/>
				<script id="cellTascaDeadlineTemplate" type="text/x-jsrender">
					{{if dataLimitExpirada}}
							<span style="color: red;" title="Duració estimada {{:duracioFormat}}">                            
                                {{:dataLimitString}}
                                <span class="fa fa-clock-o"></span>
                            </span>
					{{else}}
						{{if shouldNotifyAboutDeadline}}
							<span style="color: orange;" title="Duració estimada {{:duracioFormat}}">                            
                                {{:dataLimitString}}
                                <span class="fa fa-clock-o"></span>
                            </span>
                    	{{else}}
							<span title="Duració estimada {{:duracioFormat}}">  
                        		{{:dataLimitString}}
							</span>
                    	{{/if}}
                    {{/if}}
				</script>
			</th>
			<th data-col-name="titol" width="15%"><spring:message code="expedient.tasca.list.columna.titol"/></th>
			<th data-col-name="observacions" width="15%"><spring:message code="expedient.tasca.list.columna.observacions"/></th>
			<th data-col-name="responsablesStr" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.responsables"/></th>
			<th data-col-name="responsableActual.codi" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.responsable.actual"/></th>
			<th data-col-name="estat" data-template="#cellTascaEstatTemplate" width="10%">
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
			<th data-col-name="prioritat" data-template="#cellPrioritatTemplate" width="11%">
				<spring:message code="tasca.list.column.prioritat"/>
				<script id="cellPrioritatTemplate" type="text/x-jsrender">
					<span class="label label-{{:prioritat}}">
                    {{if prioritat == 'MOLT_BAIXA'}}
					<spring:message code="prioritat.enum.MOLT_BAIXA"/>
					{{else prioritat == 'A_BAIXA'}}
					<spring:message code="prioritat.enum.A_BAIXA"/>
					{{else prioritat == 'C_ALTA'}}
					<spring:message code="prioritat.enum.C_ALTA"/>
					{{else prioritat == 'D_MOLT_ALTA'}}
					<spring:message code="prioritat.enum.D_MOLT_ALTA"/>
					{{else prioritat == 'CRITICA'}}
					<spring:message code="prioritat.enum.CRITICA"/>
					{{else}}
					<spring:message code="prioritat.enum.B_NORMAL"/>
					{{/if}}
                    </span>
				</script>
			</th>
			<th data-col-name="numComentaris" data-orderable="false" data-template="#cellComentarisTemplate" width="1%">
				<script id="cellComentarisTemplate" type="text/x-jsrender">
					<a href='<c:url value="/expedientTasca/{{:id}}/comentaris"/>' data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
				</script>
			</th>
			<th data-col-name="delegada" data-visible="false"></th>
			<th data-col-name="usuariActualDelegat" data-visible="false"></th>
			<th data-col-name="usuariActualResponsable" data-visible="false"></th>
			<th data-col-name="id" data-orderable="false" data-template="#cellExpedientTascaTemplate" width="1%">
				<script id="cellExpedientTascaTemplate" type="text/x-jsrender">
				<div class="dropdown">
					<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu">
						<li><a href="<c:url value="/expedientTasca/{{:id}}/detall"/>" data-maximized="false" data-toggle="modal"><span class="fa fa-info"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
						<c:if test="${potModificar}">
							{{if estat != 'CANCELLADA' && estat != 'REBUTJADA'}}
								<li class="divider"></li>
							{{/if}}
							{{if estat != 'CANCELLADA' && estat != 'FINALITZADA' && estat != 'REBUTJADA'}}
								<li {{if !usuariActualResponsable && !usuariActualDelegat}}class="disabled"{{/if}}><a href="<c:url value="/contingut/${expedientId}?tascaId={{:id}}"/>"><span class="fa fa-folder-open-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.tramitar"/></a></li>
							{{/if}}
							{{if estat == 'PENDENT'}}
								<li {{if !usuariActualResponsable && !usuariActualDelegat}}class="disabled"{{/if}}><a href="<c:url value="/usuariTasca/{{:id}}/iniciar?redirectATasca=false"/>" data-toggle="ajax"><span class="fa fa-play"></span>&nbsp;&nbsp;<spring:message code="comu.boto.iniciar"/></a></li>
								<li {{if !usuariActualResponsable && !usuariActualDelegat}}class="disabled"{{/if}}><a href="<c:url value="/usuariTasca/{{:id}}/rebutjar"/>" data-maximized="true" data-toggle="modal"><span class="fa fa-reply"></span>&nbsp;&nbsp;<spring:message code="comu.boto.rebutjar"/></a></li>
							{{/if}}
							{{if estat != 'CANCELLADA' && estat != 'FINALITZADA' && estat != 'REBUTJADA'}}
								<li {{if !usuariActualResponsable && !usuariActualDelegat}}class="disabled"{{/if}}><a href="<c:url value="/expedientTasca/{{:id}}/cancellar"/>" data-toggle="ajax" data-confirm="<spring:message code="expedient.tasca.confirmacio.cancellar"/>"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.cancellar"/></a></li>
								<li {{if !usuariActualResponsable && !usuariActualDelegat}}class="disabled"{{/if}}><a href="<c:url value="/usuariTasca/{{:id}}/finalitzar?redirectATasca=false"/>" data-toggle="ajax" data-confirm="<spring:message code="expedient.tasca.finalitzar"/>"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.finalitzar"/></a></li>
								<li class="divider"></li>
								<li><a href="<c:url value="/expedientTasca/{{:id}}/reassignar"/>" data-toggle="modal"><span class="fa fa-user"></span>&nbsp;&nbsp;<spring:message code="comu.boto.reassignar"/></a></li>
								{{if !delegada}}
									<li><a href="<c:url value="/usuariTasca/{{:id}}/delegar"/>" data-toggle="modal"><span class="fa fa-share"></span>&nbsp;&nbsp;<spring:message code="comu.boto.delegar"/></a></li>
								{{else delegada && !usuariActualDelegat}}
									<li><a href="<c:url value="/usuariTasca/{{:id}}/retomar"/>" data-toggle="modal"><span class="fa fa-remove"></span>&nbsp;&nbsp;<spring:message code="comu.boto.delegacio.desfer"/></a></li>
								{{/if}}
								<li class="divider"></li>
								<li><a href="<c:url value="/expedientTasca/{{:id}}/datalimit"/>" data-toggle="modal"><span class="fa fa-clock-o"></span>&nbsp;&nbsp;<spring:message code="expedient.tasca.list.boto.dataLimit"/></a></li>
								<li><a 	href="<c:url value="/expedientTasca/{{:id}}/canviarPrioritat"/>" data-toggle="modal"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.canviarPrioritat"/>...</a></li>
							{{/if}}
							{{if estat == 'FINALITZADA'}}
								<li><a href="<c:url value="/expedientTasca/{{:id}}/reobrir"/>" data-toggle="modal"><span class="fa fa-undo"></span>&nbsp;&nbsp;<spring:message code="comu.boto.reobrir"/></a></li>
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
	<c:if test="${potModificar}">
		<p style="text-align:right"><a href="<c:url value="/expedientTasca/${expedientId}/new"/>" class="btn btn-default" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="contingut.boto.nova.tasca"/></a></p>
	</c:if>	
</script>