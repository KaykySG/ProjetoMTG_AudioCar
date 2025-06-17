package com.vpk.backapimtgaudiocar.dto;

public class BalancoAudioDTO {
    private int potenciaGraveTotal;
    private int potenciaVozTotal;
    private double percentualGrave; // exemplo: 65.5
    private double percentualVoz;   // exemplo: 34.5


    public BalancoAudioDTO() {
    }

    public BalancoAudioDTO(int potenciaGraveTotal, int potenciaVozTotal, double percentualGrave, double percentualVoz) {
        this.potenciaGraveTotal = potenciaGraveTotal;
        this.potenciaVozTotal = potenciaVozTotal;
        this.percentualGrave = percentualGrave;
        this.percentualVoz = percentualVoz;
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
}
