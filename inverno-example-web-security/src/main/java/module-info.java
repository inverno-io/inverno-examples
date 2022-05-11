@io.inverno.core.annotation.Module(excludes = "io.inverno.mod.security.http")
module io.inverno.example.app_web_security {
    requires io.inverno.mod.boot;
    requires io.inverno.mod.web;
    requires io.inverno.mod.security.http;
	requires io.inverno.mod.security.jose;
}
