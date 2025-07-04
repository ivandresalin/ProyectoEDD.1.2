package src;

import java.io.*;

public class GestorPersistencia {

    private static final String RUTA_ARCHIVO = "contactos.dat";

    public static void guardarContactos(CircularDoubleLinkedList<Contacto> lista) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            out.writeObject(lista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CircularDoubleLinkedList<Contacto> cargarContactos() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new CircularDoubleLinkedList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (CircularDoubleLinkedList<Contacto>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new CircularDoubleLinkedList<>();
        }
    }
}
