package com.rivigo.riconet.core.enums.zoomticketing;

/**
 * @author ramesh
 * @date 05-Mar-2018
 */
public enum AutoAssignSuggestion {

    PICKUP_GROUP("Pickup Group' of OU to which the PRQ is assigned"),
    LOGGED_IN_USER("Logged In User"),
    AREA_BD("BD of that Area"),
    DELIVERY_GROUP("Delivery Group' of delivery OU"),
    APPOINTMENT_CE_TEAM("Appointment Team' of CE"),
    CE_TEAM("CE team"),
    POD_CELL("POD cell"),
    DEPS_TEAM("DEPS Team"),
    FINANCE_TEAM("Finance Team"),
    CLIENT_SAM("SAM of that Client"),
    CREDIT_CLIENT_SAM("Credit Client - SAM"),
    NONE("None");

    private String displayName;

    AutoAssignSuggestion(String displayName){
        this.displayName = displayName;
    }

}
