package model;

import java.util.Calendar;
import java.util.Date;

public abstract class Abbonamento {

    protected String idAbbonamento;
    protected String tipo;                     // BASE / CORSI / COMPLETO
    protected int prezzo;                      // €/mese
    protected String fasciaOrariaConsentita;   // descrizione
    protected Date scadenza;

    // --- costruttori ---
    public Abbonamento() { }

    public Abbonamento(String tipo, int prezzo, String fasciaOrariaConsentita) {
        this.tipo = tipo;
        this.prezzo = prezzo;
        this.fasciaOrariaConsentita = fasciaOrariaConsentita;
        // per default scadenza = tra 1 mese
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        this.scadenza = cal.getTime();
    }

    // ----------------------------------------------------
    // FACTORY: restituisce la SOTTOCLASSE corretta.
    // Per tipo sconosciuto -> ritorna null (come da test)
    // ----------------------------------------------------
    public static Abbonamento creaDaTipo(String tipo, int idCliente) {
        if (tipo == null) return null;

        String t = tipo.trim().toUpperCase();

        switch (t) {
            case "BASE":
                return new AbbonamentoBasico();
            case "CORSI":
                return new AbbonamentoCorsi();
            case "COMPLETO":
                return new AbbonamentoCompleto();
            default:
                // tipo non riconosciuto -> test si aspetta null
                return null;
        }
    }

    // ---------- getter / setter ----------
    public String getIdAbbonamento() { return idAbbonamento; }
    public void setIdAbbonamento(String idAbbonamento) { this.idAbbonamento = idAbbonamento; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getPrezzo() { return prezzo; }
    public void setPrezzo(int prezzo) { this.prezzo = prezzo; }

    public String getFasciaOrariaConsentita() { return fasciaOrariaConsentita; }
    public void setFasciaOrariaConsentita(String fasciaOrariaConsentita) {
        this.fasciaOrariaConsentita = fasciaOrariaConsentita;
    }

    public Date getScadenza() { return scadenza; }
    public void setScadenza(Date scadenza) { this.scadenza = scadenza; }

    // ---------- descrizione generica ----------
    public String getDescrizioneBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipo: ").append(getTipo()).append("\n");
        sb.append("Prezzo: ").append(getPrezzo()).append(" €/mese\n");
        sb.append("Fascia oraria: ").append(getFasciaOrariaConsentita()).append("\n");
        if (getScadenza() != null) {
            sb.append("Scadenza: ").append(getScadenza()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    /** Ogni sottoclasse aggiunge la descrizione degli spazi a cui può accedere. */
    public abstract String getDescrizioneCompleta();
}
