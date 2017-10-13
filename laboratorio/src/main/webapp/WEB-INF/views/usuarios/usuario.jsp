<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <jsp:include page="../fragments/headTag.jsp" />
    <style>
        .modal .modal-dialog {
            width: 50%;
        }
        .styleButton {
            float: right;
            height: 31px;
            margin: 27px 0px 0px 5px;
            padding: 0px 22px;
            font: 300 15px/29px "Open Sans", Helvetica, Arial, sans-serif;
            cursor: pointer;
        }
        .alert{
            margin-bottom: 0px;
        }
    </style>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="">
<!-- #HEADER -->
<jsp:include page="../fragments/bodyHeader.jsp" />
<!-- #NAVIGATION -->
<jsp:include page="../fragments/bodyNavigation.jsp" />
<!-- MAIN PANEL -->
<div id="main" data-role="main">
<!-- RIBBON -->
<div id="ribbon">
			<span class="ribbon-button-alignment">
				<span id="refresh" class="btn btn-ribbon" data-action="resetWidgets" data-placement="bottom" data-original-title="<i class='text-warning fa fa-warning'></i> <spring:message code="msg.reset" />" data-html="true">
					<i class="fa fa-refresh"></i>
				</span>
			</span>
    <!-- breadcrumb -->
    <ol class="breadcrumb">
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/usuarios/list" htmlEscape="true "/>"><spring:message code="menu.admin.users" /></a></li>
    </ol>
    <!-- end breadcrumb -->
    <jsp:include page="../fragments/layoutOptions.jsp" />
</div>
<!-- END RIBBON -->
<!-- MAIN CONTENT -->
<div id="content">
<!-- row -->
<div class="row">
    <!-- col -->
    <div class="col-xs-12 col-sm-7 col-md-7 col-lg-4">
        <h1 class="page-title txt-color-blueDark">
            <!-- PAGE HEADER -->
            <i class="fa-fw fa fa-users"></i>
            <spring:message code="users" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="users.user" />
						</span>
        </h1>
    </div>
    <!-- end col -->
