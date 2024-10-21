[Lezione8.pdf](/slides/8_2_CDI_Nuova_AA2425.pdf)
# Context and Dependency Injection

## Inversion of Control
- La prima versione di Java EE ha introdotto il concetto di **inversion of control**
    - il container prende il controllo del business.
    - fornendo servizi (transazioni, sicurezza, ciclo di vita...) senza che il programmatore debba necessariamente scriverli.
- Prendere il controllo significa gestire in toto il nostro programma, configurandone il contesto di esecuzione, **Context**
    - e risolvendone le dipendenze con altre componenti: **Dependency Injection**
- Ideato da Martin Fowler nel 2004, alla base dello sviluppo del calcolo enteprise.

### Understanding Beans
- Sono gli attori principali delle versioni di Java Enterprise dalla 6 in poi.
- Tutte le componenti JEE sono CDI Managed Beans.
- Concetto di base: **loose coupling, strong typing**: unire i vantaggi dei 2 mondi.
- il disaccoppiamento permette di poter inserire interceptors, decorators e gestione di eventi in tutta la piattaforma.
<br>
- "Plain Old Java Object" -> POJO
    - sono classi Java eseguite in una JVM
- Java SE ha i suoi JavaBeans: POJOs eseguiti nella JVM che seguono delle convenzioni:
    - naming, getter/setter, costruttore di default...
- Java EE ha i suoi Enterprise Java Beans (EJB): sono eseguiti in un container, contengono metadati, costruttore non final, usufruiscono dei servizi del container (sicurezza, transazioni...)

## Pattern Dependency Injection
- Dependency Injection (DI) è un Design Pattern che disaccoppia componenti dipendenti.
- È parte dell'inversion of control.
- Dove l'inversion riguarda il processo di ottenere le dipendenze necessarie
- Termine coniato da Martin Fowler
<br>
- (JNDI) Java Naming and Directory Interface fornisce a richiesta un riferimento ad un certo oggetto.
    - il pattern fa sì che il container inietti la dipedenza nell'oggetto che ne ha bisogno.
<br>
- invece di avere un oggetto che cerca altri oggetti, il container fa inject di questi oggetti dipendenti al posto nostro.

## Classi "Tightly Coupled"
<table>
<tr>
<th> Esempio </th>
<th> Spiegazione </th>
</tr>
<tr>
<td>

```java
public class Class1 {
    public Class 2 c;
}

public class Class2 {
    // resto implementazione
}
```

</td>
<td>

- Class1 è strettamente accoppiata a Class2.
- In quanto ha una variabile che fa riferimento ad una sua istanza.
    - definita altrove

</td>
</tr>
</table>

## Classi "Loosely Coupled"
<table>
<tr>
<th> Esempio </th>
<th> Spiegazione </th>
</tr>
<tr>
<td>

```java
public class Class1 {
    public IClass 2 c;
    //...
}

public interface IClass2 {
    //...
}

public class Class2 implements IClass2{
    // resto implementazione
}
```

</td>
<td>

- Class1 è meno accoppiata a Class2.
- Infatti ha un'interfaccia, non dei riferimenti alla Class2.
    - definita altrove

</td>
</tr>
</table>
- Ma da qualche parte dovrà pur instanziare il costruttore per Class2, quindi non è veramente Loosely Coupled. È un'illusione
- Questo problema è risolvibile solo con la Dependency Injection

```Java
public class Class1 {
    public IClass2 c;

    public Class1() {
        c = DependecyFactory.Resolve<IClass2>();
    }
}
```
- È in grado di istanziare un oggetto del giusto tipo, a seconda delle esigenze del sistema. Ma non è ancora un'indipendenza!

## Life-Cycle Management
- In un POJO, il ciclo di vita è molto semplice: lo sviluppatore crea una nuova istanza con **new** e si attende che il Garbage Collector liberi memoria.
- In un CDI Bean, all'interno di un container, non è possibile usare **new**.
    - è il container che è responsabile del ciclo di vita di un bean e quindi della creazione.
    - risolve le dipendenze necessarie e invoca i metodi annotati con:
        - @PostConstruct, eseguiti dopo l'instanziazione del bean, dopo che sono state verificate tutte le dipendenze e prima della stessa invocazione di metodi di business.
        - e prima della deallocazione, chiama i metodi annotati con @Pre-Destroy

### Scope di un Bean
- I Bean CDI possono essere stateful, il che significa che sono in esecuzione in uno "scope" (contesto) ben definito.
- Al contrario di componenti stateless (e.g., stateless session beans) o
singleton (e.g., Servlets or singletons), client diversi di un bean stateful
possono vedere questo bean in stati differenti, perchè lo “scope” può dettare
che ognuno veda un bean diverso
    - Client eseguiti nello stesso contesto vedranno lo stesso stato del bean
    - Client in diversi contesti vedranno una diversa istanza
- Tutto gestito automaticamente dal container (nessun controllo da parte del
client)

## Interception
- Si frappongono tra invocazioni di metodi di business
- Utili alla Aspect-Oriented-Programming (AOP), paradigma che separa i cross-cutting-concerns di un'architettura dal codice di business.
    - technical concerns: log ingresso/uscita da metodo, log durata invocazione...
    - business concerns: check extra se un cliente spende più di 10.000€, inviare un nuovo ordine di acquisto se una merce è a livello minimo...
- CDI Beans supportano questa funzionalità (AOP) offrendo la possibilità di intercettare le invocazioni di metodo con gli Interceptor.
- Il container si occupa di chiamare gli interceptor prima/dopo le invocazioni.

### La potenza degli Interceptor
- Disaccoppiano efficacemente i cross-cutting concerns di natura tecnica dalla logica di business.
- La maniera in cui il container assicura i servizi agli EJB è attraverso una catena configurabile di interceptors.
- L'esecuzione degli interceptor non è a conoscenza alcuna da chi scrive POJO o EJB.

## Loose Coupling and Strong Typing
- Gli interceptor rappresentano un mezzo potente per disaccoppiare dettagli tecnici dalla logica di business.
- La gestione del ciclo di vita di un bean disaccoppia il bean stesso dalla gestione del suo ciclo di vita.
    - Esistono altri modi per disaccoppiare
        - I bean possono usare event notifications per disaccoppiare event producer da event consumer.
- Riusciamo ad ottenere un loosely coupling usando: annotazioni con parametri e forte tipizzazione, legata in modo "safe" ai bean.
- L'uso di identificatori (XML) viene limitato moltissimo a pochi casi di deployment.

## Deployment Descriptor
