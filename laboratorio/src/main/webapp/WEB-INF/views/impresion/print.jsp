<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: FIRSTICT
  Date: 1/20/2015
  Time: 9:43 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Imprimiendo...</title>
</head>
<body id="content">
<input id="barCodesPrint" type="hidden" value="${strBarCodes}"/>
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<jsp:include page="../fragments/corePlugins.jsp" />
<!-- BEGIN PAGE LEVEL PLUGINS -->
<spring:url value="/resources/js/plugin/qz-print/deployJava.js" var="qzDeploy" />
<script src="${qzDeploy}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/qzAppletPrint/qz-print_jnlp.jnlp" var="qzprint_jnlp" />
<!-- END PAGE LEVEL SCRIPTS -->
<script language="javascript">
    $(document).ready(function() {

        /**
         * Deploys different versions of the applet depending on Java version.
         * Useful for removing warning dialogs for Java 6.  This function is optional
         * however, if used, should replace the <applet> method.  Needed to address
         * MANIFEST.MF TrustedLibrary=true discrepency between JRE6 and JRE7.
         */
        function deployQZ() {
            var qzprint_jnlp = "${qzprint_jnlp}";
            console.log(qzprint_jnlp);
            var attributes = {id: "qz", code:'qz.PrintApplet.class',
                archive:'qz-print.jar', width:1, height:1};
            var parameters = {jnlp_href: qzprint_jnlp,
                cache_option:'plugin', disable_logging:'false',
                initial_focus:'false'};
            if (deployJava.versionCheck("1.7+") == true) {}
            else if (deployJava.versionCheck("1.6+") == true) {
                delete parameters['jnlp_href'];
            }
            deployJava.runApplet(attributes, parameters, '1.5');
        }

        /**
         * Automatically gets called when applet has loaded.
         */
        function qzReady() {
            // Setup our global qz object
            window["qz"] = document.getElementById('qz');
            var title = document.getElementById("title");
            if (qz) {
                try {
                    title.innerHTML = title.innerHTML + " " + qz.getVersion();
                    document.getElementById("content").style.background = "#F0F0F0";
                } catch(err) { // LiveConnect error, display a detailed meesage
                    document.getElementById("content").style.background = "#F5A9A9";
                    alert("ERROR:  \nThe applet did not load correctly.  Communication to the " +
                            "applet has failed, likely caused by Java Security Settings.  \n\n" +
                            "CAUSE:  \nJava 7 update 25 and higher block LiveConnect calls " +
                            "once Oracle has marked that version as outdated, which " +
                            "is likely the cause.  \n\nSOLUTION:  \n  1. Update Java to the latest " +
                            "Java version \n          (or)\n  2. Lower the security " +
                            "settings from the Java Control Panel.");
                }
            }
        }

        /**
         * Automatically gets called when "qz.print()" is finished.
         */
        function qzDonePrinting() {
            // Alert error, if any
            if (qz.getException()) {
                alert('Error printing:\n\n\t' + qz.getException().getLocalizedMessage());
                qz.clearException();
                return;
            }

            // Alert success message
            //alert('Successfully sent print data to "' + qz.getPrinter() + '" queue.');
        }


        /***************************************************************************
         * Prototype function for finding the "default printer" on the system
         * Usage:
         *    qz.findPrinter();
         *    window['qzDoneFinding'] = function() { alert(qz.getPrinter()); };
         ***************************************************************************/
        function useDefaultPrinter() {
            //if (isLoaded()) {
            // Searches for default printer
            qz.findPrinter();

            // Automatically gets called when "qz.findPrinter()" is finished.
            window['qzDoneFinding'] = function() {
                // Alert the printer name to user
                var printer = qz.getPrinter();
                /*alert(printer !== null ? 'Default printer found: "' + printer + '"':
                 'Default printer ' + 'not found');*/

                // Remove reference to this function
                window['qzDoneFinding'] = null;
            };
            //}
        }

        /***************************************************************************
         * Prototype function for printing raw EPL commands
         * Usage:
         *    qz.append('\nN\nA50,50,0,5,1,1,N,"Hello World!"\n');
         *    qz.print();
         ***************************************************************************/
        function printEPL(codigo) {

            // Send characters/raw commands to qz using "append"
            // This example is for EPL.  Please adapt to your printer language
            // Hint:  Carriage Return = \r, New Line = \n, Escape Double Quotes= \"
            qz.append('\nN\n');
            qz.append('b1,2,D,c16,r16,"'+codigo+'"\n');
            qz.append('A100,20,0,2,1,1,N,"'+codigo+'"\n');
            qz.append('P1,1\n');
            // Tell the applet to print.
            qz.print();
        }

        /***************************************************************************
         ****************************************************************************
         * *                          HELPER FUNCTIONS                             **
         ****************************************************************************
         ***************************************************************************/


        /***************************************************************************
         * Gets the current url's path, such as http://site.com/example/dist/
         ***************************************************************************/
        function getPath() {
            var path = window.location.href;
            return path.substring(0, path.lastIndexOf("/")) + "/";
        }

        deployQZ();
        useDefaultPrinter();
        var codigosImprimir = "${strBarCodes}";
        //console.log(codigosImprimir);
        var arrayCodigos = codigosImprimir.split(',');
        var lengthArray = arrayCodigos.length;
        for (var i = 0; i < lengthArray; i++) {
           // console.log(arrayCodigos[i]);
            printEPL(arrayCodigos[i]);
        }
        window['qzDoneAppending'] = null;
        setTimeout(function () {window.close();},1500);
    });
</script>
</body>
</html>
