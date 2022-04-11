<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<rip:modalHead/>

<body>
	<input id="isSignedAttached" name="isSignedAttached" value="${isSignedAttached}">
	<input id="isError" name="isError" value="${isError}">
	<input id="errorMsg" name="errorMsg" value="${errorMsg}">

</body>
</html>
