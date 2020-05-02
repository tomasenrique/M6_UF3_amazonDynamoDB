package gestorDymanoDB;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.Arrays;

public class Pruebas {

    // Credenciales de acceso al servidor de DynamoDB
    private static final String ACCESS_KEY = "access_key_id";
    private static final String SECRET_KEY = "secret_key_id";
    // Enlace con el numero de puerto con el que trabaja la base de datos
    private static final String SERVICE_END_POINT = "http://localhost:8000";
    private static final String SIGNING_REGION = "us-west-2";  // Sera la region de firmas
    private static final String NOMBRE_TABLA = "datos_pais";

    public static void main(String[] args) {

        // CONENTANDO AL GESTOR DE BASE DE DATOS LOCAL DE AMAZON DYMANODB
        BasicAWSCredentials awsCredenciales = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonDynamoDB cliente = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SERVICE_END_POINT, SIGNING_REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredenciales))
                .build();
        DynamoDB dynamoDB = new DynamoDB(cliente); // Para devolver los datos de conexion a la BBDD local

        // CONECTANDO A LA TABLA
        Table table; // Para poder acceder a la tabla.
        try {
            System.out.println("Creando la tabla: " + NOMBRE_TABLA);
            table = dynamoDB.createTable(NOMBRE_TABLA,
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
}
