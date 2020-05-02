package GestorDymanoDB.tabla;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.Arrays;

public class Tabla {


    // Builder
    public Tabla() {
    }

    // Methods

    /**
     * Este metodo crear una tabla usando como parametros un objeto de tipo dynamnodb y una cadena de texto para el
     * nombre de esta.
     *
     * @param dynamoDB    Serael objeto de tipo DynamoDB con la conexiona la BBDD
     * @param nombreTabla Sera el nombre que tendra la tabla.
     */
    public void crearTabla(DynamoDB dynamoDB, String nombreTabla) {
        try {
            System.out.println("Creando la tabla: " + nombreTabla);
            Table table = dynamoDB.createTable(nombreTabla,
                    Arrays.asList(new KeySchemaElement("Pais", KeyType.HASH), // Clave de partición
                            new KeySchemaElement("Fecha", KeyType.RANGE)), // Clave de ordenación para busqueda
                    Arrays.asList(new AttributeDefinition("Pais", ScalarAttributeType.S), // Aqui se indica el tipo de dato, ver archivo 'notas'
                            new AttributeDefinition("Fecha", ScalarAttributeType.S)), // Aqui se indica el tipo de dato
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
     * @param dynamoDB    Serael objeto de tipo DynamoDB con la conexiona la BBDD
     * @param nombreTabla Sera el nombre de la tabla a borrar
     */
    public void borrarTabla(DynamoDB dynamoDB, String nombreTabla) {
        Table table = dynamoDB.getTable(nombreTabla); // obtiene la tabla a eliminar

        try {
            System.out.println("Borrando la tabla...");
            table.delete();
            table.waitForDelete();
            System.out.print("La tabla ha sido borrada.");
        }
        catch (Exception e) {
            System.err.println("No se puede eliminar la tabla: ");
            System.err.println(e.getMessage());
        }

    }

    /**
     * Este metodo mostrara los detalles de una tabla
     *
     * @param dynamoDB    Serael objeto de tipo DynamoDB con la conexiona la BBDD
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


}
