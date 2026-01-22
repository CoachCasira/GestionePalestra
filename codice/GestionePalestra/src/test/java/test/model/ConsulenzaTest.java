package test.model;

import model.Consulenza;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ConsulenzaTest {

    @Test
    public void costruttoreCompleto_impostaCorrettamenteICampi() {
        LocalDate data = LocalDate.of(2025, 1, 10);
        LocalTime ora = LocalTime.of(15, 30);

        Consulenza c = new Consulenza(1, 2, "PERSONAL_TRAINER", data, ora, "note");

        assertEquals(1, c.getIdCliente());
        assertEquals(2, c.getIdDipendente());
        assertEquals("PERSONAL_TRAINER", c.getTipo());
        assertEquals(data, c.getData());
        assertEquals(ora, c.getOra());
        assertEquals("note", c.getNote());
    }

    @Test
    public void costruttoreVuoto_setterGetter_copronoTuttiICampi() {
        Consulenza c = new Consulenza();

        LocalDate data = LocalDate.of(2026, 12, 31);
        LocalTime ora = LocalTime.of(23, 59);

        c.setIdConsulenza(99);
        c.setIdCliente(10);
        c.setIdDipendente(20);
        c.setTipo("ISTRUTTORE_CORSO");
        c.setData(data);
        c.setOra(ora);
        c.setNote("Portare asciugamano");

        assertEquals(99, c.getIdConsulenza());
        assertEquals(10, c.getIdCliente());
        assertEquals(20, c.getIdDipendente());
        assertEquals("ISTRUTTORE_CORSO", c.getTipo());
        assertEquals(data, c.getData());
        assertEquals(ora, c.getOra());
        assertEquals("Portare asciugamano", c.getNote());
    }

    @Test
    public void toString_seNoteNull_nonStampaNull_eMetteStringaVuota() {
        Consulenza c = new Consulenza(
                1, 2, "NUTRIZIONISTA",
                LocalDate.of(2025, 2, 1),
                LocalTime.of(9, 0),
                null
        );

        String s = c.toString();

        assertTrue(s.contains("Tipo: NUTRIZIONISTA"));
        assertTrue(s.contains("Data: 2025-02-01"));
        assertTrue(s.contains("Ora: 09:00"));
        assertTrue(s.contains("Note: "));
        assertFalse(s.contains("null"));
    }

    @Test
    public void toString_seNoteValorizzata_laInclude() {
        Consulenza c = new Consulenza(
                10, 20, "PERSONAL_TRAINER",
                LocalDate.of(2025, 3, 15),
                LocalTime.of(18, 45),
                "Ok"
        );

        String s = c.toString();

        assertTrue(s.contains("Tipo: PERSONAL_TRAINER"));
        assertTrue(s.contains("Data: 2025-03-15"));
        assertTrue(s.contains("Ora: 18:45"));
        assertTrue(s.contains("Note: Ok"));
    }

    @Test
    public void toString_tipoVuoto_nonLanciaEccezioni_eRestaCoerente() {
        Consulenza c = new Consulenza(
                1, 2, "",
                LocalDate.of(2025, 4, 1),
                LocalTime.of(10, 0),
                "ok"
        );

        assertDoesNotThrow(() -> c.toString());

        String s = c.toString();
        assertTrue(s.contains("Tipo: "));
        assertTrue(s.contains("Data: 2025-04-01"));
        assertTrue(s.contains("Ora: 10:00"));
        assertTrue(s.contains("Note: ok"));
    }
}
