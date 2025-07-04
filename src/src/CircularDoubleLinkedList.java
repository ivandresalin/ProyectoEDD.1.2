package src;

import java.io.Serializable;
import java.util.Comparator;

public class CircularDoubleLinkedList<T> implements Serializable{
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;
    private Nodo<T> actual;  // para mantener el nodo actual

    public CircularDoubleLinkedList() {
        cabeza = null;
        cola = null;
        actual = null;
        tamanio = 0;
    }
    
    public T get(int index) {
        if (index < 0 || index >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < index; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }
    
    

    
    public void ordenar(Comparator<T> comparador) {
        if (cabeza == null || tamanio < 2) return;

        boolean huboCambios;
        do {
            huboCambios = false;
            Nodo<T> actual = cabeza;
            for (int i = 0; i < tamanio - 1; i++) {
                Nodo<T> siguiente = actual.getSiguiente();
                if (comparador.compare(actual.getDato(), siguiente.getDato()) > 0) {
                    // Intercambiar datos, no nodos para simplicidad
                    T temp = actual.getDato();
                    actual.setDato(siguiente.getDato());
                    siguiente.setDato(temp);
                    huboCambios = true;
                }
                actual = siguiente;
            }
        } while (huboCambios);
    }


    public void add(T dato) {
        Nodo<T> newNodo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = newNodo;
            cola = newNodo;
            cabeza.setSiguiente(cabeza);
            cabeza.setAnterior(cola);
            actual = cabeza;
        } else {
            cola.setSiguiente(newNodo);
            newNodo.setAnterior(cola);
            newNodo.setSiguiente(cabeza);
            cabeza.setAnterior(newNodo);
            cola = newNodo;
        }
        tamanio++;
    }
    
    public void setActual(T dato) {
        Nodo<T> nodo = cabeza;
        for (int i = 0; i < tamanio; i++) {
            if (nodo.getDato().equals(dato)) {
                actual = nodo;
                return;
            }
            nodo = nodo.getSiguiente();
        }
    }
    
    public void agregarTodo(CircularDoubleLinkedList<T> otraLista) {
        Nodo<T> nodo = otraLista.getCabeza();
        for (int i = 0; i < otraLista.getTamanio(); i++) {
            this.add(nodo.getDato());
            nodo = nodo.getSiguiente();
        }
    }

    
    public void vaciar() {
        cabeza = null;
        tamanio = 0;
    }



    public void remove(T dato) {
        if (cabeza == null) return;

        Nodo<T> current = cabeza;
        do {
            if (current.getDato().equals(dato)) {
                if (current == cabeza && current == cola) {
                    cabeza = null;
                    cola = null;
                    actual = null;
                } else if (current == cabeza) {
                    cabeza = cabeza.getSiguiente();
                    cabeza.setAnterior(cola);
                    cola.setSiguiente(cabeza);
                    if (actual == current) actual = cabeza;
                } else if (current == cola) {
                    cola = cola.getAnterior();
                    cola.setSiguiente(cabeza);
                    cabeza.setAnterior(cola);
                    if (actual == current) actual = cabeza;
                } else {
                    current.getAnterior().setSiguiente(current.getSiguiente());
                    current.getSiguiente().setAnterior(current.getAnterior());
                    if (actual == current) actual = current.getSiguiente();
                }
                tamanio--;
                break;
            }
            current = current.getSiguiente();
        } while (current != cabeza);
    }

    // Devuelve el dato actual y avanza el puntero actual
    public T siguiente() {
        if (actual != null) {
            actual = actual.getSiguiente();
            return actual.getDato();
        }
        return null;
    }

    // Devuelve el dato actual y retrocede el puntero actual
    public T anterior() {
        if (actual != null) {
            actual = actual.getAnterior();
            return actual.getDato();
        }
        return null;
    }

    // Devuelve el dato actual sin mover el puntero
    public T getActual() {
        if (actual != null) {
            return actual.getDato();
        }
        return null;
    }

    public Nodo<T> getCabeza() {
        return cabeza;
    }

    public int getTamanio() {
        return tamanio;
    }
}
