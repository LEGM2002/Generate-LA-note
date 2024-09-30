import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Clase que tiene como objetivo generar un archivo en 
 * formato WAV de la nota La, dependiendo su nombre, el tiempo, 
 * frecuencia de muestreo y su armónico, se accede a ella desde
 * la clase principal EjecutaWAV
 * @author Luis Eduardo García Martínez
 * @version 1.0
 */
public class GenerarWAV{
    // Atributo
    /**
     * Toma valor del número de canales en los audios, sin 
     * embargo, para este proyecto solo se considera un sonido monoaural,
     * es decir, de un solo canal
     */
    private short Canales;

    // Constructor vacío
    /**
     * Constructor vacío
     */
    public GenerarWAV(){}

    // Constructor no vacío
    /**
     * Constructor no vacío para instanciar un objeto de esta clase
     * @param Canales Número de canales que tendrá el audio a generar
     */
    public GenerarWAV(short Canales){
        this.Canales = Canales;
    }

    /**
     * Método para obtener el número de canales
     * @return retorna el número de canales del objeto actual
     */
    public short getCanales(){
        return this.Canales;
    }

    /**
     * Método encargado de escribir en un archivo todos los datos correspondientes para que
     * sea de formato WAV
     * @param name Nombre del archivo WAV a crear con su respectiva extensión .wav
     * @param iTiempo Duración del sonido, expresado en segundos
     * @param iFrecuenciaMuestreo Frecuencia con la que se realizará el muestreo, expresada en Hertz
     * @param armonico Frecuencia con la que deseamos escuchar en el canal, expresada en Hertz
     */
    public void escribe(String name, int iTiempo, int iFrecuenciaMuestreo, int armonico) {
        // Constantes necesarias para la creación del archivo
        final int amplitud = 32767;                                             // Amplitud utilizada por defecto = Math.pow(2, bitsM - 1) - 1
        final short bytesInt = 4, bytesShort = 2, restoDeBytesNecesarios = 36;  // Int constantes
        final String Riff = "RIFF", Wave = "WAVEfmt ", Data = "data";           // String constantes
        
        // Variables con valor por defecto según las especificaciones del proyecto
        long Formato = 16;  
        short PCM = 1;

        // Fórmulas para asignar los valores correspondientes
        short Bytes_m = (short) (getCanales() * Formato / 8);                        // Se divide entre 8 porque un byte equivales a 8 bits    
        short Bits_m = (short) Formato;                                         // 16 bits de resolución por defecto
        int iNumeroMuestras = iTiempo * iFrecuenciaMuestreo;                    // Frecuencia de muestreo son las muestras tomadas por unidad de tiempo
        long Tamano = (iNumeroMuestras * Bytes_m) + restoDeBytesNecesarios;     // Para el tamaño se requiere el total de muestras, los bytes de cada una y espacio adicional para la cabecera
        int F_muestreo = (int) (iFrecuenciaMuestreo * getCanales() * Formato / 8);   // Número de bytes por segundo (por eso de divide entre 8)
        int Bytes_archivo = iNumeroMuestras * getCanales() * Bytes_m;                // Bytes que ocuparán las muestras
     
        // Variables utilizadas para la creación de las muestras
        int muestra;
        double angulo = (bytesShort * Math.PI * armonico) / iFrecuenciaMuestreo;

        try {
            // Instancia archivo en formato lectura y escritura
            RandomAccessFile archivo = new RandomAccessFile(name, "rw");
                                      
            // Inicio de la escritura en el archivo
            // Cabecera
            archivo.writeBytes(Riff);                                                       // Se escribe "RIFF"
            archivo.write(convertirIntALittleEndian((int) Tamano), 0, bytesInt);       // Se escribe el tamaño
            archivo.writeBytes(Wave);                                                       // Se escribe "WAVEfmt "

            // Sub-fragmento fmt   
            archivo.write(convertirIntALittleEndian((int) Formato), 0, bytesInt);       // Se escribe el formato         
            archivo.write(convertirShortALittleEndian(PCM), 0, bytesShort);             // Se escribe el PCM
            archivo.write(convertirShortALittleEndian(getCanales()), 0, bytesShort);         // Se escriben los canales
            archivo.write(convertirIntALittleEndian((int) iFrecuenciaMuestreo), 0, bytesInt);      // Se escribe la frecuencia a escuchar
            archivo.write(convertirIntALittleEndian((int) F_muestreo), 0, bytesInt);    // Se escribe la frecuencia de muestreo   
            archivo.write(convertirShortALittleEndian(Bytes_m), 0, bytesShort);         // Se escribe el número de bytes por muestra
            archivo.write(convertirShortALittleEndian(Bits_m), 0, bytesShort);          // Se escribe el número de bits por muestra

            // Sub-fragmento data
            archivo.writeBytes(Data);                                                        // Se escribe "data"
            archivo.write(convertirIntALittleEndian((int) Bytes_archivo), 0, bytesInt); // Se escribe el número de bytes que ocupan las muestras

            // Generar la onda senoidal
            for(int i = 0; i < iNumeroMuestras; i++){                     // Se obtienen y escriben las muestras
                muestra = (int) Math.floor(amplitud * Math.sin(angulo * i));      
                archivo.write(convertirIntALittleEndian(((int) muestra)), 0, bytesInt);
            }
            
            // Cerrado del archivo
            archivo.close();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Método encargado de convertir un tipo de dato int
     * de Big-Endian a Little-Endian
     * @param valor Valor de tipo int a convertir
     * @return Cadena de bytes en tipo Little-Endian
     */
    public static byte[] convertirIntALittleEndian(long valor){
        byte[] resultado;
        byte b0 = (byte) (valor & 0xFF);
        byte b1 = (byte) ((valor >> 8) & 0xFF);
        byte b2 = (byte) ((valor >> 16) & 0xFF);
        byte b3 = (byte) ((valor >> 24) & 0xFF);
        resultado = new byte[]{ b0, b1, b2, b3 };
        return resultado;
    }

    /**
     * Método encargado de convertir un tipo de dato short
     * de Big-Endian a Little-Endian
     * @param valor Valor de tipo short a convertir
     * @return Cadena de bytes en tipo Little-Endian
     */
    public static byte[] convertirShortALittleEndian(short valor){
        byte[] resultado;
        byte b0 = (byte) (valor & 0xFF);
        byte b1 = (byte) ((valor >> 8) & 0xFF);
        resultado = new byte[]{ b0, b1 };
        return resultado;
    }
}