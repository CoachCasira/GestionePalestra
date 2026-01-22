package test.model;

import org.junit.jupiter.api.Test;

import model.Corso;

import static org.junit.jupiter.api.Assertions.*;

public class CorsoTest {

    @Test
    public void costruttoreCompleto_eGetterSetter() {
        Corso c = new Corso(10, "Yoga", "Corso di yoga", 60);

        assertEquals(10, c.getIdCorso());
        assertEquals("Yoga", c.getNome());
        assertEquals("Corso di yoga", c.getDescrizione());
        assertEquals(60, c.getDurataMinuti());

        c.setIdCorso(11);
        c.setNome("Pilates");
        c.setDescrizione("Desc");
        c.setDurataMinuti(45);

        assertEquals(11, c.getIdCorso());
        assertEquals("Pilates", c.getNome());
        assertEquals("Desc", c.getDescrizione());
        assertEquals(45, c.getDurataMinuti());
    }

    @Test
    public void toString_contieneCampiPrincipali() {
        Corso c = new Corso(7, "Cross", "X", 30);

        String s = c.toString();
        assertTrue(s.contains("idCorso=7"));
        assertTrue(s.contains("nome='Cross'"));
        assertTrue(s.contains("durataMinuti=30"));
    }
}
