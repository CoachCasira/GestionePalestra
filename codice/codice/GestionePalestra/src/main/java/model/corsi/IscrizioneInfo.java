package model.corsi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO per le iscrizioni usate nella disdetta / elenco iscrizioni future.
 */
public class IscrizioneInfo {

    public final int idLezione;
    public final int idCorso;
    public final String nomeCorso;
    public final LocalDate data;
    public final LocalTime ora;
    public final int durataMinuti;
    public final String nomeIstruttore;

    public IscrizioneInfo(int idLezione,
                          int idCorso,
                          String nomeCorso,
                          LocalDate data,
                          LocalTime ora,
                          int durataMinuti,
                          String nomeIstruttore) {
        this.idLezione = idLezione;
        this.idCorso = idCorso;
        this.nomeCorso = nomeCorso;
        this.data = data;
        this.ora = ora;
        this.durataMinuti = durataMinuti;
        this.nomeIstruttore = nomeIstruttore;
    }

    public LocalDateTime getInizio() {
        return LocalDateTime.of(data, ora);
    }
}
