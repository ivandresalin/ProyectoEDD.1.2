package src;

import java.io.Serializable;

public abstract class Contacto implements Serializable {
    protected String nombre;

    // Reemplazamos todos los ArrayList por CircularDoubleLinkedList
    protected CircularDoubleLinkedList<Atributo> telefonos;
    protected CircularDoubleLinkedList<Atributo> emails;
    protected CircularDoubleLinkedList<Atributo> direcciones;
    protected CircularDoubleLinkedList<Atributo> fechasDeInteres;
    protected CircularDoubleLinkedList<Atributo> redesSociales;
    protected CircularDoubleLinkedList<Contacto> contactosRelacionados;

    protected CircularDoubleLinkedList<Foto> fotos;

    public Contacto(String nombre) {
        this.nombre = nombre;

        this.telefonos = new CircularDoubleLinkedList<>();
        this.emails = new CircularDoubleLinkedList<>();
        this.direcciones = new CircularDoubleLinkedList<>();
        this.fechasDeInteres = new CircularDoubleLinkedList<>();
        this.redesSociales = new CircularDoubleLinkedList<>();
        this.contactosRelacionados = new CircularDoubleLinkedList<>();
        this.fotos = new CircularDoubleLinkedList<>();
    }

    public abstract String getTipo();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Accesores por tipo
    public CircularDoubleLinkedList<Atributo> getTelefonos() {
        return telefonos;
    }

    public void addTelefono(String numero, String descripcion) {
        this.telefonos.add(new Atributo(numero, descripcion));
    }

    public CircularDoubleLinkedList<Atributo> getEmails() {
        return emails;
    }

    public void addEmail(String email, String descripcion) {
        this.emails.add(new Atributo(email, descripcion));
    }

    public CircularDoubleLinkedList<Atributo> getDirecciones() {
        return direcciones;
    }

    public void addDireccion(String direccion, String descripcion) {
        this.direcciones.add(new Atributo(direccion, descripcion));
    }

    public CircularDoubleLinkedList<Atributo> getFechasDeInteres() {
        return fechasDeInteres;
    }

    public void addFechaDeInteres(String fecha, String descripcion) {
        this.fechasDeInteres.add(new Atributo(fecha, descripcion));
    }

    public CircularDoubleLinkedList<Atributo> getRedesSociales() {
        return redesSociales;
    }

    public void addRedSocial(String redSocial, String descripcion) {
        this.redesSociales.add(new Atributo(redSocial, descripcion));
    }

    public CircularDoubleLinkedList<Contacto> getContactosRelacionados() {
        return contactosRelacionados;
    }

    public void addContactoRelacionado(Contacto contacto) {
        this.contactosRelacionados.add(contacto);
    }

    public CircularDoubleLinkedList<Foto> getFotos() {
        return fotos;
    }

    public void addFoto(Foto foto) {
        this.fotos.add(foto);
    }

    public abstract String getNombreCompleto();

    public abstract String mostrarDetalles();

    public int getTotalAtributos() {
        return telefonos.getTamanio()
             + emails.getTamanio()
             + direcciones.getTamanio()
             + fechasDeInteres.getTamanio()
             + redesSociales.getTamanio();
    }
}