</div>
<!-- end row -->
<!-- widget grid -->
<section id="widget-grid" class="">
    <!-- row -->
    <div class="row">
        <!-- NEW WIDGET START -->
        <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
            <!-- Widget ID (each widget will need unique ID)-->
            <div class="jarviswidget jarviswidget-color-darken" id="wid-id-0">
                <header>
                        <span class="widget-icon"> <i class="fa fa-edit"></i> </span>
                        <h2><spring:message code="users.user" /> </h2>
                </header>
                <!-- widget div-->
                <div>
                    <!-- widget edit box -->
                    <div class="jarviswidget-editbox">
                        <!-- This area used as dropdown edit box -->
                        <input class="form-control" type="text">
                    </div>
                    <!-- end widget edit box -->
                    <!-- widget content -->
                    <div class="widget-body no-padding">
                    <c:set var="rolesString">
                        <c:forEach var="rol" items="${authorities}">
                            <c:out value="${rol.authId.authority}" />
                        </c:forEach>
                    </c:set>
                    <br />
                    <table class="table table-striped table-bordered table-hover"
                           id="tabla">
                        <tr>
                            <th><spring:message code="users.username" /></th>
                            <td><b><c:out value="${user.username}" /></b></td>
                        </tr>
                        <tr>
                            <th><spring:message code="users.desc" /></th>
                            <td><c:out value="${user.completeName}" /></td>
                        </tr>
                        <tr>
                            <th><spring:message code="users.enabled" /></th>
                            <c:choose>
                                <c:when test="${user.enabled}">
                                    <td>
                                        <span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <tr>
                            <th><spring:message code="users.nc" /></th>
                            <c:choose>
                                <c:when test="${user.nivelCentral}">
                                    <td>
                                        <span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <tr>
                            <th><spring:message code="users.roles" /></th>
                            <td><c:out value="${rolesString}" /></td>
                        </tr>
                        <tr>
                            <th><spring:message code="users.lab" /></th>
                            <td>
                                <c:forEach var="autLab" items="${autoridadLaboratorios}">
                                    <c:if test="${autLab.user.username == user.username}">
                                        <c:out value="${autLab.laboratorio.nombre}" />
                                    </c:if>
                                </c:forEach>
                            </td>
                        </tr>
                    </table>
                        </div>
                    </div>
                    <!-- end widget div -->
                </div>
                <!-- end widget -->
        </article>
        <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
            <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
                <div>
                    <div class="widget-body no-padding">
                        <input type="hidden" value="${user.username}" id="username" />
                        <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
                        <input id="msjMkAdmin" type="hidden" value="<spring:message code="msg.user.mk.admin"/>"/>
                        <input id="msjMkNoAdmin" type="hidden" value="<spring:message code="msg.user.mk.no.admin"/>"/>
                        <input id="msjMkRecept" type="hidden" value="<spring:message code="msg.user.mk.receptionist"/>"/>
                        <input id="msjMkNoRecept" type="hidden" value="<spring:message code="msg.user.mk.no.receptionist"/>"/>
                        <input id="msjMkAnalyst" type="hidden" value="<spring:message code="msg.user.mk.analyst"/>"/>
                        <input id="msjMkNoAnalyst" type="hidden" value="<spring:message code="msg.user.mk.no.analyst"/>"/>
                        <input id="msjSuccessful1" type="hidden" value="<spring:message code="msg.user.add.authority.area"/>"/>
                        <input id="msjSuccessful2" type="hidden" value="<spring:message code="msg.user.override.authority.area"/>"/>
                        <input id="msjSuccessful3" type="hidden" value="<spring:message code="msg.user.add.authority.exam"/>"/>
                        <input id="msjSuccessful4" type="hidden" value="<spring:message code="msg.user.override.authority.exam"/>"/>
                        <input id="msjSuccessful5" type="hidden" value="<spring:message code="msg.user.add.authority.direct"/>"/>
                        <input id="msjSuccessful6" type="hidden" value="<spring:message code="msg.user.override.authority.direct"/>"/>
                        <input id="msjSuccessful7" type="hidden" value="<spring:message code="msg.user.add.authority.depart"/>"/>
                        <input id="msjSuccessful8" type="hidden" value="<spring:message code="msg.user.override.authority.depart"/>"/>
                        <input id="msjSuccessful9" type="hidden" value="<spring:message code="msg.user.enabled"/>"/>
                        <input id="msjSuccessful10" type="hidden" value="<spring:message code="msg.user.disabled"/>"/>
                        <input id="msjMkDirector" type="hidden" value="<spring:message code="msg.user.mk.director"/>"/>
                        <input id="msjMkNoDirector" type="hidden" value="<spring:message code="msg.user.mk.no.director"/>"/>
                        <input id="msjMkDepartmentHead" type="hidden" value="<spring:message code="msg.user.mk.departmentHead"/>"/>
                        <input id="msjMkNoDepartmentHead" type="hidden" value="<spring:message code="msg.user.mk.no.departmentHead"/>"/>
                        <input id="text_opt_select" type="hidden" value="<spring:message code="lbl.select"/>"/>
                        <input id="confirm_msg_opc_yes" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.yes"/>"/>
                        <input id="confirm_msg_opc_no" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.no"/>"/>
                        <input id="msjOverride" type="hidden" value="<spring:message code="msg.confirm.override"/>"/>
                        <input id="msjOverrideC" type="hidden" value="<spring:message code="msg.override.canceled"/>"/>
                        <input id="msjConfirm" type="hidden" value="<spring:message code="msg.confirm.title"/>"/>
                        <input id="msjDisable" type="hidden" value="<spring:message code="msg.confirm.disable"/>"/>
                        <input id="msjCanceled" type="hidden" value="<spring:message code="msg.action.canceled"/>"/>

                        <table class="table table-striped table-bordered table-hover"
                               id="tabla2">
                            <tr class="warning">
                                <td><spring:url value="{username}/edit" var="editUrl">
                                        <spring:param name="username" value="${user.username}" />
                                    </spring:url>
                                    <button onclick="location.href='${fn:escapeXml(editUrl)}'"
                                            class="btn btn-info">
                                        <spring:message code="act.edit" />
                                    </button>
                                <td><spring:url value="{username}/chgpass"
                                                var="chgpassUrl">
                                        <spring:param name="username" value="${user.username}" />
                                    </spring:url>
                                    <button
                                            onclick="location.href='${fn:escapeXml(chgpassUrl)}'"
                                            class="btn btn-info">
                                        <spring:message code="act.change.pass" />
                                    </button>
                                <c:choose>
                                    <c:when test="${user.enabled}">
                                        <td>
                                            <button id="btn-Disable" class="btn btn-info">
                                                <spring:message code="users.disable" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <button id="btn-Enable" class="btn btn-info">
                                            <spring:message code="users.enable" />
                                        </button>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                                <td><spring:url value="/usuarios/list" var="listUrl"/>
                                    <button onclick="location.href='${fn:escapeXml(listUrl)}'"
                                            class="btn btn-info">
                                        <spring:message code="act.show.all" />
                                    </button></td>
                            </tr>
                            <tr class="info">
                                <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_RECEPCION')}">
                                        <td>
                                            <button id="btn-mkNoRecep" type="button" class="btn btn-danger">
                                                <spring:message code="users.norecep" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <button id="btn-mkRecep" type="button" class="btn btn-success">
                                                <spring:message code="users.recep" />
                                            </button>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                                   <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_ANALISTA')}">
                                        <td>
                                            <button id="btn-mkNoAnalyst" type="button" class="btn btn-danger">
                                                <spring:message code="users.noanalyst" />
                                            </button>
                                            <br/><br/>
                                            <button id="btn-Areas" type="button" class="btn btn-primary">
                                                <spring:message code="act.admin.areas" />
                                            </button>
                                            <button id="btn-Examenes" type="button" class="btn btn-primary">
                                                <spring:message code="act.admin.exams" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <button id="btn-mkAnalyst" type="button" class="btn btn-success">
                                                <spring:message code="users.analyst" />
                                            </button>
                                         </td>
                                    </c:otherwise>
                                    </c:choose>

                                <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_JEFE')}">
                                        <td>
                                            <button id="btn-mkNoJD" type="button" class="btn btn-danger">
                                                <spring:message code="users.nodept" />
                                            </button>
                                            <br/><br/>
                                            <button id="btn-department" type="button" class="btn btn-primary">
                                                <spring:message code="act.admin.department" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <button id="btn-mkJD" type="button" class="btn btn-success">
                                                <spring:message code="users.dept" />
                                            </button>
                                        </td>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_DIR')}">
                                        <td>
                                            <button id="btn-mkNoDir" type="button" class="btn btn-danger">
                                                <spring:message code="users.nodir" />
                                            </button>
                                            <br/><br/>
                                            <button id="btn-direction" type="button" class="btn btn-primary">
                                                <spring:message code="act.admin.direction" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <button id="btn-mkDir" type="button" class="btn btn-success">
                                                <spring:message code="users.dir" />
                                            </button>
                                        </td>
                                    </c:otherwise>
                                </c:choose>

                                <td>
                                    <c:choose>
                                        <c:when test="${fn:contains(rolesString,'ROLE_ADMIN')}">
                                            <button id="btn-mkNoAdmin" type="button" class="btn btn-danger">
                                                <spring:message code="users.noadmin" />
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button id="btn-mkAdmin" type="button" class="btn btn-success">
                                                <spring:message code="users.admin" />
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </article>
        <!-- WIDGET END -->
    </div>
    <!-- end row -->
    <!-- row -->
    <div class="row">
        <!-- a blank row to get started -->
        <div class="col-sm-12">
            <!-- your contents here -->
        </div>
    </div>
    <!-- end row -->
