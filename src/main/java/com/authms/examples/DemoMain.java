package com.authms.examples;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Account2 {
    private final String id;
    private BigDecimal balance;

    public Account2(String id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized boolean withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            return false;
        }
        balance = balance.subtract(amount);
        return true;
    }

    public synchronized void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
}

interface TransferService {
    boolean transferMoney(Account2 from, Account2 to, BigDecimal amount);
    void addAccount(Account2 account);
}

class TransferServiceImpl implements TransferService {
    private final Map<String, Account2> accounts = new ConcurrentHashMap<>();

    @Override
    public boolean transferMoney(Account2 from, Account2 to, BigDecimal amount) {
        Account2 account1 = accounts.get(from.getId());
        Account2 account2 = accounts.get(to.getId());

        if (account1 == null || account2 == null) return false;

        synchronized (account1) {
            synchronized (account2) {
                if (amount.compareTo(account1.getBalance()) > 0) return false;
                if (account1.withdraw(amount)) {
                    account2.deposit(amount);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addAccount(Account2 account) {
        accounts.put(account.getId(), account);
    }
}

public class DemoMain {
    public static void main(String[] args) {

        TransferService transferService = new TransferServiceImpl();

        Account2 account1 = new Account2("1", new BigDecimal(2500));
        Account2 account2 = new Account2("2", new BigDecimal(1000));

        transferService.addAccount(account1);
        transferService.addAccount(account2);

        System.out.println("Before transfer: A1 = " + account1.getBalance() + ", A2 = " + account2.getBalance());

        Runnable task1 = () -> transferService.transferMoney(account1, account2, new BigDecimal(300));
        Runnable task2 = () -> transferService.transferMoney(account1, account2, new BigDecimal(400));

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("After transfer: A1 = " + account1.getBalance() + ", A2 = " + account2.getBalance());
    }
}
