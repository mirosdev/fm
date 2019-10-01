package com.spring.fm.security;

public final class FmAuthorities {

    public static final class Privileges {

        // User & Admin
        public static final String READ_PRIVILEGE   = "READ_PRIVILEGE";
        public static final String WRITE_PRIVILEGE  = "WRITE_PRIVILEGE";

        // Admin
        public static final String READ_MONITORING_PRIVILEGE  = "READ_MONITORING_PRIVILEGE";

    }

    public static final class Roles {

        public static final String ROLE_ADMIN       = "ROLE_ADMIN";
        public static final String ROLE_USER        = "ROLE_USER";

    }
}
