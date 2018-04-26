<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 07-02-15
  Time: 01:20 AM
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
    <jsp:include page="../fragments/headTag.jsp" />
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/administracion/testsRequest/init" htmlEscape="true "/>"><spring:message code="lbl.tests.Request" /></a></li>
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
            <i class="fa-fw fa fa-link"></i>
            <spring:message code="lbl.tests.Request" />

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


        <div class="jarviswidget jarviswidget-color-darken" id="div1">
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


                    <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
                    <input id="msjSuccessful" type="hidden" value="<spring:message code="msg.sampleTypes.added"/>"/>
                    <input id="msg_conc_cancel" type="hidden" value="<spring:message code="msg.sampleTypes.successfully.cancel"/>"/>
                    <input id="succ" type="hidden" value="<spring:message code="msg.associate.test.added"/>"/>
                    <input id="msg_yes" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.yes"/>"/>
                    <input id="msg_no" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.no"/>"/>
                    <input id="msg_conf" type="hidden" value="<spring:message code="msg.confirm.title"/>"/>
                    <input id="msg_overrideT_confirm_c" type="hidden" value="<spring:message code="msg.overrideT.confirm.content"/>"/>
                    <input id="msg_activate_confirm_c" type="hidden" value="<spring:message code="msg.set.default.confirm.content"/>"/>
                    <input id="msg_deactivate_confirm_c" type="hidden" value="<spring:message code="msg.not.set.default.confirm.content"/>"/>
                    <input id="msg_succOverrideT" type="hidden" value="<spring:message code="msg.successfully.overrideT"/>"/>
                    <input id="msg_overrideT_cancel" type="hidden" value="<spring:message code="msg.override.test.cancel"/>"/>
                    <input id="msg_action_canceled" type="hidden" value="<spring:message code="msg.action.canceled"/>"/>
                    <input id="msg_action_successfull" type="hidden" value="<spring:message code="msg.test.request.successfully.edited"/>"/>

                    <table id="records" class="table table-striped table-bordered table-hover" width="100%">
                        <thead>
                        <tr>
                            <th data-class="expand" width="40%"><i class="fa fa-fw fa-sort-numeric-asc text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.name"/></th>
                            <th data-hide="phone" width="20%"><i class="fa fa-fw fa-sort-alpha-asc text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.solic.type"/></th>
                            <th data-hide="phone" width="25%"><i class="fa fa-fw fa-sort-alpha-asc text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.area"/></th>
                            <th width="15%"><spring:message code="lbl.tests"/></th>

                        </tr>
                        </thead>
                    </table>

                </div>


                <!-- end widget content -->
            </div>
            <!-- end widget div -->
        </div>
        <!-- end widget -->

        <div hidden="hidden" class="jarviswidget jarviswidget-color-darken" id="div2">
            <header>
                <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                <h2><spring:message code="lbl.associated.tests" /> </h2>
                <h2 id="sol" ></h2>
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

                    <table id="tests" class="table table-striped table-bordered table-hover" width="75%">
                        <thead>
                        <tr>
                            <th data-class="expand" width="70%"><i class="fa fa-fw fa-sort-numeric-asc text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.receipt.test"/></th>
                            <th  width="15%"><spring:message code="smart.default"/></th>
                            <th  width="15%"><spring:message code="lbl.override"/></th>
                        </tr>
                        </thead>
                    </table>
                </div>


                <!-- end widget content -->
            </div>

            <div style="border: none" class="row">
                <section class="col col-sm-12 col-md-6 col-lg-6">
                    <div style="border: none" id="dBack" class="pull-left">
                        <button type="button" id="btnBack" class="btn btn-primary"><i class="fa fa-arrow-left"></i>
                            <spring:message code="lbl.back"/></button>
                    </div>
                </section>

                <section class="col col-sm-12 col-md-6 col-lg-6">
                    <div style="border: none" id="dAdd" class="pull-right">
                        <button type="button" id="btnAddExa" class="btn btn-primary"><i class="fa fa-link"></i>
                            <spring:message code="lbl.associate.test"/></button>
                    </div>

                </section>
            </div>
            <!-- end widget div -->
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
                                <spring:message code="lbl.associate.test"/>
                            </h4>
                        </div>
                    </div>

                    <div class="modal-body">
                        <form id="form" class="smart-form" autocomplete="off">
                            <div class="row">
                                <input id="idSolicitud" hidden="hidden" type="text" name="idSolicitud"/>
                                <input id="tipo" hidden="hidden" type="text" name="tipo"/>
                                <input id="opt_select" type="hidden" value="<spring:message code="lbl.select"/>"/>


                                <section class="col col-sm-12 col-md-8 col-lg-8">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.test.name" /> </label>
                                    <div class="input-group">
                                        <span class="input-group-addon"><i class="fa fa-list fa-fw"></i></span>
                                        <select id="examen" name="examen"
                                                class="select2">
                                            <option value=""><spring:message code="lbl.select" />...</option>
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-6 col-md-4 col-lg-4">
                                    <label class="text-left txt-color-blue font-md"><spring:message code="smart.default"/></label>
                                    <div class="row">
                                        <div class="col col-4">
                                            <label class="checkbox">
                                                <input type="checkbox" name="chk_defecto" id="chk_defecto">
                                                <i></i></label>
                                        </div>
                                    </div>
                                </section>
                            </div>

                        </form>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-danger" data-dismiss="modal">
                            <i class="fa fa-times"></i> <spring:message code="act.end" />
                        </button>
                        <button type="submit" id="btnAddTest" class="btn btn-success"><i class="fa fa-save"></i> <spring:message code="act.save" /></button>

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
<spring:url value="/resources/scripts/administracion/testsRequest.js" var="testsRequestJS" />
<script src="${testsRequestJS}"></script>
<spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
<script src="${handleDatePickers}"></script>
<spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
<script src="${handleInputMask}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="getCatalogue" value="/administracion/testsRequest/getRoutinesAndStudies"/>
<c:url var="getTests" value="/administracion/testsRequest/getAssociatedTest"/>
<c:url var="getTestsByArea" value="/administracion/testsRequest/testsByArea"/>
<c:url var="saveTest" value="/administracion/testsRequest/addUpdateTest"/>



<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {blockMess: "${blockMess}",
            catalogueUrl : "${getCatalogue}",
            testsUrl: "${getTests}",
            testsAreaUrl: "${getTestsByArea}",
            saveTestUrl: "${saveTest}"

        };
        TestsRequest.init(parametros);

        handleDatePickers("${pageContext.request.locale.language}");
        handleInputMasks();
        $("li.administracion").addClass("open");
        $("li.testsRequest").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.testsRequest").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
