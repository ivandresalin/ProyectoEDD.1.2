/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

/**
 *
 * @author ivand
 */
import java.util.Comparator;

public class ComparadoresContacto {

    // Ordenar por apellido y primer nombre (solo para ContactoPersonal)
    public static Comparator<Contacto> porApellidoNombre = (c1, c2) -> {
        boolean c1EsPersonal = c1 instanceof ContactoPersonal;
        boolean c2EsPersonal = c2 instanceof ContactoPersonal;

        if (c1EsPersonal && c2EsPersonal) {
            ContactoPersonal p1 = (ContactoPersonal) c1;
            ContactoPersonal p2 = (ContactoPersonal) c2;

            int cmpApellido = p1.getApellido().compareToIgnoreCase(p2.getApellido());
            if (cmpApellido != 0) return cmpApellido;

            return p1.getNombre().compareToIgnoreCase(p2.getNombre());
        } else if (c1EsPersonal) {
            return -1; // Personal antes que empresa
        } else if (c2EsPersonal) {
            return 1;
        } else {
            // Dos empresas ordenadas por nombre
            return c1.getNombre().compareToIgnoreCase(c2.getNombre());
        }
    };

    // Ordenar por tipo: Persona (natural) primero, Empresa después
    public static Comparator<Contacto> porTipo = (c1, c2) -> {
        boolean c1EsPersonal = c1 instanceof ContactoPersonal;
        boolean c2EsPersonal = c2 instanceof ContactoPersonal;

        if (c1EsPersonal && !c2EsPersonal) return -1;
        if (!c1EsPersonal && c2EsPersonal) return 1;
        return 0; // mismo tipo
    };

    // Ordenar por cantidad de redes sociales (descendente)
    public static Comparator<Contacto> porRedesSociales = (c1, c2) -> {
        int c1Redes = c1.getRedesSociales().getTamanio();
        int c2Redes = c2.getRedesSociales().getTamanio();

        return Integer.compare(c2Redes, c1Redes); // más redes primero
    };
}
