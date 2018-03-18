package tests;

import listado.Empleado;
import listado.ListadoEmpleados;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

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

    @Test
    public void hayDnisRepetidosArchivo(){
        assertEquals(true, empleados.hayDnisRepetidosArchivo());
    }

    @Test
    public void contarEmpleadosDnisRepetidos(){
        assertEquals(4, empleados.contarEmpleadosDnisRepetidos());
    }

    @Test
    public void repararDnisRepetidos(){
        Map<String, List<Empleado>> dnis_repetidos = empleados.obtenerDnisRepetidosArchivo();
        empleados.repararDnisRepetidos(dnis_repetidos);
        assertEquals(0, empleados.contarEmpleadosDnisRepetidos());
    }

    @Test
    public void hayCorreosRepetidosArchivo(){
        assertEquals(true, empleados.hayCorreosRepetidosArchivo());
    }

    @Test
    public void contarCorreosRepetidos(){
        assertEquals(315, empleados.contarCorreosRepetidos());
    }

    @Test
    public void repararCorreosRepetidos(){
        Map<String, List<Empleado>> correos_repetidos = empleados.obtenerCorreosRepetidosArchivo();
        empleados.repararCorreosRepetidos(correos_repetidos);
        assertEquals(0, empleados.contarCorreosRepetidos());
    }
}