## Sommario

# 1. Creazione Progetti - Guida passo passo

1. Aprire la Macchina Virtuale, utente: CORSO_PD, password: CORSO_PD
2. Aprire Netbeans
3. Tasto destro sulle parti vuote sui progetti
    1. Creare Progetto Client: Java with Ant > Java Application

    ![image.png](/classNotes/external/11_tutorial/image%200.png)

      b.  Creare Progetto Server: Java Enterprise > EJB Module

    ![image.png](/classNotes/external/11_tutorial/image%201.png)

    ![image.png](/classNotes/external/11_tutorial/image%202.png)


## 1. Risultato ‘finale’ di un progetto

![image.png](/classNotes/external/11_tutorial/image%203.png)

# 1. Importare LE GIUSTE librerie!

1. Prima di iniziare a scrivere qualsiasi classe Java, EJB o altro, importa le librerie corrette, perché se fai gli auto-import ti metterà tutta MUNNEZZA che non funziona
- Tasto destro sul progetto (server) > Properties > Libraries > Add Library

![image.png](/classNotes/external/11_tutorial/image%204.png)

- Assicurarsi di avere javaee-api-7.0.jar

### Client

- Tasto destro sul progetto (client) > Properties > Libraries

![image.png](/classNotes/external/11_tutorial/image%205.png)

# Nota:

Le seguenti informazioni sono ESCLUSIVAMENTE per il progetto ‘Server’. Verrà specificato quando si interverrà sul progetto ‘Client’.

# 1. Creare l’entità

1. Potresti fare tasto destro sul progetto server e cliccare su **New > Entity Class**
2. Oppure crei una qualsiasi classe Java e inserisci a mano piano piano tutti i decoratori

**Come è fatta una classe entità delle tracce:**

- La classe è decorata con @Entity ed implementa Serializable
- Possiede un attributo (solitamente un Long o numerico) con decoratore ‘@Id’
- possiede un costruttore vuoto (necessario per interfacciarsi col Database)
- possiede un costruttore con tutti i parametri (per questioni di velocità)
- possiede una lista di queries
- possiede svariati attributi (seguendo la traccia)
    - e tutti gli appositi metodi getter e setter
- possiede un metodo String toString() [necessario per gli output delle tracce d’esame]
- Esempio codice:

    ```java
    package server;

    import java.io.Serializable;
    import javax.persistence.*;

    @Entity
    // @Table(name = "pazienti")
    @NamedQueries({
        @NamedQuery(name = "findAll", query = "SELECT b FROM Paziente b"),
        @NamedQuery(name = "findPatientByType", query = "SELECT b FROM Paziente b WHERE b.tipoAnimale = :tipo")
    })
    public class Paziente implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(name = "tipo_animale", nullable = false)
        private String tipoAnimale;

        @Column(name = "nome", nullable = false)
        private String nome;

        @Column(name = "taglia", nullable = false)
        private String taglia;

        @Column(name = "sesso", nullable = false)
        private String sesso;

        @Column(name = "eta", nullable = false)
        private int eta;

        @Column(name = "microchip")
        private Long microchip;

        @Column(name = "status_ricovero", nullable = false)
        private boolean statusRicovero;

        // Costruttore vuoto richiesto da JPA
        public Paziente() {
        }

        // Costruttore completo
        public Paziente(String tipo, String nome, String taglia, String sesso, int eta, Long microchip, Boolean ricoverato) {
            this.tipoAnimale = tipo;
            this.nome = nome;
            this.taglia = taglia;
            this.sesso = sesso;
            this.eta = eta;
            this.microchip = microchip;
            this.statusRicovero = ricoverato;
        }

        // Getter e Setter
        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTipoAnimale() {
            return this.tipoAnimale;
        }

        public void setTipoAnimale(String tipoAnimale) {
            this.tipoAnimale = tipoAnimale;
        }

        public String getNome() {
            return this.nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getTaglia() {
            return this.taglia;
        }

        public void setTaglia(String taglia) {
            this.taglia = taglia;
        }

        public String getSesso() {
            return this.sesso;
        }

        public void setSesso(String sesso) {
            this.sesso = sesso;
        }

        public int getEta() {
            return this.eta;
        }

        public void setEta(int eta) {
            this.eta = eta;
        }

        public Long getMicrochip() {
            return this.microchip;
        }

        public void setMicrochip(Long microchip) {
            this.microchip = microchip;
        }

        public boolean isStatusRicovero() {
            return this.statusRicovero;
        }

        public void setStatusRicovero(boolean statusRicovero) {
            this.statusRicovero = statusRicovero;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Paziente");
            sb.append("{id=").append(id);
            sb.append(", tipoAnimale='").append(tipoAnimale).append('\'');
            sb.append(", nome='").append(nome).append('\'');
            sb.append(", taglia='").append(taglia).append('\'');
            sb.append(", sesso='").append(sesso).append('\'');
            sb.append(", eta=").append(eta);
            sb.append(", microchip=").append(microchip);
            sb.append(", statusRicovero=").append(statusRicovero);
            sb.append('}');
            return sb.toString();
        }

    }

    ```


