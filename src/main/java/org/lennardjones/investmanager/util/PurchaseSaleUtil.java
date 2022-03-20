package org.lennardjones.investmanager.util;

import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
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
    private static record Transaction(int amount, LocalDateTime dateTime) {}

    /**
     *  This method validates presence of enough amount products to sell considering
     *  {@link org.lennardjones.investmanager.entity.Purchase purchase} and
     *  {@link org.lennardjones.investmanager.entity.Sale sale} dates.
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
            transactionList.add(new Transaction(p.getAmount(), p.getDateTime()));
        }
        for (var s : saleList) {
            transactionList.add(new Transaction(-s.getAmount(), s.getDateTime()));
        }
        transactionList = transactionList.stream().sorted(Comparator.comparing(Transaction::dateTime)).toList();

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
     * {@link org.lennardjones.investmanager.entity.Sale sales}.
     *
     * @param purchaseList list of purchases that were made by user
     * @param saleList list of sales that were made by user
     * @param productName name of the product
     * @return updated with new benefit values sale list
     */
    public static List<Sale> calculateProfitsFromSales(List<Purchase> purchaseList,
                                                       List<Sale> saleList,
                                                       String productName) {
        /* Filtering input lists by name */
        var purchaseStack = purchaseList.stream()
                .filter(p -> p.getName().equals(productName))
                .sorted(Comparator.comparing(Purchase::getDateTime)
                        .thenComparing(Purchase::getId))
                .collect(Collectors.toCollection(LinkedList::new));
        var saleStack = saleList.stream()
                .filter(s -> s.getName().equals(productName))
                .sorted(Comparator.comparing(Sale::getDateTime)
                        .thenComparing(s -> s.getId() == null ? Long.MAX_VALUE : s.getId()))
                                       /* if it is a new sale with null id
                                       then id is set to max long value
                                       so than it is put in the end of
                                       stack */
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
            sale.setAbsoluteProfit(0);
            sale.setRelativeProfit(0);
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
                var saleCurrentProfit = sale.getAbsoluteProfit();

                var purchaseAmount = purchase.getAmount();
                var saleAmount = sale.getAmount();

                var remainingProductAmount = purchaseAmount - saleAmount;

                if (remainingProductAmount >= 0) {
                    var saleBenefit = (saleFullPrice - purchaseFullPrice) * saleAmount;
                    sale.setAbsoluteProfit(saleCurrentProfit + saleBenefit);
                    resultSaleList.add(sale);
                    purchase.setAmount(remainingProductAmount);
                } else {
                    var saleBenefit = (saleFullPrice - purchaseFullPrice) * purchaseAmount;
                    sale.setAbsoluteProfit(saleCurrentProfit + saleBenefit);
                    sale.setAmount(-remainingProductAmount);
                    saleStack.addFirst(sale);
                    break;
                }
            }
        }

        for (int i = 0; i < resultSaleList.size(); i++) {
            var sale = resultSaleList.get(i);
            var absoluteProfit = sale.getAbsoluteProfit();

            /* Changing affected amounts during while-loop to original ones */
            sale.setAmount(saleOriginalValuesList.get(i));

            /* Calculating relative benefit */
            var fullPriceOfSelling = (sale.getPrice() - sale.getCommission()) * sale.getAmount();
            var relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;

            /* Setting final (for the current calculation) benefit values to the sale */
            sale.setAbsoluteProfit(absoluteProfit);
            sale.setRelativeProfit(relativeProfit);
        }

        return resultSaleList;
    }

    public static PageRequest createPageRequestByParameters(int page, SortType sortType, Sort.Direction sortDirection) {
        return switch (sortType) {
            case NONE -> PageRequest.of(page, 10, sortDirection, "id");
            case NAME -> PageRequest.of(page, 10, sortDirection, "name", "dateTime", "id");
            case DATE -> PageRequest.of(page, 10, sortDirection, "dateTime", "name", "id");
            case TAG_NAME -> PageRequest.of(page, 10, sortDirection, "tag", "name", "dateTime", "id");
            case TAG_DATE -> PageRequest.of(page, 10, sortDirection, "tag", "dateTime", "name", "id");
        };
    }
}
