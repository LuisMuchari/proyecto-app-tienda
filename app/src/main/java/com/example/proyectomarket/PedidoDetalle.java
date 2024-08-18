package com.example.proyectomarket;

import java.io.Serializable;

public class PedidoDetalle implements Serializable {

    private static int id=0;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        PedidoDetalle.id = id;
    }
}