# 1. Java Class - DatabaseProducer

- Serve ESCLUSIVAMENTE per restituire l’Entity Manager
- New > Java Class
- Modificare opportunamente l’unitName con quella **specificata nella traccia!**

```java
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Angiopasqui
 */

public class DatabaseProducer {
  @Produces
  @PersistenceContext(unitName="EsamePU")
  private EntityManager em;
}

```

- Ricordare che bisognerà modificare opportunamente anche il file **persistence.xml**

# 1. EJB Singleton - DatabasePopulator

- Ha il compito di popolare ed eliminare alcuni record dal database che utilizzeremo durante l’esecuzione
- Tasto destro sul progetto > New > Session Bean > Stateless
- Contiene la definizione del DataSource e dei parametri necessari per la connessione al database
- Modificare opportunamente l’unitName con quella **specificata nella traccia!**
- Esempio

    ```java
    package server;

    import javax.annotation.PostConstruct;
    import javax.annotation.sql.DataSourceDefinition;
    import javax.ejb.Singleton;
    import javax.ejb.Startup;
    import javax.persistence.EntityManager;
    import javax.persistence.PersistenceContext;

    @Startup
    @DataSourceDefinition(
        className ="org.apache.derby.jdbc.EmbeddedDataSource",  //JDBC Connection Pool
        name ="java:global/jdbc/EsameDS", //JDBC Resources>JNDI Name 'DataSource Name'
        user ="APP", // necessario su VM è definito solo questo
        password ="APP",  // necessario su VM è definito solo questo
        databaseName ="EsameDB", // Qualsiasi, viene creato on the go
        properties = {"connectionAttributes=;create=true"} // IMPORTANTISSIMO
    )
    @Singleton
    public class DatabasePopulator {

        @PersistenceContext(unitName = "EsamePU")
        private EntityManager em;

        @PostConstruct
        public void init() {
            // Inizializzazione dell'archivio con alcuni dati di esempio
            em.persist(new Paziente("cane", "Fido", "media", "maschio", 5, 123456789L, false));
            em.persist(new Paziente("gatto", "Micia", "piccola", "femmina", 3, 0L, true));
            em.persist(new Paziente("coccodrillo", "Coco", "grande", "maschio", 10, 0L, false));
        }
    }

    ```

- Ricordare che bisognerà modificare opportunamente anche il file **persistence.xml**

# 1. Configurazione - persistence.xml

Tasto destro > New > Persistence Unit

![image.png](/classNotes/external/11_tutorial/image%206.png)

![image.png](/classNotes/external/11_tutorial/image%207.png)

- Scrivere la corretta Persistence Unit name
- Scrivere la corretta Data Source (specificata anche in DatabasePopulator)