</section>
<!-- end widget grid -->
<!-- Modal AREAS ROL ANALISTA-->
<div class="modal fade" id="modalArea" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <div class="alert alert-info">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title">
                        <i class="fa-fw fa fa-users"></i>
                        <spring:message code="lbl.user.header.modal.area" />
                    </h4>
                </div>
            </div>
            <div class="modal-body"> <!--  no-padding -->
                <div class="row">
                    <div class="col col-sm-12 col-md-12 col-lg-12">
                        <form id="areas-form" class="smart-form" novalidate="novalidate">
                            <div class="row">
                                <section class="col col-sm-12 col-md-9 col-lg-10">
                                    <label class="text-left txt-color-blue font-md">
                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.area" />
                                    </label>
                                    <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-location-arrow fa-fw"></i>
                                    </span>
                                        <select  class="select2" id="idArea" name="idArea" >
                                            <option value=""><spring:message code="lbl.select" />...</option>
                                            <c:forEach items="${areas}" var="area">
                                                <option value="${area.idArea}">${area.nombre}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-3 col-lg-2">
                                    <button type="button" class="btn btn-success styleButton" id="btnAddArea">
                                        <i class="fa fa-save"></i>
                                    </button>
                                </section>
                            </div>
                        </form>
                    </div>
                </div>
                    <div class="widget-body no-padding">
                    <div class="row">
                        <section class="col col-sm-12 col-md-12 col-lg-12">
                            <table class="table table-striped table-bordered table-hover" id="areas-list">
                                <thead>
                                <tr>
                                    <th data-class="expand"><i class="fa fa-fw fa-file-text-o text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.name"/></th>
                                    <th><spring:message code="lbl.override"/></th>
                                </tr>
                                </thead>
                            </table>
                        </section>
                    </div>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Modal EXAMENES ROL ANALISTA -->
