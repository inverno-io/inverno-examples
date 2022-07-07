@io.inverno.core.annotation.Module
module io.inverno.example.app_web_security {
    requires io.inverno.mod.boot;
	requires io.inverno.mod.configuration;
    requires io.inverno.mod.ldap;
    requires io.inverno.mod.security.http;
	requires io.inverno.mod.security.jose;
	requires io.inverno.mod.security.ldap;
	requires io.inverno.mod.web;
}
