/*
 * JavaScript propio de Copias Certificadas (EQUIPO 5).
 *   <button data-imprimir>  abre la ventana de impresión del navegador (comprobante)
 *   <input type="color" data-color="#idDelTexto">  mantiene igual el selector de color y su campo de texto
 */
(function () {
    'use strict';

    document.querySelectorAll('[data-imprimir]').forEach(function (boton) {
        boton.addEventListener('click', function () {
            window.print();
        });
    });

    // Selector de color ↔ campo de texto #RRGGBB (administración de colores)
    document.querySelectorAll('input[type="color"][data-color]').forEach(function (selector) {
        var texto = document.querySelector(selector.dataset.color);
        var muestra = document.querySelector(selector.dataset.muestra);
        if (!texto) {
            return;
        }
        function pintar(valor) {
            if (muestra) {
                muestra.style.background = valor;
            }
        }
        selector.addEventListener('input', function () {
            texto.value = selector.value.toUpperCase();
            pintar(selector.value);
        });
        texto.addEventListener('input', function () {
            if (/^#[0-9A-Fa-f]{6}$/.test(texto.value)) {
                selector.value = texto.value;
                pintar(texto.value);
            }
        });
    });
})();
