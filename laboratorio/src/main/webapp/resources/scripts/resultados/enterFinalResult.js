/**
 * Created by souyen-ics on 02-23-15.
 */
var enterFinalResult = function () {
    return{
        //main function to initiate the module
        init: function (parametros) {

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
            var table1 = $('#records_exa').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null, null, null,null,null,
                    {
                        "className": 'details-control',
                        "orderable": false,
                        "data": null,
                        "defaultContent": ''
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#records_exa'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            getExams();

            function getExams() {

                blockUI();
                $.getJSON(parametros.searchUrl, {
                    idSolicitudE: $("#idSolicitudE").val(),
                    idSolicitudD: $("#idSolicitud").val(),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {

                            table1.fnAddData(
                                [dataToLoad[i].fechaSolicitud, dataToLoad[i].nombreSolicitud, dataToLoad[i].codigoUnicoMx,
                                    dataToLoad[i].tipoMx, dataToLoad[i].tipoNotificacion, dataToLoad[i].NombreExamen,  dataToLoad[i].laboratorio,  dataToLoad[i].procesado , dataToLoad[i].persona, " <input type='hidden' value='" + dataToLoad[i].resultado + "'/>"]);

                        }
                    } else {
                        $.smallBox({
                            title: $("#msg_no_results_found").val(),
                            content: $("#disappear").val(),
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


            /*PARA MOSTRAR TABLA DETALLE RESULTADO*/
            function format(d, indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var resultado = $(texto).val();
                var json = JSON.parse(resultado);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                    '<tr><td style="font-weight: bold">' + $('#text_response').val() + '</td><td style="font-weight: bold">' + $('#text_value').val() + '</td><td style="font-weight: bold">' + $('#text_date').val() + '</td></tr>';
                for (var i = 1; i <= len; i++) {
                    childTable = childTable +
                        '<td>' + json[i].respuesta + '</td>' +
                        '<td>' + json[i].valor + '</td>' +
                        '<td>' + json[i].fechaResultado + '</td></tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#records_exa tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = table1.api().row(tr);
                if (row.child.isShown()) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    // Open this row
                    row.child(format(row.data(), 9)).show();
                    tr.addClass('shown');
                }
            });

            /****************************************************************
             * Respuestas
             ******************************************************************/

            function fillRespuestasExamen() {
                blockUI(parametros.blockMess);
                var valoresListas = {};
                var detaResultados = {};
                var lenListas = 0;
                var lenDetRes = 0;
                var idDx = $("#idDx").val();
                var idEstudio = $('#idEstudio').val();
                var idSolicitud = null;
                var idSoliDx = $("#idSolicitud").val();
                var idSoliE = $("#idSolicitudE").val();
                if (idSoliDx != null && idSoliDx != "") {
                    idSolicitud = idSoliDx;
                } else {
                    idSolicitud = idSoliE;
                }

                //primero se obtienen los valores de las listas asociadas a las respuestas del dx o estudio

                $.getJSON(parametros.listasUrl, {
                    idDx: idDx,
                    idEstudio: idEstudio,
                    ajax: 'false'
                }, function (dataToLoad) {
                    lenListas = Object.keys(dataToLoad).length;
                    valoresListas = dataToLoad;

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

                //se obtienen los detalles de las respuestas contestadas de la solicitud
                $.getJSON(parametros.detResultadosUrl, {
                    idSolicitud: idSolicitud,
                    ajax: 'false'
                }, function (data) {
                    lenDetRes = data.length;
                    detaResultados = data;
                    var divResultado = $("#resultados");
                    divResultado.html("");
                    //obteniendo las respuestas configuradas para el dx o estudio


                    $.getJSON(parametros.conceptosUrl, {
                        idDx: $("#idDx").val(),
                        idEstudio: $('#idEstudio').val(),
                        ajax: 'false'
                    }, function (dataToLoad) {
                        var contenidoControl = '';
                        var len = Object.keys(dataToLoad).length;
                        if (len > 0) {
                            for (var i = 0; i < len; i++) {
                                var idControlRespuesta;
                                var descripcionRespuesta = '';
                                if (dataToLoad[i].descripcion != null) {
                                    descripcionRespuesta = dataToLoad[i].descripcion;
                                }
                                var seccionDescripcion = '<section class="col col-sm-4 col-md-6 col-lg-6">' +
                                    '<label class="text-left txt-color-blue font-md">' +
                                    '</label>' +
                                    '<div class="note font-sm">' +
                                    '<strong>' + descripcionRespuesta + '</strong>' +
                                    '</div>' +
                                    '</section>';
                                //se busca si existe valor registrado para la respuesta
                                var valor = '';
                                var deshabilitar = '';
                                var esRespuestaExamen = false;
                                if (lenDetRes > 0) {
                                    for (var j = 0; j < lenDetRes; j++) {
                                        //console.log(detaResultados[j]);
                                        //console.log(dataToLoad[i]);
                                        var respExistente;
                                        if (detaResultados[j].respuesta != null) {
                                            respExistente = detaResultados[j].respuesta;
                                        }
                                        else {
                                            respExistente = detaResultados[j].respuestaExamen;
                                            esRespuestaExamen = true;
                                        }
                                        if (respExistente.concepto.idConcepto == dataToLoad[i].concepto.idConcepto) {

                                            valor = detaResultados[j].valor;
                                            //console.log('se encontró valor: ' + valor);
                                            if (esRespuestaExamen)
                                                deshabilitar = 'disabled';
                                            break;
                                        }
                                    }
                                }
                                switch (dataToLoad[i].concepto.tipo.codigo) {
                                    case 'TPDATO|LOG':
                                        idControlRespuesta = dataToLoad[i].idRespuesta;
                                        contenidoControl = '<div class="row">' +
                                            '<section class="col col-sm-4 col-md-6 col-lg-6">' +
                                            '<label class="text-left txt-color-blue font-md">' +
                                            dataToLoad[i].nombre +
                                            '</label>' +
                                            '<label class="checkbox">';
                                        if (lenDetRes <= 0) {
                                            contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" ' + deshabilitar + '>';
                                        } else {
                                            if (valor == 'true') {
                                                contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" checked ' + deshabilitar + '>';
                                            } else {
                                                contenidoControl = contenidoControl + '<input type="checkbox" name="' + idControlRespuesta + '" id="' + idControlRespuesta + '" ' + deshabilitar + '>';
                                            }
                                        }
                                        contenidoControl = contenidoControl + '<i></i>' +
                                            '</label>' +
                                            '</section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        break;
                                    case 'TPDATO|LIST':

                                        idControlRespuesta = dataToLoad[i].idRespuesta;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-6 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                            '<div class="input-group">' +
                                            '<span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>';

                                        //si la respuesta es requerida
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<select id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" class="requiredConcept" style="width: 100%;" ' + deshabilitar + '>';
                                        }
                                        else {
                                            contenidoControl = contenidoControl + '<select id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" class="" style="width: 100%;" ' + deshabilitar + '>';
                                        }
                                        contenidoControl = contenidoControl + '<option value="">...</option>';
                                        for (var ii = 0; ii < lenListas; ii++) {
                                            if (valoresListas[ii].idConcepto.idConcepto == dataToLoad[i].concepto.idConcepto) {
                                                console.log(valoresListas[ii].idCatalogoLista + " == " + valor);
                                                if (valoresListas[ii].idCatalogoLista == valor) {
                                                    contenidoControl = contenidoControl + '<option  value="' + valoresListas[ii].idCatalogoLista + '" selected >' + valoresListas[ii].valor + '</option>';
                                                } else {
                                                    contenidoControl = contenidoControl + '<option  value="' + valoresListas[ii].idCatalogoLista + '">' + valoresListas[ii].valor + '</option>';
                                                }
                                            }
                                        }
                                        contenidoControl = contenidoControl + '</select></div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        $("#" + idControlRespuesta).select2();
                                        break;
                                    case 'TPDATO|TXT':
                                        console.log('texto');
                                        idControlRespuesta = dataToLoad[i].idRespuesta;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                            '<div class="">' +
                                            '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<input class="form-control requiredConcept" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" ' + deshabilitar + '>';
                                        } else {
                                            contenidoControl = contenidoControl + '<input class="form-control" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" ' + deshabilitar + '>';
                                        }

                                        contenidoControl = contenidoControl + '<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>' + dataToLoad[i].nombre + '</b></label>' +
                                            '</div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        break;
                                    case 'TPDATO|NMRO':
                                        console.log('numero');
                                        idControlRespuesta = dataToLoad[i].idRespuesta;
                                        contenidoControl = '<div class="row"><section class="col col-sm-12 col-md-12 col-lg-6"><label class="text-left txt-color-blue font-md">';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>';
                                        }
                                        contenidoControl = contenidoControl + dataToLoad[i].nombre + '</label>' +
                                            '<div class="">' +
                                            '<label class="input"><i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>';
                                        if (dataToLoad[i].requerido) {
                                            contenidoControl = contenidoControl + '<input class="form-control decimal requiredConcept" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" ' + deshabilitar + '>';
                                        } else {
                                            contenidoControl = contenidoControl + '<input class="form-control decimal" type="text"  id="' + idControlRespuesta + '" name="' + idControlRespuesta + '" value="' + valor + '" placeholder="' + dataToLoad[i].nombre + '" ' + deshabilitar + '>';
                                        }

                                        contenidoControl = contenidoControl + '<b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i>' + dataToLoad[i].nombre + '</b></label>' +
                                            '</div></section>' +
                                            seccionDescripcion +
                                            '</div>';
                                        divResultado.append(contenidoControl);
                                        $("#" + idControlRespuesta).inputmask("decimal", {
                                            allowMinus: false,
                                            radixPoint: ".",
                                            digits: 2
                                        });
                                        break;
                                    default:
                                        break;

                                }
                            }
                            unBlockUI();
                        } else {
                            unBlockUI();
                            $.smallBox({
                                title: $("#study_not_answers").val(),
                                content: $("#disappear").val(),
                                color: "#C46A69",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        }

                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }


            if (parametros.esIngreso == 'true') {
                fillRespuestasExamen();
            }


            function guardarResultadoFinal() {
                blockUI(parametros.blockMess);
                var objResultado = {};
                var objDetalle = {};
                var cantRespuestas = 0;
                $.getJSON(parametros.conceptosUrl, {
                    idDx: $("#idDx").val(),
                    idEstudio: $('#idEstudio').val(),
                    ajax: 'false'
                }, function (dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var idControlRespuesta;
                            var valorControlRespuesta;
                            var idConcepto = dataToLoad[i].concepto.idConcepto;
                            switch (dataToLoad[i].concepto.tipo.codigo) {
                                case 'TPDATO|LOG':
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    valorControlRespuesta = $('#' + idControlRespuesta).is(':checked');
                                    break;
                                case 'TPDATO|LIST':
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    valorControlRespuesta = $('#' + idControlRespuesta).find('option:selected').val();
                                    break;
                                case 'TPDATO|TXT':
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    valorControlRespuesta = $('#' + idControlRespuesta).val();
                                    break;
                                case 'TPDATO|NMRO':
                                    idControlRespuesta = dataToLoad[i].idRespuesta;
                                    valorControlRespuesta = $('#' + idControlRespuesta).val();
                                    break;
                                default:
                                    break;

                            }
                            var objConcepto = {};
                            objConcepto["idRespuesta"] = idControlRespuesta;
                            objConcepto["valor"] = valorControlRespuesta;
                            objConcepto["idConcepto"] = idConcepto;
                            console.log(objConcepto);
                            objDetalle[i] = objConcepto;
                            cantRespuestas++;
                        }
                        var idSoliDx = $("#idSolicitud").val();
                        var idSoliE = $("#idSolicitudE").val();
                        if (idSoliDx != "") {
                            objResultado["idSolicitud"] = idSoliDx;
                        } else {
                            objResultado["idSolicitud"] = idSoliE;
                        }

                        objResultado["strRespuestas"] = objDetalle;
                        objResultado["mensaje"] = '';
                        objResultado["cantRespuestas"] = cantRespuestas;
                        $.ajax(
                            {
                                url: parametros.saveFinalResult,
                                type: 'POST',
                                dataType: 'json',
                                data: JSON.stringify(objResultado),
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
                                        var msg = $("#msg_result_added").val();
                                        $.smallBox({
                                            title: msg,
                                            content: $("#disappear").val(),
                                            color: "#739E73",
                                            iconSmall: "fa fa-success",
                                            timeout: 4000
                                        });

                                        setTimeout(function () {
                                            window.close(); //window.location.href = parametros.sInitUrl
                                        }, 4000);
                                    }
                                    unBlockUI();
                                },
                                error: function (jqXHR) {
                                    desbloquearUI();
                                    validateLogin(jqXHR);
                                }
                            });
                    } else {
                        unBlockUI();
                        $.smallBox({
                            title: $("#msg_no_results_found").val(),
                            content: $("#disappear").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

            }

            jQuery.validator.addClassRules("requiredConcept", {
                required: true
            });

            <!-- formulario de registro y edición de resultado-->
            $('#result-form').validate({
                // Rules for form validation
                rules: {
                    codResultado: {required: true},
                    codSerotipo: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    guardarResultadoFinal();
                }
            });

            //formulario de anulación de resultado
            $('#override-result-form').validate({
                // Rules for form validation
                rules: {
                    causaAnulacion: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    anularResultado();
                }
            });

            function showModalOverride() {
                $("#myModal").modal({
                    show: true
                });
            }

            $("#override-result").click(function () {
                $("#causaAnulacion").val("");
                showModalOverride();
            });

            function anularResultado() {
                var objResultado = {};
                var idSoliDx = $("#idSolicitud").val();
                var idSoliE = $("#idSolicitudE").val();
                if (idSoliDx != "") {
                    objResultado["idSolicitud"] = idSoliDx;
                } else {
                    objResultado["idSolicitud"] = idSoliE;
                }
                objResultado["causaAnulacion"] = $("#causaAnulacion").val();
                objResultado["mensaje"] = '';
                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(objResultado),
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
                                var msg = $("#msg_result_override").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                $("#causaAnulacion").val("");
                                fillRespuestasExamen();
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }


        }
    }
}();