<div class="modal fade" id="modalExamen" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <div class="alert alert-info">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title">
                        <i class="fa-fw fa fa-users"></i>
                        <spring:message code="lbl.user.header.modal.exam" />
                    </h4>
                </div>
            </div>
            <div class="modal-body"> <!--  no-padding -->
                <div class="row">
                    <div class="col col-sm-12 col-md-12 col-lg-12">
                        <form id="examenes-form" class="smart-form" novalidate="novalidate">
                            <div class="row">
                                <section class="col col-sm-12 col-md-9 col-lg-10">
                                    <label class="text-left txt-color-blue font-md">
                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.test.name" />
                                    </label>
                                    <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-location-arrow fa-fw"></i>
                                    </span>
                                        <select  class="select2" id="idExamen" name="idExamen" >
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-3 col-lg-2">
                                    <button type="button" class="btn btn-success styleButton" id="btnAddExamen">
                                        <i class="fa fa-save"></i>
                                    </button>
                                </section>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="widget-body no-padding">
                    <div class="row">
                        <section class="col col-sm-12 col-md-12 col-lg-12">
                            <table class="table table-striped table-bordered table-hover"   id="examenes-list">
                                <thead>
                                <tr>
                                    <th data-class="expand"><i class="fa fa-fw fa-file-text-o text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.name"/></th>
                                    <th data-class="expand"><i class="fa fa-fw fa-file-text-o text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.area"/></th>
                                    <th><spring:message code="lbl.override"/></th>
                                </tr>
                                </thead>
                            </table>
                        </section>
                    </div>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Modal Direcciones ROL Director -->
<div class="modal fade" id="modalDireccion" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <div class="alert alert-info">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title">
                        <i class="fa-fw fa fa-users"></i>
                        <spring:message code="lbl.user.header.modal.direction" />
                    </h4>
                </div>
            </div>
            <div class="modal-body"> <!--  no-padding -->
                <div class="row">
                    <div class="col col-sm-12 col-md-12 col-lg-12">
                        <form id="direccion-form" class="smart-form" novalidate="novalidate">
                            <div class="row">
                                <section class="col col-sm-12 col-md-9 col-lg-10">
                                    <label class="text-left txt-color-blue font-md">
                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.direction" />
                                    </label>
                                    <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-location-arrow fa-fw"></i>
                                    </span>
                                        <select  class="select2" id="idDireccion" name="idDireccion" >
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-3 col-lg-2">
                                    <button type="button" class="btn btn-success styleButton" id="btnAddDireccion">
                                        <i class="fa fa-save"></i>
                                    </button>
                                </section>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="widget-body no-padding">
                    <div class="row">
                        <section class="col col-sm-12 col-md-12 col-lg-12">
                            <table class="table table-striped table-bordered table-hover"   id="direccion-list">
                                <thead>
                                <tr>
                                    <th data-class="expand"><i class="fa fa-fw fa-file-text-o text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.name"/></th>
                                    <th><spring:message code="lbl.override"/></th>
                                </tr>
                                </thead>
                            </table>
                        </section>
                    </div>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<!-- Modal Departamento ROL JEFE -->
