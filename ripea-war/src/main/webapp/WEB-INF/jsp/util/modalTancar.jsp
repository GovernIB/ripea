<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<script src="<c:url value="/js/jquery-1.10.2.min.js"/>"></script>
<script>
	//window.parent.modalTancarIRefrescar(window.frameElement);
	$('button.close', $(window.frameElement).parent().parent().parent().parent()).trigger('click');
	
</script>
</head>
<body></body>
</html>
