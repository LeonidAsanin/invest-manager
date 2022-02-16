package org.lennardjones.investmanager.util;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.model.Transaction;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * This util class is supposed to validate queue of purchases and sales considering their amount and date.
 *
 * @since 1.0
 * @author lennardjones
 */
public class PurchaseSaleUtil {
    /**
     *  This method validates presence of enough amount products to sell considering
     *  {@link org.lennardjones.investmanager.entities.Purchase purchase} and
     *  {@link org.lennardjones.investmanager.entities.Sale sale} dates.
     *
     * @param purchaseList list of purchases that were made by user
     * @param saleList list of sales that were made by user
     * @return true if current queue purchase and sale lists are correct, otherwise false
     */
    public static boolean isQueueCorrect(List<Purchase> purchaseList, List<Sale> saleList) {
        List<Transaction> transactionList = new LinkedList<>();
        for (var p : purchaseList) {
            transactionList.add(new Transaction(p.getAmount(), p.getDate()));
        }
        for (var s : saleList) {
            transactionList.add(new Transaction(-s.getAmount(), s.getDate()));
        }
        transactionList = transactionList.stream().sorted(Comparator.comparing(Transaction::date)).toList();
        var productAmount = 0;
        for (var transaction: transactionList) {
            if ((productAmount += transaction.amount()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method sorts purchase list by the specified type of sort
     *
     * @param purchaseList list to sort
     * @param sortType type of sort
     * @return sorted purchase list
     */
    public static List<Purchase> sortPurchaseList(List<Purchase> purchaseList, SortType sortType) {
        return switch (sortType) {
            case NONE -> purchaseList.stream()
                        .sorted(Comparator.comparing(Purchase::getId))
                        .toList();
            case NAME -> purchaseList.stream()
                        .sorted(Comparator.comparing(Purchase::getName)
                                .thenComparing(Purchase::getDate))
                        .toList();
            case DATE -> purchaseList.stream()
                        .sorted(Comparator.comparing(Purchase::getDate)
                                .thenComparing(Purchase::getName))
                        .toList();
        };
    }

    /**
     * This method sorts sale list by the specified type of sort
     *
     * @param saleList list to sort
     * @param sortType type of sort
     * @return sorted sale list
     */
    public static List<Sale> sortSaleList(List<Sale> saleList, SortType sortType) {
        return switch (sortType) {
            case NONE -> saleList.stream()
                        .sorted(Comparator.comparing(Sale::getId))
                        .toList();
            case NAME -> saleList.stream()
                        .sorted(Comparator.comparing(Sale::getName)
                                .thenComparing(Sale::getDate))
                        .toList();
            case DATE -> saleList.stream()
                        .sorted(Comparator.comparing(Sale::getDate)
                                .thenComparing(Sale::getName))
                        .toList();
        };
    }
}
