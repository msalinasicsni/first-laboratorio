<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<html>
<head>
    <jsp:include page="../fragments/headTag.jsp" />
</head>
<body>
<jsp:include page="../fragments/bodyHeader.jsp" />
<jsp:include page="../fragments/bodyNavigation.jsp" />
<!-- Main bar -->
<div class="mainbar">

    <!-- RIBBON -->
    <div id="ribbon">
			<span class="ribbon-button-alignment">
				<span id="refresh" class="btn btn-ribbon" data-action="resetWidgets" data-placement="bottom" data-original-title="<i class='text-warning fa fa-warning'></i> <spring:message code="msg.reset" />" data-html="true">
					<i class="fa fa-refresh"></i>
				</span>
			</span>
        <!-- breadcrumb -->
        <ol class="breadcrumb">
            <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/resultadoFinal/init" htmlEscape="true "/>"><spring:message code="menu.enter.final.result" /></a></li>
        </ol>
        <!-- end breadcrumb -->
        <jsp:include page="../fragments/layoutOptions.jsp" />
    </div>
    <!-- END RIBBON -->
    <!-- Matter -->

    <div class="matter">
        <div class="container">

            <!-- Table -->

            <div class="row">

                <div class="col-md-12">

                    <div class="widget">

                        <div class="widget-head">
                            <div class="pull-left">
                                Lista de usuarios registrados
                            </div>
                            <div class="widget-icons pull-right">
                                <a href="#" class="wminimize"><i class="icon-chevron-up"></i></a>
                                <a href="#" class="wclose"><i class="icon-remove"></i></a>
                            </div>
                            <div class="clearfix"></div>
                        </div>

                        <div class="widget-content">
                            <form action="<c:url value="/usuarios/list" />">
                                <div class="form-group">
                                    <div class="col-lg-offset-0 col-lg-9">
                                        <button type="submit" class="btn btn-primary">
                                            Agregar Usuario
                                        </button>
                                    </div>
                                </div>
                                <div class="clearfix"></div>
                            </form>
                            <br />
                            <table class="table table-striped table-bordered table-hover"
                                   id="tabla">
                                <thead>
                                <tr>
                                    <th>Nombre de Usuario</th>
                                    <th>Descrpción</th>
                                    <th>Email</th>
                                    <th>Habilitado</th>
                                    <th>Roles</th>
                                    <th>Acciones</th>
                                </tr>
                                </thead>
                                <c:forEach items="${usuarios}" var="usuario">
                                    <tr class="vigcom">
                                        <spring:url value="/usuarios/admin/{username}"
                                                    var="usuarioUrl">
                                            <spring:param name="username" value="${usuario.username}" />
                                        </spring:url>
                                        <spring:url value="/usuarios/admin/{username}/edit"
                                                    var="editUrl">
                                            <spring:param name="username" value="${usuario.username}" />
                                        </spring:url>
                                        <spring:url value="/usuarios/admin/{username}/disable"
                                                    var="disableUrl">
                                            <spring:param name="username" value="${usuario.username}" />
                                        </spring:url>
                                        <spring:url value="/usuarios/admin/{username}/chgpass"
                                                    var="chgpassUrl">
                                            <spring:param name="username" value="${usuario.username}" />
                                        </spring:url>
                                        <td><a href="${fn:escapeXml(usuarioUrl)}"><c:out
                                                value="${usuario.username}" /></a></td>
                                        <td><a href="${fn:escapeXml(usuarioUrl)}"><c:out
                                                value="${usuario.completeName}" /></a></td>
                                        <td><c:out value="${usuario.email}" /></td>
                                        <c:choose>
                                            <c:when test="${usuario.enabled}">
                                                <td><span class="label label-success"><c:out
                                                        value="${usuario.enabled}" /></span></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td><span class="label label-danger"><c:out
                                                        value="${usuario.enabled}" /></span></td>
                                            </c:otherwise>
                                        </c:choose>
                                        <td><c:forEach var="rol" items="${usuario.authorities}">
                                            <c:out value="${rol.authId.authority}" />
                                        </c:forEach></td>
                                        <td>
                                            <div class="btn-group1">
                                                <button
                                                        onclick="location.href='${fn:escapeXml(usuarioUrl)}'"
                                                        class="btn btn-xs btn-success">
                                                    <i class="icon-search">Ver</i>
                                                </button>
                                                <button onclick="location.href='${fn:escapeXml(editUrl)}'"
                                                        class="btn btn-xs btn-warning">
                                                    <i class="icon-pencil">Editar</i>
                                                </button>
                                                <button
                                                        onclick="location.href='${fn:escapeXml(chgpassUrl)}'"
                                                        class="btn btn-xs btn-warning">
                                                    <i class="icon-lock">C Contraseña</i>
                                                </button>
                                                <button
                                                        onclick="location.href='${fn:escapeXml(disableUrl)}'"
                                                        class="btn btn-xs btn-warning">
                                                    <i class="icon-remove">Deshabilitar</i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                            <div class="widget-foot"></div>
                        </div>
                    </div>

                </div>
            </div>


        </div>
        <!-- Matter ends -->
    </div>
    <!-- Mainbar ends -->
    <div class="clearfix"></div>

</div>

<!-- Content ends -->
<!-- Footer starts -->
<jsp:include page="../fragments/footer.jsp" />
<!-- Scroll to top -->
<span class="totop"><a href="#"><i class="icon-chevron-up"></i></a></span>
<jsp:include page="../fragments/corePlugins.jsp" />
<script type="text/javascript" charset="utf-8">
    $(document).ready(function() {
        $('#tabla').dataTable();
    });
</script>
</body>
</html>