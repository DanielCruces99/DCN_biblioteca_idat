package com.biblioteca_idat_api.biblioteca_idat_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre; // "ROLE_USER", "ROLE_ADMIN"

    // getters, setters, constructores
}
