<%@ attribute name="titol" required="false"%>
<%@ attribute name="buttonContainerId" required="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%
if (es.caib.ripea.war.helper.ModalHelper.isModal(request)) {%>
<meta name="decorator" content="senseCapNiPeus"/>
<script>
document.addEventListener("DOMContentLoaded", function(event) { 
	document.body.style.paddingTop = "1em";
});
</script>
<%}%>
