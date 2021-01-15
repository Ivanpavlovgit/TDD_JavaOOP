public interface Transaction {
    int getId ();

    TransactionStatus getStatus ();

    void setStatus (TransactionStatus status);


    double getAmount ();

    String getSender ();

    String getReceiver ();
}
