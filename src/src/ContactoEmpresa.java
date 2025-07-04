package src;

import java.io.Serializable;

public class ContactoEmpresa extends Contacto implements Serializable {

    public ContactoEmpresa(String nombre) {
        super(nombre);
    }
    
    @Override
    public String getNombreCompleto() {
        return getNombre();  // Solo nombre, sin apellido
    }

    @Override
    public String getTipo() {
        return "Empresa";
    }

    @Override
    public String mostrarDetalles() {
        return "Empresa: " + nombre;
    }
}
