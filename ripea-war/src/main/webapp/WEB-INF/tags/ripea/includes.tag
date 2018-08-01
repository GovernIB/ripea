<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="datatables" required="false" rtexprvalue="true"%>
<%@ attribute name="modal" required="false" rtexprvalue="true"%>
<%@ attribute name="select2" required="false" rtexprvalue="true"%>
<%@ attribute name="datepicker" required="false" rtexprvalue="true"%>
<%@ attribute name="jasny" required="false" rtexprvalue="true"%>
<%@ attribute name="jstree" required="false" rtexprvalue="true"%>
<%@ attribute name="sortable" required="false" rtexprvalue="true"%>
<%@ attribute name="numeric" required="false" rtexprvalue="true"%>
<%@ attribute name="autonumeric" required="false" rtexprvalue="true"%>
<%@ attribute name="jqueryui" required="false" rtexprvalue="true"%>
<%@ attribute name="clamp" required="false" rtexprvalue="true"%>
<c:if test="${datatables}">
<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
</c:if>
<c:if test="${modal}">
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</c:if>
<c:if test="${select2}">
<link href="<c:url value="/webjars/select2/4.0.4/dist/css/select2.min.css"/>" rel="stylesheet"/>
<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.10/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
<script src="<c:url value="/webjars/select2/4.0.4/dist/js/select2.min.js"/>"></script>
</c:if>
<c:if test="${datepicker}">
<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
</c:if>
<c:if test="${jasny}">
<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
</c:if>
<c:if test="${jstree}">
<link href="<c:url value="/webjars/jstree/3.2.1/dist/themes/default/style.min.css"/>" rel="stylesheet">
<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
</c:if>
<c:if test="${sortable}">
<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
</c:if>
<c:if test="${numeric}">
<script src="<c:url value="/js/jquery.numeric.js"/>"></script>
</c:if>
<c:if test="${autonumeric}">
<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
</c:if>
<c:if test="${jqueryui}">
<script src="<c:url value="/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
</c:if>
<c:if test="${clamp}">
<script src="<c:url value="/js/clamp.js"/>"></script>
</c:if>
<script src="<c:url value="/js/webutil.common.js"/>"></script>