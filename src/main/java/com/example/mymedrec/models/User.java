package com.example.mymedrec.models;

public class User {
    private String prenume;
    private String nume;
    private String numeUtilizator;
    private String parola;
    private String email;
    private int anNastere;
    private int lunaNastere;
    private int ziNastere;

    public User() {
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getNumeUtilizator() {
        return numeUtilizator;
    }

    public void setNumeUtilizator(String numeUtilizator) {
        this.numeUtilizator = numeUtilizator;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAnNastere() {
        return anNastere;
    }

    public void setAnNastere(int anNastere) {
        this.anNastere = anNastere;
    }

    public int getLunaNastere() {
        return lunaNastere;
    }

    public void setLunaNastere(int lunaNastere) {
        this.lunaNastere = lunaNastere;
    }

    public int getZiNastere() {
        return ziNastere;
    }

    public void setZiNastere(int ziNastere) {
        this.ziNastere = ziNastere;
    }
}
