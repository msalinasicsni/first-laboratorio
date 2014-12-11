var handleInputMasks = function () {
    $.extend($.inputmask.defaults, {
        'autounmask': true
    });

    $("#mesEpi").inputmask({
        "mask": "9",
        "repeat": 2,
        "greedy": false
    });

    $("#anioEpi").inputmask({
        "mask": "9",
        "repeat": 4,
        "greedy": true
    });

    $("#semanaEpi").inputmask({
        "mask": "9",
        "repeat": 2,
        "greedy": false
    });

    $(".entero").inputmask({
        "mask": "9",
        "repeat": 6,
        "greedy": false
    });
    $(".decimal").inputmask("decimal",{
         allowMinus: false,
        radixPoint: ".",
        digits: 2
    });
    $(".telefono").inputmask({
        "mask": "9",
        "repeat": 9,
        "greedy": false
    });
};
