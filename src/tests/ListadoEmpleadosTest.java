package tests;

import listado.ListadoEmpleados;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ListadoEmpleadosTest {
    ListadoEmpleados empleados;

    @Before
    public void setUp() throws Exception {
    empleados = new ListadoEmpleados("data/datos.txt");
    }

    @Test
    public void obtenerNumeroEmpleadosArchivo() {
        assertEquals(5000, empleados.obtenerNumeroEmpleadosArchivo());
    }
}