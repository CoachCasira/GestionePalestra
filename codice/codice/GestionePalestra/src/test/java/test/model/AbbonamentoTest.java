package test.model;

import org.junit.jupiter.api.Test;

import model.Abbonamento;
import model.AbbonamentoBasico;
import model.AbbonamentoCompleto;
import model.AbbonamentoCorsi;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class AbbonamentoTest {

    @Test
    public void creaDaTipo_nullRitornaNull() {
        Abbonamento abb = Abbonamento.creaDaTipo(null, 123);
        assertNull(abb);
    }

    @Test
    public void creaDaTipo_tipoSconosciutoRitornaNull() {
        Abbonamento abb = Abbonamento.creaDaTipo("BOH", 123);
        assertNull(abb);
    }

    @Test
    public void creaDaTipo_trimECasoInsensibile_funzionano() {
        assertInstanceOf(AbbonamentoBasico.class,
                Abbonamento.creaDaTipo("  base ", 1));

        assertInstanceOf(AbbonamentoCorsi.class,
                Abbonamento.creaDaTipo("CoRsI", 1));

        assertInstanceOf(AbbonamentoCompleto.class,
                Abbonamento.creaDaTipo(" COMPLETO ", 1));
    }

    @Test
    public void descrizioneBase_contieneCampiPrincipali_eIncludeScadenzaSeNonNull() {
        Abbonamento abb = new AbbonamentoBasico();

        Calendar cal = Calendar.getInstance();
        cal.set(2030, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date scad = cal.getTime();
        abb.setScadenza(scad);

        String base = abb.getDescrizioneBase();

        assertTrue(base.contains("Tipo: " + abb.getTipo()));
        assertTrue(base.contains("Prezzo: " + abb.getPrezzo())); // nel testo c'è anche "€/mese", va bene così
        assertTrue(base.contains("Fascia oraria: " + abb.getFasciaOrariaConsentita()));
        assertTrue(base.contains("Scadenza: " + scad));
    }

    @Test
    public void descrizioneBase_nonIncludeScadenzaSeNull() {
        Abbonamento abb = new AbbonamentoBasico();
        abb.setScadenza(null);

        String base = abb.getDescrizioneBase();

        assertTrue(base.contains("Tipo: " + abb.getTipo()));
        assertFalse(base.contains("Scadenza:"));
    }

    @Test
    public void descrizioneCompleta_sottoclassiAggiungonoTestoSpecifico() {
        Abbonamento b = new AbbonamentoBasico();
        Abbonamento c = new AbbonamentoCorsi();
        Abbonamento k = new AbbonamentoCompleto();

        String db = b.getDescrizioneCompleta();
        String dc = c.getDescrizioneCompleta();
        String dk = k.getDescrizioneCompleta();

        // tutte includono la parte base
        assertTrue(db.contains("Tipo: " + b.getTipo()));
        assertTrue(dc.contains("Tipo: " + c.getTipo()));
        assertTrue(dk.contains("Tipo: " + k.getTipo()));

        // testo specifico (parole chiave presenti nelle descrizioni)
        assertTrue(db.toUpperCase().contains("SALA PESI"));
        assertTrue(dc.toUpperCase().contains("SALA CORSI"));
        assertTrue(dk.toUpperCase().contains("SPA"));
    }

    @Test
    public void getterSetter_base() {
        Abbonamento abb = new AbbonamentoBasico();

        Date d = new Date(0);

        abb.setIdAbbonamento("A1");
        abb.setTipo("BASE");
        abb.setPrezzo(99);
        abb.setFasciaOrariaConsentita("X");
        abb.setScadenza(d);

        assertEquals("A1", abb.getIdAbbonamento());
        assertEquals("BASE", abb.getTipo());
        assertEquals(99, abb.getPrezzo());
        assertEquals("X", abb.getFasciaOrariaConsentita());
        assertEquals(d, abb.getScadenza());
    }
}
