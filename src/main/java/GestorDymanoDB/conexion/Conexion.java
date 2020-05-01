package GestorDymanoDB.conexion;

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
        BasicAWSCredentials awsCredenciales = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonDynamoDB cliente = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SERVICE_END_POINT, SIGNING_REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredenciales))
                .build();
        return new DynamoDB(cliente); // Para devolver los datos de conexion a la BBDD local
    }


}
