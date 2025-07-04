package src;

import java.io.Serializable;

public class ContactoPersonal extends Contacto implements Serializable{
    private String apellido;

    public ContactoPersonal(String nombre, String apellido) {
        super(nombre);
        this.apellido = apellido;
    }
    
    @Override
    public String getNombreCompleto() {
        return getNombre() + " " + getApellido();
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    @Override
    public String getTipo() {
        return "Personal";
    }

    @Override
    public String mostrarDetalles() {
        return "Nombre: " + nombre + " " + apellido;
    }
}
