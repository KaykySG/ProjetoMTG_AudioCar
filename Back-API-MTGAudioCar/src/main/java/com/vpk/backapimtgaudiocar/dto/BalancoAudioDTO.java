package com.vpk.backapimtgaudiocar.dto;

public class BalancoAudioDTO {
    private int potenciaGraveTotal;
    private int potenciaVozTotal;
    private double percentualGrave; // exemplo: 65.5
    private double percentualVoz;   // exemplo: 34.5
    private double consumo;


    public BalancoAudioDTO() {
    }

    public BalancoAudioDTO(int potenciaGraveTotal, int potenciaVozTotal, double percentualGrave, double percentualVoz, double consumo) {
        this.potenciaGraveTotal = potenciaGraveTotal;
        this.potenciaVozTotal = potenciaVozTotal;
        this.percentualGrave = percentualGrave;
        this.percentualVoz = percentualVoz;
        this.consumo = consumo;
    }

    public double getPercentualVoz() {
        return percentualVoz;
    }

    public void setPercentualVoz(double percentualVoz) {
        this.percentualVoz = percentualVoz;
    }

    public double getPercentualGrave() {
        return percentualGrave;
    }

    public void setPercentualGrave(double percentualGrave) {
        this.percentualGrave = percentualGrave;
    }

    public int getPotenciaVozTotal() {
        return potenciaVozTotal;
    }

    public void setPotenciaVozTotal(int potenciaVozTotal) {
        this.potenciaVozTotal = potenciaVozTotal;
    }

    public int getPotenciaGraveTotal() {
        return potenciaGraveTotal;
    }

    public void setPotenciaGraveTotal(int potenciaGraveTotal) {
        this.potenciaGraveTotal = potenciaGraveTotal;
    }

    public double getConsumo() {
        return consumo;
    }

    public void setConsumo(double consumo) {
        this.consumo = consumo;
    }
}
