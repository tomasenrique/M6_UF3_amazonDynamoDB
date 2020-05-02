package gestorDymanoDB.tabla;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class Tabla {

    private static final String PAIS = "Pais"; // Para la clave de particion
    private static final String FECHA = "Fecha"; // Para la clave de ordenacion
    // Campos adicionales para la tabla
    private static final String CONT_NUE = "Contagios_nuevos";
    private static final String CONT_ACU = "Contagios_acumulados";
    private static final String M_NUE = "Muertos_nuevos";
    private static final String M_ACU = "Muertos_acumulados";
    private static final String CURA_NUE = "Curados_nuevos";
    private static final String CURA_ACU = "Curados_acumulados";
    private static final String TEST_NUE = "Tests_nuevos";
    private static final String TEST_ACU = "Tests_acumulados";

    private Table table; // Para poder acceder a la tabla.

    // Builder
    public Tabla() {
    }

    // Methods

    /**
     * Este metodo crear una tabla usando como parametros un objeto de tipo dynamnodb y una cadena de texto para el
     * nombre de esta.
     *
     * @param dynamoDB    Sera el objeto de tipo DynamoDB con la conexiona la BBDD
     * @param nombreTabla Sera el nombre que tendra la tabla.
     */
    public void crearTabla(DynamoDB dynamoDB, String nombreTabla) {
        try {
            System.out.println("Creando la tabla: " + nombreTabla);
            table = dynamoDB.createTable(nombreTabla,
                    Arrays.asList(new KeySchemaElement(PAIS, KeyType.HASH), // Clave de partición
                            new KeySchemaElement(FECHA, KeyType.RANGE)), // Clave de ordenación para busqueda
                    Arrays.asList(new AttributeDefinition(PAIS, ScalarAttributeType.S), // Aqui se indica el tipo de dato, ver archivo 'notas'
                            new AttributeDefinition(FECHA, ScalarAttributeType.S)), // Aqui se indica el tipo de dato
                    new ProvisionedThroughput(10L, 10L)); // Indica la capacidad de lectura y escritura
            table.waitForActive(); // Se pone la tabla en espera para su uso porteriormente.
            System.out.println("Estado de la tabla: " + table.getDescription().getTableStatus()); // Muestra el estado de la tabla creada
        } catch (Exception e) {
            System.err.println("No se puede crear la tabla: ");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Este metodo elimina una tabla creada.
     *
     * @param dynamoDB    Sera el objeto de tipo DynamoDB con la conexiona la BBDD
     * @param nombreTabla Sera el nombre de la tabla a borrar
     */
    public void borrarTabla(DynamoDB dynamoDB, String nombreTabla) {
        table = dynamoDB.getTable(nombreTabla); // obtiene la tabla a eliminar
        try {
            System.out.println("Borrando la tabla...");
            table.delete();
            table.waitForDelete();
            System.out.print("La tabla ha sido borrada.");
        } catch (Exception e) {
            System.err.println("No se puede eliminar la tabla: ");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Este metodo mostrara los detalles de una tabla
     *
     * @param dynamoDB    Sera el objeto de tipo DynamoDB con la conexiona la BBDD
     * @param nombreTabla Sera el nombre de la tabla a describir
     */
    public void descripcionTabla(DynamoDB dynamoDB, String nombreTabla) {
        TableDescription tableDescription = dynamoDB.getTable(nombreTabla).describe();

        System.out.println("Id = " + tableDescription.getTableId());
        System.out.println("Nombre = " + tableDescription.getTableName());
        System.out.println("Recurso Arn = " + tableDescription.getTableArn());
        System.out.println("Tamaño = " + tableDescription.getTableSizeBytes());
        System.out.println("Estado = " + tableDescription.getTableStatus());
        System.out.println("Rendimiento Aprovisionado = " + tableDescription.getProvisionedThroughput());
        System.out.println("Fecha de creacion = " + tableDescription.getCreationDateTime());
    }


    /**
     * Este metodo se encagr de cargar los datos de un archivo JSON a la base de datos de amazon DymanoDB
     *
     * @param dynamoDB          Sera el objeto de tipo DynamoDB con la conexiona la BBDD
     * @param nombreTabla       Sera el nombre de la tabla donde insertar los datos
     * @param nombreArchivoJson Sera el nombre del archivo JSON a cargar sus datos en la tabla
     * @throws IOException Excepcion por si se genera un error al cargar los datos.
     */
    public void cargarDatos(DynamoDB dynamoDB, String nombreTabla, String nombreArchivoJson) throws IOException {
        Table table = dynamoDB.getTable(nombreTabla); // Para ubicar y conectar a la tabla
        // Ubica el archivo JSON  y lo parsea
        JsonParser parser = new JsonFactory().createParser(new File(nombreArchivoJson));
        // Crea las ramas o nodos para el documento
        JsonNode rootNode = new ObjectMapper().readTree(parser);
        // Se para a iterador para poder recorrerlo
        Iterator<JsonNode> iter = rootNode.iterator();
        ObjectNode currentNode; // instancia para poder almacenar los campos que tiene cada registro a insertar en la tabla

        System.out.print("Insertando datos.\n");
        while (iter.hasNext()) {
            currentNode = (ObjectNode) iter.next();
            //Claves principales
            String pais = currentNode.path(PAIS).asText(); // clave particion
            String fecha = currentNode.path(FECHA).asText(); //clave ordenacion
            // Datos para cargar en los campos, nombre de campo punto tipo.
            int conNuevos = currentNode.path(CONT_NUE).asInt();
            int conAcumulados = currentNode.path(CONT_ACU).asInt();
            int muNuevos = currentNode.path(M_NUE).asInt();
            int muAcumulados = currentNode.path(M_ACU).asInt();
            int curaNuevos = currentNode.path(CURA_NUE).asInt();
            int curaAcumulados = currentNode.path(CURA_ACU).asInt();
            int testNuevos = currentNode.path(TEST_NUE).asInt();
            int testAcumulados = currentNode.path(TEST_ACU).asInt();

            try {
                table.putItem(new Item().withPrimaryKey(PAIS, pais, FECHA, fecha) // campos clave, particion - ordenacion
                        // Campo adicionales para agregar a la tabla, nombre de atributo y valor.
                        .withNumber(CONT_NUE, conNuevos)
                        .withNumber(CONT_ACU, conAcumulados)
                        .withNumber(M_NUE, muNuevos)
                        .withNumber(M_ACU, muAcumulados)
                        .withNumber(CURA_NUE, curaNuevos)
                        .withNumber(CURA_ACU, curaAcumulados)
                        .withNumber(TEST_NUE, testNuevos)
                        .withNumber(TEST_ACU, testAcumulados)
                );
                System.out.println("Articulo agregado: " + pais + " " + fecha); // Muestra los datos agregados
            } catch (Exception e) {
                System.err.println("No se puede agregar el dato: " + pais + " " + fecha);
                System.err.println(e.getMessage());
                break;
            }
        }
        parser.close();
    }


    /**
     * Este metodo mostrara todo el contenido de la base de datos.
     *
     * @param dynamoDB    Sera el objeto de tipo DynamoDB con la conexiona la BBDD
     * @param nombreTabla Sera el nombre de la tabla elegida para mostrar sus datos
     */
    public void mostrarDatos(DynamoDB dynamoDB, String nombreTabla) {
        Table table = dynamoDB.getTable(nombreTabla); // Ubica y conecta a la tabla

        System.out.print("Mostrando datos de la tabla: " + nombreTabla);
        System.out.println();
        ScanSpec scanSpec = new ScanSpec(); // mostrara todo el contenido de la tabla

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec); // para obtener los item o campos
            Iterator<Item> iter = items.iterator(); // Para poder iterarlos para mostrarlos
            while (iter.hasNext()) {
                Item item = iter.next();
                System.out.println(item.toString()); // Muestra el contenido de la base  de datos
            }
        } catch (Exception e) {
            System.err.println("No se puede escanear la tabla:");
            System.err.println(e.getMessage());
        }
    }


}
