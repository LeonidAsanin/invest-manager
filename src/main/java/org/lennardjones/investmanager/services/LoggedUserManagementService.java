package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.util.SortOrderType;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class LoggedUserManagementService {
    private Long userId;
    private String username;
    private boolean loggedIn;
    private SortType sortType = SortType.NONE;
    private SortOrderType sortOrderType = SortOrderType.ASC;
    private String filterByNameString = "";

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public SortOrderType getSortOrderType() {
        return sortOrderType;
    }

    public void setSortOrderType(SortOrderType sortOrderType) {
        this.sortOrderType = sortOrderType;
    }

    public String getFilterByNameString() {
        return filterByNameString;
    }

    public void setFilterByNameString(String filterByNameString) {
        this.filterByNameString = filterByNameString;
    }
}
