package com.authms.examples;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

// Account class with AtomicReference for lock-free balance updates
class Account {
    private final String id;
    private final AtomicReference<Integer> balance;

    public Account(String id, int initialBalance) {
        this.id = id;
        this.balance = new AtomicReference<>(initialBalance);
    }

    public String getId() {
        return id;
    }

    public int getBalance() {
        return balance.get();
    }

    public boolean withdraw(int amount) {
        while (true) {
            int currentBalance = balance.get();
            if (currentBalance < amount) {
                return false; // Not enough funds
            }
            if (balance.compareAndSet(currentBalance, currentBalance - amount)) {
                return true;
            }
        }
    }

    public void deposit(int amount) {
        while (true) {
            int currentBalance = balance.get();
            if (balance.compareAndSet(currentBalance, currentBalance + amount)) {
                return;
            }
        }
    }
}

// Money Transfer Service
class MoneyTransferService {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getId(), account);
    }

    public boolean transfer(String fromId, String toId, int amount) {
        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);

        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid account");
        }

        if (from.withdraw(amount)) {
            to.deposit(amount);
            return true;
        }
        return false;
    }
}

// Example usage
public class DepositMain {
    public static void main(String[] args) {
        MoneyTransferService service = new MoneyTransferService();

        Account acc1 = new Account("A1", 1000);
        Account acc2 = new Account("A2", 500);

        service.addAccount(acc1);
        service.addAccount(acc2);

        System.out.println("Before transfer: A1 = " + acc1.getBalance() + ", A2 = " + acc2.getBalance());

        boolean success = service.transfer("A1", "A2", 300);
        System.out.println("Transfer success: " + success);

        System.out.println("After transfer: A1 = " + acc1.getBalance() + ", A2 = " + acc2.getBalance());
    }
}
