import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ChainblockImplTest {
    private Chainblock chainblock;
    private Transaction transaction;
    private List<Transaction> transactionList;

    @Before
    public void setUp () throws Exception {
        chainblock = new ChainblockImpl ();
        transaction = new TransactionImpl (3,TransactionStatus.SUCCESSFUL,"From","To",12);
        this.createTransactions ();
    }

    @Test
    public void testContainsBooleanValue () {
        assertFalse (chainblock.contains (transaction));
        chainblock.add (transaction);
        assertTrue (chainblock.contains (transaction.getId ()));
    }

    @Test
    public void testAddAddingCorrectTransaction () {
        chainblock.add (transaction);
        assertEquals (3,transaction.getId ());
    }

    @Test
    public void testAddAddingDoesntAddDuplicateTransaction () {
        chainblock.add (transaction);
        chainblock.add (transaction);
        assertEquals (1,chainblock.getCount ());

    }

    @Test
    public void testAddAddingMultipleDifferentTransactions () {
        chainblock.add (transaction);
        Transaction transaction2 = new TransactionImpl
                (12,TransactionStatus.SUCCESSFUL,"Sender","Receiver",26);
        chainblock.add (transaction2);
        assertEquals (2,chainblock.getCount ());
    }

    @Test
    public void testAddIncreasesCount () {
        chainblock.add (transaction);
        assertEquals (1,chainblock.getCount ());
    }

    @Test
    public void testChangeTransactionStatusChangesTransactionStatusOfExistingTransaction () {
        chainblock.add (transaction);
        chainblock.changeTransactionStatus (transaction.getId (),TransactionStatus.FAILED);
        assertEquals (TransactionStatus.FAILED,transaction.getStatus ());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeTransactionStatusThrowsExceptionIfTransactionIsNotPresent () {
        chainblock.changeTransactionStatus (transaction.getId (),TransactionStatus.FAILED);
    }

    @Test
    public void testRemoveTransactionByIdRemovesTransaction () {
        chainblock.add (transaction);
        chainblock.removeTransactionById (3);
        assertFalse (chainblock.contains (3));
        assertEquals (0,chainblock.getCount ());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTransactionByIdThrowsExceptionIfTransactionNoPresent () {
        int idOfNotPresentTransaction = transaction.getId () + 1;
        chainblock.removeTransactionById (idOfNotPresentTransaction);
    }

    @Test
    public void testGetByIdReturnsCorrectTransaction () {
        chainblock.add (transaction);
        assertEquals (transaction,chainblock.getById (transaction.getId ()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByIdThrowsExceptionIfTransactionNotPresent () {
        chainblock.add (transaction);
        assertEquals (transaction,chainblock.getById (transaction.getId () + 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByTransactionStatusThrowsExceptionWhenNoTransactionWithSuchStatusPresent () {
        fillChainBlock ();
        chainblock.getByTransactionStatus (TransactionStatus.UNAUTHORIZED);
    }

    @Test
    public void testGetByTransactionStatusReturnsAllTransactionsWithSuchStatus () {
        fillChainBlock ();
        Iterable<Transaction> byTransactionStatus = chainblock.getByTransactionStatus (TransactionStatus.SUCCESSFUL);
        int                   counter             = 0;
        for (Transaction bts : byTransactionStatus) {
            assertEquals (bts.getStatus (),TransactionStatus.SUCCESSFUL);
            counter++;
        }
        assertEquals (3,counter);
    }

    @Test
    public void testGetByTransactionStatusReturnsAllTransactionsWithSuchStatusSortedByAmount () {
        fillChainBlock ();
        Iterable<Transaction> byTransactionStatus = chainblock.getByTransactionStatus (TransactionStatus.SUCCESSFUL);
        List<Transaction>     tList               = new ArrayList<> ();
        for (Transaction bts : byTransactionStatus) {
            assertEquals (bts.getStatus (),TransactionStatus.SUCCESSFUL);
            tList.add (bts);
        }
        assertEquals (33,tList.get (1).getAmount (),0.0);
        assertEquals (55,tList.get (0).getAmount (),0.0);
        assertEquals (22,tList.get (2).getAmount (),0.0);
        assertTrue (tList.get (0).getAmount () > tList.get (1).getAmount ());
    }

    @Test
    public void testGetAllSendersWithTransactionStatusReturnsAllSenderWithTransactionStatus () {
        fillChainBlock ();
        Iterable<String> sendersWithTransactionStatus = chainblock.getAllSendersWithTransactionStatus (TransactionStatus.SUCCESSFUL);
        List<String>     senders                      = new ArrayList<> ();
        for (String s : sendersWithTransactionStatus) {
            senders.add (s);
        }
        assertEquals (senders.size (),3);
        assertEquals ("Sender_3",senders.get (1));
        assertEquals ("Sender_2",senders.get (2));
        assertEquals ("Sender_2",senders.get (0));


    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllSendersWithTransactionStatusThrowsExceptionIfNoTransactionWithSuchStatusExists () {
        fillChainBlock ();

        chainblock.getAllSendersWithTransactionStatus (TransactionStatus.UNAUTHORIZED);

    }

    @Test
    public void testGetAllReceiversWithTransactionStatusReturnsAllReceiversWithTransactionStatus () {
        fillChainBlock ();
        Iterable<String> receiverWithTransactionStatus = chainblock.getAllReceiversWithTransactionStatus (TransactionStatus.SUCCESSFUL);
        List<String>     receivers                     = new ArrayList<> ();
        for (String s : receiverWithTransactionStatus) {
            receivers.add (s);
        }
        assertEquals (receivers.size (),3);
        assertEquals ("Receiver_3",receivers.get (1));
        assertEquals ("Receiver_2",receivers.get (2));
        assertEquals ("Receiver_2",receivers.get (0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAllReceiversWithTransactionStatusThrowsExceptionIfNoTransactionWithSuchStatusExists () {
        fillChainBlock ();
        chainblock.getAllReceiversWithTransactionStatus (TransactionStatus.UNAUTHORIZED);
    }

    @Test
    public void testGetAllOrderedByAmountDescendingThenByIdReturnsTransactionInCorrectOrder () {
        fillChainBlock ();
        Iterable<Transaction> transactionsOrderedByAmountThenByID = chainblock.getAllOrderedByAmountDescendingThenById ();
        List<Transaction>     transactions                        = getTransactionsWithSpecifiedProperties (transactionsOrderedByAmountThenByID);
        assertEquals (chainblock.getCount (),transactions.size ());
        assertEquals (chainblock.getById (24),transactions.get (0));
        assertEquals (chainblock.getById (23),transactions.get (1));
        assertEquals (chainblock.getById (21),transactions.get (2));
        assertEquals (chainblock.getById (5),transactions.get (3));
        assertEquals (chainblock.getById (4),transactions.get (4));
        assertEquals (chainblock.getById (3),transactions.get (5));
        assertEquals (chainblock.getById (2),transactions.get (6));
        assertEquals (chainblock.getById (1),transactions.get (7));
    }

    @Test
    public void testGetBySenderOrderedByAmountDescendingReturnCorrectTransactionsInCorrectOrder () {
        fillChainBlock ();
        Iterable<Transaction> tListOfSenderByAmountDescending = chainblock.getBySenderOrderedByAmountDescending ("Sender_2");
        List<Transaction>     transactionList                 = getTransactionsWithSpecifiedProperties (tListOfSenderByAmountDescending);
        assertEquals (4,transactionList.size ());
        assertEquals (chainblock.getById (21),transactionList.get (0));
        assertEquals (chainblock.getById (23),transactionList.get (1));
        assertEquals (chainblock.getById (24),transactionList.get (2));
        assertEquals (chainblock.getById (2),transactionList.get (3));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBySenderOrderedByAmountDescendingThrowsExceptionWhenNoTransactionFromSenderPresent () {
        fillChainBlock ();
        Iterable<Transaction> tOfSenderByAmountDescending = chainblock.getBySenderOrderedByAmountDescending ("No such Sender");
        List<Transaction>     transactionList             = getTransactionsWithSpecifiedProperties (tOfSenderByAmountDescending);
        assertNotNull (transactionList);
    }

    @Test
    public void testGetByReceiverOrderedByAmountThenById () {
        fillChainBlock ();
        Iterable<Transaction> tOfReceiverByAmountThenById = chainblock.getByReceiverOrderedByAmountThenById ("Receiver_2");
        List<Transaction>     transactionList             = getTransactionsWithSpecifiedProperties (tOfReceiverByAmountThenById);
        assertEquals (4,transactionList.size ());
        assertEquals (chainblock.getById (21),transactionList.get (0));
        assertEquals (chainblock.getById (23),transactionList.get (1));
        assertEquals (chainblock.getById (24),transactionList.get (2));
        assertEquals (chainblock.getById (2),transactionList.get (3));
        assertEquals (transactionList.get (0).getAmount (),transactionList.get (1).getAmount (),0.0);
        assertTrue (transactionList.get (1).getAmount () > transactionList.get (2).getAmount ());

    }

    @Test
    public void testGetByReceiverOrderedByAmountThenByIdThrowsExceptionIfNoReceiverFound () {
        fillChainBlock ();
        chainblock.getByReceiverOrderedByAmountThenById ("Receiver_5");
    }

    @Test
    public void testGetByTransactionStatusAndMaximumAmount () {
        fillChainBlock ();
        Iterable<Transaction> tOfReceiverByAmountThenById = chainblock.getByTransactionStatusAndMaximumAmount (TransactionStatus.SUCCESSFUL,44);
        List<Transaction>     transactionList             = getTransactionsWithSpecifiedProperties (tOfReceiverByAmountThenById);
        assertEquals (2,transactionList.size ());
        assertEquals (33,transactionList.get (0).getAmount (),0);
        assertEquals (22,transactionList.get (1).getAmount (),0);
    }

    @Test
    public void testGetByTransactionStatusAndMaximumAmountReturnsEmptyCollectionIfNoSuchTransaction () {
        fillChainBlock ();
        Iterable<Transaction> tOfReceiverByAmountThenById = chainblock.getByTransactionStatusAndMaximumAmount (TransactionStatus.SUCCESSFUL,2);
        List<Transaction>     transactionList             = getTransactionsWithSpecifiedProperties (tOfReceiverByAmountThenById);
        assertTrue (transactionList.isEmpty ());
    }

    @Test
    public void testGetBySenderAndMinimumAmountDescendingReturnsCorrectTransactions () {
        fillChainBlock ();
        Iterable<Transaction> tBySenderAndMinimumAmountDescending = chainblock.getBySenderAndMinimumAmountDescending ("Sender_2",49);
        List<Transaction>     transactions                        = getTransactionsWithSpecifiedProperties (tBySenderAndMinimumAmountDescending);
        assertEquals (3,transactions.size ());
        assertEquals (chainblock.getById (21),transactions.get (0));
        assertEquals (chainblock.getById (23),transactions.get (1));
        assertEquals (chainblock.getById (24),transactions.get (2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBySenderAndMinimumAmountDescendingThrowsExceptionIfNoTransactionBySender () {
        fillChainBlock ();
        chainblock.getBySenderAndMinimumAmountDescending ("NoSuchSender",49);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBySenderAndMinimumAmountDescendingThrowsExceptionIfNoTransactionWithBiggerAmount () {
        fillChainBlock ();
        chainblock.getBySenderAndMinimumAmountDescending ("Sender_2",Integer.MAX_VALUE);
    }

    @Test
    public void testGetByReceiverAndAmountRangeReturnCorrectTransactions () {
        fillChainBlock ();
        Iterable<Transaction> tByReceiverAndAmountRange = chainblock.getByReceiverAndAmountRange ("Receiver_2",50,60);
        List<Transaction>     transactions              = getTransactionsWithSpecifiedProperties (tByReceiverAndAmountRange);
        assertEquals (3,transactions.size ());
        assertEquals (chainblock.getById (21),transactions.get (0));
        assertEquals (chainblock.getById (23),transactions.get (1));
        assertEquals (chainblock.getById (24),transactions.get (2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByReceiverAndAmountRangeThrowsExceptionIfNoSuchTransactionReceiverIsPresent () {
        fillChainBlock ();
        chainblock.getByReceiverAndAmountRange ("NotPresent",Double.MIN_VALUE,Double.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByReceiverAndAmountRangeThrowsExceptionIfNoTransactionWithinRangeExists () {
        fillChainBlock ();
        chainblock.getByReceiverAndAmountRange ("Receiver_2",57,Double.MAX_VALUE);
    }

    @Test
    public void testGetAllInAmountRangeReturnsCorrectTransactionsInCorrectOrder () {
        fillChainBlock ();
        Iterable<Transaction> allInAmountRange = chainblock.getAllInAmountRange (22,44);
        List<Transaction>     transactions     = getTransactionsWithSpecifiedProperties (allInAmountRange);
        assertEquals (3,transactions.size ());
        assertEquals (chainblock.getById (2),transactions.get (0));
        assertEquals (chainblock.getById (3),transactions.get (1));
        assertEquals (chainblock.getById (4),transactions.get (2));
    }

    @Test
    public void testGetAllInAmountRange () {
        fillChainBlock ();
        Iterable<Transaction> allInAmountRange = chainblock.getAllInAmountRange (Double.MAX_VALUE - 2,Double.MAX_VALUE - 1);
        List<Transaction>     empty            = getTransactionsWithSpecifiedProperties (allInAmountRange);
        assertTrue (empty.isEmpty ());
    }

    private List<Transaction> getTransactionsWithSpecifiedProperties (Iterable<Transaction> allInAmountRange) {
        List<Transaction> empty = new ArrayList<> ();
        for (Transaction t : allInAmountRange) {
            empty.add (t);
        }
        return empty;
    }


    // Helper Methods //
    private void createTransactions () {
        this.transactionList = new ArrayList<> (Arrays.asList (
                new TransactionImpl (1,TransactionStatus.ABORTED,"Sender_1","Receiver_1",11),
                new TransactionImpl (2,TransactionStatus.SUCCESSFUL,"Sender_2","Receiver_2",22),
                new TransactionImpl (21,TransactionStatus.SUCCESSFUL,"Sender_2","Receiver_2",55),
                new TransactionImpl (23,TransactionStatus.ABORTED,"Sender_2","Receiver_2",55),
                new TransactionImpl (24,TransactionStatus.ABORTED,"Sender_2","Receiver_2",50),
                new TransactionImpl (3,TransactionStatus.SUCCESSFUL,"Sender_3","Receiver_3",33),
                new TransactionImpl (4,TransactionStatus.FAILED,"Sender_4","Receiver_4",44),
                new TransactionImpl (5,TransactionStatus.FAILED,"Sender_5","Receiver_5",55)));
    }

    private void fillChainBlock () {
        for (Transaction t : transactionList) {
            chainblock.add (t);
        }
    }

}