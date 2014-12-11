/**
 * Created by FIRSTICT on 11/11/2014.
 */
var CreatePerson = function () {

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

    var desbloquearUI = function() {
        setTimeout($.unblockUI, 500);
    };

    return {
        //main function to initiate the module
        init: function (parametros) {

            function agregarPersona(){
                var persona = {
                    idPersona: $("#idPersona").val(), // se pasa el id del maestro que se esta editando,
                    primerNombre: $("#primerNombre").val(),
                    segundoNombre: $("#segundoNombre").val(),
                    primerApellido: $("#primerApellido").val(),
                    segundoApellido: $("#segundoApellido").val(),
                    fechaNac: $("#fechaNacimiento").val(),
                    numAsegurado: $("#numAsegurado").val(),
                    numIdent: $('#numIdentificacion').val(),
                    direccion: $('#direccionReside').val(),
                    telReside: $("#telefReside").val(),
                    telMovil: $('#teleMovil').val(),
                    codSexo: $('#codSexo option:selected').val(),
                    codEstadoCivil: $('#codEstadoCivil option:selected').val(),
                    codTipIdent: $('#codTipIdent option:selected').val(),
                    codEtnia: $('#codEtnia option:selected').val(),
                    codEscolaridad: $('#codEscolaridad option:selected').val(),
                    codOcupacion: $('#codOcupacion option:selected').val(),
                    codTipoAseg: $('#codTipoAseg option:selected').val(),
                    codPaisNac: $('#codPaisNacimi option:selected').val(),
                    codMuniNac: $('#codMuniNacimi option:selected').val(),
                    codMuniRes: $('#codMuniReside option:selected').val(),
                    codComunidadRes: $('#codComuniReside option:selected').val()
                };
                var esEdicion = ($("#idPersona").val()!=null && $("#idPersona").val().trim().length > 0);
                var personaObj = {};
                personaObj['idPersona'] = '';
                personaObj['mensaje'] = '';
                personaObj['persona'] = persona;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sAgreparPersonaUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(personaObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content4s").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                var msg;
                                if (esEdicion){
                                    msg = $("#msg_person_updated").val();
                                }else{
                                    msg = $("#msg_person_added").val();
                                }
                                $.smallBox({
                                    title: msg ,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 2000
                                });
                                window.location.href = parametros.sPersonUrl + '/'+ data.idPersona;
                            }
                            desbloquearUI();
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    }
                )
            }

            $('#create-form').validate({
                // Rules for form validation
                rules: {
                    primerNombre: {
                        required: true
                    },
                    primerApellido: {
                        required: true
                    },
                    fechaNacimiento: {
                        required: true
                    },
                    codSexo: {
                        required: true
                    }
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    //table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    agregarPersona();
                }
            });


            <!-- al seleccionar pais de nacimiento -->
            $('#codPaisNacimi').change(function() {
                bloquearUI(parametros.blockMess);
                if ($(this).val().length > 0) {
                    if ($(this).val()=='NI'){
                        $('#codDepaNacimi').prop('disabled', false);
                        $('#codMuniNacimi').prop('disabled', false);
                        $('#codDepaNacimi').val($("#codDepaNacimiEd").val()).change();
                        setTimeout(function(){$('#codMuniNacimi').val($("#codMuniNacimiEd").val()).change();},200);
                    }else{
                        $('#codDepaNacimi').prop('disabled', 'disabled');
                        $('#codMuniNacimi').prop('disabled', 'disabled');
                        $('#codDepaNacimi').prop('selectedIndex', 0).change();
                        $('#codMuniNacimi').prop('selectedIndex', 0).change();
                    }
                }
                desbloquearUI();
            });

            <!-- al seleccionar departamento de nacimiento -->
            $('#codDepaNacimi').change(function(){
                bloquearUI(parametros.blockMess);
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sMunicipiosUrl, {
                        departamentoId: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].codigoNacional + '">'
                                + data[i].nombre
                                + '</option>';
                            // html += '</option>';
                        }
                        $('#codMuniNacimi').html(html);
                    })
                }else{
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codMuniNacimi').html(html);
                }
                $('#codMuniNacimi').val('').change();
                desbloquearUI();
            });

            <!-- al seleccionar departamento de residencia -->
            $('#codDepaReside').change(function(){
                bloquearUI(parametros.blockMess);
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sMunicipiosUrl, {
                        departamentoId: $(this).val(),
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].codigoNacional + '">'
                                + data[i].nombre
                                + '</option>';
                            // html += '</option>';
                        }
                        $('#codMuniReside').html(html);
                    })
                }else{
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codMuniReside').html(html);
                }
                $('#codMuniReside').val('').change();
                desbloquearUI();
            });

            <!-- al seleccionar municipio de residencia -->
            $('#codMuniReside').change(function(){
                bloquearUI(parametros.blockMess);
                if ($(this).val().length > 0) {
                    $.getJSON(parametros.sComunidadesUrl, {
                        municipioId: $(this).val(),
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
                        $('#codComuniReside').html(html);
                    })
                }else{
                    var html = '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    $('#codComuniReside').html(html);
                }
                $('#codComuniReside').val('').change();
                desbloquearUI();
            });

            $("#fechaNacimiento").change(
                function() {
                    if ($("#fechaNacimiento").val()!=null && $("#fechaNacimiento").val().length > 0) {
                        $("#edad").val(getAge($("#fechaNacimiento").val()));
                    }else{
                        $("#edad").val('');
                    }
                });
        }
    };

}();