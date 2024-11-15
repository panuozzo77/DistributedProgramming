/**
 *  Il client utilizza il bean stateless per stampare gli animali non dotati di microchip.
 */
package clinica_veterinaria;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

public class Client {

    public static void main(String[] args) {
        try {
            Context ctx = new InitialContext();
            PazienteBean pazienteBean = (PazienteBean) ctx.lookup("java:global/EsameDB/PazienteBean");

            // Recupero e stampa degli animali senza microchip
            List<Paziente> animaliSenzaMicrochip = pazienteBean.trovaSenzaMicrochip();
            for (Paziente animale : animaliSenzaMicrochip) {
                System.out.println("ID: " + animale.getId() + ", Nome: " + animale.getNome() + ", Tipo: " + animale.getTipoAnimale());
            }

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}

