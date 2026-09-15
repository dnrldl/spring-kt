package com.springkt.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Spring KT API")
                    .description("Spring KT 인증/유저 API 문서")
                    .version("v1"),
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        DPOP_AUTH_SCHEME,
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name("Authorization")
                            .description("DPoP access token. 예: DPoP {accessToken}"),
                    )
                    .addSecuritySchemes(
                        DPOP_PROOF_SCHEME,
                        SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.HEADER)
                            .name("DPoP")
                            .description("요청마다 새로 생성한 DPoP proof JWT"),
                    ),
            )
    }

    private companion object {
        const val DPOP_AUTH_SCHEME = "dpopAuth"
        const val DPOP_PROOF_SCHEME = "dpopProof"
    }
}
