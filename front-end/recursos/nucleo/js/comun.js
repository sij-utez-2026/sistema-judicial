/*
 * JavaScript común a todas las pantallas (INTEGRADORES). Lo carga layout.tag después de Bootstrap.
 * Todo es una mejora: las pantallas funcionan igual sin JavaScript y el servidor siempre vuelve a validar.
 *
 * Lo que ofrece a los módulos:
 *   SIJ.confirmar(mensaje)      → Promise<boolean> con la ventana de confirmación del sistema
 *   SIJ.encabezadosCsrf()        → { 'X-CSRF-TOKEN': '...' } para usar en fetch con POST
 *   <form data-confirmar="¿...?">  pide confirmación antes de enviar (lo usa <n:confirmar>)
 *   <select data-autoenvio>        envía su formulario al cambiar (filtros)
 *   <input type="file" data-max-mb="5">  avisa si el archivo pasa del tamaño (lo usa <n:archivo>)
 */
window.SIJ = (function () {

    // ---------- Ventana de confirmación (modal #sijConfirmar del layout) ----------
    function confirmar(mensaje) {
        var ventana = document.getElementById('sijConfirmar');
        if (!ventana || !window.bootstrap) {
            return Promise.resolve(window.confirm(mensaje));
        }
        return new Promise(function (resolver) {
            var aceptado = false;
            var modal = bootstrap.Modal.getOrCreateInstance(ventana);
            ventana.querySelector('[data-mensaje]').textContent = mensaje;
            var botonAceptar = ventana.querySelector('[data-aceptar]');

            var alAceptar = function () {
                aceptado = true;
                modal.hide();
            };
            var alCerrar = function () {
                botonAceptar.removeEventListener('click', alAceptar);
                ventana.removeEventListener('hidden.bs.modal', alCerrar);
                resolver(aceptado);
            };
            botonAceptar.addEventListener('click', alAceptar);
            ventana.addEventListener('hidden.bs.modal', alCerrar);
            modal.show();
        });
    }

    // ---------- Token CSRF para fetch (lo pone el layout en <meta name="_csrf">) ----------
    function encabezadosCsrf() {
        var token = document.querySelector('meta[name="_csrf"]');
        var encabezado = document.querySelector('meta[name="_csrf_header"]');
        var resultado = {};
        if (token && encabezado) {
            resultado[encabezado.content] = token.content;
        }
        return resultado;
    }

    return {confirmar: confirmar, encabezadosCsrf: encabezadosCsrf};
})();

document.addEventListener('DOMContentLoaded', function () {

    // ---------- Avisos de éxito (<n:mensajes>): se ocultan solos a los 6 segundos ----------
    if (window.bootstrap) {
        document.querySelectorAll('.toast[data-sij-aviso]').forEach(function (aviso) {
            new bootstrap.Toast(aviso, {delay: 6000}).show();
        });
    }

    // ---------- Filtros: enviar el formulario al cambiar una lista ----------
    document.querySelectorAll('[data-autoenvio]').forEach(function (lista) {
        lista.addEventListener('change', function () {
            lista.form.submit();
        });
    });

    // ---------- Al corregir un campo con error, se quita la marca roja ----------
    document.addEventListener('input', function (evento) {
        if (evento.target.classList) {
            evento.target.classList.remove('is-invalid');
        }
    });

    // ---------- Archivos: tamaño máximo (<n:archivo maxMb="5">) ----------
    document.querySelectorAll('input[type="file"][data-max-mb]').forEach(function (campo) {
        campo.addEventListener('change', function () {
            var maximo = parseFloat(campo.dataset.maxMb) * 1024 * 1024;
            var muyGrande = Array.from(campo.files).some(function (archivo) { return archivo.size > maximo; });
            var aviso = campo.parentElement.querySelector('[data-aviso-archivo]');
            campo.classList.toggle('is-invalid', muyGrande);
            if (aviso) {
                aviso.textContent = muyGrande ? 'El archivo supera el tamaño máximo de ' + campo.dataset.maxMb + ' MB' : '';
            }
            if (muyGrande) {
                campo.value = '';
            }
        });
    });

    // ---------- Envío de formularios POST ----------
    document.addEventListener('submit', function (evento) {
        var formulario = evento.target;
        if (evento.defaultPrevented || (formulario.method || '').toLowerCase() !== 'post') {
            return;
        }

        // 1. Confirmación (<n:confirmar> o data-confirmar="...")
        if (formulario.dataset.confirmar && !formulario.dataset.confirmado) {
            evento.preventDefault();
            var boton = evento.submitter;
            SIJ.confirmar(formulario.dataset.confirmar).then(function (aceptado) {
                if (aceptado) {
                    formulario.dataset.confirmado = 'si';
                    formulario.requestSubmit(boton || undefined);
                }
            });
            return;
        }

        // 2. Evitar enviar dos veces: se desactivan los botones después de enviar
        var botones = Array.from(formulario.querySelectorAll('[type="submit"]'));
        if (formulario.id) {
            // Botones fuera del formulario que lo envían con el atributo form="..."
            botones = botones.concat(Array.from(document.querySelectorAll('[form="' + formulario.id + '"]')));
        }
        setTimeout(function () {
            botones.forEach(function (b) { b.disabled = true; });
        }, 0);
    });
});
