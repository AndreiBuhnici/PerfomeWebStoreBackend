package org.ecommerce.productapi.constants;

public class PathConstants {

    public static final String API_V1 = "/api/v1";
    public static final String ORDER = "/order";
    public static final String ORDERS = "/orders";
    public static final String PERFUMES = "/perfumes";
    public static final String GRAPHQL = "/graphql";

    public static final String API_V1_ORDER = API_V1 + ORDER;
    public static final String API_V1_PERFUMES = API_V1 + PERFUMES;

    public static final String ADD = "/add";
    public static final String EDIT = "/edit";
    public static final String DELETE_BY_PERFUME_ID = "/delete/{perfumeId}";
    public static final String ORDER_BY_EMAIL = ORDER + "/{userEmail}";
    public static final String ORDER_DELETE = ORDER + "/delete/{orderId}";
    public static final String GRAPHQL_ORDERS = GRAPHQL + ORDERS;
    public static final String GRAPHQL_ORDER = GRAPHQL + ORDER;

    public static final String ORDER_ID = "/{orderId}";
    public static final String ORDER_ID_ITEMS = ORDER_ID + "/items";

    public static final String PERFUME_ID = "/{perfumeId}";
    public static final String IDS = "/ids";
    public static final String SEARCH = "/search";
    public static final String SEARCH_GENDER = SEARCH + "/gender";
    public static final String SEARCH_PERFUMER = SEARCH + "/perfumer";
    public static final String SEARCH_TEXT = SEARCH + "/text";
    public static final String GRAPHQL_IDS = GRAPHQL + IDS;
    public static final String GRAPHQL_PERFUMES = GRAPHQL + PERFUMES;
    public static final String GRAPHQL_PERFUME = GRAPHQL + "/perfume";
}
