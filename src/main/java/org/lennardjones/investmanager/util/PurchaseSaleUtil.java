package org.lennardjones.investmanager.util;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.entities.Sale;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This util class is supposed to help to work with purchase and sale lists.
 *
 * @since 1.0
 * @author lennardjones
 */
public class PurchaseSaleUtil {
    /**
     * This record describes user's transaction (purchase or sale)
     */
    private static record Transaction(int amount, LocalDate date) {}

    /**
     *  This method validates presence of enough amount products to sell considering
     *  {@link org.lennardjones.investmanager.entities.Purchase purchase} and
     *  {@link org.lennardjones.investmanager.entities.Sale sale} dates.
     *
     * @param purchaseList list of purchases that were made by user
     * @param saleList list of sales that were made by user
     * @param productName name of the product
     * @return true if current queue purchase and sale lists are correct, otherwise false
     */
    public static boolean isQueueIncorrect(List<Purchase> purchaseList, List<Sale> saleList, String productName) {
        purchaseList = purchaseList.stream().filter(p -> p.getName().equals(productName)).toList();
        saleList = saleList.stream().filter(s -> s.getName().equals(productName)).toList();

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
                return true;
            }
        }
        return false;
    }

    /**
     * This method calculates absolute and relative benefits from
     * {@link org.lennardjones.investmanager.entities.Sale sales}.
     *
     * @param purchaseList list of purchases that were made by user
     * @param saleList list of sales that were made by user
     * @param productName name of the product
     * @return updated with new benefit values sale list
     */
    public static List<Sale> calculateBenefitsFromSales(List<Purchase> purchaseList,
                                                        List<Sale> saleList,
                                                        String productName) {
        /* Filtering input lists by name */
        var purchaseStack = purchaseList.stream()
                .filter(p -> p.getName().equals(productName))
                .sorted(Comparator.comparing(Purchase::getDate))
                .collect(Collectors.toCollection(LinkedList::new));
        var saleStack = saleList.stream()
                .filter(s -> s.getName().equals(productName))
                .sorted(Comparator.comparing(Sale::getDate))
                .collect(Collectors.toCollection(LinkedList::new));

        /* Cloning of input list elements in order not to affect them */
        for (int i = 0; i < purchaseStack.size(); i++) {
            var purchase = purchaseStack.get(i);
            Purchase purchaseClone = null;
            try {
                purchaseClone = (Purchase) purchase.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            purchaseStack.set(i, purchaseClone);
        }
        for (int i = 0; i < saleStack.size(); i++) {
            var sale = saleStack.get(i);
            Sale saleClone = null;
            try {
                saleClone = (Sale) sale.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            saleStack.set(i, saleClone);
        }

        /* Setting up sale benefits to zero in order to calculate new values */
        saleStack = saleStack.stream().peek(sale -> {
            sale.setAbsoluteBenefit(0);
            sale.setRelativeBenefit(0);
        }).collect(Collectors.toCollection(LinkedList::new));

        var saleOriginalValuesList = saleStack.stream().map(Sale::getAmount).toList();

        var resultSaleList = new LinkedList<Sale>();

        /* Filling in result sale list */
        while (!saleStack.isEmpty()) {
            var purchase = purchaseStack.removeFirst();
            var purchaseFullPrice = purchase.getPrice() + purchase.getCommission();
            while (!saleStack.isEmpty()) {
                var sale = saleStack.removeFirst();
                var saleFullPrice = sale.getPrice() - sale.getCommission();
                var saleCurrentBenefit = sale.getAbsoluteBenefit();

                var purchaseAmount = purchase.getAmount();
                var saleAmount = sale.getAmount();

                var remainingProductAmount = purchaseAmount - saleAmount;

                if (remainingProductAmount >= 0) {
                    var saleBenefit = (saleFullPrice - purchaseFullPrice) * saleAmount;
                    sale.setAbsoluteBenefit(saleCurrentBenefit + saleBenefit);
                    resultSaleList.add(sale);
                    purchase.setAmount(remainingProductAmount);
                } else {
                    var saleBenefit = (saleFullPrice - purchaseFullPrice) * purchaseAmount;
                    sale.setAbsoluteBenefit(saleCurrentBenefit + saleBenefit);
                    sale.setAmount(-remainingProductAmount);
                    saleStack.addFirst(sale);
                    break;
                }
            }
        }

        for (int i = 0; i < resultSaleList.size(); i++) {
            var sale = resultSaleList.get(i);
            var absoluteBenefit = sale.getAbsoluteBenefit();

            /* Changing affected amounts during while-loop to original ones */
            sale.setAmount(saleOriginalValuesList.get(i));

            /* Calculating relative benefit */
            var fullPriceOfSelling = (sale.getPrice() - sale.getCommission()) * sale.getAmount();
            var relativeBenefit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteBenefit) - 1) * 100;

            /* Setting final (for the current calculation) benefit values to the sale */
            sale.setAbsoluteBenefit(absoluteBenefit);
            sale.setRelativeBenefit(relativeBenefit);
        }

        return resultSaleList;
    }

    /**
     * This method sorts {@link org.lennardjones.investmanager.entities.Purchase purchase} list
     * by the specified {@link org.lennardjones.investmanager.util.SortType type of sort}
     * in the specified {@link org.lennardjones.investmanager.util.SortOrderType order type}
     *
     * @param purchaseList list to sort
     * @param sortType type of sort
     * @param sortOrderType type of order sort
     * @return sorted purchase list
     */
    public static List<Purchase> sortPurchaseList(List<Purchase> purchaseList,
                                                  SortType sortType,
                                                  SortOrderType sortOrderType) {
        var resultPurchaseList = switch (sortType) {
            case NONE -> purchaseList.stream()
                        .sorted(Comparator.comparing(Purchase::getId))
                        .collect(Collectors.toList());
            case NAME -> purchaseList.stream()
                        .sorted(Comparator.comparing(Purchase::getName)
                                .thenComparing(Purchase::getDate))
                        .collect(Collectors.toList());
            case DATE -> purchaseList.stream()
                        .sorted(Comparator.comparing(Purchase::getDate)
                                .thenComparing(Purchase::getName))
                        .collect(Collectors.toList());
        };

        if (sortOrderType.equals(SortOrderType.DEC)) {
            Collections.reverse(resultPurchaseList);
        }

        return resultPurchaseList;
    }

    /**
     * This method sorts {@link org.lennardjones.investmanager.entities.Sale sale} list
     * by the specified {@link org.lennardjones.investmanager.util.SortType type of sort}
     * in the specified {@link org.lennardjones.investmanager.util.SortOrderType order type}
     *
     * @param saleList list to sort
     * @param sortType type of sort
     * @param sortOrderType type of order sort
     * @return sorted sale list
     */
    public static List<Sale> sortSaleList(List<Sale> saleList,
                                          SortType sortType,
                                          SortOrderType sortOrderType) {
        var resultSaleList = switch (sortType) {
            case NONE -> saleList.stream()
                        .sorted(Comparator.comparing(Sale::getId))
                        .collect(Collectors.toList());
            case NAME -> saleList.stream()
                        .sorted(Comparator.comparing(Sale::getName)
                                .thenComparing(Sale::getDate))
                        .collect(Collectors.toList());
            case DATE -> saleList.stream()
                        .sorted(Comparator.comparing(Sale::getDate)
                                .thenComparing(Sale::getName))
                        .collect(Collectors.toList());
        };

        if (sortOrderType.equals(SortOrderType.DEC)) {
            Collections.reverse(resultSaleList);
        }

        return resultSaleList;
    }
}
