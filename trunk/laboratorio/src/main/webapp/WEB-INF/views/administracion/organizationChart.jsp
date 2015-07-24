<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 07-21-15
  Time: 08:59 AM
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
            <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/administracion/organizationChart/init" htmlEscape="true "/>"><spring:message code="lbl.organizationchart" /></a></li>
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
            <div class="col-xs-12 col-sm-7 col-md-7 col-lg-7">
                <h1 class="page-title txt-color-blueDark">
                    <!-- PAGE HEADER -->
                    <i class="fa-fw fa fa-link"></i>
                    <spring:message code="lbl.organizationchart" />

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
                            <h2><spring:message code="lbl.laboratories" /> </h2>
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
                                <input id="succesfulOverride" type="hidden" value="<spring:message code="msg.successfully.manag.override"/>"/>
                                <input id="yes" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.yes"/>"/>
                                <input id="no" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.no"/>"/>
                                <input id="confirmation" type="hidden" value="<spring:message code="msg.confirm.title"/>"/>
                                <input id="confirm_content" type="hidden" value="<spring:message code="msg.management.override.conf.content"/>"/>
                                <input id="override_cancel" type="hidden" value="<spring:message code="msg.management.override.cancel"/>"/>
                                <input id="msjSuccManagment" type="hidden" value="<spring:message code="msg.associate.managment.added"/>"/>
                                <input id="msjSuccDep" type="hidden" value="<spring:message code="msg.associate.dep.added"/>"/>
                                <input id="confirm_content_dep" type="hidden" value="<spring:message code="msg.dep.override.conf.content"/>"/>
                                <input id="succesfulDepOverride" type="hidden" value="<spring:message code="msg.successfully.dep.override"/>"/>
                                <input id="depOverride_cancel" type="hidden" value="<spring:message code="msg.dep.override.cancel"/>"/>
                                <input id="msjSuccArea" type="hidden" value="<spring:message code="msg.associate.area.added"/>"/>
                                <input id="confirm_content_area" type="hidden" value="<spring:message code="msg.area.override.conf.content"/>"/>
                                <input id="areaOverride_cancel" type="hidden" value="<spring:message code="msg.area.override.cancel"/>"/>
                                <input id="succesfulAreaOverride" type="hidden" value="<spring:message code="msg.successfully.area.override"/>"/>


                                <table  id="labs-records" class="table table-striped table-bordered table-hover" width="100%">
                                    <thead>
                                    <tr>
                                        <th data-class="expand" width="15%"><spring:message code="lbl.code"/></th>
                                        <th width="25%" data-hide="phone"><spring:message code="lbl.name"/></th>
                                        <th width="25%"><spring:message code="lbl.associatedManagement"/></th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                            <!-- end widget content -->
                        </div>
                        <!-- end widget div -->
                    </div>


                    <div hidden="hidden" class="jarviswidget jarviswidget-color-darken" id="div2">
                        <header>
                            <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                            <h2><i class="fa fa-angle-right"></i> </h2>
                            <h2 id="managementName" ></h2>
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

                                <table  id="management-records" class="table table-striped table-bordered table-hover" width="100%">
                                    <thead>
                                    <tr>
                                        <th data-class="expand" style="width: 25%"><spring:message code="lbl.management"/></th>
                                        <th style="width: 25%"><spring:message code="lbl.department.associate"/></th>
                                        <th style="width: 25%"><spring:message code="act.override"/></th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                            <!-- end widget content -->
                        </div>
                        <!-- end widget div -->

                        <div hidden="hidden" id="dBack1" class="row" style="border: none">
                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                <div style="border: none" class="pull-left">
                                    <button type="button" id="btnBack1" class="btn btn-primary"><i class="fa fa-arrow-left"></i> <spring:message code="lbl.back" /></button>
                                </div>
                            </section>

                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                <div style="border: none" class="pull-right">
                                    <button type="button" id="btnAddManagement" class="btn btn-primary"><i class="fa fa-link"></i> <spring:message code="lbl.associate.management" /></button>
                                </div>

                            </section>

                        </div>

                    </div>


                    <div hidden="hidden" class="jarviswidget jarviswidget-color-darken" id="div3">
                        <header>
                            <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                            <h2 id="lab" ></h2>
                            <h2><i class="fa fa-angle-right"></i> </h2>
                            <h2 id="managementLab" ></h2>
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

                                <table  id="depart-manag-lab" class="table table-striped table-bordered table-hover" width="100%">
                                    <thead>
                                    <tr>
                                        <th data-class="expand" style="width: 25%"><spring:message code="lbl.department"/></th>
                                        <th style="width: 25%"><spring:message code="lbl.associated.area"/></th>
                                        <th width="25%"><spring:message code="act.override"/></th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                            <!-- end widget content -->
                        </div>
                        <!-- end widget div -->

                        <div hidden="hidden" id="dBack2" class="row" style="border: none">
                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                <div style="border: none" class="pull-left">
                                    <button type="button" id="btnBack2" class="btn btn-primary"><i class="fa fa-arrow-left"></i> <spring:message code="lbl.back" /></button>
                                </div>
                            </section>

                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                <div style="border: none" class="pull-right">
                                    <button type="button" id="btnAddDepartment" class="btn btn-primary"><i class="fa fa-link"></i> <spring:message code="lbl.associate.department" /></button>
                                </div>

                            </section>

                        </div>

                    </div>
                    <!-- end widget -->

                    <div hidden="hidden" class="jarviswidget jarviswidget-color-darken" id="div4">
                        <header>
                            <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>

                            <h2 id="labo" ></h2>
                            <h2><i class="fa fa-angle-right"></i></h2>
                            <h2 id="dir" ></h2>
                            <h2><i class="fa fa-angle-right"></i> </h2>
                            <h2 id="dep" ></h2>
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

                                <table  id="areas-records" class="table table-striped table-bordered table-hover" width="100%">
                                    <thead>
                                    <tr>
                                        <th data-class="expand" style="width: 25%"><spring:message code="lbl.area"/></th>
                                        <th width="25%"><spring:message code="act.override"/></th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                            <!-- end widget content -->
                        </div>
                        <!-- end widget div -->

                        <div hidden="hidden" id="dBack3" class="row" style="border: none">
                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                <div style="border: none" class="pull-left">
                                    <button type="button" id="btnBack3" class="btn btn-primary"><i class="fa fa-arrow-left"></i> <spring:message code="lbl.back" /></button>
                                </div>
                            </section>

                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                <div style="border: none" class="pull-right">
                                    <button type="button" id="btnAddArea" class="btn btn-primary"><i class="fa fa-link"></i> <spring:message code="lbl.associate.area" /></button>
                                </div>

                            </section>

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
                    <!-- Modal -->
                    <div class="modal fade" id="myModal1" aria-hidden="true" data-backdrop="static">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <div class="alert alert-info">
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                            &times;
                                        </button>
                                        <h4 class="modal-title">
                                            <i class="fa-fw fa fa-list-ul"></i>
                                            <spring:message code="lbl.associate.management"/>
                                        </h4>
                                    </div>
                                </div>

                                <div class="modal-body">
                                    <form id="ass-managment-form" class="smart-form" autocomplete="off">
                                        <div class="row">
                                            <input id="idLab" hidden="hidden" type="text" name="idLab"/>
                                            <section class="col col-sm-12 col-md-8 col-lg-8">
                                                <label class="text-left txt-color-blue font-md">
                                                    <spring:message code="lbl.managements" /> </label>
                                                <div class="input-group">
                                                    <span class="input-group-addon"><i class="fa fa-list fa-fw"></i></span>
                                                    <select id="management" name="management"
                                                            class="select2">
                                                        <option value=""><spring:message code="lbl.select" />...</option>
                                                        <c:forEach items="${managmentList}" var="manag">
                                                            <option value="${manag.idDireccion}">${manag.nombre}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </section>

                                        </div>
                                    </form>
                                </div>

                                <div class="modal-footer">
                                    <button type="button" class="btn btn-danger" data-dismiss="modal"><i class="fa fa-times"></i>
                                        <spring:message code="act.end" />
                                    </button>
                                    <button type="submit" id="btnSave1" class="btn btn-success"><i class="fa fa-save"></i> <spring:message code="act.save"/></button>

                                </div>
                            </div>
                            <!-- /.modal-content -->
                        </div>
                        <!-- /.modal-dialog -->
                    </div>
                    <!-- /.modal -->


                    <!-- Modal -->
                    <div class="modal fade" id="myModal2" aria-hidden="true" data-backdrop="static">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <div class="alert alert-info">
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                            &times;
                                        </button>
                                        <h4 class="modal-title">
                                            <i class="fa-fw fa fa-list-ul"></i>
                                            <spring:message code="lbl.associate.department"/>
                                        </h4>
                                    </div>
                                </div>

                                <div class="modal-body">
                                    <form id="department-form" class="smart-form" autocomplete="off">
                                        <div class="row">
                                            <input id="idManagLab" hidden="hidden" type="text" name="idManagLab"/>
                                            <section class="col col-sm-12 col-md-8 col-lg-8">
                                                <label class="text-left txt-color-blue font-md">
                                                    <spring:message code="lbl.departments" /> </label>
                                                <div class="input-group">
                                                    <span class="input-group-addon"><i class="fa fa-list fa-fw"></i></span>
                                                    <select id="department" name="department"
                                                            class="select2">
                                                        <option value=""><spring:message code="lbl.select" />...</option>
                                                        <c:forEach items="${departmentList}" var="depa">
                                                            <option value="${depa.idDepartamento}">${depa.nombre}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </section>

                                        </div>
                                    </form>
                                </div>

                                <div class="modal-footer">
                                    <button type="button" class="btn btn-danger" data-dismiss="modal"><i class="fa fa-times"></i>
                                        <spring:message code="act.end" />
                                    </button>
                                    <button type="submit" id="btnSave2" class="btn btn-success"><i class="fa fa-save"></i> <spring:message code="act.save"/></button>

                                </div>
                            </div>
                            <!-- /.modal-content -->
                        </div>
                        <!-- /.modal-dialog -->
                    </div>
                    <!-- /.modal -->

                    <!-- Modal -->
                    <div class="modal fade" id="myModal3" aria-hidden="true" data-backdrop="static">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <div class="alert alert-info">
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                            &times;
                                        </button>
                                        <h4 class="modal-title">
                                            <i class="fa-fw fa fa-list-ul"></i>
                                            <spring:message code="lbl.associate.area"/>
                                        </h4>
                                    </div>
                                </div>

                                <div class="modal-body">
                                    <form id="areas-form" class="smart-form" autocomplete="off">
                                        <div class="row">
                                            <input id="idDepManag" hidden="hidden" type="text" name="idDepManag"/>
                                            <section class="col col-sm-12 col-md-8 col-lg-8">
                                                <label class="text-left txt-color-blue font-md">
                                                    <spring:message code="lbl.areas" /> </label>
                                                <div class="input-group">
                                                    <span class="input-group-addon"><i class="fa fa-list fa-fw"></i></span>
                                                    <select id="area" name="area"
                                                            class="select2">
                                                        <option value=""><spring:message code="lbl.select" />...</option>
                                                        <c:forEach items="${areasList}" var="area">
                                                            <option value="${area.idArea}">${area.nombre}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </section>

                                        </div>
                                    </form>
                                </div>

                                <div class="modal-footer">
                                    <button type="button" class="btn btn-danger" data-dismiss="modal"><i class="fa fa-times"></i>
                                        <spring:message code="act.end" />
                                    </button>
                                    <button type="submit" id="btnSave3" class="btn btn-success"><i class="fa fa-save"></i> <spring:message code="act.save"/></button>

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
<spring:url value="/resources/scripts/administracion/organizationChart.js" var="organizationChart" />
<script src="${organizationChart}"></script>
<spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
<script src="${handleDatePickers}"></script>
<spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
<script src="${handleInputMask}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="getLabs" value="/administracion/organizationChart/getLabs"/>
<c:url var="getManagement" value="/administracion/organizationChart/getManagementAssociated"/>
<c:url var="saveManagment" value="/administracion/organizationChart/addUpdateManagment"/>
<c:url var="getDepartments" value="/administracion/organizationChart/getDepartmentAssociated"/>
<c:url var="saveDepartment" value="/administracion/organizationChart/addUpdateDepartment"/>
<c:url var="getAreas" value="/administracion/organizationChart/getAreaDep"/>
<c:url var="saveArea" value="/administracion/organizationChart/addUpdateArea"/>



<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {blockMess: "${blockMess}",
            labsUrl : "${getLabs}",
            managementUrl: "${getManagement}",
            saveManagmentUrl:"${saveManagment}",
            departmentsUrl: "${getDepartments}",
            saveDepartmentUrl: "${saveDepartment}",
            areasUrl: "${getAreas}",
            saveAreaUrl: "${saveArea}"



        };
        OrganizationChart.init(parametros);

        handleDatePickers("${pageContext.request.locale.language}");
        handleInputMasks();
        $("li.administracion").addClass("open");
        $("li.organizationChart").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.organizationChart").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
