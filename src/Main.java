import listado.ListadoEmpleados;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        ListadoEmpleados listado = new ListadoEmpleados("data/datos.txt");
        System.out.println(listado.obtenerNumeroEmpleadosArchivo());
    }
}
