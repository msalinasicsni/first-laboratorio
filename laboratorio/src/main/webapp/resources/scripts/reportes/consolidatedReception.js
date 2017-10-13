/**
 * Created by FIRSTICT on 4/22/2015.
 */
var Consolidated = function () {
    return {
        //main function to initiate the module
        init: function (parametros) {

            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var table1 = $('#mx-consolidated').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "bPaginate": false,
                "columns": [
                    null, null
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#mx-consolidated'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            // This must be a hyperlink
            $(".export").on('click', function (event) {
                // CSV
                var fileHeader = $("#fileTitle").val()+'"\r\n"'+$("#desde").val()+$("#fechaInicio").val()+'  '+$("#hasta").val()+$("#fechaFin").val();
                var args = [$('#mx-consolidated'), $("#fileName").val()+'.csv', fileHeader];
                exportTableToCSV.apply(this, args);

                // If CSV, don't do event.preventDefault() or return false
                // We actually need this to be a typical hyperlink
            });

            <!-- formulario de búsqueda de mx -->
            $('#searchMx-form').validate({
                // Rules for form validation
                rules: {
                    fechaInicio: {required: true},
                    fechaFin: {required: true},
                    tipoConsolidado: {required: true},
                    codSilais: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getReport();
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

            $('#tipoConsolidado').change(function () {
                console.log($(this).val());
                if ($(this).val().length > 0) {
                    if ($(this).val() != 'Municipio') {
                        $("#dvSilais").hide();
                    } else {
                        $("#dvSilais").show();
                    }
                } else {
                    $("#dvSilais").hide();
                }
            });

            function getReport() {
                var tipoConsolidado = $('#tipoConsolidado').find('option:selected').val();
                var descTipoConsolidado = $('#tipoConsolidado').find('option:selected').text();
                var urlReporte = '';
                if (tipoConsolidado==='SILAIS') {
                    $("#fileName").val("RecepcionMX_SILAIS");
                    urlReporte = parametros.searchUrl;
                }else if (tipoConsolidado==='Municipio') {
                    $("#fileName").val("RecepcionMX_Mun_SILAIS");
                    urlReporte = parametros.searchUrlMun;
                }else {
                    $("#fileName").val("RecepcionMX_Dx");
                    urlReporte = parametros.searchUrlDx;
                }
                blockUI();
                $.getJSON(urlReporte, {
                    fechaInicio: $("#fechaInicio").val(),
                    fechaFin: $("#fechaFin").val(),
                    codSilais: $('#codSilais').find('option:selected').val(),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        $('.replaceme').html(descTipoConsolidado);

                        for (var i = 0; i < len; i++) {

                            //   var actionUrl = parametros.sActionUrl+idLoad;
                            //'<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>'
                            table1.fnAddData(
                                [dataToLoad[i].entidad, dataToLoad[i].total]);

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
        }
    };

}();

