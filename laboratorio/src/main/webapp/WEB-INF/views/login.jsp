<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>

<head>
    <title><fmt:message key="loginpage" /></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">
    <style>
        .errorblock {
            color: #ff0000;
            background-color: #ffEEEE;
            border: 3px solid #ff0000;
            padding: 8px;
            margin: 16px;
            width: 100%;
        }
    </style>
    <!-- Stylesheets -->
    <!-- BEGIN GLOBAL MANDATORY STYLES -->
    <spring:url value="/resources/css/bootstrap.min.css" var="bootstrapCss" />
    <link href="${bootstrapCss}" rel="stylesheet" type="text/css"/>
    <spring:url value="/resources/css/font-awesome.min.css" var="fontawesomeCss" />
    <link href="${fontawesomeCss}" rel="stylesheet" type="text/css"/>
    <!-- END GLOBAL MANDATORY STYLES -->
    <!-- BEGIN THEME STYLES -->
    <spring:url value="/resources/css/smartadmin-production.min.css" var="smartadminProdCss" />
    <link href="${smartadminProdCss}" rel="stylesheet" type="text/css"/>
    <spring:url value="/resources/css/smartadmin-skins.min.css" var="smartadminSkinCss" />
    <link href="${smartadminSkinCss}" rel="stylesheet" type="text/css"/>
    <spring:url value="/resources/css/layout.min.css" var="layoutCss" />
    <link href="${layoutCss}" rel="stylesheet" type="text/css"/>
    <!-- END THEME STYLES -->
    <!-- FAVICONS -->
    <spring:url value="/resources/img/favicon/alerta.ico" var="favicon" />
    <link rel="shortcut icon" href="${favicon}" type="image/x-icon"/>
    <!-- END FAVICONS -->
    <!-- GOOGLE FONT -->
    <spring:url value="/resources/css/googlefonts.css" var="googleFontsCss" />
    <link href="${googleFontsCss}" rel="stylesheet" type="text/css"/>
</head>

<body>
<header id="header" >
    <div id="logo-group">
        <!-- PLACE YOUR LOGO HERE -->
        <spring:url value="/resources/img/logo.png" var="logo" />
        <span id="logo"> <img src="${logo}" alt="<spring:message code="heading" />"> </span>
        <!-- END LOGO PLACEHOLDER -->
    </div>
    <!-- pulled right: nav area -->
   <%-- <div class="pull-right">

        <!-- collapse menu button -->
        <!-- end logout button -->
        <!-- fullscreen button -->
        <div id="fullscreen" class="btn-header transparent pull-right">

        </div>
        <!-- end fullscreen button -->
    </div>--%>
    <!-- end pulled right: nav area -->
</header>
<!-- Form area -->
<div class="admin-form">
    <div class="container">

        <div style="height: 100px">


            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-block">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    <h4 class="alert-heading"> <fmt:message key="lbl.error" /></h4>
                    <fmt:message key="login.msg.failure" />
                    <br>
                </div>
            </c:if>

        </div>
        <div class="row">

            <div class="col-xs-12 col-sm-6 col-md-6 col-lg-8">
           <div class="row">

                        <div class="col-xs-12 col-lg-5 hidden-xs hidden-sm hidden-md" >
                            <h4 class="paragraph-header"><spring:message code="lab.summary" /></h4>

                        </div>

                        <div class="col-xs-12 col-sm-12 col-md-12 col-lg-7" >
                            <spring:url value="/resources/img/lab.png" var="lab" />
                            <img src="${lab}" alt="" style="width:350px; height:290px; ">
                        </div>
                    </div>


                </div>
            <div class="col-xs-12 col-sm-6 col-md-6 col-lg-4">
                <!-- Widget starts -->
                <div class="widget worange">
                    <!-- Widget head -->
                    <div class="widget-head">
                    </div>

                    <div class="widget-content" >
                        <div class="well no-padding">
                            <!-- Login form -->
                            <form class="smart-form"
                                  action="<c:url value='j_spring_security_check' />"
                                  method='POST'>
                                <header>
                                    <i class="icon-lock"></i>
                                    <fmt:message key="login.title"/>
                                </header>
                                <fieldset>
                                    <section>
                                        <label class="label"><fmt:message key="login.username"/></label>
                                        <label class="input"> <i class="icon-append fa fa-user"></i>
                                            <input type="text" class="form-control" id="username"
                                                   name='j_username' value=''
                                                   placeholder="<fmt:message key="login.username"/>">
                                            <b class="tooltip tooltip-top-right"><i class="fa fa-user txt-color-teal"></i><fmt:message key="login.username.tooltip"/> </b></label>
                                    </section>
                                    <section>
                                        <label class="label"><fmt:message key="login.password"/></label>
                                        <label class="input"> <i class="icon-append fa fa-lock"></i>
                                            <input type="password" class="form-control" name='j_password'
                                                   placeholder="<fmt:message key="login.password"/>">
                                            <b class="tooltip tooltip-top-right"><i class="fa fa-lock txt-color-teal"></i><fmt:message key="login.password.tooltip"/> </b> </label>
                                        <!--<div class="note">
                                            <a href="forgotpassword.html">Forgot password?</a>
                                        </div>-->
                                    </section>
                                </fieldset>
                                <footer>
                                    <button name="submit" type="submit" class="btn btn-primary">
                                        <fmt:message key="button.login"/>
                                    </button>
                                    <button name="reset" type="reset" class="btn btn-default">
                                        <fmt:message key="button.Reset"/>
                                    </button>
                                </footer>
                                <br />
                            </form>

                        </div>
                    </div>
                </div>
            </div>

        </div>

    </div>
</div>



<div class="page-footer" style="background-color: #e2e2e2; height: 60px; text-align: center; padding-top: 10px; padding-left: 0; ">

            <!-- PLACE YOUR LOGO HERE -->
            <spring:url value="/resources/img/logo_ssi.png" var="logoSSI" />
            <img src="${logoSSI}" style="width:200px; height: 40px;">

</div>




<!-- JS -->
<!-- jQuery -->
<spring:url value="/resources/js/libs/jquery-2.1.1.min.js" var="jQuery" />
<script src="${jQuery}" type="text/javascript"></script>
<!-- jQuery UI-->
<spring:url value="/resources/js/libs/jquery-ui-1.10.3.min.js" var="jQueryUI" />
<script src="${jQueryUI}" type="text/javascript"></script>
<!-- IMPORTANT: APP CONFIG -->
<spring:url value="/resources/js/app.config.js" var="appConfigJs" />
<script src="${appConfigJs}"></script>
<!-- JS TOUCH : include this plugin for mobile drag / drop touch events-->
<spring:url value="/resources/js/plugin/jquery-touch/jquery.ui.touch-punch.min.js" var="jsTouchPlugin" />
<script src="${jsTouchPlugin}"></script>
<!-- Bootstrap-->
<spring:url value="/resources/js/bootstrap/bootstrap.min.js" var="Bootstrap" />
<script src="${Bootstrap}" type="text/javascript"></script>
</body>
</html>