package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Consulenza {

    private int idConsulenza;
    private int idCliente;
    private int idDipendente;
    private String tipo;          // PERSONAL_TRAINER / NUTRIZIONISTA / ISTRUTTORE_CORSO
    private LocalDate data;
    private LocalTime ora;
    private String note;

    public Consulenza() {}

    public Consulenza(int idCliente, int idDipendente,
                      String tipo, LocalDate data,
                      LocalTime ora, String note) {
        this.idCliente = idCliente;
        this.idDipendente = idDipendente;
        this.tipo = tipo;
        this.data = data;
        this.ora = ora;
        this.note = note;
    }

    // getter/setter

    public int getIdConsulenza() { return idConsulenza; }
    public void setIdConsulenza(int idConsulenza) { this.idConsulenza = idConsulenza; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdDipendente() { return idDipendente; }
    public void setIdDipendente(int idDipendente) { this.idDipendente = idDipendente; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getOra() { return ora; }
    public void setOra(LocalTime ora) { this.ora = ora; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    // comodo per messaggio di conferma
    @Override
    public String toString() {
        return "Tipo: " + tipo +
                "\nData: " + data +
                "\nOra: " + ora +
                "\nNote: " + (note == null ? "" : note);
    }
}
