/**
 *  Questa classe rappresenta l'entità dei pazienti (animali) nel database.
 */

package clinica_veterinaria;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pazienti")
public class Paziente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @Setter
    @Column(name = "tipo_animale")
    private String tipoAnimale; // cane, gatto, coccodrillo

    @Getter
    @Setter
    @Column(name = "nome")
    private String nome;

    @Getter
    @Setter
    @Column(name = "taglia")
    private String taglia; // piccola, media, grande

    @Getter
    @Setter
    @Column(name = "sesso")
    private String sesso; // maschio o femmina

    @Getter
    @Setter
    @Column(name = "eta")
    private int eta;

    @Getter
    @Setter
    @Column(name = "microchip")
    private Long microchip; // codice numerico, 0 se non dotato

    @Getter
    @Setter
    @Column(name = "status_ricovero")
    private boolean statusRicovero; // true se deve essere ricoverato

}


