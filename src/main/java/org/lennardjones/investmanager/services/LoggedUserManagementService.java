package org.lennardjones.investmanager.services;

import lombok.Getter;
import lombok.Setter;
import org.lennardjones.investmanager.util.SortOrderType;
import org.lennardjones.investmanager.util.SortType;
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
    private SortOrderType sortOrderType = SortOrderType.ASC;
    private String filterByNameString = "";
}
