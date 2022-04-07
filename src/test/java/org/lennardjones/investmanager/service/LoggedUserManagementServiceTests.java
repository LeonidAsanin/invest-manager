package org.lennardjones.investmanager.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;

class LoggedUserManagementServiceTests {
    LoggedUserManagementService loggedUserManagementService;

    @BeforeEach
    void before() {
        loggedUserManagementService = new LoggedUserManagementService();
    }

    @Test
    void setChosenTableIfNotNullTest() {
        var chosenTableToSee = loggedUserManagementService.getChosenTableToSee();
        loggedUserManagementService.setChosenTableIfNotNull(null);
        assertEquals(chosenTableToSee, loggedUserManagementService.getChosenTableToSee());

        loggedUserManagementService.setChosenTableIfNotNull("SALE");
        assertEquals(ChosenTableToSee.SALE, loggedUserManagementService.getChosenTableToSee());
    }

    @Test
    void setFilterParametersIfNotNullTest() {
        var filterByName = loggedUserManagementService.getFilterByNameString();
        var filterByTag = loggedUserManagementService.getFilterByTagString();

        loggedUserManagementService.setFilterParametersIfNotNull(null, null);
        assertAll(
                () -> assertEquals(filterByName, loggedUserManagementService.getFilterByNameString()),
                () -> assertEquals(filterByTag, loggedUserManagementService.getFilterByTagString())
        );

        loggedUserManagementService.setFilterParametersIfNotNull("filterByName", null);
        assertAll(
                () -> assertEquals("filterByName", loggedUserManagementService.getFilterByNameString()),
                () -> assertEquals(filterByTag, loggedUserManagementService.getFilterByTagString())
        );

        loggedUserManagementService.setFilterParametersIfNotNull(null, "filterByTag");
        assertAll(
                () -> assertEquals("filterByName", loggedUserManagementService.getFilterByNameString()),
                () -> assertEquals("filterByTag", loggedUserManagementService.getFilterByTagString())
        );
    }

    @Test
    void setSortingParametersIfNotNullTest() {
        var sortType = loggedUserManagementService.getSortType();
        var sortOrderType = loggedUserManagementService.getSortOrderType();

        loggedUserManagementService.setSortingParametersIfNotNull(null, null);
        assertAll(
                () -> assertEquals(sortType, loggedUserManagementService.getSortType()),
                () -> assertEquals(sortOrderType, loggedUserManagementService.getSortOrderType())
        );

        loggedUserManagementService.setSortingParametersIfNotNull("TAG_DATE", null);
        assertAll(
                () -> assertEquals(SortType.TAG_DATE, loggedUserManagementService.getSortType()),
                () -> assertEquals(sortOrderType, loggedUserManagementService.getSortOrderType())
        );

        loggedUserManagementService.setSortingParametersIfNotNull(null, "DESC");
        assertAll(
                () -> assertEquals(SortType.TAG_DATE, loggedUserManagementService.getSortType()),
                () -> assertEquals(Sort.Direction.DESC, loggedUserManagementService.getSortOrderType())
        );
    }
}