<div class="modal fade" id="modalDepartamento" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <div class="alert alert-info">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title">
                        <i class="fa-fw fa fa-users"></i>
                        <spring:message code="lbl.user.header.modal.department" />
                    </h4>
                </div>
            </div>
            <div class="modal-body"> <!--  no-padding -->
                <div class="row">
                    <div class="col col-sm-12 col-md-12 col-lg-12">
                        <form id="departamento-form" class="smart-form" novalidate="novalidate">
                            <div class="row">
                                <section class="col col-sm-12 col-md-9 col-lg-10">
                                    <label class="text-left txt-color-blue font-md">
                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.department" />
                                    </label>
                                    <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-location-arrow fa-fw"></i>
                                    </span>
                                        <select  class="select2" id="idDepartamento" name="idDepartamento" >
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-3 col-lg-2">
                                    <button type="button" class="btn btn-success styleButton" id="btnAddDepartamento">
                                        <i class="fa fa-save"></i>
                                    </button>
                                </section>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="widget-body no-padding">
                    <div class="row">
                        <section class="col col-sm-12 col-md-12 col-lg-12">
                            <table class="table table-striped table-bordered table-hover"   id="departamento-list">
                                <thead>
                                <tr>
                                    <th data-class="expand"><i class="fa fa-fw fa-file-text-o text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.name"/></th>
                                    <th><spring:message code="lbl.override"/></th>
                                </tr>
                                </thead>
                            </table>
                        </section>
                    </div>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

</div>
<!-- END MAIN CONTENT -->
</div>
<!-- END MAIN PANEL -->
<!-- BEGIN FOOTER -->
<jsp:include page="../fragments/footer.jsp" />
<!-- END FOOTER -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<jsp:include page="../fragments/corePlugins.jsp" />
<!-- BEGIN PAGE LEVEL PLUGINS -->
<spring:url value="/resources/js/plugin/datatables/jquery.dataTables.min.js" var="dataTables" />
<script src="${dataTables}"></script>
<spring:url value="/resources/js/plugin/datatables/dataTables.colVis.min.js" var="dataTablesColVis" />
<script src="${dataTablesColVis}"></script>
<spring:url value="/resources/js/plugin/datatables/dataTables.tableTools.min.js" var="dataTablesTableTools" />
<script src="${dataTablesTableTools}"></script>
<spring:url value="/resources/js/plugin/datatables/dataTables.bootstrap.min.js" var="dataTablesBootstrap" />
<script src="${dataTablesBootstrap}"></script>
<spring:url value="/resources/js/plugin/datatable-responsive/datatables.responsive.min.js" var="dataTablesResponsive" />
<script src="${dataTablesResponsive}"></script>
<!-- jQuery Selecte2 Input -->
<spring:url value="/resources/js/plugin/select2/select2.min.js" var="selectPlugin"/>
<script src="${selectPlugin}"></script>
<!-- bootstrap datepicker -->
<spring:url value="/resources/js/plugin/bootstrap-datepicker/bootstrap-datepicker.js" var="datepickerPlugin" />
<script src="${datepickerPlugin}"></script>
<spring:url value="/resources/js/plugin/bootstrap-datepicker/locales/bootstrap-datepicker.{languagedt}.js" var="datePickerLoc">
    <spring:param name="languagedt" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${datePickerLoc}"></script>
<!-- JQUERY VALIDATE -->
<spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate" />
<script src="${jqueryValidate}"></script>
<spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
    <spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${jQValidationLoc}"></script>
