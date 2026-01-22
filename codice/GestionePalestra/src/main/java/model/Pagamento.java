package model;

import java.util.Date;

public class Pagamento {

    private String metodo;        // es. "Carta", "Contanti", "Bonifico"
    private float importo;        // in euro
    private Date dataPagamento;   // quando è stato effettuato

    // ============================
    // COSTRUTTORI
    // ============================
    public Pagamento() {
    }

    public Pagamento(String metodo, float importo, Date dataPagamento) {
        this.metodo = metodo;
        this.importo = importo;
        this.dataPagamento = dataPagamento;
    }

    // ============================
    // GETTER & SETTER
    // ============================
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public float getImporto() { return importo; }
    public void setImporto(float importo) { this.importo = importo; }

    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }

    // ============================
    // METODI DI BUSINESS
    // ============================

    /**
     * Metodo di comodo per “confermare” il pagamento nel flusso GUI.
     * Nel tuo pagamento simulato puoi semplicemente chiamarlo quando
     * l’utente preme il bottone "Paga".
     */
    public void pagamentoEffettuato() {
        // qui potresti, ad esempio, aggiornare la data al momento corrente
        this.dataPagamento = new Date();
        // se un domani colleghi il DB, qui potresti salvare il record
    }
}
