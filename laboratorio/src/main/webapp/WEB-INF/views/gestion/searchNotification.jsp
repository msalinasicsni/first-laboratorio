<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 08-11-15
  Time: 04:26 PM
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <jsp:include page="../fragments/headTag.jsp"/>
    <spring:url value="/resources/img/plus.png" var="plus"/>
    <spring:url value="/resources/img/minus.png" var="minus"/>
    <style>
        .modal .modal-dialog {
            width: 60%;
        }
        td.details-control {
            background: url("${plus}") no-repeat center center;
            cursor: pointer;
        }

        tr.shown td.details-control {
            background: url("${minus}") no-repeat center center;
        }
    </style>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="">
<!-- #HEADER -->
<jsp:include page="../fragments/bodyHeader.jsp"/>
<!-- #NAVIGATION -->
<jsp:include page="../fragments/bodyNavigation.jsp"/>
<!-- MAIN PANEL -->
<div id="main" data-role="main">
<!-- RIBBON -->
<div id="ribbon">
			<span class="ribbon-button-alignment">
				<span id="refresh" class="btn btn-ribbon" data-action="resetWidgets" data-placement="bottom"
                      data-original-title="<i class='text-warning fa fa-warning'></i> <spring:message code="msg.reset" />"
                      data-html="true">
					<i class="fa fa-refresh"></i>
				</span>
			</span>
    <!-- breadcrumb -->
    <ol class="breadcrumb">
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home"/></a> <i class="fa fa-angle-right"></i>
            <spring:message code="menu.management"/> <i class="fa fa-angle-right"></i>
            <a href="<spring:url value="/gestion/initnoti" htmlEscape="true "/>"><spring:message code="menu.management.notifications"/></a></li>
    </ol>
    <!-- end breadcrumb -->
    <jsp:include page="../fragments/layoutOptions.jsp"/>
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
            <i class="fa-fw fa fa-book"></i>
            <spring:message code="menu.management.notifications"/>
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="lbl.search"/>
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
                <span class="widget-icon"> <i class="fa fa-search"></i> </span>

                <h2><spring:message code="lbl.parameters"/></h2>
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
                    <input id="text_dx_date" type="hidden" value="<spring:message code="lbl.sampling.date"/>"/>
                    <input id="text_codmx" type="hidden" value="<spring:message code="lbl.lab.code.mx"/>"/>
                    <input id="text_dx" type="hidden" value="<spring:message code="lbl.desc.request"/>"/>
                    <input id="text_conres" type="hidden" value="<spring:message code="lbl.result"/>"/>
                    <input id="text_detres" type="hidden" value="<spring:message code="lbl.final.result"/>"/>
                    <input id="smallBox_content" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                    <input id="msg_no_results_found" type="hidden"  value="<spring:message code="msg.no.results.found"/>"/>
                    <input id="text_opt_select" type="hidden" value="<spring:message code="lbl.select"/>"/>
                    <input id="msg_override_success" type="hidden" value="<spring:message code="msg.noti.successfully.override"/>"/>
                    <input id="msg_update_success" type="hidden" value="<spring:message code="msg.noti.successfully.updated"/>"/>
                    <input id="msg_confirm_title" type="hidden" value="<spring:message code="msg.confirm.title"/>"/>
                    <input id="msg_confirm_content" type="hidden" value="<spring:message code="msg.confirm.change.person"/>"/>
                    <input id="confirm_msg_opc_yes" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.yes"/>"/>
                    <input id="confirm_msg_opc_no" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.no"/>"/>
                    <input id="msg_action_canceled" type="hidden" value="<spring:message code="msg.action.canceled"/>"/>
                    <input id="msg_change_successful" type="hidden" value="<spring:message code="msg.person.successfully.changed"/>"/>
                    <form id="searchOrders-form" class="smart-form" autocomplete="off">
                        <fieldset>
                            <div class="row">
                                <section class="col col-sm-12 col-md-12 col-lg-4">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.receipt.person.applicant.name"/>
                                    </label>
                                    <label class="input"><i class="icon-prepend fa fa-pencil"></i> <i
                                            class="icon-append fa fa-sort-alpha-asc"></i>
                                        <input type="text" id="txtfiltroNombre" name="filtroNombre"
                                               placeholder="<spring:message code="lbl.receipt.person.applicant.name"/>">
                                        <b class="tooltip tooltip-bottom-right"><i
                                                class="fa fa-warning txt-color-pink"></i><spring:message
                                                code="tooltip.receipt.person.applicant.name"/></b>
                                    </label>
                                </section>
                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.init.date"/> <spring:message code="lbl.notification"/>
                                    </label>
                                    <label class="input">
                                        <i class="icon-prepend fa fa-pencil"></i> <i
                                            class="icon-append fa fa-calendar"></i>
                                        <input type="text" name="fechaInicioNoti" id="fechaInicioNoti"
                                               placeholder="<spring:message code="lbl.date.format"/>"
                                               class="form-control from_date" data-date-end-date="+0d"/>
                                        <b class="tooltip tooltip-bottom-right"> <i
                                                class="fa fa-warning txt-color-pink"></i> <spring:message
                                                code="tooltip.notification.startdate"/></b>
                                    </label>
                                </section>
                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.end.date"/> <spring:message code="lbl.notification"/>
                                    </label>
                                    <label class="input">
                                        <i class="icon-prepend fa fa-pencil"></i> <i
                                            class="icon-append fa fa-calendar"></i>
                                        <input type="text" name="fechaFinNoti" id="fechaFinNoti"
                                               placeholder="<spring:message code="lbl.date.format"/>"
                                               class="form-control to_date" data-date-end-date="+0d"/>
                                        <b class="tooltip tooltip-bottom-right"> <i
                                                class="fa fa-warning txt-color-pink"></i> <spring:message
                                                code="tooltip.notification.enddate"/></b>
                                    </label>
                                </section>
                            </div>
                            <div class="row">
                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.notification.type" /> </label>
                                    <div class="input-group">
                                        <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                        <select id="codTipoNoti" name="codTipoNoti"
                                                class="select2">
                                            <option value=""><spring:message code="lbl.select" />...</option>
                                            <c:forEach items="${notificaciones}" var="tipoNoti">
                                                <option value="${tipoNoti.codigo}">${tipoNoti.valor}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-6 col-lg-4">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.silais"/> </label>
                                    <div class="input-group">
                                        <span class="input-group-addon"><i
                                                class="fa fa-location-arrow fa-fw"></i></span>
                                        <select id="codSilais" name="codSilais"
                                                class="select2">
                                            <option value=""><spring:message code="lbl.select"/>...</option>
                                            <c:forEach items="${entidades}" var="entidad">
                                                <option value="${entidad.entidadAdtvaId}">${entidad.nombre}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-12 col-lg-5">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.health.unit"/> </label>

                                    <div class="input-group">
                                        <span class="input-group-addon"><i
                                                class="fa fa-location-arrow fa-fw"></i></span>
                                        <select id="codUnidadSalud" name="codUnidadSalud"
                                                class="select2">
                                            <option value=""><spring:message code="lbl.select"/>...</option>
                                        </select>
                                    </div>
                                </section>
                            </div>
                            <div class="row">
                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.unique.code.mx"/> </label>
                                    <label class="input"><i class="icon-prepend fa fa-pencil"></i> <i
                                            class="icon-append fa fa-sort-alpha-asc"></i>
                                        <input type="text" id="txtCodUnicoMx" name="txtCodUnicoMx"
                                               placeholder="<spring:message code="lbl.unique.code.mx"/>">
                                        <b class="tooltip tooltip-bottom-right"><i
                                                class="fa fa-warning txt-color-pink"></i><spring:message
                                                code="tooltip.unique.code.mx"/></b>
                                    </label>
                                </section>
                            </div>
                        </fieldset>
                        <footer>
                            <button type="button" id="all-orders" class="btn btn-info"><i class="fa fa-search"></i>
                                <spring:message code="act.show.all"/></button>
                            <button type="submit" id="search-orders" class="btn btn-info"><i class="fa fa-search"></i>
                                <spring:message code="act.search"/></button>

                        </footer>
                    </form>
                </div>
                <!-- end widget content -->
            </div>
            <!-- end widget div -->
        </div>
        <!-- end widget -->
    </article>
    <!-- WIDGET END -->
    <!-- NEW WIDGET START -->
    <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
        <!-- Widget ID (each widget will need unique ID)-->
        <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
            <header>
                <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>

                <h2><spring:message code="lbl.active.notifications"/></h2>
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
                    <table id="orders_result" class="table table-striped table-bordered table-hover" width="100%">
                        <thead>
                        <tr>
                            <th data-class="expand"><spring:message code="lbl.receipt.person.applicant.name"/></th>
                            <th data-hide="phone"><spring:message code="lbl.age2"/></th>
                            <th data-hide="phone"><spring:message code="person.sexo"/></th>
                            <th data-hide="phone"><spring:message code="lbl.silais"/></th>
                            <th data-hide="phone"><spring:message code="lbl.health.unit"/></th>
                            <th data-hide="phone"><spring:message code="lbl.notification.type"/></th>
                            <th data-hide="phone"><spring:message code="lbl.notification.date"/></th>
                            <th data-hide="phone"><spring:message code="lbl.fis.short"/></th>
                            <th style="width: 5%" valign="center"><spring:message code="lbl.request.large"/></th>
                            <th style="width: 5%" valign="center"><spring:message code="act.redefine"/></th>
                            <th style="width: 5%" valign="center"><spring:message code="lbl.override"/></th>
                            <th style="width: 5%" valign="center"><spring:message code="lbl.person"/></th>
                            <th style="width: 5%" valign="center"><spring:message code="lbl.person"/></th>
                        </tr>
                        </thead>
                    </table>
                </div>

                <!-- end widget content -->
            </div>
            <!-- end widget div -->
        </div>
        <!-- end widget -->
        <!-- end widget -->
        <div class="modal fade" id="d_confirmacion"  role="dialog" tabindex="-1" data-aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header alert-info">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                            &times;
                        </button>
                        <h4 class="modal-title"> <spring:message code="lbl.override" /> <spring:message code="lbl.notification" /></h4>
                    </div>

                    <div class="modal-body">
                        <form id="override-noti-form" class="smart-form" novalidate="novalidate">
                            <input type=hidden id="idOverride"/>
                            <div id="cuerpo">
                                <section class="col col-sm-12 col-md-12 col-lg-12">
                                    <label class="text-left txt-color-blue font-md">
                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.annulment.cause" /> </label>
                                    <label class="textarea">
                                        <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                        <textarea class="form-control" rows="3" name="causaAnulacion" id="causaAnulacion"
                                                  placeholder="<spring:message code="lbl.annulment.cause" />"></textarea>
                                        <b class="tooltip tooltip-bottom-right"> <i
                                                class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.annulment.cause"/>
                                        </b>
                                    </label>
                                </section>
                            </div>
                            <footer>
                                <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="act.cancel" /></button>
                                <button type="submit" class="btn btn-info" ><spring:message code="act.ok" /></button>
                            </footer>
                        </form>
                    </div>
                </div>

                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- end modal -->

        <!-- end widget -->
        <div class="modal fade" id="d_redefine"  role="dialog" tabindex="-1" data-aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header alert-info">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                            &times;
                        </button>
                        <h4 class="modal-title"> <spring:message code="act.redefine" /> <spring:message code="lbl.notification" /> <spring:message code="lbl.patient" /></h4>
                        <h4 class="modal-title"><label class="text-left txt-color-blue font-md" id="lblPersona"></label></h4>
                    </div>

                    <div class="modal-body">
                        <form id="redefine-noti-form" class="smart-form" novalidate="novalidate">
                            <input type=hidden id="idRedefine"/>
                            <input type=hidden id="codMunicipioNoti"/>
                            <input type=hidden id="codUnidadNoti"/>
                            <div id="cuerpo2">
                                <div class="row">
                                    <section class="col col-sm-12 col-md-12 col-lg-12">
                                        <label class="text-left txt-color-blue font-md">
                                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.notification.type" /> </label>
                                        <div class="input-group">
                                            <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                            <select id="codTipoNotiRedef" name="codTipoNotiRedef"
                                                    class="select2">
                                                <option value=""><spring:message code="lbl.select" />...</option>
                                                <c:forEach items="${notificaciones}" var="tipoNoti">
                                                    <c:if test="${tipoNoti.codigo ne 'TPNOTI|PCNT'}">
                                                        <option value="${tipoNoti.codigo}">${tipoNoti.valor}</option>
                                                    </c:if>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </section>
                                    <section class="col col-sm-12 col-md-6 col-lg-6">
                                        <label  class="text-left txt-color-blue font-md">
                                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.receipt.symptoms.start.date.full" />
                                        </label>
                                        <div class="">
                                            <label class="input">
                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-calendar fa-fw"></i>
                                                <input name="fechaInicioSintomas" id="fechaInicioSintomas" type='text'
                                                       class="form-control date-picker" data-date-end-date="+0d"
                                                       placeholder="<spring:message code="lbl.receipt.symptoms.start.date.full" />"/>
                                            </label>
                                        </div>
                                    </section>
                                </div>
                                <!-- START ROW -->
                                <div class="row">
                                    <section class="col col-6">
                                        <label class="text-left txt-color-blue font-md hidden-xs">
                                            <spring:message code="lbl.silais" />
                                        </label>
                                        <div class="input-group">
                                            <span class="input-group-addon"> <i class="fa fa-location-arrow"></i></span>
                                            <select data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.silais" />" name="codSilaisAtencion" id="codSilaisAtencion" class="select2">
                                                <option value=""><spring:message code="lbl.select"/> ...</option>
                                                <c:forEach items="${entidades}" var="entidad">
                                                            <option value="${entidad.codigo}">${entidad.nombre}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </section>
                                    <section class="col col-6">
                                        <label class="text-left txt-color-blue font-md hidden-xs">
                                            <spring:message code="lbl.muni" />
                                        </label>
                                        <div class="input-group">
                                            <span class="input-group-addon"> <i class="fa fa-location-arrow"></i></span>
                                            <select data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.muni" />" name="codMunicipio" id="codMunicipio" class="select2">
                                            </select>
                                        </div>
                                    </section>
                                </div>
                                <div class="row">
                                    <section class="col col-12">
                                        <label class="text-left txt-color-blue font-md hidden-xs">
                                            <spring:message code="lbl.health.unit" />
                                        </label>
                                        <div class="input-group">
                                            <span class="input-group-addon"> <i class="fa fa-location-arrow"></i></span>
                                            <select data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.health.unit" />" name="codUnidadAtencion" id="codUnidadAtencion" class="select2">
                                            </select>
                                        </div>
                                    </section>
                                </div>
                            </div>
                            <footer>
                                <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="act.cancel" /></button>
                                <button type="submit" class="btn btn-info" ><spring:message code="act.ok" /></button>
                            </footer>
                        </form>
                    </div>
                </div>

                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- end modal -->

        <div class="modal fade" id="modalPerson"  role="dialog" tabindex="-1" data-aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header alert-info">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                            &times;
                        </button>
                        <h4 class="modal-title"> <spring:message code="act.change" /> <spring:message code="lbl.person" /></h4>
                        <h4 class="modal-title"><label class="text-left txt-color-blue font-md" id="lblPersonaActual"></label></h4>
                    </div>

                    <div class="modal-body">
                        <form id="search-form" class="smart-form" autocomplete="off">
                            <input type=hidden id="idNotificacion"/>
                            <fieldset>
                                <section>
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="person.search" />
                                    </label>
                                    <label class="input"> <i class="icon-append fa fa-check"></i>
                                        <input type="text" id="filtro" name="filtro" placeholder="<spring:message code="person.search.parameters"/>">
                                        <b class="tooltip tooltip-bottom-right"><spring:message code="person.search.parameters"/></b> </label>
                                </section>
                            </fieldset>
                            <footer>
                                <button type="submit" id="search-person" class="btn btn-info"><i class="fa fa-search"></i> <spring:message code="act.search" /></button>
                            </footer>
                        </form>
                        <div class="widget-body no-padding">
                            <table id="persons_result" class="table table-striped table-bordered table-hover" width="100%">
                                <thead>
                                <tr>
                                    <th data-class="expand"><i class="fa fa-fw fa-key text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.id"/></th>
                                    <th data-hide="phone"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.name1"/></th>
                                    <th data-hide="phone"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.name2"/></th>
                                    <th data-hide="phone"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.lastname1"/></th>
                                    <th data-hide="phone"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.lastname2"/></th>
                                    <th data-hide="phone"><i class="fa fa-fw fa-calendar txt-color-blue hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.fecnac"/></th>
                                    <th data-hide="phone"><i class="fa fa-fw fa-calendar txt-color-blue hidden-md hidden-sm hidden-xs"></i> <spring:message code="lbl.age2"/></th>
                                    <th data-hide="phone"><i class="fa fa-fw fa-map-marker text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message code="person.mun.res"/></th>
                                    <th style="width: 5%" valign="center"><i class="fa fa-check"></i></th>
                                </tr>
                                </thead>
                            </table>
                            <form id="paginate-form" class="smart-form" autocomplete="off">
                                <footer>
                                    <button type="button" title="<spring:message code="lbl.next"/>" id="next" class="btn btn-primary"><i class="fa fa-fast-forward"></i></button>
                                    <button type="button" title="<spring:message code="lbl.prev"/>" id="prev" class="btn btn-primary"><i class="fa fa-fast-backward"></i></button>
                                </footer>
                            </form>
                        </div>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- end modal -->

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
</div>
<!-- END MAIN CONTENT -->
</div>
<!-- END MAIN PANEL -->
<!-- BEGIN FOOTER -->
<jsp:include page="../fragments/footer.jsp"/>
<!-- END FOOTER -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<jsp:include page="../fragments/corePlugins.jsp"/>
<!-- BEGIN PAGE LEVEL PLUGINS -->
<spring:url value="/resources/js/plugin/datatables/jquery.dataTables.min.js" var="dataTables"/>
<script src="${dataTables}"></script>
<spring:url value="/resources/js/plugin/datatables/dataTables.colVis.min.js" var="dataTablesColVis"/>
<script src="${dataTablesColVis}"></script>

