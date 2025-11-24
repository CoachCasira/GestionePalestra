package main;
import org.apache.logging.log4j.LogManager;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Logger;

import controller.LoginController;
import db.GestioneDB;
import db.InizializzazioneDB;
import view.LoginView;
public class Palestra {
	
	 private static final Logger logger = LogManager.getLogger(Palestra.class);
	public static void main(String[] args) {
		
		 // Inizializza DB (tabelle)
        InizializzazioneDB.init();

        // Avvia GUI sul thread grafico
        SwingUtilities.invokeLater(() -> {
            logger.info("Avvio dell'interfaccia grafica di login");

            LoginView view = new LoginView();
            new LoginController(view); // il controller si registra nella view

            view.setVisible(true);
        });

        GestioneDB.stampaClienti();
	}

}
