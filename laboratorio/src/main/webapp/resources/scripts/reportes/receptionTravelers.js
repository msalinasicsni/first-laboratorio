/**
 * Created by Miguel Salinas on 07-05-2020
 */


var ReceptionTravelers = function () {
    return {

        //main function to initiate the module
        init: function (parametros) {

            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };


            var table1 = $('#received-samples').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-6 hidden-xs'T>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-sm-6 col-xs-12'p>>",

                "oTableTools": {
                    "aButtons": [
                        {
                            "sExtends": "xls",
                            "sTitle": "Reporte de Muestras Recepcionadas Viajeros"
                        }
                    ],
                    "sSwfPath": parametros.sTableToolsPath
                },

                "aaSorting": [],

                "autoWidth": true,

                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#received-samples'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }

            });

            <!-- filtro Mx -->
            $('#received-samples-form').validate({
                // Rules for form validation
                rules: {
                    fecInicioRecepcion: {required: function () {
                        return $('#fecFinRecepcion').val().length > 0;
                    }},
                    fecFinRecepcion: {required: function () {
                        return $('#fecInicioRecepcion').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    getMxs(false, false)
                }
            });

            function blockUI() {
                var loc = window.location;
                var pathName = loc.pathname.substring(0, loc.pathname.indexOf('/', 1) + 1);
                //var mess = $("#blockUI_message").val()+' <img src=' + pathName + 'resources/img/loading.gif>';
                var mess = '<img src=' + pathName + 'resources/img/ajax-loading.gif> ' + parametros.blockMess;
                $.blockUI({ message: mess,
                    css: {
                        border: 'none',
                        padding: '15px',
                        backgroundColor: '#000',
                        '-webkit-border-radius': '10px',
                        '-moz-border-radius': '10px',
                        opacity: .5,
                        color: '#fff'
                    }});
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            function getMxs(showAll, cargaInicial) {
                var mxFiltros = {};
                if (showAll) {

                    mxFiltros['fechaInicio'] = '';
                    mxFiltros['fechaFin'] = '';
                } else {
                    if (cargaInicial){
                        mxFiltros['fechaInicio'] = $('#fechaActual').val();
                        mxFiltros['fechaFin'] = $('#fechaActual').val();
                    }else {
                        mxFiltros['fechaInicio'] = $('#fecInicioRecepcion').val();
                        mxFiltros['fechaFin'] = $('#fecFinRecepcion').val();
                    }
                }
                blockUI();
                $.getJSON(parametros.searchUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;

                    if (len > 0) {
                        codigos = "";
                        for (var i = 0; i < len; i++) {
                            table1.fnAddData(
                                [dataToLoad[i].fechaRecepcion , dataToLoad[i].enLinea, dataToLoad[i].enRecepcion, dataToLoad[i].total]);
                        }
                    } else {
                        $.smallBox({
                            title: $("#msg_no_results_found").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    unBlockUI();
                })
                    .fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
            }


            $("#all-orders").click(function () {
                getMxs(true, false);
            });

            //precargar datos de hoy
            getMxs(false, true);

        }
    };

}();

