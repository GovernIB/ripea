<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script type="text/javascript">
var publicacioEstatText = new Array();
<c:forEach var="option" items="${publicacioEstatEnumOptions}">
publicacioEstatText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>
</script>

<table
	id="taulaPublicacions"
	data-toggle="datatable"
	data-url="<c:url value="/expedient/${expedientId}/publicacio/datatable"/>"
	data-paging-enabled="false"
	class="table table-bordered table-striped"
	style="width:100%"
	data-row-info="true">
	<thead>
		<tr>
			<th data-col-name="error" data-visible="false"></th>
			<th data-col-name="notificacio" data-visible="false"></th>
			<th data-col-name="publicacio" data-visible="false"></th>
			<th data-col-name="tipus" data-orderable="false" data-template="#cellPublicacioTipusTemplate" width="15%">
				<spring:message code="contingut.enviament.columna.tipus"/>
				<script id="cellPublicacioTipusTemplate" type="text/x-jsrender">
					<spring:message code="contingut.enviament.publicacio"/>
				</script>
			</th>
			<th data-col-name="createdDate" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.enviament.columna.data"/></th>
			<th data-col-name="processatData" data-converter="datetime" data-orderable="false" width="20%"><spring:message code="contingut.enviament.columna.dataFinalitzada"/></th>
			<th data-col-name="assumpte" data-orderable="false" width="25%"><spring:message code="contingut.enviament.columna.assumpte"/></th>
			<th data-col-name="destinatari" data-orderable="false" data-visible="false" width="20%"><spring:message code="contingut.enviament.columna.destinatari"/></th>
			<th data-col-name="documentId" data-visible="false"/>
			<th data-col-name="documentNom" data-orderable="false" width="25%"><spring:message code="contingut.enviament.columna.document"/></th>
			<th data-col-name="estat" data-template="#cellPublicacioEstatTemplate" data-orderable="false" width="10%">
				<spring:message code="contingut.enviament.columna.estat"/>
				<script id="cellPublicacioEstatTemplate" type="text/x-jsrender">
					{{if estat == 'PENDENT'}}
						<span class="label label-warning"><span class="fa fa-clock-o"></span> {{:~eval('publicacioEstatText["' + estat + '"]')}}</span>
					{{else estat == 'ENVIAT'}}
						<span class="label label-info"><span class="fa fa-envelope-o"></span> {{:~eval('publicacioEstatText["' + estat + '"]')}}</span>
					{{else estat == 'REBUTJAT'}}
						<span class="label label-default"><span class="fa fa-times"></span> {{:~eval('publicacioEstatText["' + estat + '"]')}}</span>
					{{else estat == 'PROCESSAT'}}
						<span class="label label-danger"><span class="fa fa-check"></span> {{:~eval('publicacioEstatText["' + estat + '"]')}}</span>
					{{/if}}
				</script>
			</th>
			<th data-col-name="id" data-orderable="false" data-template="#cellPublicacioAccionsTemplate" width="10%">
				<script id="cellPublicacioAccionsTemplate" type="text/x-jsrender">
				<div class="dropdown">
					<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu">
						<li><a href="<c:url value="/document/{{:documentId}}/publicacio/{{:id}}/info"/>" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
						<li><a href="<c:url value="/document/{{:documentId}}/publicacio/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
						<li><a href="<c:url value="/document/{{:documentId}}/publicacio/{{:id}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="contingut.confirmacio.esborrar.publicacio"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
					</ul>
				</div>
			</script>
			</th>
		</tr>
	</thead>
</table>