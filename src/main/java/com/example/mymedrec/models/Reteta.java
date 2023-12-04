package com.example.mymedrec.models;

public class Reteta {
    private String numePacient;
    private String afectiune;
    private String tratament;
    private String status;
    private String medicConstatator;

    public Reteta(String numePacient, String afectiune, String tratament, String status, String medicConstatator) {
        this.numePacient = numePacient;
        this.afectiune = afectiune;
        this.tratament = tratament;
        this.status = status;
        this.medicConstatator = medicConstatator;
    }

    public String getNumePacient() {
        return numePacient;
    }

    public String getAfectiune() {
        return afectiune;
    }

    public String getTratament() {
        return tratament;
    }

    public String getStatus() {
        return status;
    }

    public void setNumePacient(String numePacient) {
        this.numePacient = numePacient;
    }

    public void setAfectiune(String afectiune) {
        this.afectiune = afectiune;
    }

    public void setTratament(String tratament) {
        this.tratament = tratament;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMedicConstatator() {
        return medicConstatator;
    }

    public void setMedicConstatator(String medicConstatator) {
        this.medicConstatator = medicConstatator;
    }
}