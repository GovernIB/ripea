<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
  <title><spring:message code="config.titol"/> - ${entitatNom}</title>
  <script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
  <script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
  <link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
  <link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
  <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
  <script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
  <script src="<c:url value="/webjars/select2/4.0.5/dist/js/i18n/${requestLocale}.js"/>"></script>
  <script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
  <link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
  <link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
  <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
  <script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
  <script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
  <script src="<c:url value="/js/webutil.common.js"/>"></script>
  <script src="<c:url value="/js/webutil.datatable.js"/>"></script>
  <script src="<c:url value="/js/webutil.modal.js"/>"></script>
  <script src="<c:url value="/js/configProperties/configProperties.js"/>"></script>
</head>
<body>
<script>

  $(document).ready(function() {

    $(".selector2").select2({
      theme: "bootstrap",
      allowClear: true,
      minimumResultsForSearch: -1,
      placeholder: ""
    });

    $(".entitat-save").click(e =>  entitatSave(e));
    $(".entitat-trash").click(e => entitatTrash(e));
    $(".entitat-config").click(e => entitatConfig(e));

    $('.a-config-group:first').tab('show');
  });
</script>
<div class="text-right" data-toggle="botons-titol">
  <c:if test="${isRolActualAdministrador}">
    <a class="btn btn-default" href="<c:url value="/entitat"/>" data-datatable-id="permisos"><span class="fa fa-reply"></span>&nbsp;<spring:message code="entitat.permis.list.boto.tornar"/></a>
  </c:if>
</div>
<div id="syncModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Sincronitzant propietats</h4>
      </div>
      <div id="syncModal-body" class="modal-body">
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Tanca</button>
      </div>
    </div>
  </div>
</div>
<div class="row">
  <div class="col-md-3">
    <ul id="tab-list" class="nav nav-pills nav-stacked">
      <c:forEach items="${config_groups}" var="group" varStatus="status_group">
        <li role="presentation">
          <a class="a-config-group" data-toggle="tab" href="#group-${group.key}">${group.description}</a>
        </li>
      </c:forEach>
    </ul>
  </div>
  <div class="col-md-9">
    <div class="tab-content">
      <c:forEach items="${config_groups}" var="group" varStatus="status_group">
        <c:set var="group" value="${group}" scope="request"/>
        <c:set var="level" value="0" scope="request"/>
        <div id="group-${group.key}" class="tab-pane fade">
          <jsp:include page="configEntitatGroup.jsp"/>
        </div>
      </c:forEach>
    </div>
  </div>
</div>
</body>
</html>
