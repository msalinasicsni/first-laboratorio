
var tiemposProcesamiento = function () {

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

            $("#exportExcel").click(function(){
                var $validarForm = $("#result_form").valid();
                if (!$validarForm) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    bloquearUI(parametros.blockMess);
                    var filtro = {};
                    filtro['fechaInicio'] = $('#initDate').val();
                    filtro['fechaFin'] = $('#endDate').val();
                    filtro['codSilais'] = $('#codSilais').find('option:selected').val();
                    filtro['codUnidadSalud'] = $('#codUnidadAtencion').find('option:selected').val();
                    filtro['codMunicipio'] = $('#codMunicipio').find('option:selected').val();
                    filtro['codArea'] = $('#codArea').find('option:selected').val();
                    filtro['porSilais'] = "true"; //$('input[name="rbNivelPais"]:checked', '#result_form').val();
                    filtro['idDx'] = $('#idDx').find('option:selected').val();
                    filtro['codLabo'] = $('#codigoLab').find('option:selected').val();
                    $(this).attr("href",parametros.sExcelUrl+"?filtro="+JSON.stringify(filtro));
                    desbloquearUI();
                }
            });

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
                                html += '<option value="' + data[i].codigo + '">'
                                    + data[i].nombre + '</option>';
                            }
                            $('#codUnidadAtencion').html(html);
                            $('#codUnidadAtencion').focus();
                            $('#s2id_codUnidadAtencion').addClass('select2-container-active');
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
        }
    };

}();
