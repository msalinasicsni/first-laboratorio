<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!-- BEGIN CORE PLUGINS -->
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
<!-- CUSTOM NOTIFICATION -->
<spring:url value="/resources/js/notification/SmartNotification.min.js" var="smartNotJS" />
<script src="${smartNotJS}"></script>
<!-- JARVIS WIDGETS -->
<spring:url value="/resources/js/smartwidgets/jarvis.widget.min.js" var="smartWidJS" />
<script src="${smartWidJS}"></script>
<!-- SPARKLINES -->
<spring:url value="/resources/js/plugin/sparkline/jquery.sparkline.min.js" var="sparkLineJS" />
<script src="${sparkLineJS}"></script>
<!-- browser msie issue fix -->
<spring:url value="/resources/js/plugin/msie-fix/jquery.mb.browser.min.js" var="msieFixJS" />
<script src="${msieFixJS}"></script>
<!-- FastClick: For mobile devices -->
<spring:url value="/resources/js/plugin/fastclick/fastclick.min.js" var="fastClickJS" />
<script src="${fastClickJS}"></script>
<!-- END CORE PLUGINS -->
<!-- LAYOUT CONF JS FILE -->
<spring:url value="/resources/js/layoutconf.min.js" var="layoutjs" />
<script src="${layoutjs}" type="text/javascript"></script>
<!-- MAIN APP JS FILE -->
<spring:url value="/resources/js/app.min.js" var="App" />
<script src="${App}" type="text/javascript"></script>