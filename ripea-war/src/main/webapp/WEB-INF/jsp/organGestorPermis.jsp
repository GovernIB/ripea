<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<c:set var="pageTitle"><spring:message code="organgestor.permis.titol"/></c:set>
	<c:if test="${not empty organ}">
		<c:set var="pageTitle"><spring:message code="organgestor.permis.titol"/>: ${ organ.nom }</c:set>
		<meta name="subtitle" content="${organ.nom}"/>
 	</c:if>
	<title>${ pageTitle }</title>
	<meta name="subtitle" content="${organ.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		
	</div>
	<c:url var="datatableUrl" value="/organgestor/permis/datatable"/>
	<c:if test="${not empty organ}">
		<c:url var="datatableUrl" value="/organgestor/${ organ.id }/permis/datatable"/>
 	</c:if>
	<table id="permisos" data-toggle="datatable" 
			data-url="${ datatableUrl }" 
			data-default-order="1" 
			data-default-dir="asc" 
			data-botons-template="#botonsTemplate" 
			class="table table-striped table-bordered" 
			style="width:100%">
		<thead>
			<tr>
<!-- 				<th data-col-name="organGestor.nom"> -->
<%-- 					<spring:message code="organgestor.permis.columna.organ"/> --%>
<!-- 				</th> -->
				<th data-col-name="principalTipus">
					<spring:message code="organgestor.permis.columna.tipus"/>
				</th>
				<th data-col-name="principalCodiNom">
					<spring:message code="organgestor.permis.columna.principal"/>
				</th>
				<th data-col-name="create" data-template="#cellPermisCreateTemplate">
					<spring:message code="organgestor.permis.columna.creacio"/>
					<script id="cellPermisCreateTemplate" type="text/x-jsrender">
						{{if create}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="read" data-template="#cellPermisReadTemplate">
					<spring:message code="organgestor.permis.columna.consulta"/>
					<script id="cellPermisReadTemplate" type="text/x-jsrender">
						{{if read}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="write" data-template="#cellPermisWriteTemplate">
					<spring:message code="organgestor.permis.columna.modificacio"/>
					<script id="cellPermisWriteTemplate" type="text/x-jsrender">
						{{if write}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="delete" data-template="#cellPermisDeleteTemplate">
					<spring:message code="organgestor.permis.columna.eliminacio"/>
					<script id="cellPermisDeleteTemplate" type="text/x-jsrender">
						{{if delete}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="procedimentsComuns" data-template="#cellProcedimentsComunsTemplate" width="5%">
					<spring:message code="organgestor.permis.columna.procedimentsComuns"/>
					<script id="cellProcedimentsComunsTemplate" type="text/x-jsrender">
 						{{if procedimentsComuns}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="administration" data-template="#cellPermisAdministrationTemplate" width="5%">
					<spring:message code="organgestor.permis.columna.administracio"/>
					<script id="cellPermisAdministrationTemplate" type="text/x-jsrender">
 						{{if administration}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>				
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
 						<div class="dropdown"> 
 							<button class="btn btn-primary" data-toggle="dropdown">
								<span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span>
 							</button> 
							<ul class="dropdown-menu">
								<li><a href="permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="permis/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="entitat.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
 							</ul>
 						</div>
					</script> 
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right">
			<a class="btn btn-default" href="permis/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="organgestor.permis.form.titol.crear"/></a>
		</p>
	</script>
		<a href="<c:url value="/organgestor"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
</body>
</html>