<!-- JQUERY BLOCK UI -->
<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
<script src="${jqueryBlockUi}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/usuarios/users.js" var="usersJs" />
<script src="${usersJs}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="mkAdminUrl" value="/usuarios/adminUser"/>
<c:url var="mkNoAdminUrl" value="/usuarios/noAdminUser"/>
<c:url var="mkReceptUrl" value="/usuarios/receptionistUser"/>
<c:url var="mkNoReceptUrl" value="/usuarios/noReceptionistUser"/>
<c:url var="mkAnalystUrl" value="/usuarios/analystUser"/>
<c:url var="mkNoAnalystUrl" value="/usuarios/noAnalystUser"/>
<c:url var="mkDirectorUrl" value="/usuarios/directorUser"/>
<c:url var="mkNoDirectorUrl" value="/usuarios/noDirectorUser"/>
<c:url var="mkDepartmentHeadUrl" value="/usuarios/departmentHeadUser"/>
<c:url var="mkNoDepartmentHeadUrl" value="/usuarios/noDepartmentHeadUser"/>
<c:url var="autoridadAreaUrl" value="/usuarios/getAutoridadAreaUsuario"/>
<c:url var="autoridadExamenUrl" value="/usuarios/getAutoridadExamenUsuario"/>
<c:url var="areaUsuarioUrl" value="/usuarios/asociarAreaUsuario"/>
<c:url var="overrideAreaUsuarioUrl" value="/usuarios/overrideAreaUsuario"/>
<c:url var="examenUsuarioUrl" value="/usuarios/asociarExamenUsuario"/>
<c:url var="overrideExamenUsuarioUrl" value="/usuarios/overrideExamenUsuario"/>
<c:url var="areasUrl" value="/usuarios/getAreasDispUsuario"/>
<c:url var="examenesUrl" value="/usuarios/getExamenesDisponiblesUsuario"/>
<c:url var="autoridadDireccionUrl" value="/usuarios/getAutoridadDireccionUsuario"/>
<c:url var="direccionesUrl" value="/usuarios/getDireccionesDisponiblesUsuario"/>
<c:url var="direccionUsuarioUrl" value="/usuarios/asociarDireccionUsuario"/>
<c:url var="overrideDireccionUsuarioUrl" value="/usuarios/overrideDireccionUsuario"/>
<c:url var="autoridadDepartaUrl" value="/usuarios/getAutoridadDepartaUsuario"/>
<c:url var="departamentosUrl" value="/usuarios/getDepartaDisponiblesUsuario"/>
<c:url var="departaUsuarioUrl" value="/usuarios/asociarDepartamentoUsuario"/>
<c:url var="overrideDepartaUsuarioUrl" value="/usuarios/overrideDepartaUsuario"/>
<c:url var="enableUrl" value="/usuarios/enable"/>
<c:url var="disableUrl" value="/usuarios/disable"/>

<c:url var="usuarioUrl" value="/usuarios/admin/"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {sUsuarioUrl: "${usuarioUrl}",
            sMkAdminUrl : "${mkAdminUrl}",
            sMkNoAdminUrl : "${mkNoAdminUrl}",
            mkReceptUrl : "${mkReceptUrl}",
            mkNoReceptUrl : "${mkNoReceptUrl}",
            mkAnalystUrl : "${mkAnalystUrl}",
            mkNoAnalystUrl : "${mkNoAnalystUrl}",
            autoridadAreaUrl : "${autoridadAreaUrl}",
            autoridadExamenUrl : "${autoridadExamenUrl}",
            areaUsuarioUrl : "${areaUsuarioUrl}",
            overrideAreaUsuarioUrl : "${overrideAreaUsuarioUrl}",
            examenUsuarioUrl : "${examenUsuarioUrl}",
            overrideExamenUsuarioUrl : "${overrideExamenUsuarioUrl}",
            examenesUrl : "${examenesUrl}",
            autoridadDireccionUrl : "${autoridadDireccionUrl}",
            direccionesUrl : "${direccionesUrl}",
            direccionUsuarioUrl : "${direccionUsuarioUrl}",
            overrideDireccionUsuarioUrl : "${overrideDireccionUsuarioUrl}",
            areasUrl : "${areasUrl}",
            autoridadDepartaUrl : "${autoridadDepartaUrl}",
            departamentosUrl : "${departamentosUrl}",
            departaUsuarioUrl : "${departaUsuarioUrl}",
            overrideDepartaUsuarioUrl : "${overrideDepartaUsuarioUrl}",
            mkDirectorUrl : "${mkDirectorUrl}",
            mkNoDirectorUrl : "${mkNoDirectorUrl}",
            mkDepartmentHeadUrl : "${mkDepartmentHeadUrl}",
            mkNoDepartmentHeadUrl : "${mkNoDepartmentHeadUrl}",
            enableUrl : "${enableUrl}",
            disableUrl : "${disableUrl}",
            blockMess: "${blockMess}"
        };
        Users.init(parametros);
        $("li.administracion").addClass("open");
        $("li.users").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.users").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>