<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty bustiaCommand.id}">
		<c:set var="titol">
			<spring:message code="unitat.synchronize.dialog.header" />
		</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol">
			<spring:message code="bustia.form.titol.modificar" />
		</c:set>
	</c:otherwise>
</c:choose>

<c:set var="isAllEmpty" value="${empty substMap and empty splitMap and empty mergeMap and empty unitatsVigents and empty unitatsNew}" />

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet" />
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet" />
	<link href="<c:url value="/css/horizontal-tree.css"/>" rel="stylesheet" />
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<rip:modalHead />
</head>
<body>

	<div class="panel-group">
	
		<!-- If this is first sincronization it shows all currently vigent unitats that will be created in db  -->
		<c:if test="${isFirstSincronization}">
			<div class="panel panel-default">
				<div class="panel-heading">
					<spring:message
						code="unitat.synchronize.prediction.firstSincroHeader" />
				</div>
				<div class="panel-body">

					<c:if test="${empty unitatsVigents}">
						<spring:message code="unitat.synchronize.prediction.firstSincroNoUnitatsVigent" />
					</c:if>

					<c:if test="${!empty unitatsVigents}">
						<c:forEach var="unitatVigentFirstSincro" items="${unitatsVigents}">

							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-success border-green overflow-ellipsis create-label"></span>
									<div class="branch lv1">
										<div class="entry sole">
											<span class="label bg-success border-green overflow-ellipsis" title="${unitatVigentFirstSincro.codi} - ${unitatVigentFirstSincro.denominacio}">
													${unitatVigentFirstSincro.codi} - ${unitatVigentFirstSincro.denominacio}
											</span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</c:if>
		<c:if test="${!isFirstSincronization}">

			<!-- If unitats didn't change from the last time of synchronization show message: no changes -->
			<c:if test="${isAllEmpty}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.noChanges" />
					</div>
					<div class="panel-body">
						<spring:message code="unitat.synchronize.prediction.upToDate" />
					</div>
				</div>
			</c:if>

			<!-- If they exist show unitats that splited  (e.g. unitat A splits to unitats B and C) -->
			<c:if test="${!empty splitMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.splits" />
					</div>
					<div class="panel-body">
						<c:forEach var="splitMap" items="${splitMap}">
							<c:set var="key" value="${splitMap.key}" />
							<c:set var="values" value="${splitMap.value}" />
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-danger border-red overflow-ellipsis"
										title="${key.codi} - ${key.denominacio}"> ${key.codi} -
										${key.denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<div class="entry">
												<span
													class="label bg-success border-green overflow-ellipsis"
													title="${value.codi} - ${value.denominacio}">${value.codi}
													- ${value.denominacio}</span>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>


			<!-- If they exist show unitats that merged (e.g. unitats D and E merge to unitat F) -->
			<c:if test="${!empty mergeMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.merges" />
					</div>
					<div class="panel-body">
						<c:forEach var="mergeMap" items="${mergeMap}">
							<c:set var="key" value="${mergeMap.key}" />
							<c:set var="values" value="${mergeMap.value}" />
							<div class=horizontal-right>
								<div id="wrapper">
									<span
										class="label bg-success border-green right-postion-20 overflow-ellipsis"
										title="${key.codi} - ${key.denominacio}"> ${key.codi} -
										${key.denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<div class="entry">
												<span class="label bg-danger border-red overflow-ellipsis"
													title="${value.codi} - ${value.denominacio}">
													${value.codi} - ${value.denominacio} </span>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
			
			
			<!-- If they exist show unitats that were substituted by the others  (e.g. unitat G is substituted by unitat H) -->
			<c:if test="${!empty substMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.substitucions" />
					</div>
					<div class="panel-body">
						<c:forEach var="substMap" items="${substMap}">
							<c:set var="key" value="${substMap.key}" />
							<c:set var="values" value="${substMap.value}" />
							<div class=horizontal-right>
								<div id="wrapper">
									<span
										class="label bg-success border-green right-postion-20 overflow-ellipsis"
										title="${key.codi} - ${key.denominacio}"> ${key.codi} -
										${key.denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<div class="entry sole">
												<span class="label bg-danger border-red overflow-ellipsis"
													title="${value.codi} - ${value.denominacio}">
													${value.codi} - ${value.denominacio} </span>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>			

			<!-- If they exist show unitats that only had some of their properties changed -->
			<c:if test="${!empty unitatsVigents}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.atributesChanged" />
					</div>
					<div class="panel-body">
						<c:forEach var="unitatVigent" items="${unitatsVigents}">

							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-success border-green overflow-ellipsis" title="${unitatVigent.codi} - ${unitatVigent.denominacio}">
										${unitatVigent.codi} - ${unitatVigent.denominacio}
									</span>
									<div class="branch lv1">
										<div class="entry sole">
											<span class="label bg-warning border-yellow overflow-ellipsis" title="${unitatVigent.codi} - ${unitatVigent.denominacio}">
												${unitatVigent.codi} - ${unitatVigent.denominacio}
											</span>
										</div>
									</div>
								</div>
							</div>

						</c:forEach>
					</div>
				</div>
			</c:if>
			
			<!-- If they exist show unitats that are new (are not transitioned from any other unitat) -->
			<c:if test="${!empty unitatsNew}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.noves" />
					</div>
					<div class="panel-body">
						<c:forEach var="unitatNew" items="${unitatsNew}">
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-success border-green overflow-ellipsis create-label"></span>
									<div class="branch lv1">
										<div class="entry sole">
											<span class="label bg-success border-green overflow-ellipsis" title="${unitatNew.codi} - ${unitatNew.denominacio}">
												${unitatNew.codi} - ${unitatNew.denominacio}
											</span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>

			<!-- If they exist show unitats that are extint (are not vigent and has not any transition to any other unitat) -->
			<c:if test="${!empty unitatsExtingides}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.extingides" />
					</div>
					<div class="panel-body">
						<c:forEach var="unitatExtingida" items="${unitatsExtingides}">
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-danger border-red right-postion-20 overflow-ellipsis" title="${unitatExtingida.codi} - ${unitatExtingida.denominacio}">
										${unitatExtingida.codi} - ${unitatExtingida.denominacio}
									</span>
									<div class="branch lv1">
										<div class="entry sole remove">
											<span class="label bg-success border-green overflow-ellipsis remove-label"></span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</c:if>

	</div>

	<c:set var="formAction">
		<dis:modalUrl value="/unitatOrganitzativa/saveSynchronize" />
	</c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" role="form">
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"
				<c:if test="${isAllEmpty and !isFirstSincronization}"><c:out value="disabled='disabled'"/></c:if>>
				<span class="fa fa-save"></span>
				<spring:message code="unitat.list.boto.synchronize" />
			</button>
			<a href="<c:url value="/organgestor"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>

</body>
</html>
