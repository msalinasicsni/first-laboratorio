<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<html>
<head>
    <jsp:include page="../../fragments/headTag.jsp" />
    <style>
        .modal .modal-dialog {
            width: 50%;
        }
        .styleButton {
            float: right;
            height: 31px;
            margin: 10px 0px 0px 5px;
            padding: 0px 22px;
            font: 300 15px/29px "Open Sans", Helvetica, Arial, sans-serif;
            cursor: pointer;
        }
        .alert{
            margin-bottom: 0px;
        }
    </style>
</head>
<body class="">
<jsp:include page="../../fragments/bodyHeader.jsp" />
<jsp:include page="../../fragments/bodyNavigation.jsp" />
<!-- Main bar -->
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
            <li><a href="<spring:url value="/" htmlEscape="true "/>"> <spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <spring:message code="menu.catalogs" /> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/administracion/equipos/list" htmlEscape="true "/>"><spring:message code="menu.equipments" /></a></li>
        </ol>
        <!-- end breadcrumb -->
        <jsp:include page="../../fragments/layoutOptions.jsp" />
    </div>
    <!-- END RIBBON -->
    <!-- Matter -->

    <div id="content">
        <!-- row -->
        <div class="row">
            <!-- col -->
            <div class="col-xs-12 col-sm-7 col-md-7 col-lg-4">
                <h1 class="page-title txt-color-blueDark">
                    <!-- PAGE HEADER -->
                    <i class="fa-fw fa fa-sitemap"></i>
                    <spring:message code="lbl.equipment" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="lbl.equipment.list" />
						</span>
                </h1>
            </div>
            <!-- end col -->
        </div>
        <!-- end row -->
        <section id="widget-grid" class="">

            <!-- Table -->

            <div class="row">
                <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                    <!-- Widget ID (each widget will need unique ID)-->
                    <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
                        <header>
                            <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                            <h2><spring:message code="lbl.results" /> </h2>
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
                                <p class="alert alert-info">
                                    <button id="btnAdd" type="button" class="btn btn-primary">
                                        <i class="fa fa-plus"></i>
                                        <spring:message code="lbl.equipment" />
                                    </button>
                                </p>
                                <input id="blockUI_message" type="hidden" value="<spring:message code="blockUI.message"/>"/>
                                <input id="msgSave" type="hidden" value="<spring:message code="msg.save.equipment"/>"/>
                                <input id="msgSaveTest" type="hidden" value="<spring:message code="msg.save.equipment.test"/>"/>
                                <input id="msgOverride" type="hidden" value="<spring:message code="msg.override.equipment"/>"/>
                                <input id="msgOverrideTest" type="hidden" value="<spring:message code="msg.override.equipment.test"/>"/>
                                <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
                                <input id="confirm_msg_opc_yes" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.yes"/>"/>
                                <input id="confirm_msg_opc_no" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.no"/>"/>
                                <input id="msgConfirmOverride" type="hidden" value="<spring:message code="msg.confirm.override"/>"/>
                                <input id="msgOverrideCanceled" type="hidden" value="<spring:message code="msg.override.canceled"/>"/>
                                <input id="msgConfirmTitle" type="hidden" value="<spring:message code="msg.confirm.title"/>"/>
                                <input id="text_opt_select" type="hidden" value="<spring:message code="lbl.select"/>"/>
                                <table class="table table-striped table-bordered table-hover" width="100%" id="equiposList">
                                    <thead>
                                    <tr>
                                        <th><spring:message code="lbl.name" /></th>
                                        <th><spring:message code="lbl.brand" /></th>
                                        <th><spring:message code="lbl.model" /></th>
                                        <th><spring:message code="lbl.description" /></th>
                                        <th><spring:message code="lbl.enabled" /></th>
                                        <th style="width: 5%" align="center"><spring:message code="lbl.tests" /></th>
                                        <th style="width: 5%" align="center"><spring:message code="act.edit" /></th>
                                        <th style="width: 5%" align="center"><spring:message code="act.override" /></th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                            </div>
                            <!-- end widget div -->
                        </div>
                        <!-- end widget -->
                </article>
                <!-- WIDGET END -->
            </div>

        </section>
        <!-- Matter ends -->
        <!-- Modal equipo-->
        <div class="modal fade" id="modalEquipo" aria-hidden="true" data-backdrop="static">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <div class="alert alert-info">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>
                            <h4 class="modal-title">
                                <i class="fa-fw fa fa-sitemap"></i>
                                <spring:message code="lbl.header.modal.equipment" />
                            </h4>
                        </div>
                    </div>
                    <div class="modal-body"> <!--  no-padding -->
                        <div class="row">
                            <div class="col col-sm-12 col-md-12 col-lg-12">
                                <form id="enterform" class="smart-form" novalidate="novalidate">
                                    <div class="row">
                                        <section class="col col-sm-12 col-md-8 col-lg-12">
                                            <label class="text-left txt-color-blue font-md">
                                                <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.name" />
                                            </label>
                                            <div class="">
                                                <label class="input">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <input class="form-control" type="text" id="nombre"  name="nombre" value="" placeholder=" <spring:message code="lbl.name" />">
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.equipment.name"/>
                                                    </b>
                                                </label>
                                            </div>
                                        </section>
                                        <section class="col col-sm-12 col-md-8 col-lg-12">
                                            <label class="text-left txt-color-blue font-md">
                                                <spring:message code="lbl.brand" />
                                            </label>
                                            <div class="">
                                                <label class="input">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <input class="form-control" type="text" id="marca"  name="marca" value="" placeholder=" <spring:message code="lbl.brand" />">
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.equipment.brand"/>
                                                    </b>
                                                </label>
                                            </div>
                                        </section>
                                        <section class="col col-sm-12 col-md-8 col-lg-12">
                                            <label class="text-left txt-color-blue font-md">
                                                <spring:message code="lbl.model" />
                                            </label>
                                            <div class="">
                                                <label class="input">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <input class="form-control" type="text" id="modelo"  name="modelo" value="" placeholder=" <spring:message code="lbl.model" />">
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.equipment.model"/>
                                                    </b>
                                                </label>
                                            </div>
                                        </section>
                                        <section class="col col-sm-12 col-md-8 col-lg-12">
                                            <label class="text-left txt-color-blue font-md">
                                                <spring:message code="lbl.description" />
                                            </label>
                                            <div class="">
                                                <label class="input">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <input class="form-control" type="text" id="descripcion"  name="descripcion" value="" placeholder=" <spring:message code="lbl.description" />">
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.equipment.description"/>
                                                    </b>
                                                </label>
                                            </div>
                                        </section>
                                    </div>
                                    <div class="row">
                                        <section class="col col-sm-12 col-md-4 col-lg-4">
                                            <label class="text-left txt-color-blue font-md"><spring:message code="lbl.enabled"/></label>
                                            <div class="row">
                                                <div class="col col-4">
                                                    <label class="checkbox">
                                                        <input type="checkbox" name="checkbox-enable" id="checkbox-enable" checked>
                                                        <i></i>
                                                    </label>
                                                </div>
                                            </div>
                                        </section>
                                    </div>
                                    <footer>
                                        <input type="hidden" id="idEquipo" value="">
                                        <button type="submit" class="btn btn-success styleButton" id="btnSave">
                                            <i class="fa fa-save"></i>  <spring:message code="act.save" />
                                        </button>
                                        <button type="button" class="btn btn-danger" data-dismiss="modal">
                                            <i class="fa fa-times"></i>  <spring:message code="act.end" />
                                        </button>
                                    </footer>
                                </form>
                            </div>
                        </div>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->

        <!-- Modal SILAIS-->
        <div class="modal fade" id="modalExamenes" aria-hidden="true" data-backdrop="static">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <div class="alert alert-info">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>

                            <h4 class="modal-title">
                                <i class="fa-fw fa fa-sitemap"></i>
                                <spring:message code="lbl.header.modal.equipment.test"/>

                            </h4>

                        </div>
                    </div>
                    <div class="modal-body"> <!--  no-padding -->
                        <div class="row">
                            <div class="col col-sm-12 col-md-12 col-lg-12">
                                <form id="testForm" class="smart-form" novalidate="novalidate">
                                    <input type="hidden" id="idEquipoDet" value="">
                                    <input id="equipo" type="hidden" value="<spring:message code="lbl.equipment"/>"/>
                                    <div style="padding-left: 15px; padding-bottom: 5px" class="row">
                                        <h4 id="equipmentName" ></h4>
                                    </div>

                                    <div class="row">
                                        <section class="col col-sm-12 col-md-9 col-lg-10">
                                            <label class="text-left txt-color-blue font-md">
                                                <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.test2" />
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
                                            <button type="submit" class="btn btn-success styleButton">
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
                                    <table class="table table-striped table-bordered table-hover" id="testsList">
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
</div>

