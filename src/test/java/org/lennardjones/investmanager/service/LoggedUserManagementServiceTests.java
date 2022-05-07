package org.lennardjones.investmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggedUserManagementServiceTests {
    LoggedUserManagementService loggedUserManagementService;

    @BeforeEach
    void setup() {
        loggedUserManagementService = new LoggedUserManagementService();
    }

    @Test
    void setChosenTableIfNotNullTest() {
        assertAll(
                () -> {
                    //given
                    var chosenTableToSee = loggedUserManagementService.getChosenTableToSee();

                    //when
                    loggedUserManagementService.setChosenTableIfNotNull(null);

                    //then
                    assertEquals(chosenTableToSee, loggedUserManagementService.getChosenTableToSee());
                },
                () -> {
                    //given
                    var chosenTableToSeeString = "SALE";

                    //when
                    loggedUserManagementService.setChosenTableIfNotNull(chosenTableToSeeString);

                    //then
                    assertEquals(ChosenTableToSee.SALE, loggedUserManagementService.getChosenTableToSee());
                }
        );
    }

    @Test
    void setFilterParametersIfNotNullTest() {
        //given
        var filterByName = loggedUserManagementService.getFilterByNameString();
        var filterByTag = loggedUserManagementService.getFilterByTagString();

        assertAll(
                () -> {
                    //when
                    loggedUserManagementService.setFilterParametersIfNotNull(null, null);

                    //then
                    assertAll(
                        () -> assertEquals(filterByName, loggedUserManagementService.getFilterByNameString()),
                        () -> assertEquals(filterByTag, loggedUserManagementService.getFilterByTagString())
                    );
                },
                () -> {
                    //when
                    loggedUserManagementService.setFilterParametersIfNotNull("filterByName", null);

                    //then
                    assertAll(
                            () -> assertEquals("filterByName", loggedUserManagementService.getFilterByNameString()),
                            () -> assertEquals(filterByTag, loggedUserManagementService.getFilterByTagString())
                    );
                },
                () -> {
                    //when
                    loggedUserManagementService.setFilterParametersIfNotNull(null, "filterByTag");

                    //then
                    assertAll(
                            () -> assertEquals("filterByName", loggedUserManagementService.getFilterByNameString()),
                            () -> assertEquals("filterByTag", loggedUserManagementService.getFilterByTagString())
                    );
                }
        );
    }

    @Test
    void setSortingParametersIfNotNullTest() {
        //given
        var sortType = loggedUserManagementService.getSortType();
        var sortOrderType = loggedUserManagementService.getSortOrderType();

        assertAll(
                () -> {
                    //when
                    loggedUserManagementService.setSortingParametersIfNotNull(null, null);

                    //then
                    assertAll(
                        () -> assertEquals(sortType, loggedUserManagementService.getSortType()),
                        () -> assertEquals(sortOrderType, loggedUserManagementService.getSortOrderType())
                    );
                },
                () -> {
                    //when
                    loggedUserManagementService.setSortingParametersIfNotNull("TAG_DATE", null);

                    //then
                    assertAll(
                        () -> assertEquals(SortType.TAG_DATE, loggedUserManagementService.getSortType()),
                        () -> assertEquals(sortOrderType, loggedUserManagementService.getSortOrderType())
                    );
                },
                () -> {
                    //when
                    loggedUserManagementService.setSortingParametersIfNotNull(null, "DESC");

                    //then
                    assertAll(
                        () -> assertEquals(SortType.TAG_DATE, loggedUserManagementService.getSortType()),
                        () -> assertEquals(Sort.Direction.DESC, loggedUserManagementService.getSortOrderType())
                    );
                }
        );
    }
}
