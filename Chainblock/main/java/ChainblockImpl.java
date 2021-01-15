import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChainblockImpl implements Chainblock {
    private LinkedHashMap<Integer, Transaction> transactionMap;


    public ChainblockImpl () {
        this.transactionMap = new LinkedHashMap<> ();
    }

    public int getCount () {
        return transactionMap.size ();
    }

    public void add (Transaction transaction) {
        if (!transactionMap.containsKey (transaction.getId ())) {
            this.transactionMap.put (transaction.getId (),transaction);
        }
    }

    public boolean contains (Transaction transaction) {
        return contains (transaction.getId ());
    }

    public boolean contains (int id) {
        return this.transactionMap.containsKey (id);
    }

    public void changeTransactionStatus (int id,TransactionStatus newStatus) {
        if (!this.transactionMap.containsKey (id)) {
            throw new IllegalArgumentException ();
        }
        this.transactionMap.get (id).setStatus (newStatus);
    }

    public void removeTransactionById (int id) {
        if (!this.transactionMap.containsKey (id)) {
            throw new IllegalArgumentException ();
        }
        this.transactionMap.remove (id);
    }

    public Transaction getById (int id) {
        if (!this.transactionMap.containsKey (id)) {
            throw new IllegalArgumentException ();
        }
        return this.transactionMap.get (id);
    }

    public Iterable<Transaction> getByTransactionStatus (TransactionStatus status) {
        List<Transaction> transactionList = new ArrayList<> ();
        for (Transaction t : this.transactionMap.values ()) {
            if (t.getStatus () == status) {
                transactionList.add (t);
            }
        }
        if (transactionList.isEmpty ()) {
            throw new IllegalArgumentException ();
        }
        return transactionList.stream ()
                .sorted (Comparator.comparing
                        (Transaction::getAmount)
                        .reversed ())
                .collect (Collectors.toList ());
    }

    public Iterable<String> getAllSendersWithTransactionStatus (TransactionStatus status) {
        Iterable<Transaction> byTransactionStatus = getByTransactionStatus (status);
        List<String>          senders             = new ArrayList<> ();
        for (Transaction transaction : byTransactionStatus) {
            senders.add (transaction.getSender ());
        }
        return senders;
    }

    public Iterable<String> getAllReceiversWithTransactionStatus (TransactionStatus status) {
        Iterable<Transaction> byTransactionStatus = getByTransactionStatus (status);
        List<String>          receivers           = new ArrayList<> ();
        for (Transaction transaction : byTransactionStatus) {
            receivers.add (transaction.getReceiver ());
        }
        return receivers;
    }

    public Iterable<Transaction> getAllOrderedByAmountDescendingThenById () {
//TODO there should be more elegant way to to this
        List<Transaction> transactionList1 = this.transactionMap.values ().stream ()
                .sorted (Comparator.comparingDouble (Transaction::getAmount)
                        .reversed ()).collect (Collectors.toList ());

        return transactionList1.stream ()
                .sorted (Comparator.comparing (Transaction::getId)
                        .reversed ()).collect (Collectors.toList ());
    }

    public Iterable<Transaction> getBySenderOrderedByAmountDescending (String sender) {
        List<Transaction> transactionList = new ArrayList<> ();
        for (Transaction t : this.transactionMap.values ()) {
            if (t.getSender ().equals (sender)) {
                transactionList.add (t);
            }
        }
        if (transactionList.isEmpty ()) {
            throw new IllegalArgumentException ();
        }
        return transactionList.stream ()
                .sorted (Comparator.comparing (Transaction::getAmount).reversed ())
                .collect (Collectors.toList ());

    }

    public Iterable<Transaction> getByReceiverOrderedByAmountThenById (String receiver) {
        if (this.transactionMap.values ().stream ().noneMatch (t -> t.getReceiver ().equals (receiver))) {
            throw new IllegalArgumentException ();
        }
        return this.transactionMap.values ().stream ()
                .filter (t -> t.getReceiver ().equals (receiver))
                .sorted (Comparator.comparingDouble (Transaction::getAmount)
                        .reversed ()
                        .thenComparingInt (Transaction::getId))
                .collect (Collectors.toList ());
    }

    public Iterable<Transaction> getByTransactionStatusAndMaximumAmount (TransactionStatus status,double amount) {
        List<Transaction>     transactionList     = new ArrayList<> ();
        Iterable<Transaction> byTransactionStatus = getByTransactionStatus (status);
        for (Transaction transaction : byTransactionStatus) {
            transactionList.add (transaction);
        }
        if (transactionList.isEmpty ()) {
            return Collections.emptyList ();
        } else {
            return transactionList.stream ()
                    .filter (t -> t.getAmount () < amount)
                    .sorted (Comparator.comparing (Transaction::getAmount).reversed ())
                    .collect (Collectors.toList ());
        }
    }

    public Iterable<Transaction> getBySenderAndMinimumAmountDescending (String sender,double amount) {
        if (this.transactionMap
                .values ()
                .stream ()
                .noneMatch (v -> v.getSender ().equals (sender))
                || this.transactionMap
                .values ()
                .stream ()
                .noneMatch (v -> v.getAmount () > amount)) {
            throw new IllegalArgumentException ();
        }

        return this.transactionMap.values ().stream ()
                .filter (v -> v.getSender ().equals (sender))
                .filter (v -> v.getAmount () > amount)
                .sorted (Comparator.comparingDouble (Transaction::getAmount).reversed ())
                .collect (Collectors.toList ());
    }


    public Iterable<Transaction> getByReceiverAndAmountRange (String receiver,double lo,double hi) {
        List<Transaction> collect = this.transactionMap.values ().stream ()
                .filter (v -> v.getReceiver ().equals (receiver))
                .filter (v -> v.getAmount () >= lo)
                .filter (v -> v.getAmount () < hi)
                .sorted (Comparator.comparingDouble (Transaction::getAmount).reversed ())
                .sorted (Comparator.comparingInt (Transaction::getId))
                .collect (Collectors.toList ());
        if (collect.isEmpty ()) {
            throw new IllegalArgumentException ();
        }
        return collect;
    }

    public Iterable<Transaction> getAllInAmountRange (double lo,double hi) {
        List<Transaction> collect = this.transactionMap.values ().stream ().filter (v -> v.getAmount () >= lo && v.getAmount () <= hi).collect (Collectors.toList ());
        if (collect.isEmpty ()) {
            return Collections.emptyList ();
        }
        return collect;
    }

    public Iterator<Transaction> iterator () {
        return null;
    }
}
