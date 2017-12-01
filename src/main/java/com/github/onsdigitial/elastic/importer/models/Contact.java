package com.github.onsdigitial.elastic.importer.models;

/**
 * @author sullid (David Sullivan) on 01/12/2017
 * @project dp-elastic-importer
 */
public class Contact {

    private String email;
    private String name;
    private String telephone;

    public Contact(String email, String name, String telephone) {
        this.email = email;
        this.name = name;
        this.telephone = telephone;
    }

    private Contact() {
        // For jackson
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }
}