<spring:url value="/resources/js/plugin/datatables/dataTables.bootstrap.min.js" var="dataTablesBootstrap"/>
<script src="${dataTablesBootstrap}"></script>
<spring:url value="/resources/js/plugin/datatable-responsive/datatables.responsive.min.js" var="dataTablesResponsive"/>
<script src="${dataTablesResponsive}"></script>
<!-- jQuery Selecte2 Input -->
<spring:url value="/resources/js/plugin/select2/select2.min.js" var="selectPlugin"/>
<script src="${selectPlugin}"></script>
<!-- bootstrap datepicker -->
<spring:url value="/resources/js/plugin/bootstrap-datepicker/bootstrap-datepicker.js" var="datepickerPlugin"/>
<script src="${datepickerPlugin}"></script>
<spring:url value="/resources/js/plugin/bootstrap-datepicker/locales/bootstrap-datepicker.{languagedt}.js"
            var="datePickerLoc">
    <spring:param name="languagedt" value="${pageContext.request.locale.language}"/></spring:url>
<script src="${datePickerLoc}"></script>
<!-- JQUERY VALIDATE -->
<spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate"/>
<script src="${jqueryValidate}"></script>
<spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
    <spring:param name="language" value="${pageContext.request.locale.language}"/></spring:url>
