package gestorDymanoDB.vista;

import gestorDymanoDB.conexion.Conexion;
import gestorDymanoDB.tabla.Tabla;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import java.io.IOException;
import java.util.Scanner;

public class Pantalla {

    private final static int CREATE_TABLE = 1;
    private final static int DELETE_TABLE = 2;
    private final static int DESCRIBE_TABLE = 3;
    private final static int INSERT_DATA_TABLE = 4;
    private final static int SHOW_DATA_TABLE = 5;
    private final static int SALIR = 0;
    private final static Scanner lectura = new Scanner(System.in); // para la lectura de las opciones

    private Conexion conexion;
    private DynamoDB dynamoDB;
    private Tabla tabla;


    // Builder
    public Pantalla() {
        conexion = new Conexion(); // Se implementa la clase para la conexion a la BBDD
        tabla = new Tabla(); // Se implementa la clase para crear o borrar tablas.
        try {
            dynamoDB = conexion.getConexion(); // Se obtiene la conexiona la BBDD local
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Funcion para iniciar el programa principal
     */
    public void inicio()  {
        int opcion; // opcion elegida
        do {
            menuPrincipal();
            opcion = elegirOpcion();
            switch (opcion) {
                case SALIR:
                    break;

                case CREATE_TABLE:
                    System.out.print("Ingrese como el nombre de la tabla: ");
                    tabla.crearTabla(dynamoDB, lectura.nextLine());
                    break;

                case DELETE_TABLE:
                    System.out.print("Ingrese el nombre de la tabla a eliminar: ");
                    tabla.borrarTabla(dynamoDB, lectura.nextLine());
                    break;

                case DESCRIBE_TABLE:
                    System.out.print("Ingrese el nombre de la tabla para dar descripcion: ");
                    tabla.descripcionTabla(dynamoDB, lectura.nextLine());
                    break;

                case INSERT_DATA_TABLE:
                    System.out.print("Ingrese el nombre de la tabla para cargar los datos: ");
                    String nombreTablaCargar = lectura.nextLine();
                    System.out.print("Ingrese el nombre del archivo JSON a cargar: ");
                    String archivoJsonCargar = lectura.nextLine() + ".json";

                    try {
                        tabla.cargarDatos(dynamoDB, nombreTablaCargar, archivoJsonCargar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case SHOW_DATA_TABLE:
                    System.out.print("Ingrese el nombre de la tabla a mostrar: ");
                    String nombreTablaMostrar = lectura.nextLine();
                    tabla.mostrarDatos(dynamoDB, nombreTablaMostrar);
                    break;

                default:
                    System.out.println("Opcion incorrecta");
                    break;
            }
        } while (opcion != SALIR);
    }


    /**
     * Mostra el menú principal de la aplicación.
     */
    private static void menuPrincipal() {
        System.out.println("\n\nMENU PRINCIPAL");
        System.out.println("1. Crear tabla");
        System.out.println("2. Borrar tabla");
        System.out.println("3. Describir tabla.");
        System.out.println("4. Cargar datos en la tabla.");
        System.out.println("5. Mostrar todos los datos de la tabla");
        System.out.println("0. Salir");
        System.out.print("\tOpcion: ");
    }

    /**
     * Lee un entero que representa una opción de menos.
     *
     * @return un entero, -1 si la entrada no se numerica
     */
    private static int elegirOpcion() {
        int opcion = 0;
        try {
            opcion = Integer.parseInt(lectura.nextLine());
        } catch (NumberFormatException e) {
            opcion = -1;
        }
        return opcion;
    }

}
