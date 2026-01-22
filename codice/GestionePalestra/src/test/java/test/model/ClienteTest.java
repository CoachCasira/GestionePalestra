package test.model;

import org.junit.jupiter.api.Test;

import model.Abbonamento;
import model.AbbonamentoBasico;
import model.AbbonamentoCompleto;
import model.AbbonamentoCorsi;
import model.Cliente;
import model.Pagamento;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    public static Date daysFromNow(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    @Test
    public void costruttoreCompleto_settaCampiPrincipali() {
        Date nascita = new Date(0);

        Cliente c = new Cliente(
                "user",
                "pass",
                "Mario",
                "Rossi",
                "CF123",
                "Bergamo",
                nascita,
                "IT00X",
                "mario@x.it"
        );

        assertEquals("user", c.getUsername());
        assertEquals("pass", c.getPassword());
        assertEquals("Mario", c.getNome());
        assertEquals("Rossi", c.getCognome());
        assertEquals("CF123", c.getCF());
        assertEquals("Bergamo", c.getLuogoNascita());
        assertEquals(nascita, c.getDataNascita());
        assertEquals("IT00X", c.getIban());
        assertEquals("mario@x.it", c.getEmail());
    }

    @Test
    public void hasAbbonamentoAttivo_seAbbonamentoNull_false() {
        Cliente c = new Cliente();
        c.setAbbonamento(null);

        assertFalse(c.hasAbbonamentoAttivo());
    }

    @Test
    public void hasAbbonamentoAttivo_seScadenzaNull_true() {
        Cliente c = new Cliente();
        Abbonamento abb = new AbbonamentoBasico();
        abb.setScadenza(null);
        c.setAbbonamento(abb);

        assertTrue(c.hasAbbonamentoAttivo());
    }

    @Test
    public void hasAbbonamentoAttivo_scadenzaFutura_true_scadenzaPassata_false() {
        Cliente c = new Cliente();
        Abbonamento abb = new AbbonamentoBasico();
        c.setAbbonamento(abb);

        abb.setScadenza(daysFromNow(+2));
        assertTrue(c.hasAbbonamentoAttivo());

        abb.setScadenza(daysFromNow(-2));
        assertFalse(c.hasAbbonamentoAttivo());
    }

    @Test
    public void sottoscriviAbbonamento_collegaAbbonamentoEPagamento() {
        Cliente c = new Cliente();
        Abbonamento abb = new AbbonamentoCorsi();
        Pagamento p = new Pagamento("Carta", 40f, new Date());

        c.sottoscriviAbbonamento(abb, p);

        assertSame(abb, c.getAbbonamento());
        assertSame(p, c.getPagamento());
    }

    @Test
    public void disdiciAbbonamento_settaAbbonamentoANull() {
        Cliente c = new Cliente();
        c.setAbbonamento(new AbbonamentoCompleto());

        c.disdiciAbbonamento();

        assertNull(c.getAbbonamento());
    }

    @Test
    public void effettuaPagamento_creaPagamento_loAssegna_eRitornaStessoOggetto() {
        Cliente c = new Cliente();

        Pagamento p = c.effettuaPagamento(12.5f, "Bonifico");

        assertNotNull(p);
        assertSame(p, c.getPagamento());
        assertEquals("Bonifico", p.getMetodo());
        assertEquals(12.5f, p.getImporto(), 0.0001);
        assertNotNull(p.getDataPagamento());
    }

    @Test
    public void getterSetter_vari() {
        Cliente c = new Cliente();
        Date d = new Date(0);

        c.setIdCliente(10);
        c.setUsername("u");
        c.setPassword("p");
        c.setNome("n");
        c.setCognome("c");
        c.setCF("cf");
        c.setLuogoNascita("ln");
        c.setDataNascita(d);
        c.setIban("iban");
        c.setEmail("e");

        assertEquals(10, c.getIdCliente());
        assertEquals("u", c.getUsername());
        assertEquals("p", c.getPassword());
        assertEquals("n", c.getNome());
        assertEquals("c", c.getCognome());
        assertEquals("cf", c.getCF());
        assertEquals("ln", c.getLuogoNascita());
        assertEquals(d, c.getDataNascita());
        assertEquals("iban", c.getIban());
        assertEquals("e", c.getEmail());
    }
}
