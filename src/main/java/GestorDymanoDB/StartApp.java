package GestorDymanoDB;

import GestorDymanoDB.conexion.Conexion;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class StartApp {
    public static void main(String[] args) {

        Conexion conexion = new Conexion();

        DynamoDB dynamoDB =  conexion.getConexion();

        if (dynamoDB != null){
            System.out.println("Conectado");
        }else {
            System.out.println("No conectado");
        }


    }
}