NOTA: è possibile cliccare ‘**Source**’ invece di ‘**Design**’ per modificare direttamente il file xml (se ti piace)

![image.png](/classNotes/external/11_tutorial/image%208.png)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
  <persistence-unit name="EsamePU" transaction-type="JTA">
    <jta-data-source>java:global/jdbc/EsameDS</jta-data-source>
    <exclude-unlisted-classes>false</exclude-unlisted-classes>
    <properties>
      <property name="javax.persistence.schema-generation.database.action" value="create"/>
      <property name="eclipselink.logging.level" value="FINE"/> <!-- Optional: for logging -->
    </properties>
  </persistence-unit>
</persistence>
```

# Nota:

Il Progetto lato Server potrebbe dirsi concluso per svolgere il primo punto del compito.

Non ci serve che creare l’ultimo Enterprise Java Bean che verrà chiamato dal client per eseguire alcune operazioni (coincidono solitamente con le query sull’entità e la manipolazione di questi dati).

**Segui i prossimi passi per capire come fare.**

## 1. EJB Stateless + Interfaccia Remota

Tasto destro sul progetto > New > Session Bean

![image.png](/classNotes/external/11_tutorial/image%209.png)

- Gli diamo un nome, ‘NewSessionBean’ ad esempio, lo inseriamo in un package. Questo package verrà clonato anche dal lato Client specificato nel ‘Remote in project’ dove avremo cura di indicare il progetto ‘Client’ creato all’inizio. Nel package del lato client si genererà un’interfaccia di questa classe Bean. Sono i metodi che ‘esporremo’ all’esterno dell’applicazione server.
- Esempio codice Server:

    ```java
    /*
     * Questo EJB si trova nel package server del progetto Server e creerà un'interfaccia
     * nel package server del progetto Client, di default col nome
     * <nomeDiQuestaClasse>Remote
     */
    package server;

    import java.util.List;
    import javax.ejb.Stateless;
    import javax.inject.Inject;
    import javax.persistence.EntityManager;

    @Stateless
    public class PazienteBean implements PazienteBeanRemote {

        @Inject
        private EntityManager em;

        public Paziente trovaPerId(int id) {
            return em.find(Paziente.class, id);
        }

        public List<Paziente> trovaPerTipo(String tipo) {
            return em.createQuery("SELECT p FROM Paziente p WHERE p.tipoAnimale = :tipo", Paziente.class)
                    .setParameter("tipo", tipo)
                    .getResultList();
        }

        public List<Paziente> trovaPerStatusRicovero(boolean status) {
            return em.createQuery("SELECT p FROM Paziente p WHERE p.statusRicovero = :status", Paziente.class)
                    .setParameter("status", status)
                    .getResultList();
        }

        public List<Paziente> trovaTutti() {
            return em.createQuery("SELECT p FROM Paziente p", Paziente.class).getResultList();
        }

        public List<Paziente> trovaSenzaMicrochip() {
            return em.createQuery("SELECT p FROM Paziente p WHERE p.microchip = 0", Paziente.class).getResultList();
        }
    }
    ```
### 1. Riguardo alle query...
Alternativamente a definire le Query all'interno del Bean, la professoressa ci ha spiegato che preferisce avere la definizione delle query all'interno della classe entità a cui appartengono, e poi di creare le query aggiungendone gli eventuali parametri.
<table>
<tr>
<th> Classe Entità </th>
<th> Classe Bean </th>
</tr>
<tr>
<td>

```java
// ... import librerie

@Entity
@NamedQueries({
        @NamedQuery(name = "findAll", query = "SELECT a FROM Animal a"),
        @NamedQuery(name = "findById", query = "SELECT a FROM Animal a WHERE a.id = :id"),
        @NamedQuery(name = "findByType", query = "SELECT a FROM Animal a WHERE a.type = :tipo"),
        @NamedQuery(name = "findByHospitalizationStatus", query = "SELECT a FROM Animal a WHERE a.hospitalized = :status"),
        @NamedQuery(name = "noMicrochip", query = "SELECT a FROM Animal a WHERE a.microchip = 0 OR a.microchip IS NULL")
})
public class Animal implements Serializable {
    // ... resto della classe
}

