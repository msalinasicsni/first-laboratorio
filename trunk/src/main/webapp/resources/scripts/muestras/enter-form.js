/**
 * Created by souyen-ics on 11-05-14.
 */

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
                $.getJSON(parametros.examenesUrl, {
                    codMx: $('#codTipoMx').val(),
                    tipoNoti: $('#tipoNoti').val(),
                    ajax: 'true'
                }, function (data) {
                    var len = data.length;
                    var html = null;
                    for (var i = 0; i < len; i++) {
                       html += '<option value="' + data[i].idExamen + '">'
                            + data[i].codExamen.valor
                            + '</option>';
                    }

                    $('#examenes').html(html);
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
                var datos_form = $('#registroMx').serialize();
                $.ajax({
                    type: "GET",
                    url: parametros.saveTomaUrl,
                    data: datos_form,
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function () {
                        $.smallBox({
                            title: $('#msjSuccessful').val() ,
                            content:  $('#disappear').val(),
                            color: "#739E73",
                            iconSmall: "fa fa-check-circle",
                            timeout: 2000
                        });

                        window.location.href = parametros.searchUrl;

                    },
                    error: function (status, er) {
                        alert("error: " + " status: " + status + " er:" + er);
                        $.smallBox({
                            title: $('#msjErrorSaving').val(),
                            content:  $('#disappear').val(),
                            color: "#C46A69",
                            iconSmall: "fa fa-warning",
                            timeout: 2000
                        });
                    }
                });

            }




        }
    }



}();