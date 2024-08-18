package com.example.proyectomarket;

import java.io.Serializable;

public class Pedido implements Serializable {

    private static int id;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Pedido.id = id;
    }
}
