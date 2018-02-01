
var resultReport = function () {

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
            }
        });
    };

    var desbloquearUI = function () {
        setTimeout($.unblockUI, 500);
    };

    return {
        //main function to initiate the module
        init: function (parametros) {
            var responsiveHelper_data_result = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };
            var title = "";

            /* TABLETOOLS */
            var fecha = new Date();
            var fechaFormateada = (fecha.getDate()<10?'0'+fecha.getDate():fecha.getDate())
                +''+(fecha.getMonth()+1<10?'0'+(fecha.getMonth()+1):fecha.getMonth()+1)
                +''+fecha.getFullYear();

            var table1 = $('#tableRES').dataTable({

                // Tabletools options:
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-6 hidden-xs'>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-sm-6 col-xs-12'p>>",
                "aoColumns" : [ {sClass: "aw-left" },{sClass: "aw-right"},{sClass: "aw-right"},{sClass: "aw-right"},{sClass: "aw-right"},{sClass: "aw-right"},{sClass: "aw-right"}],
                "createdRow": function ( row, data, index ) {
                    if ( data[2] > 0 ) {
                        $('td', row).eq(2).addClass('highlight');
                    }
                    if ( data[6]  > 0 ) {
                        $('td', row).eq(6).addClass('highlight');
                    }
                },
                "autoWidth" : true,
                "pageLength": 20,
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_data_result) {
                        responsiveHelper_data_result = new ResponsiveDatatablesHelper($('#tableRES'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_data_result.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_data_result.respond();
                }
            });

            $(".export").on('click', function (event) {
                // CSV
                var fileHeader = $("#fileTitle").val()+'"\r\n"'+$("#from").val()+$("#initDate").val()+'  '+$("#to").val()+$("#endDate").val();
                var args = [$('#tableRES'), $("#fileName").val()+'.csv', fileHeader];
                exportTableToCSV.apply(this, args);

                // If CSV, don't do event.preventDefault() or return false
                // We actually need this to be a typical hyperlink
            });

            /* END TABLETOOLS */

            $('#result_form').validate({
                // Rules for form validation
                rules : {

                    codArea : {
                        required : true
                    },
                    codSilais: {
                        required : true
                    },
                    codUnidadAtencion: {
                        required : true
                    },
                    idDx:{
                        required:true
                    },
                    initDate:{
                        required:true
                    },
                    endDate:{
                        required:true
                    },
                    codigoLab: {
                        required:true
                    }
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    table1.fnClearTable();

                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    getData();
                }
            });


            $('#codArea').change(
                function() {
                    if ($('#codArea option:selected').val() == "AREAREP|PAIS"){
                        $('#silais').hide();
                        $('#departamento').hide();
                        $('#municipio').hide();
                        $('#unidad').hide();
                        $('#dSubUnits').hide();
                        $('#dNivelPais').hide();
                        $('#zona').hide();
                    }
                    else if ($('#codArea option:selected').val() == "AREAREP|SILAIS"){
                        $('#silais').show();
                        $('#departamento').hide();
                        $('#municipio').hide();
                        $('#unidad').hide();
                        $('#dSubUnits').hide();
                        $('#dNivelPais').hide();
                        $('#zona').hide();
                    }
                    else if ($('#codArea option:selected').val() == "AREAREP|MUNI"){
                        $('#silais').show();
                        $('#departamento').hide();
                        $('#municipio').show();
                        $('#unidad').hide();
                        $('#dSubUnits').hide();
                        $('#dNivelPais').hide();
                        $('#zona').hide();
                    }
                    else if ($('#codArea option:selected').val() == "AREAREP|UNI"){
                        $('#silais').show();
                        $('#departamento').hide();
                        $('#municipio').show();
                        $('#unidad').show();
                        //$('#dSubUnits').show();
                        $('#dNivelPais').hide();
                        $('#zona').hide();
                    }
                });

            function getData() {
                var filtro = {};
                //filtro['subunidades'] = $('#ckUS').is(':checked');
                filtro['fechaInicio'] = $('#initDate').val();
                filtro['fechaFin'] = $('#endDate').val();
                filtro['codSilais'] = $('#codSilais').find('option:selected').val();
                filtro['codUnidadSalud'] = $('#codUnidadAtencion').find('option:selected').val();
                //filtro['codDepartamento'] = $('#codDepartamento').find('option:selected').val();
                filtro['codMunicipio'] = $('#codMunicipio').find('option:selected').val();
                filtro['codArea'] = $('#codArea').find('option:selected').val();
                //filtro['tipoNotificacion'] = $('#codTipoNoti').find('option:selected').val();
                filtro['porSilais'] = "true"; //$('input[name="rbNivelPais"]:checked', '#result_form').val();
                //filtro['codZona'] = $('#codZona').find('option:selected').val();
                filtro['idDx'] = $('#idDx').find('option:selected').val();
                filtro['codLabo'] = $('#codigoLab').find('option:selected').val();

                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sActionUrl, {
                    filtro: JSON.stringify(filtro),
                    ajax: 'true'
                }, function(data) {

                    //title = $('#codTipoNoti option:selected').text();
                    var encontrado = false;
                    if ($('#codArea option:selected').val() == "AREAREP|PAIS") {
                        //title = title + '</br>' + $('#nicRepublic').val();
                        console.log(filtro['porSilais']);
                        if (filtro['porSilais'] == 'true') {
                            $('#firstTh').html($('#silaisT').val());
                        }else {
                            $('#firstTh').html($('#departaT').val());
                        }

                    }
                    else if ($('#codArea option:selected').val() == "AREAREP|SILAIS"){
                        //title = title + '</br>'+$('#codSilaisAtencion option:selected').text() + " " + "-" + " " + $('#municps').val() ;
                        $('#firstTh').html( $('#municT').val() );

                    }
                    else if ($('#codArea option:selected').val() == "AREAREP|DEPTO"){
                        title = title + '</br>' + $('#dep').val() + " " +$('#codDepartamento option:selected').text();
                        $('#firstTh').html( $('#municT').val() );

                    }
                    else if ($('#codArea option:selected').val() == "AREAREP|MUNI"){

                        //title = title + '</br>'+ $('#munic').val() + " "  +$('#codMunicipio option:selected').text();
                        $('#firstTh').html( $('#usT').val() );

                    }
                    else if ($('#codArea option:selected').val() == "AREAREP|UNI"){
                        $('#firstTh').html( $('#usT').val() );

                        /*var ckeckd = $('#ckUS').is(':checked');

                        if (ckeckd) {
                            title = title + '</br>' + $('#areaL').val() + " " + $('#codUnidadAtencion option:selected').text();
                        } else {
                            title = title + '</br>' + $('#unit').val() + " " + $('#codUnidadAtencion option:selected').text();

                        }*/

                    }
                    else if ($('#codArea option:selected').val() == "AREAREP|ZE") {
                        //title = title + '</br>'+ $('#lblZona').val() + " "  +$('#codZona option:selected').text();
                        $('#firstTh').html( $('#usT').val() );
                    }
                    //title = title + '</br>' + $('#from').val() +" " +$('#initDate').val()  + " "+ "-" + " " + $('#to').val() + " " +$('#endDate').val();



                    for (var row in data) {
                        table1.fnAddData([data[row][0], data[row][2], data[row][3], data[row][4], data[row][5],data[row][7], data[row][6]]);
                        encontrado = true;

                    }


                    if(!encontrado){
                        showMessage(parametros.msgTitle, parametros.msgNoData, "#AF801C", "fa fa-warning", 3000);
                        //title='';
                        //$('#lineChart-title').html("<h5>"+title+"</h5>");

                    }
                    desbloquearUI();
                })
                    .fail(function(XMLHttpRequest, textStatus, errorThrown) {
                        alert(" status: " + textStatus + " er:" + errorThrown);
                        setTimeout($.unblockUI, 5);
                    });
            }


            function showMessage(title,content,color,icon,timeout){
                $.smallBox({
                    title: title,
                    content: content,
                    color: color,
                    iconSmall: icon,
                    timeout: timeout
                });
            }

            <!-- al seleccionar SILAIS -->
            /*$('#codSilais').change(function () {
                bloquearUI();
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sUnidadesUrl, {
                        codSilais: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].unidadId + '">'
                                + data[i].nombre
                                + '</option>';
                            // html += '</option>';
                        }
                        $('#codUnidadAtencion').html(html);
                    }).fail(function (jqXHR) {
                        desbloquearUI();
                        validateLogin(jqXHR);
                    });
                } else {
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codUnidadAtencion').html(html);
                }
                $('#codUnidadAtencion').val('').change();
                desbloquearUI();
            });*/

            $('#codSilais').change(
                function() {
                    bloquearUI(parametros.blockMess);
                    if ($(this).val().length > 0) {
                        $.getJSON(parametros.sMunicipiosUrl, {
                            idSilais: $('#codSilais').val(),
                            ajax: 'true'
                        }, function (data) {
                            $("#codMunicipio").select2('data', null);
                            $("#codUnidadAtencion").select2('data', null);
                            $("#codMunicipio").empty();
                            $("#codUnidadAtencion").empty();
                            var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                            var len = data.length;
                            for (var i = 0; i < len; i++) {
                                html += '<option value="' + data[i].codigoNacional + '">'
                                    + data[i].nombre + '</option>';
                            }
                            $('#codMunicipio').html(html);

                            $('#codMunicipio').focus();
                            $('#s2id_codMunicipio').addClass('select2-container-active');
                        }).fail(function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        });
                    }else {
                        var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        $('#codMunicipio').html(html);
                    }
                    $('#codMunicipio').val('').change();
                    desbloquearUI();
                });


            $('#codMunicipio').change(
                function() {
                    bloquearUI(parametros.blockMess);
                    if ($(this).val().length > 0) {
                        $.getJSON(parametros.sUnidadesUrl, {
                            codMunicipio: $('#codMunicipio').val(),
                            codSilais: $('#codSilais').val(),
                            ajax: 'true'
                        }, function (data) {
                            $("#codUnidadAtencion").select2('data', null);
                            $("#codUnidadAtencion").empty();
                            var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                            var len = data.length;
                            for (var i = 0; i < len; i++) {
                                html += '<option value="' + data[i]. unidadId+ '">'
                                    + data[i].nombre + '</option>';
                            }
                            $('#codUnidadAtencion').html(html);
                            $('#codUnidadAtencion').focus();
                            $('#s2id_codUnidadAtencion').addClass('select2-container-active');
                        }).fail(function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        });

                    }else {
                        var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        $('#codUnidadAtencion').html(html);
                    }
                    $('#codUnidadAtencion').val('').change();
                    desbloquearUI();
                });

            $("#sendMail").click(function () {
                sendMail();
            });

            function sendMail() {
                // CSV
                var fileHeader = $("#fileTitle").val()+'"\r\n"'+$("#from").val()+$("#initDate").val()+'  '+$("#to").val()+$("#endDate").val();
                var csv = exportTableToCSV($('#tableRES'), $("#fileName").val()+'.csv', fileHeader);

                var filtro = {};
                //filtro['subunidades'] = $('#ckUS').is(':checked');
                filtro['fechaInicio'] = $('#initDate').val();
                filtro['fechaFin'] = $('#endDate').val();
                filtro['codSilais'] = $('#codSilais').find('option:selected').val();
                filtro['codUnidadSalud'] = $('#codUnidadAtencion').find('option:selected').val();
                //filtro['codDepartamento'] = $('#codDepartamento').find('option:selected').val();
                //filtro['codMunicipio'] = $('#codMunicipio').find('option:selected').val();
                filtro['codArea'] = $('#codArea').find('option:selected').val();
                //filtro['tipoNotificacion'] = $('#codTipoNoti').find('option:selected').val();
                filtro['porSilais'] = $('input[name="rbNivelPais"]:checked', '#result_form').val();
                //filtro['codZona'] = $('#codZona').find('option:selected').val();
                filtro['idDx'] = $('#idDx').find('option:selected').val();

                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sMailUrl, {
                    filtro: JSON.stringify(filtro),
                    csv : encodeURIComponent(csv),
                    ajax: 'true'
                }, function(data) {
                    if (data==='OK'){
                        showMessage(parametros.msgTitle, $("#msg_email_ok").val(), "#739E73", "fa fa-success", 3000);
                    }else {
                        showMessage(parametros.msgTitle, data, "#AF801C", "fa fa-warning", 6000);
                    }
                    desbloquearUI();
                })
                    .fail(function(XMLHttpRequest, textStatus, errorThrown) {
                        alert(" status: " + textStatus + " er:" + errorThrown);
                        setTimeout($.unblockUI, 5);
                    });
            }
        }
    };

}();