<!-- Content ends -->
<!-- Footer starts -->
<jsp:include page="../../fragments/footer.jsp" />
<jsp:include page="../../fragments/corePlugins.jsp" />
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
<!-- JQUERY VALIDATE -->
<spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate" />
<script src="${jqueryValidate}"></script>
<spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
    <spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${jQValidationLoc}"></script>
<!-- jQuery Selecte2 Input -->
<spring:url value="/resources/js/plugin/select2/select2.min.js" var="selectPlugin"/>
<script src="${selectPlugin}"></script>
<!-- JQUERY BLOCK UI -->
<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
<script src="${jqueryBlockUi}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/administracion/catalogos/processingEquipment.js" var="equipJs" />
<script src="${equipJs}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:url var="saveUrl" value="/administracion/equipos/save"/>
<c:url var="saveTestUrl" value="/administracion/equipos/saveExamenEquipo"/>
<c:url var="equiposUrl" value="/administracion/equipos/getEquiposProcesamiento"/>
<c:url var="overrideUrl" value="/administracion/equipos/override"/>
<c:url var="overrideTestUrl" value="/administracion/equipos/overrideTest"/>
<c:url var="equipoUrl" value="/administracion/equipos/getEquipoProcesamiento"/>
<c:url var="examenesUrl" value="/administracion/equipos/getExamenesEquipo"/>
<c:url var="examenesDispUrl" value="/administracion/equipos/getExamenesDisponiblesEquipo"/>

<script type="text/javascript" charset="utf-8">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {blockMess : $("#blockUI_message").val(),
            saveUrl : "${saveUrl}",
            equiposUrl : "${equiposUrl}",
            overrideUrl : "${overrideUrl}",
            equipoUrl : "${equipoUrl}",
            examenesUrl : "${examenesUrl}",
            overrideTestUrl : "${overrideTestUrl}",
            examenesDispUrl : "${examenesDispUrl}",
            saveTestUrl : "${saveTestUrl}"
        };

        Equipment.init(parametros);
        $("li.administracion").addClass("open");
        $("li.catalogos").addClass("active");
        $("li.equipos").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.equipos").parents("ul").slideDown(200);
        }
    });
</script>
</body>
</html>