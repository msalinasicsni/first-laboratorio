<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<!-- BEGIN HEAD -->
<head>
    <jsp:include page="../fragments/headTag.jsp" />
    <style>
        .progress{
            width: 100%;
            border-style: solid;
            border-color: darkgreen darkgreen darkgreen darkgreen;
            border-radius: 4px;
            border-width: 1px 1px 1px 1px;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1) inset;
            overflow: hidden;
        }
        .bar {
            height: 100%;
            background: green;
            box-shadow: 0 -1px 0 rgba(0, 0, 0, 0.15) inset;
            float: left;
            transition: width 0.6s ease 0s;
        }
        .styleButton {

            float: left;
            height: 31px;
            margin: 5px 0px 0px 5px;
            padding: 0px 10px;
            font: 300 15px/29px "Open Sans", Helvetica, Arial, sans-serif;
            cursor: pointer;
        }
    </style>
    <spring:url value="/resources/css/jquery.fileupload.css" var="fileuploadCss" />
    <link href="${fileuploadCss}" rel="stylesheet" type="text/css"/>
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/administracion/file/init" htmlEscape="true "/>"><spring:message code="menu.admin.images" /></a></li>
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
            <i class="fa-fw fa fa-picture-o"></i>
            <spring:message code="menu.admin.images" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="lbl.update" />
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
                <h2><spring:message code="lbl.upload.image" /> </h2>
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
                    <input id="msg_wrongformat" type="hidden" value="<spring:message code="lbl.wrong.file.format"/>"/>
                    <input id="msg_wrongsize" type="hidden" value="<spring:message code="lbl.wrong.file.size"/>"/>
                    <input id="smallBox_content" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                    <form id="file-form" class="smart-form" autocomplete="off">
                        <fieldset>
                            <div class="row">
                                <section class="col col-sm-12 col-md-2 col-lg-2">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.imagen.type"/>
                                    </label>

                                    <div class="inline-group">
                                        <label class="radio">
                                            <input type="radio" name="rdTipoImagen" value="header" checked>
                                            <i></i><spring:message code="lbl.header"/></label>
                                        <label class="radio">
                                            <input type="radio" name="rdTipoImagen" value="footer">
                                            <i></i><spring:message code="lbl.footer"/></label>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-4 col-lg-3" id="headerFU">
                                                <span class="btn btn-success fileinput-button styleButton">
                                                    <i class="fa fa-plus"></i>
                                                    <span><spring:message code="lbl.select.image"/></span>
                                                        <input id="fileupload" type="file" name="files[]"
                                                               data-url="uploadheader">
                                                 </span>
                                </section>
                                <section class="col col-sm-12 col-md-6 col-lg-7" id="headerFUP">
                                    <div id="progress" class="progress">
                                        <div class="bar" style="width: 0;"></div>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-2 col-lg-3" id="footerFU" hidden="hidden">
                                                <span class="btn btn-success fileinput-button styleButton">
                                                    <i class="fa fa-plus"></i>
                                                    <span><spring:message code="lbl.select.image"/></span>
                                                        <input id="fileupload2" type="file" name="files[]"
                                                               data-url="uploadfooter">
                                                 </span>
                                </section>
                                <section class="col col-sm-12 col-md-6 col-lg-7" id="footerFUP" hidden="hidden">
                                    <div id="progress2" class="progress">
                                        <div class="bar" style="width: 0;"></div>
                                    </div>
                                </section>
                            </div>
                        </fieldset>
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


        <div class="jarviswidget jarviswidget-color-darken" id="wid-id-2">
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
                     <table id="uploaded-files" class="table table-striped table-bordered table-hover" width="100%">
                        <thead>
                        <tr>
                            <th><spring:message code="lbl.name"/></th>
                            <th><spring:message code="lbl.description"/></th>
                            <th align="center"><spring:message code="act.download"/></th>
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
<!-- JQUERY BLOCK UI -->
<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
<script src="${jqueryBlockUi}"></script>
<!-- JQUERY FILE UPLOAD -->
<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
<script src="https://blueimp.github.io/JavaScript-Load-Image/js/load-image.all.min.js"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality -->
<script src="https://blueimp.github.io/JavaScript-Canvas-to-Blob/js/canvas-to-blob.min.js"></script>
<spring:url value="/resources/js/plugin/jQuery-File-Upload/jquery.iframe-transport.js" var="jqueryIframeTrans" />
<script src="${jqueryIframeTrans}"></script>
<spring:url value="/resources/js/plugin/jQuery-File-Upload/jquery.fileupload.js" var="jqueryFileupload" />
<script src="${jqueryFileupload}"></script>
<spring:url value="/resources/js/plugin/jQuery-File-Upload/jquery.fileupload-process.js" var="jqueryFileuploadProc" />
<script src="${jqueryFileuploadProc}"></script>
<spring:url value="/resources/js/plugin/jQuery-File-Upload/jquery.fileupload-image.js" var="jqueryFileuploadImage" />
<script src="${jqueryFileuploadImage}"></script>
<spring:url value="/resources/js/plugin/jQuery-File-Upload/jquery.fileupload-validate.js" var="jqueryFileuploadValidate" />
<script src="${jqueryFileuploadValidate}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/administracion/fileUpload.js" var="fileUpload" />
<script src="${fileUpload}"></script>
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="filesUrl" value="/administracion/file/getAll"/>
<c:url var="getFileUrl" value="/administracion/file/get/"/>

<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {blockMess: "${blockMess}",
            filesUrl: "${filesUrl}",
            getFileUrl: "${getFileUrl}"
        };
        Files.init(parametros);
        $("li.administracion").addClass("open");
        $("li.reportImages").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.reportImages").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>