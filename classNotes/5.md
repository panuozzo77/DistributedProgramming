[Lezione5.pdf](/slides/5_TEORIA_4_Thread2_Nuove_AA2425.pdf)

## **La legge di Amdahl**

esempio: 5 amici che vogliono dipingere una casa con 5 stanze. Se devono verniciare le stanze uguali e sono tutti produttivi, finiscono in 1/5 del tempo che ci avrebbe messo una persona sola.  “**speedup”**
Se una stanza è grande il doppio, il risultato cambia
**Legge di Amdahl**: Lo speedup S di un programma X è il rapporto tra il tempo impiegato da un processore per eseguire X rispetto al tempo impiegato da n processori per eseguire X.
Sia P la parte del programma X che è possibile parallelizzare, allora 1-p è la parte sequenziale del programma. 

$S = \frac{1}{1-p+\frac{p}{n}}$

Per velocizzare un programma non basta investire sull’hardware ma è assolutamente necessario e molto più cost-effective impiegnarsi a rendere la parte predominante la parallelizzazione rispetto alla parte sequenziale.

### L'uso di macchine multiprocessore
- La legge di Amdahl ci dice che la parte sequenziale del programma rallenta
significativamente qualsiasi speedup che possiamo pensare di ottenere
- Quindi, per velocizzare un programma non basta investire sull’hardware (più
processori, più veloci, .. ) ma è assolutamente necessario e molto più cost-
effective impegnarsi a rendere la parte parallela predominante rispetto alla
parte sequenziale

## Un idioma per prevenire errori

- Per rendere un metodo sincronizzato, basta aggiungere synchronized alla sua dichiarazione.
    - Non è possibile che due esecuzioni dello stesso metodo sullo stesso oggetto siano interfogliate.
    - Quando un thread esegue un metodo sincronizzato per un oggetto, gli altri thread che invocano metodi sincronizzati dello stesso oggetto sono sospesi fino a quando il primo thread non ha finito.
    - Quando un thread esce da un metodo sincronizzato, allora si stabilisce una relazione happens-before con tutte le successive invocazioni dello stesso metodo sullo stesso oggetto
        - perché il cambio allo stato, effettuato dal thread appena uscito sono visibili a tutti i thread.
    - I costruttori non possono essere sincronizzati (solo il thread che crea dovrebbe avere accesso all’oggetto in costruzione)

    ```java
        public class SynchronyzedCounter {
        private int c = 0;
        public synchronized void increment() {
            c ++ ;
        }
        public synchronized void decrement() {
            c -- ;
        }
        public synchronized void value() {
            return c ;
        }
    }
    ```

## Cosa comporta un metodo sincronizzato?
- Non è possibile che due esecuzioni dello stesso metodo sullo stesso oggetto siano interfogliate
- Quando un thread esegue un metodo sincronizzato per un oggetto, gli altri thread
che invocano metodi sincronizzati dello stesso oggetto sono sospesi fino a quando
il primo thread non ha finito
- Quando un thread esce da un metodo sincronizzato, allora si stabilisce una
relazione happens-before con tutte le successive invocazioni dello stesso metodo
sullo stesso oggetto
    - o i cambi allo stato, effettuati dal thread appena uscito sono visibili a tutti i thread
- I costruttori non possono essere sincronizzati (solo il thread che crea dovrebbe avere accesso all’oggetto in costruzione)

## Lock Intrinseci (quando usi Synchronized)

- Un monitor lock è un’entità associata ad ogni oggetto.
- Garantisce sia accesso esclusivo sia accesso consistente (relazione happens-before)
- Un thread deve:
    - acquisire il lock di un oggetto
    - rilasciarlo quando ha terminato
- Quando il lock che possedeva viene rilasciato, viene stabilita la relazione happens-before
- Quando un thread esegue un metodo sincronizzato di un oggetto ne acquisisce il lock e lo rilascia al termine (anche se c’è un’eccezione).

### Synchronized Statements
- Specificando di quale oggetto si usa il lock:
```Java
public void addName(String name) {
    synchronized (this) { //sincronizziamo l'accesso solo nella modifica
        lastName = name;
        nameCount++;
    }
    nameList.add(name); //poi procediamo con l'inserimento in maniera concorrente.
}

```
- Sincronizziamo gli accesso solo durante la modifica, poi si provvede in maniera concorrente all'inserimento in lista.

### Sincronizzazione a Grana Fine
```Java
public class MsLunch {
    private long c1 = 0;
    private long c2 = 0;
    private Object lock1 = new Object();
    private Object lock2 = new Object();

    public void inc1() {
        // lots of code
        synchronized(lock1) {
            c1++;
        }
        // lots of code
    }

    public void inc2() {
        // lots of code
        synchronized(lock2) {
            c2++;
        }
        // lots of code
    }
}
```

## Azioni Atomiche
- Azioni che non sono interrompibili e si completano (del tutto) oppure per niente
- Si possono specificare azioni atomiche in Java per:
    - read e write su variabili di riferimento e su tipi primitivi (a parte long e double)
    - read e write su tutte le variabili volatile
- Write a variabili volatile stabiliscono una relazione happens-before con le letture
successive
- Tipi di dato definiti in __java.util.concurrent.atomic__

```Java
import java.util.concurrent.atomic.AtomicInteger;

class AtomicCounter {
    private AtomicInteger c = new AtomicInteger(0);

    public void increment() {
        c.incrementAndGet();
    }

    public void decrement() {
        c.decrementAndGet();
    }

    public int value() {
        return c.get();
    }
}
```

## DeadLock

### Cos'è un Deadlock
Due thread sono bloccati perché entrambi in attesa dell’altro. Solitamente accade perché cercano di ottenere delle risorse tra loro utilizzate e non vi è maniera di sbloccarlo. 

### Cos'è la Starvation
- Quando un thread non riesce a acquisire accesso ad una risorsa condivisa in maniera da non riuscire a fare progresso.
    - la risorsa è indisponibile per thread 'ingordi'
- Es: metodo sincronizzato che impiega troppo tempo.
    - se invocato spesso, altri thread possono essere prevenuti dall'accesso.
- Es: arbitrarietà dello scheduler
    - Priorità 3 e 4 mappate allo stesso modo...

### Livelock
- Un Thread A può reagire ad azioni di un altro Thread B
    - che reagisce con una risposta verso A.
- I 2 thread non sono bloccati (Deadlock) ma sono occupati a rispondere alle azioni dell'altro.
- Anche se in esecuzione, non vi è progresso.
- Es: 2 persone si incontrano in una strada stretta.
    - belligerante: aspetta che l'altro si sposti
    - garbato: si sposta di lato
        - se avessimo 2 belligeranti: deadlock!
        - se avessimo 2 garbati: livelock!

```Java
package errore;

public class Friend {
    private String name;

    public Friend(String name) {
        this.name = name;
    }

    public void bow(Friend bower) {
        synchronized (this) {
            System.out.format("%s: %s has bowed to me!%n", this.name, bower.getName());
            bower.bowBack(this);
        }
    }

    public void bowBack(Friend bower) {
        synchronized (this) {
            System.out.format("%s: %s has bowed back to me!%n", this.name, bower.getName());
        }
    }

    public String getName() {
        return name;
    }
}

--------------------------------------------------------------------------------

package errore;
import errore.Friend;
public class Main {
	public static void main (String[] args) {
		Friend one = new Friend("Anna");
		Friend two = new Friend("Francesca");
		
		two.bow(one);
		
	}

}
```