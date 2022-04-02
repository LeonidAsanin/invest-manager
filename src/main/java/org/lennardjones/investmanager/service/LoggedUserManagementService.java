package org.lennardjones.investmanager.service;

import lombok.Getter;
import lombok.Setter;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Service for working with session data of logged user.
 *
 * @since 1.0
 * @author lennardjones
 */
@Service
@SessionScope
@Getter
@Setter
public class LoggedUserManagementService {
    private SortType sortType = SortType.NONE;
    private Sort.Direction sortOrderType = Sort.Direction.ASC;
    private String filterByNameString = "";
    private String filterByTagString = "";
    private ChosenTableToSee chosenTableToSee = ChosenTableToSee.PURCHASE;
    private int purchasePage = 0;
    private int salePage = 0;

    public void setChosenTableIfNotNull(String chosenTableToSee) {
        if (chosenTableToSee != null) {
            this.chosenTableToSee = ChosenTableToSee.valueOf(chosenTableToSee);
        }
    }

    public void setFilterParametersIfNotNull(String filterByNameString, String filterByTagString) {
        if (filterByNameString != null) {
            this.filterByNameString = filterByNameString;
        }
        if (filterByTagString != null) {
            this.filterByTagString = filterByTagString;
        }
    }

    public void setSortingParametersIfNotNull(String sortType, String sortOrderType) {
        if (sortType != null) {
            this.sortType = SortType.valueOf(sortType);
        }
        if (sortOrderType != null) {
            this.sortOrderType = Sort.Direction.valueOf(sortOrderType);
        }
    }
}
