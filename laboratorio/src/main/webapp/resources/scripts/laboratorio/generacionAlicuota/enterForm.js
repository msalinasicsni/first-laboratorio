/**
 * Created by souyen-ics on 01-05-15.
 */
var GenerateAliquot = function () {

    return {
        init: function (parametros) {
            var codigo = $('#codigoUnicoMx').val();
            getAliquots(codigo);
            getTestOrders(codigo);

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
                    },
                    baseZ: 1051 // para que se muestre bien en los modales
                });
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var text_selected_all = $("#text_selected_all").val();
            var text_selected_none = $("#text_selected_none").val();
            var aliquotsTable = $('#aliquots-list').dataTable({


                "pageLength": 10,

                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "T" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true, //"T<'clear'>"+
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#aliquots-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                "oTableTools": {
                    "sSwfPath": parametros.sTableToolsPath,
                    "sRowSelect": "multi",
                    "aButtons": [
                        {"sExtends": "select_all", "sButtonText": text_selected_all},
                        {"sExtends": "select_none", "sButtonText": text_selected_none}
                    ]
                }

            });


            var testOrdersTable = $('#test-orders-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-5 col-xs-12 hidden'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "filter": false,
                "bPaginate": false,


                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#test-orders-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }

            });


            <!-- Validacion formulario de generacion de alicuotas -->
            var $validator = $("#generateAliquot-form").validate({
                // Rules for form validation
                rules: {
                    etiqueta: {required: true},
                    volumen: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnAdd').click(function () {
                var $validarModal = $("#generateAliquot-form").valid();
                if (!$validarModal) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    addAliquot();

                }
            });


            function addAliquot() {
                blockUI(parametros.blockMess);
                var aliqObj = {};
                aliqObj['mensaje'] = '';
                aliqObj['idAlicuota'] = '';
                aliqObj['etiqueta'] = $('#etiqueta').val();
                aliqObj['volumen'] = $('#volumen').val();
                aliqObj['idSoliE'] = $('#idSoliE').val();
                aliqObj['idSoliDx'] = $('#idSoliDx').val();
                aliqObj['codigoUnicoMx'] = $('#codigoUnicoMx').val();

                $.ajax(
                    {
                        url: parametros.addAliquot,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(aliqObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {

                                getAliquots(aliqObj.codigoUnicoMx);
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                aliqObj['etiqueta'] = $('#etiqueta').val('').change();
                                aliqObj['volumen'] = $('#volumen').val('');
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });

            }

            function getTestOrders(codigoUnicoMx) {

                $.getJSON(parametros.getTestOrders, {
                    codigoUnicoMx: codigoUnicoMx,
                    ajax: 'true'
                }, function (data) {
                    testOrdersTable.fnClearTable();
                    var len = data.length;

                    for (var i = 0; i < len; i++) {

                        testOrdersTable.fnAddData(
                            [data[i].codExamen.nombre]);
                    }

                })
            }

            function getAliquots(codigoUnicoMx) {

                $.getJSON(parametros.getAliquots, {
                    codigoUnicoMx: codigoUnicoMx,
                    ajax: 'true'
                }, function (data) {
                    aliquotsTable.fnClearTable();
                    var len = data.length;

                    var settings = {
                        moduleSize: 2,
                        showHRI: false

                    };
                    for (var i = 0; i < len; i++) {
                        var overrideUrl = parametros.overrideAliquot + data[i].idAlicuota;
                        var idDiv = "divBarcode" + i;

                        aliquotsTable.fnAddData(
                            ['<div  id="' + idDiv + '"></div>', data[i].idAlicuota, data[i].alicuotaCatalogo.etiquetaPara, data[i].volumen, '<a href=' + overrideUrl + ' class="btn btn-default btn-xs btn-danger"><i class="fa fa-times"></i></a>']);

                        $('#' + idDiv + '').html("").show().barcode(data[i].idAlicuota, "datamatrix", settings);


                    }

                    $(".dataTables_paginate").on('click', function () {
                        var count = aliquotsTable.fnGetData().length;
                        for (var i = 0; i < count; i++) {
                            var data2 = aliquotsTable.api().row(i).data();
                            $('#' + idDiv + '').html("").show().barcode(data2[i], "datamatrix", settings);
                        }


                    });
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

            }


            $('#btnPrint').click(function () {
                printSelected()
            });

            $('#etiqueta').click(function () {
                $.getJSON(parametros.volumeUrl, {
                    idAlicuota: $(this).val(),
                    ajax: 'true'
                }, function (data) {
                    $('#inVolume').val(data);
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });


            });

            function printSelected() {
                var oTT = TableTools.fnGetInstance('aliquots-list');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                if (len > 0) {
                    $.SmartMessageBox({
                        title: $("#msg_print_confirm").val(),
                        content: $("#msg_print_confirm_content").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            blockUI(parametros.blockMess);
                            var idalicuotas = [];
                            for (var i = 0; i < len; i++) {
                                var texto = aSelectedTrs[i].cells[1].innerHTML;
                                if (i + 1 < len) {
                                    idalicuotas += texto + ",";
                                } else {
                                    idalicuotas += texto;
                                }

                                unBlockUI();
                            }
                            idalicuotas = reemplazar(idalicuotas, ".", "*");
                            imprimir(idalicuotas);

                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msg_print_canceled").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                                color: "#C46A69",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 4000
                            });
                        }

                    });
                } else {
                    $.smallBox({
                        title: $("#msg_print_aliquot_select").val(),
                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                        color: "#C46A69",
                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                        timeout: 4000
                    });
                }

            }

            function imprimir(strBarCodes){
                $.getJSON(parametros.sPrintUrl, {
                    strBarCodes: strBarCodes,
                    ajax: 'true'
                }, function (data) {
                    var len = Object.keys(data).length;
                    console.log(data);
                    if (len > 0) {
                        console.log(data.respuesta.length);
                        if (data.respuesta.length>0){
                            $.smallBox({
                                title: data.respuesta,
                                content: $("#disappear").val(),
                                color: "#C46A69",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        }else{
                            $.smallBox({
                                title: "etiquetas impresas",
                                content: $("#disappear").val(),
                                color: "#739E73",
                                iconSmall: "fa fa-success",
                                timeout: 4000
                            });
                        }

                    }
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            function reemplazar(texto, buscar, nuevo) {
                var temp = '';
                var long = texto.length;
                for (j = 0; j < long; j++) {
                    if (texto[j] == buscar) {
                        temp += nuevo;
                    } else
                        temp += texto[j];
                }
                return temp;
            }

        }
    };

}();