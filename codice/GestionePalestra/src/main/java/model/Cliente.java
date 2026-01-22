package model;

import java.util.Date;

public class Cliente {

    private int idCliente;
    private String username;
    private String password;   // hash BCrypt
    private String nome;
    private String cognome;
    private String CF;
    private String luogoNascita;
    private Date dataNascita;
    private String iban;
    private String email;

    // Oggetti collegati
    private Abbonamento abbonamento;
    private Pagamento pagamento;

    public Cliente() {}

    public Cliente(String username,
                   String password,
                   String nome,
                   String cognome,
                   String CF,
                   String luogoNascita,
                   Date dataNascita,
                   String iban,
                   String email) {

        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.CF = CF;
        this.luogoNascita = luogoNascita;
        this.dataNascita = dataNascita;
        this.iban = iban;
        this.email = email;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getCF() { return CF; }
    public void setCF(String cF) { CF = cF; }

    public String getLuogoNascita() { return luogoNascita; }
    public void setLuogoNascita(String luogoNascita) { this.luogoNascita = luogoNascita; }

    public Date getDataNascita() { return dataNascita; }
    public void setDataNascita(Date dataNascita) { this.dataNascita = dataNascita; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Abbonamento getAbbonamento() { return abbonamento; }
    public void setAbbonamento(Abbonamento abbonamento) { this.abbonamento = abbonamento; }

    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }

    public boolean hasAbbonamentoAttivo() {
        if (abbonamento == null) return false;
        Date oggi = new Date();
        return abbonamento.getScadenza() == null
                || abbonamento.getScadenza().after(oggi);
    }

    public void sottoscriviAbbonamento(Abbonamento nuovo, Pagamento pag) {
        this.abbonamento = nuovo;
        this.pagamento = pag;
    }

    public void disdiciAbbonamento() {
        this.abbonamento = null;
    }

    public void loginApp(String username, String password) {
        // gestito lato controller
    }

    public void vediAbbonamento() {
        // usato dalla GUI
    }

    public void prenotaConsulenza(Dipendente dip, Date data) {
        // non implementato
    }

    public Pagamento effettuaPagamento(float importo, String metodo) {
        Pagamento p = new Pagamento(metodo, importo, new Date());
        this.pagamento = p;
        return p;
    }
}
