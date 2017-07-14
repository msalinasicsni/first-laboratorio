var SearchMx = function () {
    return {
        //main function to initiate the module
        init: function (parametros) {
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var text_selected_all = $("#text_selected_all").val();
            var text_selected_none = $("#text_selected_none").val();
            var table1 = $('#mx-results').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "T" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null, null, null, null, null, null,
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
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#mx-results'), breakpointDefinition);
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


            <!-- formulario de búsqueda de mx -->
            $('#searchMx-form').validate({
                // Rules for form validation
                rules: {
                    fecFinTomaMx: {required: function () {
                        return $('#fecInicioTomaMx').val().length > 0;
                    }},
                    fecInicioTomaMx: {required: function () {
                        return $('#fecFinTomaMx').val().length > 0;
                    }}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getMxs(false)
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

            function getMxs(showAll) {
                var mxFiltros = {};
                if (showAll) {
                    mxFiltros['nombreApellido'] = '';
                    mxFiltros['fechaInicioTomaMx'] = '';
                    mxFiltros['fechaFinTomaMx'] = '';
                    mxFiltros['codSilais'] = '';
                    mxFiltros['codUnidadSalud'] = '';
                    mxFiltros['codTipoMx'] = '';
                    mxFiltros['esLab'] = $('#txtEsLaboratorio').val();
                    mxFiltros['codigoUnicoMx'] = '';
                    mxFiltros['codTipoSolicitud'] = '';
                    mxFiltros['nombreSolicitud'] = '';
                    mxFiltros['solicitudAprobada'] = '';
                } else {
                    mxFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    mxFiltros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                    mxFiltros['fechaFinTomaMx'] = $('#fecFinTomaMx').val();
                    mxFiltros['codSilais'] = $('#codSilais').find('option:selected').val();
                    mxFiltros['codUnidadSalud'] = $('#codUnidadSalud').find('option:selected').val();
                    mxFiltros['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                    mxFiltros['esLab'] = $('#txtEsLaboratorio').val();
                    mxFiltros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                    mxFiltros['codTipoSolicitud'] = $('#tipo option:selected').val();
                    mxFiltros['nombreSolicitud'] = $('#nombreSoli').val();
                    mxFiltros['solicitudAprobada'] = $('#aprobado').val();
                }
                blockUI();
                $.getJSON(parametros.searchUrl, {
                    strFilter: JSON.stringify(mxFiltros),
                    ajax: 'true'
                }, function (dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {

                        for (var i = 0; i < len; i++) {

                            //   var actionUrl = parametros.sActionUrl+idLoad;
                            //'<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>'
                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx + " <input type='hidden' value='" + dataToLoad[i].idTomaMx + "'/>", dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx, dataToLoad[i].estadoMx, dataToLoad[i].calidad, dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].laboratorio, dataToLoad[i].area, dataToLoad[i].persona, " <input type='hidden' value='" + dataToLoad[i].solicitudes + "'/>"]);

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
                getMxs(true);
            });

            /*PARA MOSTRAR TABLA DETALLE DX*/
            function format(d, indice) {
                // `d` is the original data object for the row
                var texto = d[indice]; //indice donde esta el input hidden
                var diagnosticos = $(texto).val();

                var json = JSON.parse(diagnosticos);
                var len = Object.keys(json).length;
                var childTable = '<table style="padding-left:20px;border-collapse: separate;border-spacing:  10px 3px;">' +
                    '<tr><td style="font-weight: bold">' + $('#text_dx').val() + '</td><td style="font-weight: bold">' + $('#text_cc').val() + '</td><td style="font-weight: bold">' + $('#text_dx_date').val() + '</td><td style="font-weight: bold">' + $('#res_aprob').val() + '</td> </tr>';
                for (var i = 1; i <= len; i++) {
                    childTable = childTable +
                        '<tr><td>' + json[i].nombre + '</td>' +
                        '<td>' + json[i].cc + '</td>' +
                        '<td>' + json[i].fechaSolicitud + '</td>' +
                        '<td>' + json[i].estado + '</td></tr>';
                }
                childTable = childTable + '</table>';
                return childTable;
            }

            $('#mx-results tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = table1.api().row(tr);
                if (row.child.isShown()) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    // Open this row
                    row.child(format(row.data(), 10)).show();
                    tr.addClass('shown');
                }
            });

            <!-- al seleccionar SILAIS -->
            $('#codSilais').change(function () {
                blockUI();
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sUnidadesUrl, {
                        codSilais: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].codigo + '">'
                                + data[i].nombre
                                + '</option>';
                            // html += '</option>';
                        }
                        $('#codUnidadSalud').html(html);
                    }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        validateLogin(jqXHR);
                    });
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codUnidadSalud').html(html);
                }
                $('#codUnidadSalud').val('').change();
                unBlockUI();
            });


            <!-- para buscar código de barra -->
            var timer;
            var iniciado = false;
            var contador;
            //var codigo;
            function tiempo() {
                console.log('tiempo');
                contador++;
                if (contador >= 10) {
                    clearInterval(timer);
                    iniciado = false;
                    //codigo = $.trim($('#codigo').val());
                    console.log('consulta con tiempo');
                    getMxs(false);

                }
            }

            $('#txtCodUnicoMx').keypress(function (event) {
                if (!iniciado) {
                    timer = setInterval(tiempo(), 100);
                    iniciado = true;
                }
                contador = 0;

                if (event.keyCode == '13') {
                    clearInterval(timer);
                    iniciado = false;
                    event.preventDefault();
                    //codigo = $.trim($(this).val());
                    getMxs(false);
                    $('#txtCodUnicoMx').val('');
                }
            });


            /*     $("#print").click(function() {
             var oTT = TableTools.fnGetInstance('mx-results');
             var aSelectedTrs = oTT.fnGetSelected();
             var len = aSelectedTrs.length;

             if (len > 0) {

             blockUI(parametros.blockMess);
             var idToma = {};
             //el input hidden debe estar siempre en la primera columna
             for (var i = 0; i < len; i++) {
             var texto = aSelectedTrs[i].firstChild.innerHTML;
             var input = texto.substring(texto.lastIndexOf("<"), texto.length);
             idToma[i] = $(input).val();
             }

             var  obj = {};
             obj['strCodigos'] = idToma;
             obj['mensaje'] = '';
             obj['cantImprimir'] = len;
             obj['cantResImpresos'] = '';

             $.ajax(
             {
             url: parametros.printUrl,
             type: 'POST',
             dataType: 'json',
             data: JSON.stringify(obj),
             contentType: 'application/json',
             mimeType: 'application/json',
             async: false,
             success: function (data) {
             if (data.mensaje.length > 0) {
             $.smallBox({
             title: data.mensaje,
             content: $("#smallBox_content").val(),
             color: "#C46A69",
             iconSmall: "fa fa-warning",
             timeout: 4000
             });
             } else {
             var msg = $("#msg_request_printed").val();
             msg = msg.replace(/\{0\}/, data.cantResImpresos);
             $.smallBox({
             title: msg,
             content: $("#smallBox_content").val(),
             color: "#739E73",
             iconSmall: "fa fa-success",
             timeout: 4000
             });

             }
             unBlockUI();
             },
             error: function (data, status, er) {
             unBlockUI()
             alert("error: " + data + " status: " + status + " er:" + er);
             }
             });



             }else{
             $.smallBox({
             title : $("#msg_select_receipt").val(),
             content : "<i class='fa fa-clock-o'></i> <i>"+$("#smallBox_content").val()+"</i>",
             color : "#C46A69",
             iconSmall : "fa fa-times fa-2x fadeInRight animated",
             timeout : 4000
             });
             }


             });*/


            $("#print").click(function () {
                var oTT = TableTools.fnGetInstance('mx-results');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;

                if (len > 0) {

                    blockUI(parametros.blockMess);
                    var idToma = "";
                    //el input hidden debe estar siempre en la primera columna
                    for (var i = 0; i < len; i++) {
                        var texto = aSelectedTrs[i].firstChild.innerHTML;
                        var input = texto.substring(texto.lastIndexOf("<"), texto.length);

                        if (i + 1 < len) {
                            idToma += $(input).val() + ",";

                        } else {
                            idToma += $(input).val();

                        }
                    }

                    $.ajax(
                        {
                            url: parametros.printUrl,
                            type: 'GET',
                            dataType: 'text',
                            data: {codigos: idToma},
                            contentType: 'application/json',
                            mimeType: 'application/json',
                            success: function (data) {
                                if (data.length != 0) {
                                    var blob = b64toBlob(data, 'application/pdf');
                                    var blobUrl = URL.createObjectURL(blob);

                                    window.open(blobUrl, '', 'width=600,height=400,left=50,top=50,toolbar=yes');
                                } else {
                                    $.smallBox({
                                        title: $("#msg_select").val(),
                                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                                        color: "#C46A69",
                                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                        timeout: 4000
                                    });
                                }


                                unBlockUI();
                            },
                            error: function (jqXHR) {
                                unBlockUI();
                                validateLogin(jqXHR);
                            }
                        });


                } else {
                    $.smallBox({
                        title: $("#msg_select_sample").val(),
                        content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                        color: "#C46A69",
                        iconSmall: "fa fa-times fa-2x fadeInRight animated",
                        timeout: 4000
                    });
                }


            });


            function b64toBlob(b64Data, contentType, sliceSize) {
                contentType = contentType || '';
                sliceSize = sliceSize || 512;

                var byteCharacters = atob(b64Data);
                var byteArrays = [];

                for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
                    var slice = byteCharacters.slice(offset, offset + sliceSize);

                    var byteNumbers = new Array(slice.length);
                    for (var i = 0; i < slice.length; i++) {
                        byteNumbers[i] = slice.charCodeAt(i);
                    }

                    var byteArray = new Uint8Array(byteNumbers);

                    byteArrays.push(byteArray);
                }

                var blob = new Blob(byteArrays, {type: contentType});
                return blob;
            }


        }
    };

}();

