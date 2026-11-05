/*
 * Mejoras de las pantallas de peritos (catálogo, registro y edición).
 * Todo funciona igual sin JavaScript: el servidor siempre vuelve a validar.
 * Cada bloque revisa si su elemento existe en la página, así el mismo archivo sirve para las tres vistas.
 */
document.addEventListener('DOMContentLoaded', function () {

    // ---------- Catálogo: aplicar filtros al cambiar una lista ----------
    document.querySelectorAll('[data-autoenvio]').forEach(function (lista) {
        lista.addEventListener('change', function () {
            lista.form.submit();
        });
    });

    // ---------- Especialidades: agregar y quitar renglones ----------
    var contenedor = document.querySelector('[data-especialidades]');
    if (contenedor) {
        // Los nombres deben quedar seguidos: especialidades[0], especialidades[1], ...
        var renumerar = function () {
            contenedor.querySelectorAll('.per-especialidad input').forEach(function (campo, i) {
                campo.name = 'especialidades[' + i + ']';
                campo.id = 'especialidad' + i;
            });
        };

        document.querySelector('[data-agregar-especialidad]').addEventListener('click', function () {
            var renglones = contenedor.querySelectorAll('.per-especialidad');
            var nuevo = renglones[0].cloneNode(true);
            var campo = nuevo.querySelector('input');
            campo.value = '';
            campo.classList.remove('is-invalid');
            contenedor.appendChild(nuevo);
            renumerar();
            campo.focus();
        });

        contenedor.addEventListener('click', function (evento) {
            var boton = evento.target.closest('[data-quitar-especialidad]');
            if (!boton) {
                return;
            }
            var renglones = contenedor.querySelectorAll('.per-especialidad');
            if (renglones.length === 1) {
                // Siempre queda al menos un renglón: solo se limpia
                renglones[0].querySelector('input').value = '';
            } else {
                boton.closest('.per-especialidad').remove();
                renumerar();
            }
        });
    }

    // ---------- Teléfono: formato 777 000 0000 mientras se escribe ----------
    document.querySelectorAll('[data-telefono]').forEach(function (campo) {
        campo.addEventListener('input', function () {
            var digitos = campo.value.replace(/\D/g, '').slice(0, 10);
            var partes = [digitos.slice(0, 3), digitos.slice(3, 6), digitos.slice(6)];
            campo.value = partes.filter(function (p) { return p !== ''; }).join(' ');
        });
    });

    // ---------- Número de registro en mayúsculas (per-0057 → PER-0057) ----------
    document.querySelectorAll('[data-mayusculas]').forEach(function (campo) {
        campo.addEventListener('input', function () {
            campo.value = campo.value.toUpperCase();
        });
    });

    // ---------- Al corregir un campo con error, se quita la marca roja ----------
    document.querySelectorAll('form').forEach(function (formulario) {
        formulario.addEventListener('input', function (evento) {
            if (evento.target.classList) {
                evento.target.classList.remove('is-invalid');
            }
        });
    });

    // ---------- Materias: cuántas van seleccionadas ----------
    var contadorMaterias = document.querySelector('[data-contador-materias]');
    if (contadorMaterias) {
        var casillas = document.querySelectorAll('.per-materia');
        var actualizarContador = function () {
            var total = document.querySelectorAll('.per-materia:checked').length;
            contadorMaterias.textContent = total === 0
                ? 'Seleccione al menos una materia para la asignación.'
                : (total === 1 ? '1 materia seleccionada.' : total + ' materias seleccionadas.');
        };
        casillas.forEach(function (casilla) {
            casilla.addEventListener('change', actualizarContador);
        });
        actualizarContador();
    }

    // ---------- Estado en el padrón: solo se aplica si cambió, y con confirmación (DFR 3.3.10) ----------
    var formularioEstado = document.querySelector('[data-formulario-estado]');
    if (formularioEstado) {
        var botonAplicar = formularioEstado.querySelector('[data-aplicar-estado]');
        var estadoActual = formularioEstado.dataset.estadoActual;   // "true" o "false"
        var nombre = formularioEstado.dataset.nombre;
        var seleccionado = function () {
            return formularioEstado.querySelector('input[name="activo"]:checked').value;
        };

        var revisar = function () {
            botonAplicar.disabled = seleccionado() === estadoActual;
        };
        formularioEstado.querySelectorAll('input[name="activo"]').forEach(function (opcion) {
            opcion.addEventListener('change', revisar);
        });
        revisar();

        formularioEstado.addEventListener('submit', function (evento) {
            var mensaje = seleccionado() === 'false'
                ? '¿Dar de baja a ' + nombre + '? Quedará Inactivo y no recibirá asignaciones.'
                : '¿Reactivar a ' + nombre + '? Volverá a participar en la asignación automática.';
            if (!confirm(mensaje)) {
                evento.preventDefault();
            }
        });
    }

    // ---------- Evitar enviar dos veces el mismo formulario ----------
    document.querySelectorAll('form[method="post"]').forEach(function (formulario) {
        formulario.addEventListener('submit', function (evento) {
            if (evento.defaultPrevented) {
                return;
            }
            var botones = Array.from(formulario.querySelectorAll('[type="submit"]'));
            if (formulario.id) {
                // Botones que están fuera del formulario y lo envían con el atributo form="..."
                botones = botones.concat(Array.from(document.querySelectorAll('[form="' + formulario.id + '"]')));
            }
            // Se desactivan después de enviar para no cancelar el envío
            setTimeout(function () {
                botones.forEach(function (boton) { boton.disabled = true; });
            }, 0);
        });
    });
});
