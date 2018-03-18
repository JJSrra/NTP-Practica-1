package listado;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    // Conocer si hay DNIs repetidos en el archivo
    public boolean hayDnisRepetidosArchivo(){
        int total_dnis = obtenerNumeroEmpleadosArchivo();
        long dnis_distintos = listadoArchivo.stream().map(Empleado::obtenerDni).distinct().count();
        return (total_dnis != dnis_distintos);
    }

    // Obtener datos de empleados con DNIs repetidos
    public Map<String, List<Empleado>> obtenerDnisRepetidosArchivo(){
        // Primero se obtienen los DNIs con los datos de los empleados asociados
        Map<String, List<Empleado>> dnis = listadoArchivo.stream().collect(Collectors.groupingBy(Empleado::obtenerDni));

        // Y a continuación se filtran solo aquellos DNIs que correspondan a más de un empleado, almacenándolos en un Map
        Map<String, List<Empleado>> dnis_repetidos = dnis.entrySet().stream().filter(entrada -> entrada.getValue().size() > 1).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return dnis_repetidos;
    }

    // Conteo de empleados con DNI repetido, haciendo uso del método anterior
    public int contarEmpleadosDnisRepetidos(){
        Map<String, List<Empleado>> dnis_repetidos = obtenerDnisRepetidosArchivo();

        // Ahora obtenemos el número de empleados afectados, la suma de los valores del mapa
        int empleados_repetidos = dnis_repetidos.entrySet().stream().mapToInt(entrada -> entrada.getValue().size()).sum();
        return empleados_repetidos;
    }

    // Reparación de DNIs repetidos, generando nuevos DNIs para los empleados afectados
    public void repararDnisRepetidos(Map<String, List<Empleado>> lista_repeticion) {
        while (!lista_repeticion.isEmpty()) {
            lista_repeticion.values().stream().flatMap(empleado -> empleado.stream()).forEach(empleado -> {
                int indice_empleado = listadoArchivo.indexOf(empleado);
                listadoArchivo.get(indice_empleado).asignarDniAleatorio();
            });

            lista_repeticion = obtenerDnisRepetidosArchivo();
        }
    }

}