package model;

import java.util.Date;

public class Cliente {

    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String CF;
    private String luogoNascita;
    private Date dataNascita;
    private String iban;

    // Eventuali altri oggetti collegati
    private Abbonamento abbonamento;
    private Pagamento pagamento;

    // ============================
    // COSTRUTTORE
    // ============================
    public Cliente(String username,
                   String password,
                   String nome,
                   String cognome,
                   String CF,
                   String luogoNascita,
                   Date dataNascita,
                   String iban) {

        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.CF = CF;
        this.luogoNascita = luogoNascita;
        this.dataNascita = dataNascita;
        this.iban = iban;
    }

    // ============================
    // GETTER & SETTER
    // ============================
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getCF() { return CF; }
    public void setCF(String CF) { this.CF = CF; }

    public String getLuogoNascita() { return luogoNascita; }
    public void setLuogoNascita(String luogoNascita) { this.luogoNascita = luogoNascita; }

    public Date getDataNascita() { return dataNascita; }
    public void setDataNascita(Date dataNascita) { this.dataNascita = dataNascita; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public Abbonamento getAbbonamento() { return abbonamento; }
    public void setAbbonamento(Abbonamento abbonamento) { this.abbonamento = abbonamento; }

    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }

    
    
    
    public void loginApp(String username, String password) {
        // TODO: logica di login
    }

    public void vediAbbonamento() {
        // TODO: mostra dati abbonamento
    }

    public void prenotaConsulenza(Dipendente dip, Date data) {
        // TODO: prenotazione consulenza
    }

    public Pagamento effettuaPagamento(int importo, String metodo) {
        // TODO: logica pagamento
        return pagamento;
    }
}
