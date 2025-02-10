<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h${level + 4}>${ group.description }</h${level + 4}>
    </div>
    <div class="panel-body flex-column no-side-padding">
        <c:forEach items="${ group.configs }" var="config" varStatus="status_group">
            <c:set var = "configKey" value = "${fn:replace(config.key,'.','-')}"/>
            <c:set var = "textGray" value = "${not empty config.value ? '' : 'text-gray'}"/>
            <c:set var = "disabled" value = "${config.jbossProperty || empty config.value ? 'disabled' : ''}"/>
            <div class="padding-top-bottom">
                <label id="${configKey}_codi_entitat" for="${configKey}" class="col-sm-3 control-label label-config ${textGray}" style="word-wrap: break-word;">${ config.description }</label>
                <div class="col-sm-8 padding-top-7 div-form-control">
                    <c:choose>
                        <c:when test="${config.typeCode == 'INT'}">
                            <input id="${configKey}" class="form-control entitat-input" type="number" maxlength="2048" value="${config.value}"
                                   placeholder="${config.key}" ${disabled}>
                        </c:when>
                        <c:when test="${config.typeCode == 'FLOAT'}">
                            <input id="${configKey}" class="form-control entitat-input" type="number" step="0.01" maxlength="2048" value="${config.value}"
                                   placeholder="${config.key}" ${disabled}>
                        </c:when>
                        <c:when test="${config.typeCode == 'CREDENTIALS'}">
                            <input id="${configKey}" class="form-control entitat-input" type="password" maxlength="2048" value="${config.value}"
                                   placeholder="${config.key}" ${disabled}>
                        </c:when>
                        <c:when test="${config.typeCode == 'BOOL'}">
                            <input id="${configKey}" name="booleanValue" class="visualitzar entitat-input" type="checkbox"
                                ${disabled} <c:if test="${config.value == 'true'}">checked</c:if>>
                        </c:when>
                        <c:when test="${config.validValues != null and fn:length(config.validValues) > 2}">
                            <select id="${configKey}" class="form-control selector2" ${disabled} style="width:100%;">
                                <option value=""></option>
                                <c:forEach var="opt" items="${config.validValues}">
                                    <option value="${opt}">${opt}</option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:when test="${config.validValues != null and fn:length(config.validValues) == 2}">
                            <div id="${configKey}" class="visualitzar entitat-input radio-div">
                                <label id="${configKey}_radio_1" class="radio-inline ${textGray}">
                                    <input id="${configKey}_1" name="${configKey}" type="radio" value="${config.validValues[0]}"
                                        ${disabled} <c:if test="${config.validValues[0] == config.value}">checked</c:if>>
                                        ${config.validValues[0]}
                                </label>
                                <label id="${configKey}_radio_2" class="radio-inline ${textGray}">
                                    <input id="${configKey}_2" name="${configKey}" type="radio" value="${config.validValues[1]}"
                                        ${disabled} <c:if test="${config.validValues[1] == config.value}">checked</c:if>>
                                        ${config.validValues[1]}
                                </label>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <input id="${configKey}" class="form-control" type="text" maxlength="2048" value="${config.value}"
                                   placeholder="${config.key}" ${disabled}>
                        </c:otherwise>
                    </c:choose>
                    <div id="${configKey}_key"><span id="${configKey}_key_entitat" class="help-block display-inline ${textGray}">${config.key}</span></div>
                </div>
                <div class="col-sm-1 padding-top-7 padding-right-0" style="padding-right: 0;">
                    <c:if test="${not config.jbossProperty}">
                        <button id="${configKey}_button_save" name="${config.entitatCodi}" type="button"
                                class="btn btn-success entitat-save <c:if test="${empty config.value}">no-display</c:if>"><i class="fa fa-save"></i></button>
                        <button id="${configKey}_button_trash" name="${config.entitatCodi}" type="button"
                                class="btn btn-danger entitat-trash <c:if test="${empty config.value}">no-display</c:if>"><i class="fa fa-trash"></i></button>
                    </c:if>
                    <c:if test="${config.configurable}">
                        <button id="${configKey}_button_config" name="${config.entitatCodi}" type="button"
                                class="btn btn-success entitat-config <c:if test="${not empty config.value}">no-display</c:if>"><i class="fa fa-pencil"></i></button>
                    </c:if>
                </div>
            </div>
        </c:forEach>

        <c:set var="level" value="${level + 1}" scope="request"/>
        <c:forEach items="${ group.innerConfigs }" var="group" varStatus="status_group">
            <c:set var="group" value="${group}" scope="request"/>
            <jsp:include page="configEntitatGroup.jsp"/>
            <c:set var="level" value="${level - 1}" scope="request"/>
        </c:forEach>
    </div>
</div>
