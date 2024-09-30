import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Clase principal encargada de abrir un archivo llamado
 * "Prueba.txt", tomar los valores en él y con ayuda de la
 * clase GenerarWAV crear un archivo WAV con ayuda de los
 * parámetros leídos
 * @author Luis Eduardo García Martínez
 * @version 1.0
 */
public class EjecutaWAV {
    /**
     * Método main el cuál realiza el processo de apertura de archivo,
     * almacenamiento de valores, arrojar excepciones y comunicación
     * con la clase GenerarWAV
     * @param args
     * @throws NullPointerException Excepción arrojada en caso de que la línea leída esté vacía
     * @throws IllegalArgumentException Excepción arrojada en caso de que el valor leído sea negativo o igual a 0
     */
    public static void main(String[] args) throws NullPointerException, IllegalArgumentException {
        // Variables que almacenarán la 
        // información que hay en Prueba.txt
        String name = "";
        String auxiliar = "";
        int iFrecuenciaMuestreo;
        short canales;
        int armonico;
        int iTiempo;

        // Al indicar de esta manera la creacion del archivo indicamos autocierre
        // es decir que no requerimos colocar al final <<nombre>>.close()
        try (RandomAccessFile prueba = new RandomAccessFile("Prueba.txt", "r")) {
            name = prueba.readLine();
            if(!(name.substring(name.length() - 4, name.length())).equals(".wav"))      //Validación de la extensión en el nombre del archivo
                System.out.println("Falta la extensión (.wav) en el nombre del archivo...");
            if(name.equals(""))
                throw new NullPointerException("No se permite valor nulo para el nombre del archivo...");
            
            auxiliar = prueba.readLine();               // Lectura de la frecuencia de muestreo
            if(auxiliar.equals(""))
                throw new NullPointerException("No se permite valor nulo para la frecuencia de muestreo...");
            else if(Integer.parseInt(auxiliar) <= 0)
                throw new IllegalArgumentException("No se permite un valor negativo para la frecuencia de muestreo");
            else    
                iFrecuenciaMuestreo = Integer.parseInt(auxiliar);
                
            auxiliar = prueba.readLine();               // Lectura del número de canales
            if(auxiliar.equals(""))
                throw new NullPointerException("No se permite valor nulo para los canales...");
            else if(Short.parseShort(auxiliar) <= 0)
                throw new IllegalArgumentException("No se permite un valor negativo para la frecuencia de muestreo");
            else 
                canales = Short.parseShort(auxiliar);
                
            auxiliar = prueba.readLine();               // Frecuencia con la que se escuchara la nota
            if(auxiliar.equals(""))
                throw new NullPointerException("No se permite valor nulo para la frecuencia a escuchar...");
            else if(Integer.parseInt(auxiliar) <= 0)
                throw new IllegalArgumentException("No se permite un valor negativo para la frecuencia a escuchar...");
            else 
                armonico = Integer.parseInt(auxiliar);

            auxiliar = prueba.readLine();               // Lectura del tiempo
            if(auxiliar.equals(""))
                throw new NullPointerException("No se permite valor nulo para el tiempo...");
            else if(Integer.parseInt(auxiliar) <= 0)
                throw new IllegalArgumentException("No se permite un valor negativo para el tiempo...");
            else 
                iTiempo = Integer.parseInt(auxiliar);
                
            // Instancia de la clase GenerarWAV
            GenerarWAV generarWAV = new GenerarWAV(canales);
            generarWAV.escribe(name, iTiempo, iFrecuenciaMuestreo, armonico);

        } catch (FileNotFoundException e) {             // Excepción generada si el archivo no existe
            System.out.println(e.getMessage());     
        } catch (IOException e) {                       // Excepcion generada si hay errores en entrada o salida
            System.out.println(e.getMessage());
        }
    }
}