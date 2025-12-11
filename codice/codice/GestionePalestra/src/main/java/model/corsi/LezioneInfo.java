package model.corsi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO per le informazioni di una lezione di corso.
 */
public class LezioneInfo {

    public final int idLezione;
    public final int idCorso;
    public final LocalDate data;
    public final LocalTime ora;
    public final int durataMinuti;
    public final int postiTotali;
    public final int postiPrenotati;
    public final String nomeIstruttore;  // "Nome Cognome"

    public LezioneInfo(int idLezione,
                       int idCorso,
                       LocalDate data,
                       LocalTime ora,
                       int durataMinuti,
                       int postiTotali,
                       int postiPrenotati,
                       String nomeIstruttore) {
        this.idLezione = idLezione;
        this.idCorso = idCorso;
        this.data = data;
        this.ora = ora;
        this.durataMinuti = durataMinuti;
        this.postiTotali = postiTotali;
        this.postiPrenotati = postiPrenotati;
        this.nomeIstruttore = nomeIstruttore;
    }

    public int postiDisponibili() {
        return postiTotali - postiPrenotati;
    }

    public LocalDateTime getInizio() {
        return LocalDateTime.of(data, ora);
    }

    public LocalDateTime getFine() {
        return getInizio().plusMinutes(durataMinuti);
    }
}
