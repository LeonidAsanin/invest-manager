package org.lennardjones.investmanager.util.purchaseSaleUtil;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;

import java.util.List;

@DisplayName("Creating PageRequest object by input parameters")
class CreatePageRequestByParametersTests {

    @Test
    @DisplayName("Number of page")
    void pageNumber() {
        var pageNumber = 5;
        var sortType = SortType.NONE;
        var sortDirection = Sort.Direction.DESC;
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);
        assertEquals(pageNumber, pageRequest.getPageNumber());
    }

    @Test
    @DisplayName("Ascending order")
    void ascendingOrder() {
        var pageNumber = 1;
        var sortType = SortType.NAME;
        var sortDirection = Sort.Direction.ASC;
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        for (var order : pageRequest.getSort()) {
            assertEquals(sortDirection, order.getDirection());
        }
    }

    @Test
    @DisplayName("Descending order")
    void descendingOrder() {
        var pageNumber = 1;
        var sortType = SortType.NAME;
        var sortDirection = Sort.Direction.DESC;
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        for (var order : pageRequest.getSort()) {
            assertEquals(sortDirection, order.getDirection());
        }
    }

    @Test
    @DisplayName("Sort by id")
    void noneSortType() {
        var pageNumber = 0;
        var sortType = SortType.NONE;
        var sortDirection = Sort.Direction.DESC;
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        assertEquals(1, pageRequest.getSort().stream().count());
        assertTrue(pageRequest.getSort().stream().anyMatch(o -> o.getProperty().equals("id")));
    }

    @Test
    @DisplayName("Sort by name then dateTime, then id")
    void nameSortType() {
        var pageNumber = 1;
        var sortType = SortType.NAME;
        var sortDirection = Sort.Direction.ASC;
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        var list = List.of("name", "dateTime", "id");
        assertEquals(list.size(), pageRequest.getSort().stream().count());
        var i = 0;
        for (var order : pageRequest.getSort()) {
            assertEquals(list.get(i), order.getProperty());
            i++;
        }

    }

    @Test
    @DisplayName("Sort by dateTime then name, then id")
    void dateSortType() {
        var pageNumber = 2;
        var sortType = SortType.DATE;
        var sortDirection = Sort.Direction.ASC;
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        var list = List.of("dateTime", "name", "id");
        assertEquals(list.size(), pageRequest.getSort().stream().count());
        var i = 0;
        for (var order : pageRequest.getSort()) {
            assertEquals(list.get(i), order.getProperty());
            i++;
        }
    }

    @Test
    @DisplayName("Sort by tag then name, then dateTime, then id")
    void tagNameSortType() {
        var pageNumber = 3;
        var sortType = SortType.TAG_NAME;
        var sortDirection = Sort.Direction.DESC;
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        var list = List.of("tag", "name", "dateTime", "id");
        assertEquals(list.size(), pageRequest.getSort().stream().count());
        var i = 0;
        for (var order : pageRequest.getSort()) {
            assertEquals(list.get(i), order.getProperty());
            i++;
        }
    }

    @Test
    @DisplayName("Sort by tag then dateTime, then name, then id")
    void tagDateSortType() {
        var pageNumber = 4;
        var sortType = SortType.TAG_DATE;
        var sortDirection = Sort.Direction.ASC;
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        var list = List.of("tag", "dateTime", "name", "id");
        assertEquals(list.size(), pageRequest.getSort().stream().count());
        var i = 0;
        for (var order : pageRequest.getSort()) {
            assertEquals(list.get(i), order.getProperty());
            i++;
        }
    }
}
