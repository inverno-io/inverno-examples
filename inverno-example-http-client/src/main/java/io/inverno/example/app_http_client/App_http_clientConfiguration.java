package io.inverno.example.app_http_client;

import io.inverno.core.annotation.NestedBean;
import io.inverno.mod.boot.BootConfiguration;
import io.inverno.mod.configuration.Configuration;
import io.inverno.mod.http.client.HttpClientConfiguration;

@Configuration
public interface App_http_clientConfiguration {
    
    @NestedBean
    BootConfiguration boot();

    @NestedBean
    HttpClientConfiguration http_client();
}