```

</td>
<td>

```java
// ... import librerie

@Stateless(name = "AnimalBean")
public class AnimalBean implements AnimalBeanRemote {

    @Inject
    private EntityManager em;

    public Animal searchById(int id) {
        return em.createNamedQuery("findById", Animal.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Animal> searchByType(String tipo) {
        return em.createNamedQuery("findByType", Animal.class)
                .setParameter("tipo", tipo)
                .getResultList();
    }

    public List<Animal> searchHospitalized(boolean status) {
        return em.createNamedQuery("findByHospitalizationStatus", Animal.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Animal> findAll() {
        return em.createNamedQuery("findAll", Animal.class)
                .getResultList();
    }

    public List<Animal> searchNoMicrochip() {
        return em.createNamedQuery("noMicrochip", Animal.class).getResultList();
    }
} 
```

</td>
</tr>
</table>
-->

- Esempio codice Client:

    ```java
    /*
     * Auto-Generata, vanno solo specificati i metodi richiamabili dal client!
     * si trova nel package server del Client
     */
    package server;

    import java.util.List;
    import javax.ejb.Remote;

    @Remote
    public interface PazienteBeanRemote {
                Paziente trovaPerId(int id);

        List<Paziente> trovaPerTipo(String tipo);

        List<Paziente> trovaPerStatusRicovero(boolean status);

        List<Paziente> trovaTutti();

        List<Paziente> trovaSenzaMicrochip();
    }

    ```


## beans.xml

- Se non modificassimo questo file di configurazione il Bean non potrebbe essere visto dall’esterno del progetto Server!
- Tasto destro sul progetto > New > beans.xml
- **Modificare  bean-discovery-mode su "all"**

![image.png](/classNotes/external/11_tutorial/image%2010.png)

# Non ci resta che terminare il Client

- Abbiamo terminato il codice lato Server.
- **Ora dobbiamo importare l'entità dal Server ed inserirla nel Client**
- Non ci resta che invocare il nostro Bean con il Context Lookup ed eseguire le varie operazioni specificate nella traccia.
- Il formato del Context Lookup è così definito:
ctx.lookup("java:global/clinica_veterinaria_new/PazienteBean!server.PazienteBeanRemote");
    - java:global/     è necessario
    - clinica_veterinaria_new/     è il nome del progetto Server
    - PazienteBean!     è il nome del Bean presente sul progetto Server
    - server.PazienteBeanRemote"     è il percorso del Client che specifica dove si trova l’interfaccia di PazienteBean
- Utilizzare il bean per eseguire le operazioni
- Esempio

    ```java
    package clinica_veterinaria_client_new;

    import java.util.List;
    import javax.naming.Context;
    import javax.naming.InitialContext;
    import javax.naming.NamingException;
    import server.PazienteBeanRemote;
    import server.Paziente;

    public class Clinica_veterinaria_client_new {

        /**
         * @param args the command line arguments
         */
        public static void main(String[] args) {
            try {
                Context ctx;
                ctx= new InitialContext();
                PazienteBeanRemote pazienteBean = (PazienteBeanRemote)ctx.lookup("java:global/clinica_veterinaria_new/PazienteBean!server.PazienteBeanRemote");
                // Recupera la lista dei pazienti
                List<Paziente> pazienti = pazienteBean.trovaTutti();

                // Stampa la lista dei pazienti
                System.out.println("Lista dei pazienti:");
                for (Paziente paziente : pazienti) {
                    System.out.println(paziente);
                }

            } catch (NamingException e) {
                e.printStackTrace();
            }
        }

    }

    ```