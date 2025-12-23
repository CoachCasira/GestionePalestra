package test.model;

import org.junit.jupiter.api.Test;

import model.Pagamento;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class PagamentoTest {

    @Test
    public void costruttoreCompleto_eGetterSetter() {
        Date d = new Date(123);

        Pagamento p = new Pagamento("Carta", 50f, d);

        assertEquals("Carta", p.getMetodo());
        assertEquals(50f, p.getImporto(), 0.0001);
        assertEquals(d, p.getDataPagamento());

        p.setMetodo("Contanti");
        p.setImporto(10f);
        Date d2 = new Date(456);
        p.setDataPagamento(d2);

        assertEquals("Contanti", p.getMetodo());
        assertEquals(10f, p.getImporto(), 0.0001);
        assertEquals(d2, p.getDataPagamento());
    }

    @Test
    public void pagamentoEffettuato_aggiornaDataPagamento() throws InterruptedException {
        Pagamento p = new Pagamento();
        p.setDataPagamento(new Date(0));

        Date prima = p.getDataPagamento();
        Thread.sleep(5); // evita collisione sullo stesso millisecondo

        p.pagamentoEffettuato();

        assertNotNull(p.getDataPagamento());
        assertTrue(p.getDataPagamento().after(prima) || !p.getDataPagamento().equals(prima));
    }
}
