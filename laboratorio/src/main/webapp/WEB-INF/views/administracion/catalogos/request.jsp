<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 07-10-15
  Time: 09:00 AM
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
    <jsp:include page="../../fragments/headTag.jsp" />
    <style>
        textarea {
            resize: none;
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
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="">
<!-- #HEADER -->
<jsp:include page="../../fragments/bodyHeader.jsp" />
<!-- #NAVIGATION -->
<jsp:include page="../../fragments/bodyNavigation.jsp" />
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/administracion/request/init" htmlEscape="true "/>"><spring:message code="lbl.dx.large" /></a></li>
    </ol>
    <!-- end breadcrumb -->
    <jsp:include page="../../fragments/layoutOptions.jsp" />
</div>
<!-- END RIBBON -->
<!-- MAIN CONTENT -->
<div id="content">
<!-- row -->
<div class="row">
    <!-- col -->
    <div class="col-xs-12 col-sm-5 col-md-5 col-lg-3">
        <h1 class="page-title txt-color-blueDark">
            <!-- PAGE HEADER -->
            <i class="fa-fw fa fa-file-text-o"></i>
            <spring:message code="lbl.dx.large"/>
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="lbl.add.edit"/>
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

        <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
            <header>
                <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                <h2><spring:message code="lbl.records" /> </h2>
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

                    <p class="alert alert-info" >

                        <button type="submit" id="btnAdd" class="btn btn-primary"><i class="fa fa-plus"></i> <spring:message code="lbl.dxs.large" /></button>
                    </p>

                    <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
                    <input id="msjSucc" type="hidden" value="<spring:message code="msg.request.added"/>"/>
                    <input id="yes" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.yes"/>"/>
                    <input id="no" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.no"/>"/>
                    <input id="confirmation" type="hidden" value="<spring:message code="msg.confirm.title"/>"/>
                    <input id="confirm_c" type="hidden" value="<spring:message code="msg.overrideReq.confirm.content"/>"/>
                    <input id="succOverride" type="hidden" value="<spring:message code="msg.request.successfully.cancel"/>"/>
                    <input id="cancel" type="hidden" value="<spring:message code="msg.override.request.cancel"/>"/>



                    <table id="records" class="table table-striped table-bordered table-hover" width="100%">
                        <thead>
                        <tr>
                            <th data-class="expand" width="25%"><spring:message code="lbl.name"/></th>
                            <th data-hide="phone"><spring:message code="lbl.solic.type"/></th>
                            <th data-hide="phone"><spring:message code="lbl.area"/></th>
                            <th width="10%"><spring:message code="lbl.enabled"/></th>
                            <th width="10%"><spring:message code="act.edit"/></th>
                            <th width="10%"><spring:message code="act.override"/></th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <!-- end widget content -->
            </div>
            <!-- end widget div -->
        </div>
        <!-- end widget -->
    </article>
    <!-- WIDGET END -->
</div>
<!-- end row -->
<!-- row -->
<div class="row">
    <!-- a blank row to get started -->
    <div class="col-sm-12">
        <!-- your contents here -->
        <!-- Modal Aliquot -->
        <div class="modal fade" id="myModal" aria-hidden="true" data-backdrop="static">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <div class="alert alert-info">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                &times;
                            </button>
                            <h4 class="modal-title">
                                <i class="fa-fw fa fa-list-ul"></i>
                                <spring:message code="lbl.add.edit.request"/>
                            </h4>
                        </div>
                    </div>

                    <div class="modal-body">
                        <form id="request-form" class="smart-form" autocomplete="off">

                                <input hidden="hidden" id="id" type="text" name="id"/>


                                    <div class="row">
                                        <section class="col col-sm-12 col-md-8 col-lg-7">
                                            <label class="text-left txt-color-blue font-md">
                                                <spring:message code="lbl.name"/>
                                            </label>
                                        <label class="input">
                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i
                                                class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                            <input class="form-control" type="text" name="nombre" id="nombre"
                                                   placeholder=" <spring:message code="lbl.name" />"/>
                                            <b class="tooltip tooltip-bottom-right"> <i
                                                    class="fa fa-warning txt-color-pink"></i> <spring:message
                                                    code="tooltip.enter.sample.type.name"/>
                                            </b>
                                        </label>
                                        </section>

                                        <section class="col col-sm-6 col-md-4 col-lg-5">
                                            <label class="text-left txt-color-blue font-md">
                                                <spring:message code="lbl.solic.type" /> </label>
                                            <div class="input-group">
                                                <span class="input-group-addon"><i class="fa fa-list fa-fw"></i></span>
                                                <select id="tipo" name="tipo"
                                                        class="select2">
                                                    <option value=""><spring:message code="lbl.select" />...</option>
                                                    <option value="Estudio"><spring:message code="lbl.study" /></option>
                                                    <option value="Rutina"><spring:message code="lbl.routine" /></option>
                                                </select>
                                            </div>
                                        </section>
                                    </div>

                                        <div class="row">


                                            <section class="col col-sm-12 col-md-8 col-lg-7">
                                            <label class="text-left txt-color-blue font-md">
                                                <spring:message code="lbl.area" /> </label>
                                            <div class="input-group">
                                                <span class="input-group-addon"><i class="fa fa-list fa-fw"></i></span>
                                                <select id="area" name="area"
                                                        class="select2">
                                                    <option value=""><spring:message code="lbl.select" />...</option>
                                                    <c:forEach items="${areaList}" var="area">
                                                        <option value="${area.idArea}">${area.nombre}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </section>

                                            <section id="sPriority" hidden="hidden" class="col col-sm-6 col-md-4 col-lg-5">
                                                <label class="text-left txt-color-blue font-md">
                                                    <spring:message code="lbl.priority"/>
                                                </label>
                                                <label class="input">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i
                                                        class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                                    <input class="form-control" type="number" name="prioridad" id="prioridad"
                                                           placeholder=" <spring:message code="lbl.priority" />"/>
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message
                                                            code="tooltip.enter.priority"/>
                                                    </b>
                                                </label>
                                            </section>

                                            <section id="sCodigo" hidden="hidden" class="col col-sm-6 col-md-4 col-lg-5">
                                                <label class="text-left txt-color-blue font-md">
                                                    <spring:message code="lbl.code"/>
                                                </label>
                                                <label class="input">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i
                                                        class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                                    <input class="form-control" type="text" name="codigo" id="codigo"
                                                           placeholder=" <spring:message code="lbl.code" />"/>
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message
                                                            code="tooltip.enter.code"/>
                                                    </b>
                                                </label>
                                            </section>


                                    </div>

                            <div class="row">
                                <section class="col col-sm-12 col-md-4 col-lg-4">
                                    <label class="text-left txt-color-blue font-md"><spring:message code="lbl.enabled"/></label>
                                    <div class="row">
                                        <div class="col col-4">
                                            <label class="checkbox">
                                                <input type="checkbox" name="checkbox" id="checkbox" checked>
                                                <i></i>
                                            </label>
                                        </div>
                                    </div>
                                </section>
                            </div>



                        </form>
                    </div>

                    <div class="modal-footer">

                        <button type="button" class="btn btn-danger" data-dismiss="modal">
                            <i class="fa fa-times"></i>
                            <spring:message code="act.end" />
                        </button>
                        <button type="submit" id="btnSave" class="btn btn-success"><i class="fa fa-save"></i> <spring:message code="act.save" /></button>

                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->

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
<jsp:include page="../../fragments/footer.jsp" />
<!-- END FOOTER -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
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
<spring:url value="/resources/js/plugin/datatables/swf/copy_csv_xls_pdf.swf" var="tabletools" />
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
<!-- JQUERY INPUT MASK -->
<spring:url value="/resources/js/plugin/jquery-inputmask/jquery.inputmask.bundle.min.js" var="jqueryInputMask" />
<script src="${jqueryInputMask}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/administracion/catalogos/request.js" var="requestJS" />
<script src="${requestJS}"></script>
<spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
<script src="${handleDatePickers}"></script>
<spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
<script src="${handleInputMask}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="catalogueUrl" value="/administracion/request/getRoutinesAndStudies"/>
<c:url var="addUpdateUrl" value="/administracion/request/addUpdateRequest"/>
<c:url var="getRequestById" value="/administracion/request/getRequestById"/>


<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {blockMess: "${blockMess}",
            catalogueUrl : "${catalogueUrl}",
            addUpdateUrl: "${addUpdateUrl}",
            getRequestUrl: "${getRequestById}"



        };
        Request.init(parametros);

        handleDatePickers("${pageContext.request.locale.language}");
        handleInputMasks();
        $("li.administracion").addClass("open");
        $("li.catalogos").addClass("active");
        $("li.request").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.request").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
