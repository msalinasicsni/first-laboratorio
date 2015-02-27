var ReceiptOrders = function () {
    var bloquearUI = function(mensaje){
        var loc = window.location;
        var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
        var mess = '<img src=' + pathName + 'resources/img/ajax-loading.gif>' + mensaje;
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
    };

    var desbloquearUI = function() {
        setTimeout($.unblockUI, 500);
    };
    return {
        //main function to initiate the module
        init: function (parametros) {
			var responsiveHelper_dt_basic = undefined;
			var breakpointDefinition = {
				tablet : 1024,
				phone : 480
			};
			var table1 = $('#orders_result').dataTable({
				"sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
					"t"+
					"<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
				"autoWidth" : true,
                "preDrawCallback" : function() {
					// Initialize the responsive datatables helper once.
					if (!responsiveHelper_dt_basic) {
						responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#orders_result'), breakpointDefinition);
					}
				},
				"rowCallback" : function(nRow) {
					responsiveHelper_dt_basic.createExpandIcon(nRow);
				},
				"drawCallback" : function(oSettings) {
					responsiveHelper_dt_basic.respond();
				}
			});
            var table2 = $('#dx_list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "paging":   false,
                "ordering": false,
                "searching": false,
                "lengthChange": false,
                "columns": [
                    null,null,null,null,null,
                    {
                        "className": 'cancelar',
                        "orderable": false
                    }
                ],
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#dx_list'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });
            if($("#txtEsLaboratorio").val()=='true') {
                getOrdersReview();
            }
            <!-- formulario de búsqueda de ordenes -->
            $('#searchOrders-form').validate({
    			// Rules for form validation
                rules: {
                    fecFinTomaMx:{required:function(){return $('#fecInicioTomaMx').val().length>0;}},
                    fecInicioTomaMx:{required:function(){return $('#fecFinTomaMx').val().length>0;}}
                },
    				// Do not change code below
    				errorPlacement : function(error, element) {
    					error.insertAfter(element.parent());
    				},
    				submitHandler: function (form) {
                        table1.fnClearTable();
                        //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                        getMxs(false)
                    }
            });

            <!-- formulario de recepción general -->
            $('#receiptOrders-form').validate({
                // Rules for form validation
                rules: {
                    rdCantTubos: {required : true},
                    rdTipoMx: {required : true},
                    causaRechazo: {required : true}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    if (element.attr("name") === "rdCantTubos") {
                        $("#dErrorCantTubos").fadeIn('slow');
                    }else if (element.attr("name") === "rdTipoMx") {
                        $("#dErrorTipoMx").fadeIn('slow');
                    }else {
                        error.insertAfter(element.parent());
                    }
                },
                submitHandler: function (form) {
                    $("#dErrorCantTubos").fadeOut('slow');
                    $("#dErrorTipoMx").fadeOut('slow');
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    guardarRecepcion();
                }
            });

            <!-- formulario de recepción en laboratorio -->
            $('#receiptOrdersLab-form').validate({
                // Rules for form validation
                rules: {
                    codCalidadMx: {required : true},
                    causaRechazo: {required : true}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                        error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    guardarRecepcionLab();
                }
            });

            <!-- formulario de recepción en laboratorio -->
            $('#AgregarExamen-form').validate({
                // Rules for form validation
                rules: {
                    codDX: {required : true},
                    codExamen: {required : true}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    guardarExamen();
                }
            });

            function blockUI(){
                var loc = window.location;
                var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
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
                var encuestaFiltros = {};
                if (showAll){
                    encuestaFiltros['nombreApellido'] = '';
                    encuestaFiltros['fechaInicioTomaMx'] = '';
                    encuestaFiltros['fechaFinTomaMx'] = '';
                    encuestaFiltros['codSilais'] = '';
                    encuestaFiltros['codUnidadSalud'] = '';
                    encuestaFiltros['codTipoMx'] = '';
                    encuestaFiltros['esLab'] =  $('#txtEsLaboratorio').val();
                }else {
                    encuestaFiltros['nombreApellido'] = $('#txtfiltroNombre').val();
                    encuestaFiltros['fechaInicioTomaMx'] = $('#fecInicioTomaMx').val();
                    encuestaFiltros['fechaFinTomaMx'] = $('#fecFinTomaMx').val();
                    encuestaFiltros['codSilais'] = $('#codSilais option:selected').val();
                    encuestaFiltros['codUnidadSalud'] = $('#codUnidadSalud option:selected').val();
                    encuestaFiltros['codTipoMx'] = $('#codTipoMx option:selected').val();
                    encuestaFiltros['esLab'] =  $('#txtEsLaboratorio').val();
                    encuestaFiltros['codigoUnicoMx'] = $('#txtCodUnicoMx').val();
                }
                blockUI();
    			$.getJSON(parametros.sOrdersUrl, {
                    strFilter: JSON.stringify(encuestaFiltros),
    				ajax : 'true'
    			}, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var idLoad;
                            if ($('#txtEsLaboratorio').val()=='true'){
                                idLoad =dataToLoad[i]. idRecepcion;
                            }else{
                                idLoad = dataToLoad[i].idTomaMx;
                            }
                            var actionUrl = parametros.sActionUrl+idLoad;
                            table1.fnAddData(
                                [dataToLoad[i].codigoUnicoMx,dataToLoad[i].tipoMuestra, dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].separadaMx, dataToLoad[i].cantidadTubos,
                                    dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud,dataToLoad[i].persona, '<a href='+ actionUrl + ' class="btn btn-default btn-xs"><i class="fa fa-mail-forward"></i></a>']);

                        }
                    }else{
                        $.smallBox({
                            title: $("#msg_no_results_found").val() ,
                            content: $("#smallBox_content").val(),
                            color: "#C46A69",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    unBlockUI();
    			})
    			.fail(function() {
                    unBlockUI();
				    alert( "error" );
				});
            }

            function getOrdersReview(){
                $.getJSON(parametros.sgetOrdenesExamenUrl, {
                    idTomaMx: $("#idTomaMx").val(),
                    contentType : "charset=ISO-8859-1",
                    ajax : 'true'
                }, function(response) {
                    table2.fnClearTable();
                    var len = Object.keys(response).length;
                    for (var i = 0; i < len; i++) {
                        table2.fnAddData(
                            [response[i].nombreExamen, response[i].nombreAreaPrc, response[i].tipo, response[i].nombreSolic, response[i].fechaSolicitud,
                                    '<a data-toggle="modal" class="btn btn-danger btn-xs anularExamen" data-id='+response[i].idOrdenExamen+'><i class="fa fa-times"></i></a>']);

                    }
                    $(".anularExamen").on("click", function(){
                        anularExamen($(this).data('id'));
                    });

                    //al paginar se define nuevamente la función de cargar el detalle
                    $(".dataTables_paginate").on('click', function() {
                        $(".anularExamen").on('click', function () {
                            anularExamen($(this).data('id'));
                        });
                    });
                });
            }

            $("#all-orders").click(function() {
                getMxs(true);
            });

            <!-- para guardar recepción general -->
            function guardarRecepcion() {
                bloquearUI(parametros.blockMess);
                var urlImpresion ='';
                var recepcionObj = {};
                recepcionObj['idRecepcion'] = '';
                recepcionObj['mensaje'] = '';
                recepcionObj['idTomaMx']=$("#idTomaMx").val();
                recepcionObj['verificaCantTb'] = $('input[name="rdCantTubos"]:checked', '#receiptOrders-form').val();
                recepcionObj['verificaTipoMx'] = $('input[name="rdTipoMx"]:checked', '#receiptOrders-form').val();
                recepcionObj['causaRechazo'] = $('#causaRechazo').val();
                recepcionObj['codigoUnicoMx'] = '';

                $.ajax(
                    {
                        url: parametros.sAddReceiptUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(recepcionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        async:false,
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                var msg = $("#msg_receipt_added").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                var loc = window.location;
                                urlImpresion = 'http://'+loc.host+parametros.sPrintUrl+data.codigoUnicoMx;
                                limpiarDatosRecepcion();
                                setTimeout(function () {window.location.href = parametros.sSearchReceiptUrl},4000);
                            }
                            desbloquearUI();
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
                imprimir(urlImpresion);
            }

            function imprimir (urlImpresion) {
                if (urlImpresion.length>0) {
                    window.open(urlImpresion, '', 'width=600,height=400,left=50,top=50,toolbar=yes');
                }
            }

            <!-- para guardar recepción en laboratorio -->
            function guardarRecepcionLab() {
                bloquearUI(parametros.blockMess);
                var recepcionObj = {};
                recepcionObj['mensaje'] = '';
                recepcionObj['idRecepcion']=$("#idRecepcion").val();
                recepcionObj['calidadMx'] = $('#codCalidadMx option:selected').val();
                recepcionObj['causaRechazo'] = $('#causaRechazo').val();
                $.ajax(
                    {
                        url: parametros.sAddReceiptUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(recepcionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                var msg = $("#msg_receipt_added").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                limpiarDatosRecepcion();
                                setTimeout(function () {window.location.href = parametros.sSearchReceiptUrl},2000);
                            }
                            desbloquearUI();
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });

            }

            function limpiarDatosRecepcion(){
                //$("#txtNombreTransporta").val('');
                //$("#txtTemperatura").val('');
                //$("#codLaboratorioProce").val('').change();
            }

            function anularExamen(idOrdenExamen) {
                var anulacionObj = {};
                anulacionObj['idOrdenExamen'] = idOrdenExamen;
                anulacionObj['mensaje'] = '';
                blockUI();
                $.ajax(
                    {
                        url: parametros.sAnularExamenUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(anulacionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                getOrdersReview();
                                var msg = $("#msg_review_cancel").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                            }
                            unBlockUI();
                        },
                        error: function (data, status, er) {
                            unBlockUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            function guardarExamen() {
                var ordenExamenObj = {};
                ordenExamenObj['idTomaMx'] = $("#idTomaMx").val();
                ordenExamenObj['idDiagnostico'] = $('#codDX').find('option:selected').val();
                ordenExamenObj['idEstudio'] = $('#codEstudio').find('option:selected').val();
                ordenExamenObj['idExamen'] = $('#codExamen').find('option:selected').val();
                ordenExamenObj['esEstudio'] = $('#esEstudio').val();
                ordenExamenObj['mensaje'] = '';
                blockUI();
                $.ajax(
                    {
                        url: parametros.sAgregarOrdenExamenUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(ordenExamenObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                getOrdersReview();
                                var msg = $("#msg_review_added").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                            }
                            unBlockUI();
                        },
                        error: function (data, status, er) {
                            unBlockUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            <!--al seleccionar calidad de la muestra -->
            $('#codCalidadMx').change(function(){
                $('#causaRechazo').val('');
                if ($(this).val().length > 0) {
                    if ($(this).val()=='CALIDMX|IDC'){
                        $("#dvCausa").show();
                    }else{
                        $("#dvCausa").hide();
                    }
                }else{
                    $("#dvCausa").hide();
                }
            });

            <!-- al seleccionar SILAIS -->
            $('#codSilais').change(function(){
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
                    })
                }else{
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codUnidadSalud').html(html);
                }
                $('#codUnidadSalud').val('').change();
                unBlockUI();
            });

            //En recepción general Se valida que si selecciona "Si" en Mx Inadecuada se solicite causa de rechazo
            $("input[name$='rdMxInadequate']").click(function () {
                var valor = $(this).val();
                $('#causaRechazo').val('');
                if (valor=='true')
                    $('#dvCausa').show();
                else
                    $('#dvCausa').hide();
            });

            $("#btnAddTest").click(function(){
                if ($("#esEstudio").val()=='true'){
                    getEstudios($("#idTipoMx").val(), $("#codTipoNoti").val());
                }else{
                    getDiagnosticos($("#idTipoMx").val(), $("#codTipoNoti").val());
                }
                $("#myModal").modal({
                    show: true
                });
            });

            <!-- cargar dx -->
            function getDiagnosticos(idTipoMx, codTipoNoti) {
                $.getJSON(parametros.sDxURL, {
                    codMx: idTipoMx, tipoNoti : codTipoNoti,
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].diagnostico.idDiagnostico + '">'
                            + data[i].diagnostico.nombre
                            + '</option>';
                    }
                    $('#codDX').html(html);
                });
            }
            <!-- cargar estudios -->
            function getEstudios(idTipoMx, codTipoNoti) {
                $.getJSON(parametros.sEstudiosURL, {
                    codMx: idTipoMx, tipoNoti : codTipoNoti,
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].estudio.idEstudio + '">'
                            + data[i].estudio.nombre
                            + '</option>';
                    }
                    $('#codEstudio').html(html);
                });
            }


            <!-- Al seleccionar diagnóstico-->
            $('#codDX').change(function () {
                if ($(this).val().length > 0){
                    $.getJSON(parametros.sExamenesURL, {
                        idDx: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].idExamen + '">'
                                + data[i].nombre
                                + '</option>';
                            html += '</option>';
                        }
                        $('#codExamen').html(html);
                    });
                }else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codExamen').html(html);
                }
                $('#codExamen').val('').change();
            });

            <!-- Al seleccionar estudio-->
            $('#codEstudio').change(function () {
                if ($(this).val().length > 0){
                    $.getJSON(parametros.sExamenesEstURL, {
                        idEstudio: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].idExamen + '">'
                                + data[i].nombre
                                + '</option>';
                            html += '</option>';
                        }
                        $('#codExamen').html(html);
                    });
                }else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codExamen').html(html);
                }
                $('#codExamen').val('').change();
            });

            <!-- para buscar código de barra -->
            var timer;
            var iniciado=false;
            var contador;
            //var codigo;
            function tiempo(){
                console.log('tiempo');
                contador++;
                if(contador >= 10){
                    clearInterval(timer);
                    iniciado = false;
                    //codigo = $.trim($('#codigo').val());
                    console.log('consulta con tiempo');
                    getMxs(false);

                }
            }
            $('#txtCodUnicoMx').keypress(function(event){
                if(!iniciado){
                    timer    = setInterval(tiempo(),100);
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
        }
    };

}();

