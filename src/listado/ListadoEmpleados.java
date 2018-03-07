package listado;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ListadoEmpleados {
    private List<Empleado> listadoArchivo;

    private Map<String,Empleado> listado;

    public ListadoEmpleados(String rutaArchivo) throws IOException{
        listado = new HashMap<>();
        Stream<String> lineasEmpleados = Files.lines(Paths.get(rutaArchivo), StandardCharsets.ISO_8859_1);
        listadoArchivo = lineasEmpleados.map(empleado -> crearEmpleado(empleado)).collect(Collectors.toList());
    }

    private Empleado crearEmpleado(String lineaEmpleado){
        Pattern patron = Pattern.compile(",");
        List<String> datosEmpleado = patron.splitAsStream(lineaEmpleado).collect(Collectors.toList());
        return new Empleado(datosEmpleado.get(0), datosEmpleado.get(1), datosEmpleado.get(2), datosEmpleado.get(3));
    }

    // Obtener número de empleados en el archivo, para comprobar si la lectura de datos ha sido exitosa
    public int obtenerNumeroEmpleadosArchivo(){
        return listadoArchivo.size();
    }
}