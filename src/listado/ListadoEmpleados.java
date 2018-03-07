package listado;

import listado.Empleado;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class ListadoEmpleados {
    private List<Empleado> listadoArchivo;

    private Map<String,Empleado> listado;

    public ListadoEmpleados(String rutaArchivo) throws IOException{
        Stream<String> lineasEmpleados = Files.lines(Paths.get("datos.txt"), StandardCharsets.ISO_8859_1);

    }

    private Empleado crearEmpleado(String lineaEmpleado){
        return new Empleado("88888888", "John", "Doe", "jdoe@acme.com");
    }

    // Obtener número de empleados en el archivo, para comprobar si la lectura de datos ha sido exitosa
    public Integer obtenerNumeroEmpleadosArchivo(){
        return listadoArchivo.size();
    }
}
