<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<script>
let rootIframe = window.frameElement;
while (rootIframe && rootIframe.ownerDocument.defaultView.frameElement) {
	rootIframe = rootIframe.ownerDocument.defaultView.frameElement;
}
if (rootIframe) {
	rootIframe.src = '../modal/tancar';
	//rootIframe.parentElement.parentElement.querySelector('button.close').click()
}
</script>
</head>
<body></body>
</html>
