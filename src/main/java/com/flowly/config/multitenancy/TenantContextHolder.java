package com.flowly.config.multitenancy;

public class TenantContextHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setCurrentTenant(String tenantId) {
        CONTEXT.set(tenantId);
    }

    public static String getCurrentTenant() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
