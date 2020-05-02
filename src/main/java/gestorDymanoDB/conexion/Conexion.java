package gestorDymanoDB.conexion;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class Conexion {

    // Credenciales de acceso al servidor de DynamoDB
    private static final String ACCESS_KEY = "access_key_id";
    private static final String SECRET_KEY = "secret_key_id";
    // Enlace con el numero de puerto con el que trabaja la base de datos
    private static final String SERVICE_END_POINT = "http://localhost:8000";
    private static final String SIGNING_REGION = "us-west-2";  // Sera la region de firmas
    private DynamoDB dynamoDB;

    // Builder
    public Conexion() {
    }

    // Method

    /**
     * Este metodo devolvera la conexion a la bse de datos local de amazon DynamoDB
     *
     * @return Retorna la conexion a la base de datos
     */
    public DynamoDB getConexion() {
        long before = getTime(); // Inicia el conteo de tiempo de inicio de la conexion a la BD

        BasicAWSCredentials awsCredenciales = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonDynamoDB cliente = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SERVICE_END_POINT, SIGNING_REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredenciales))
                .build();

        long after = getTime(); // Finaliza el conteo de tiempo
        long milisegundos = after - before; // se resta y eso es lo que tarda en conectarse a la BBDD
        System.out.println("Conectado a la Base de datos local de Amazon DynamoDB.");
        System.out.println("Tiempo de conexion: " + milisegundos + " milisegundos");

        dynamoDB = new DynamoDB(cliente); // Para devolver los datos de conexion a la BBDD local
        return dynamoDB;
    }

    // Metodo para contar el tiempo.
    private static long getTime() {
        return System.currentTimeMillis();
    }

}
