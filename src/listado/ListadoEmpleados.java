package listado;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class ListadoEmpleados {
    private List<Empleado> listadoArchivo;

    private Map<String,Empleado> listado;

    private Pattern patron_espacios = Pattern.compile("\\s+");


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

    // Conocer si hay correos repetidos en el archivo
    public boolean hayCorreosRepetidosArchivo(){
        int total_correos = obtenerNumeroEmpleadosArchivo();
        long correos_distintos = listadoArchivo.stream().map(Empleado::obtenerCorreo).distinct().count();
        return (total_correos != correos_distintos);
    }

    // Obtener datos de empleados con correos repetidos
    public Map<String, List<Empleado>> obtenerCorreosRepetidosArchivo(){
        // Primero se obtienen los correos con los datos de los empleados asociados
        Map<String, List<Empleado>> correos = listadoArchivo.stream().collect(Collectors.groupingBy(Empleado::obtenerCorreo));

        // Y a continuación se filtran solo aquellos correos que correspondan a más de un empleado, almacenándolos en un Map
        Map<String, List<Empleado>> correos_repetidos = correos.entrySet().stream().filter(entrada -> entrada.getValue().size() > 1).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return correos_repetidos;
    }

    // Conteo de empleados con correo repetido, haciendo uso del método anterior
    public int contarCorreosRepetidos(){
        Map<String, List<Empleado>> correos_repetidos = obtenerCorreosRepetidosArchivo();

        // Ahora obtenemos el número de empleados afectados, la suma de los valores del mapa
        int empleados_repetidos = correos_repetidos.entrySet().stream().mapToInt(entrada -> entrada.getValue().size()).sum();
        return empleados_repetidos;
    }

    // Reparación de correos repetidos, generando nuevos correos para los empleados afectados
    public void repararCorreosRepetidos(Map<String, List<Empleado>> lista_repeticion) {
        while (!lista_repeticion.isEmpty()) {
            lista_repeticion.values().stream().flatMap(empleado -> empleado.stream()).forEach(empleado -> {
                int indice_empleado = listadoArchivo.indexOf(empleado);
                listadoArchivo.get(indice_empleado).generarCorreoCompleto();
            });

            lista_repeticion = obtenerCorreosRepetidosArchivo();
        }
    }

    // Guardar todos los datos en la variable listado, una vez reparados los errores
    public void validarListaArchivo(){
        // Reparar DNIs
        Map<String, List<Empleado>> dnis_repetidos = obtenerDnisRepetidosArchivo();
        repararDnisRepetidos(dnis_repetidos);

        // Reparar correos
        Map<String, List<Empleado>> correos_repetidos = obtenerCorreosRepetidosArchivo();
        repararCorreosRepetidos(correos_repetidos);

        // Almacenar los empleados en listado, con un par (DNI, empleado)
        listado = listadoArchivo.stream().collect(Collectors.toMap(Empleado::obtenerDni, empleado -> empleado));
    }

    public long cargarArchivoAsignacionSector(String ruta_archivo) throws IOException{
        List<String> lineas_archivo = Files.lines(Paths.get(ruta_archivo), StandardCharsets.ISO_8859_1).collect(Collectors.toList());

        Sector sector = procesarNombreSector(lineas_archivo.get(0));

        long errores = lineas_archivo.stream().skip(2).map(linea -> procesarAsignacionSector(sector, linea)).
                filter(flag -> flag == false).count();

        return errores;
    }

    public Sector procesarNombreSector(String nombre){
        List<String> infos = patron_espacios.splitAsStream(nombre).collect(Collectors.toList());

        Predicate<Sector> condicion = sector -> (sector.name().equals(infos.get(0)));
        Sector nuevo_sector = Arrays.stream(Sector.values()).filter(condicion).findFirst().get();

        return nuevo_sector;
    }

    public boolean procesarAsignacionSector(Sector sector, String linea){
        List<String> infos = patron_espacios.splitAsStream(linea).collect(Collectors.toList());
        Empleado empleado = listado.get(infos.get(0));

        if (empleado != null){
            empleado.asignarSector(sector);
            return true;
        }
        else
            return false;
    }

    public long cargarArchivoAsignacionRuta(String ruta_archivo) throws IOException{
        List<String> lineas_archivo = Files.lines(Paths.get(ruta_archivo), StandardCharsets.ISO_8859_1).collect(Collectors.toList());

        Ruta ruta = procesarNombreRuta(lineas_archivo.get(0));

        long errores = lineas_archivo.stream().skip(2).map(linea -> procesarAsignacionRuta(ruta, linea)).
                filter(flag -> flag == false).count();

        return errores;
    }
    
    public Ruta procesarNombreRuta(String nombre){
        List<String> infos = patron_espacios.splitAsStream(nombre).collect(Collectors.toList());

        Predicate<Ruta> condicion = ruta -> (ruta.name().equals(infos.get(0)));
        Ruta nueva_ruta = Arrays.stream(Ruta.values()).filter(condicion).findFirst().get();

        return nueva_ruta;
    }

    public boolean procesarAsignacionRuta(Ruta ruta, String linea){
        List<String> infos = patron_espacios.splitAsStream(linea).collect(Collectors.toList());
        Empleado empleado = listado.get(infos.get(0));

        if (empleado != null){
            empleado.asignarRuta(ruta);
            return true;
        }
        else
            return false;
    }

    public Map<Ruta,Long> obtenerContadoresRuta(Sector sector){
        Map<Ruta,Long> contadores = listado.values().stream().
                filter(empleado -> empleado.obtenerSector() == sector).
                collect(Collectors.groupingBy(Empleado::obtenerRuta, TreeMap::new, Collectors.counting()));

        return contadores;
    }

    public Map<Sector, Map<Ruta,Long>> obtenerContadoresSectorRuta(){
        // Se devuelven ordenados por orden alfanumérico para que el test se pueda realizar correctamente
        return Arrays.stream(Sector.values()).
                sorted(Comparator.comparing(Sector::ordinal)).
                collect(Collectors.toMap(sector -> sector, sector -> obtenerContadoresRuta(sector)));
    }

    public List<Long> obtenerContadoresSectores(){
        List<Long> contadores = obtenerContadoresSectorRuta().values().stream().map(Map::values).
                map(contador -> contador.stream().reduce(0L, Long::sum)).collect(Collectors.toList());

        return contadores;
    }
}