<script src="${jQValidationLoc}"></script>
<!-- JQUERY BLOCK UI -->
<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi"/>
<script src="${jqueryBlockUi}"></script>
<!-- JQUERY INPUT MASK -->
<spring:url value="/resources/js/plugin/jquery-inputmask/jquery.inputmask.bundle.min.js" var="jqueryInputMask"/>
<script src="${jqueryInputMask}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/gestion/searchNoti.js" var="snoti"/>
<script src="${snoti}"></script>
<spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers"/>
<script src="${handleDatePickers}"></script>
<spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask"/>
<script src="${handleInputMask}"></script>
<spring:url value="/resources/scripts/utilidades/generarReporte.js" var="generarReporte"/>
<script src="${generarReporte}"></script>
<spring:url value="/resources/scripts/personas/person-change.js" var="personChange" />
<script src="${personChange}"></script>
<spring:url value="/resources/scripts/utilidades/calcularEdad.js" var="edadJS" />
<script src="${edadJS}"></script>
<spring:url value="/resources/scripts/utilidades/unicodeEscaper.js" var="unicodeEscaper" />
<script src="${unicodeEscaper}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message"/></c:set>
<c:url var="ordersUrl" value="/gestion/searchMx"/>
<c:url var="unidadesURL" value="/api/v1/unidadesPrimariasHospSilais"/>
<spring:url var="municipiosUrl" value="/api/v1/municipiosbysilais"/>
<spring:url var="unidadesUrl"   value="/api/v1/unidadesPrimHosp"  />
<c:url var="notificacionesUrl" value="/gestion/getNotifications"/>
<c:url var="overrideUrl" value="/gestion/overridenoti"/>
<c:url var="changePersonUrl" value="/gestion/changePersonNoti"/>
<c:url var="updateUrl" value="/tomaMx/updatenoti"/>
<spring:url value="/personas/persons" var="sPersonUrl"/>
<spring:url value="/personas/update/" var="sUpdatePersonUrl"/>
<script type="text/javascript">
    $(document).ready(function () {
        pageSetUp();
        var parametros = {sOrdersUrl: "${ordersUrl}",
            sUnidadesUrl: "${unidadesURL}",
            blockMess: "${blockMess}",
            notificacionesUrl: "${notificacionesUrl}",
            sOverrideUrl: "${overrideUrl}",
            municipiosUrl : "${municipiosUrl}",
            unidadesUrl : "${unidadesUrl}",
            updateUrl : "${updateUrl}",
            sPersonUrl: "${sPersonUrl}",
            changePersonUrl : "${changePersonUrl}",
            sUpdatePersonUrl : "${sUpdatePersonUrl}"
        };
        BuscarNotificacion.init(parametros);
        ChangePerson.init(parametros);

        handleDatePickers("${pageContext.request.locale.language}");
        handleInputMasks();
        $("li.gestion").addClass("open");
        $("li.gestion_noti").addClass("active");
        if ("top" != localStorage.getItem("sm-setmenu")) {
            $("li.gestion_noti").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
