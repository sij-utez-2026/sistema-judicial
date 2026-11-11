package mx.edu.utez.sij.copias.temporal;

import mx.edu.utez.sij.copias.RolesCopias;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** TEMPORAL: "Modo de prueba" y revisión de rol (se borra junto con AccesoTemporal). */
class AccesoTemporalTest {

    private final SesionTemporal sesion = new SesionTemporal();
    private final AccesoTemporal acceso = new AccesoTemporal(sesion);

    @BeforeEach
    void simularPeticion() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void limpiar() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void porDefectoEsTrabajador() {
        assertEquals(RolesCopias.PERSONAL, sesion.rol());
        assertFalse(sesion.esAdmin());
        assertDoesNotThrow(() -> acceso.exigirRol(RolesCopias.PERSONAL));
    }

    @Test
    void cadaRolSoloEntraASusPantallas() {
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> acceso.exigirRol(RolesCopias.ADMIN));
        assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

        sesion.cambiarRol(RolesCopias.ADMIN);
        assertTrue(sesion.esAdmin());
        assertDoesNotThrow(() -> acceso.exigirRol(RolesCopias.ADMIN));
        assertThrows(ResponseStatusException.class, () -> acceso.exigirRol(RolesCopias.PERSONAL));
    }

    @Test
    void soloAceptaLosRolesDelModulo() {
        assertThrows(ReglaNegocioException.class, () -> sesion.cambiarRol("SISTEMA_ADMIN"));
        assertEquals(RolesCopias.PERSONAL, sesion.rol());
    }
}
