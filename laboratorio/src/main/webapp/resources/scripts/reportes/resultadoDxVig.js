
var resultReportDxVig = function () {

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

            var $validator = $('#result_form').validate({
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
            $('#codSilais').change(function () {
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
            });

            $("#sendMail").click(function () {
                var $validarForm = $("#result_form").valid();
                if (!$validarForm) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    sendMail();
                }
            });


            function sendMail() {
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
                filtro['incluirMxInadecuadas']=($('#ckbmxinadecuada').is(':checked'));
                filtro['codLabo'] = $('#codigoLab').find('option:selected').val();
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sMailUrl, {
                    filtro: JSON.stringify(filtro),
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

            $("#exportExcel").click(function(){
                var $validarForm = $("#result_form").valid();
                if (!$validarForm) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    bloquearUI('');
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
                    filtro['incluirMxInadecuadas']=($('#ckbmxinadecuada').is(':checked'));
                    filtro['codLabo'] = $('#codigoLab').find('option:selected').val();
                    $(this).attr("href",parametros.excelUrl+"?filtro="+JSON.stringify(filtro));
                    desbloquearUI();
                }
            });
        }
    };

}();
