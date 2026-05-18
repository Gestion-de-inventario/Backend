package com.comedor.backend.domain.model.enums;

public enum PermissionCode {

    // =========================
    // BENEFICIARY
    // =========================
    BENEFICIARY_CREATE,
    BENEFICIARY_SEARCH_BY_DNI,
    BENEFICIARY_CREATE_BY_DNI,
    BENEFICIARY_EDIT,
    BENEFICIARY_LIST_BY_STATUS,

    // =========================
    // CATEGORY
    // =========================
    CATEGORY_LIST_BY_STATUS,
    CATEGORY_CREATE,
    CATEGORY_CHANGE_STATUS,

    // =========================
    // MENU REPORT
    // =========================
    MENU_REPORT_CREATE_REPORT,
    MENU_REPORT_GET_BY_DATE,
    MENU_REPORT_ADD_PRODUCT,
    MENU_REPORT_EDIT_PRODUCT,
    MENU_REPORT_REMOVE_PRODUCT,
    MENU_REPORT_ADD_BENEFICIARY,
    MENU_REPORT_EDIT_BENEFICIARY,
    MENU_REPORT_REMOVE_BENEFICIARY,
    MENU_REPORT_GET_SUMMARY,

    // =========================
    // PRODUCT
    // =========================
    PRODUCT_LIST_BY_STATUS,
    PRODUCT_CREATE,
    PRODUCT_CHANGE_STATUS,
    PRODUCT_EDIT,

    // =========================
    // TAG
    // =========================
    TAG_LIST_BY_STATUS,
    TAG_CREATE,
    TAG_CHANGE_STATUS,

    // =========================
    // TRANSACTION
    // =========================
    TRANSACTION_LIST_ALL,

    // =========================
    // MODIFICATION
    // =========================
    MODIFICATION_LIST_ALL,
    
    // =========================
    // USER
    // =========================
    USER_LIST_ALL,
    USER_LIST_ACTIVE,
    USER_CREATE,
    USER_EDIT,
    USER_DEACTIVATE,
    USER_ACTIVATE,

    // =========================
    // ROLE
    // =========================
    ROLE_CREATE,
    ROLE_EDIT,
    ROLE_LIST_BY_STATUS,
    ROLE_GET_BY_ID,

    // =========================
    // PERMISSION
    // =========================
    PERMISSION_LIST_ALL

}