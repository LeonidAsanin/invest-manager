package org.lennardjones.investmanager.util.purchaseSaleUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Creating PageRequest object by input parameters")
class CreatePageRequestByParametersTests {

    @Test
    @DisplayName("Proper number of page")
    void pageNumberTest() {
        //given
        var pageNumber = 5;
        var sortType = SortType.NONE;
        var sortDirection = Sort.Direction.DESC;

        //when
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        //then
        assertEquals(pageNumber, pageRequest.getPageNumber());
    }

    @Test
    @DisplayName("Proper ascending order")
    void ascendingOrderTest() {
        //given
        var pageNumber = 1;
        var sortType = SortType.NAME;
        var sortDirection = Sort.Direction.ASC;

        //when
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        //then
        for (var order : pageRequest.getSort()) {
            assertEquals(sortDirection, order.getDirection());
        }
    }

    @Test
    @DisplayName("Proper descending order")
    void descendingOrderTest() {
        //given
        var pageNumber = 1;
        var sortType = SortType.NAME;
        var sortDirection = Sort.Direction.DESC;

        //when
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        //then
        for (var order : pageRequest.getSort()) {
            assertEquals(sortDirection, order.getDirection());
        }
    }

    @Test
    @DisplayName("Proper sort by id")
    void noneSortTypeTest() {
        //given
        var pageNumber = 0;
        var sortType = SortType.NONE;
        var sortDirection = Sort.Direction.DESC;

        //when
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        //then
        assertEquals(1, pageRequest.getSort()
                                            .stream()
                                            .count());
        assertTrue(pageRequest.getSort()
                              .stream()
                              .anyMatch(o -> o.getProperty().equals("id")));
    }

    @Test
    @DisplayName("Proper sort by name then dateTime, then id")
    void nameSortTypeTest() {
        //given
        var pageNumber = 1;
        var sortType = SortType.NAME;
        var sortDirection = Sort.Direction.ASC;

        //when
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        //then
        var list = List.of("name", "dateTime", "id");
        assertEquals(list.size(), pageRequest.getSort()
                                             .stream()
                                             .count());
        var i = 0;
        for (var order : pageRequest.getSort()) {
            assertEquals(list.get(i), order.getProperty());
            i++;
        }

    }

    @Test
    @DisplayName("Proper sort by dateTime then name, then id")
    void dateSortTypeTest() {
        //given
        var pageNumber = 2;
        var sortType = SortType.DATE;
        var sortDirection = Sort.Direction.ASC;

        //when
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        //then
        var list = List.of("dateTime", "name", "id");
        assertEquals(list.size(), pageRequest.getSort()
                                             .stream()
                                             .count());
        var i = 0;
        for (var order : pageRequest.getSort()) {
            assertEquals(list.get(i), order.getProperty());
            i++;
        }
    }

    @Test
    @DisplayName("Proper sort by tag then name, then dateTime, then id")
    void tagNameSortTypeTest() {
        //given
        var pageNumber = 3;
        var sortType = SortType.TAG_NAME;
        var sortDirection = Sort.Direction.DESC;

        //when
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        //then
        var list = List.of("tag", "name", "dateTime", "id");
        assertEquals(list.size(), pageRequest.getSort()
                                             .stream()
                                             .count());
        var i = 0;
        for (var order : pageRequest.getSort()) {
            assertEquals(list.get(i), order.getProperty());
            i++;
        }
    }

    @Test
    @DisplayName("Proper sort by tag then dateTime, then name, then id")
    void tagDateSortTypeTest() {
        //given
        var pageNumber = 4;
        var sortType = SortType.TAG_DATE;
        var sortDirection = Sort.Direction.ASC;

        //when
        var pageRequest = PurchaseSaleUtil
                .createPageRequestByParameters(pageNumber, sortType, sortDirection);

        //then
        var list = List.of("tag", "dateTime", "name", "id");
        assertEquals(list.size(), pageRequest.getSort()
                                             .stream()
                                             .count());
        var i = 0;
        for (var order : pageRequest.getSort()) {
            assertEquals(list.get(i), order.getProperty());
            i++;
        }
    }
}
