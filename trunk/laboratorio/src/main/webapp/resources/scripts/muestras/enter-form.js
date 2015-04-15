/**
 * Created by souyen-ics on 11-05-14.
 */
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

var EnterFormTomaMx = function () {

    return {
        init: function (parametros) {

            $('.datetimepicker').datetimepicker({
                language: 'es',
               format: 'DD/MM/YYYY h:m A'

            });

            $('#horaRefrigeracion').datetimepicker({
                pickDate: false
            });

            $('#codTipoMx').change(function() {
                $.getJSON(parametros.dxUrl, {
                    codMx: $('#codTipoMx').val(),
                    tipoNoti: $('#tipoNoti').val(),
                    ajax: 'true'
                }, function (data) {
                    var len = data.length;
                    var html = null;
                    for (var i = 0; i < len; i++) {
                        console.log(data[i]);
                       html += '<option value="' + data[i].diagnostico.idDiagnostico + '">'
                            + data[i].diagnostico.nombre
                            + '</option>';
                    }

                    $('#dx').html(html);
                });
            });


         var $validator = $("#registroMx").validate({
                rules: {
                    fechaHTomaMx: {
                        required: true

                    },

                    codTipoMx:{
                        required:true
                    },

                    examenes:{
                        required:true
                    }

                },

                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                }

              /*  submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                   save();
                }*/
            });


            $('#submit').click(function() {
                var $validarForm = $("#registroMx").valid();
                if (!$validarForm) {
                    $validator.focusInvalid();
                    return false;
                }else{
                   save();

                }

            });

            function save() {
                var objetoTomaMx = {};
                objetoTomaMx['idNotificacion'] = $("#idNotificacion").val();
                objetoTomaMx['fechaHTomaMx'] = $("#fechaHTomaMx").val();
                objetoTomaMx['canTubos'] = $("#canTubos").val();
                objetoTomaMx['volumen'] = $("#volumen").val();
                objetoTomaMx['horaRefrigeracion'] = $("#horaRefrigeracion").val();
                objetoTomaMx['codTipoMx'] = $('#codTipoMx').find('option:selected').val();
                objetoTomaMx['dx'] = $('#dx').val();
                objetoTomaMx['categoriaMx'] = $('#codCategoriaMx').find('option:selected').val();
                objetoTomaMx['mensaje'] = '';
                bloquearUI(parametros.blockMess);
                $.ajax({
                    url: parametros.saveTomaUrl,
                    type: 'POST',
                    dataType: 'json',
                    data: JSON.stringify(objetoTomaMx),
                    contentType: 'application/json',
                    mimeType: 'application/json',
                    success: function (data) {
                        desbloquearUI();
                        if (data.mensaje.length > 0) {
                            $.smallBox({
                                title: data.mensaje,
                                content: $("#disappear").val(),
                                color: "#C46A69",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        } else {
                            $.smallBox({
                                title: $('#msjSuccessful').val(),
                                content: $('#disappear').val(),
                                color: "#739E73",
                                iconSmall: "fa fa-check-circle",
                                timeout: 4000
                            });
                            setTimeout(function () {
                                window.location.href = parametros.searchUrl;
                            }, 4000);
                        }

                    },
                    error: function (data, status, er) {
                        desbloquearUI();
                        $.smallBox({
                            title: $('#msjErrorSaving').val() + " error: " + data + " status: " + status + " er:" + er,
                            content: $('#disappear').val(),
                            color: "#C46A69",
                            iconSmall: "fa fa-warning",
                            timeout: 5000
                        });
                    }
                });
            }




        }
    }



}();
