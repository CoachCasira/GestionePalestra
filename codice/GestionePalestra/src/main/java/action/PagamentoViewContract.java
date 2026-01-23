package action;

import model.Abbonamento;
import model.Cliente;

import java.awt.Component;

public interface PagamentoViewContract {

    void setAction(PagamentoAction action);

    Cliente getCliente();
    Abbonamento getAbbonamento();

    void mostraMessaggioInfo(String msg);
    void mostraMessaggioErrore(String msg);

    void dispose();

    // utile nei controller per dialog generici, logging UI, ecc.
    Component asComponent();
}
