# Package Structure

This project uses a domain-first package structure with lightweight DDD boundaries.

```text
com.springkt
├── global
│   ├── config
│   ├── error
│   ├── security
│   └── web
├── user
│   ├── domain
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   ├── application
│   │   ├── service
│   │   └── usecase
│   ├── infrastructure
│   │   ├── persistence
│   │   └── querydsl
│   └── presentation
│       ├── dto
│       └── web
├── auth
│   ├── domain
│   │   ├── model
│   │   ├── repository
│   │   └── service
│   ├── application
│   │   ├── service
│   │   └── usecase
│   ├── infrastructure
│   │   ├── persistence
│   │   └── redis
│   └── presentation
│       ├── dto
│       └── web
└── todo
    ├── domain
    │   ├── model
    │   ├── repository
    │   └── service
    ├── application
    │   ├── service
    │   └── usecase
    ├── infrastructure
    │   ├── persistence
    │   └── querydsl
    └── presentation
        ├── dto
        └── web
```

## Rules

- `presentation` handles HTTP requests and response DTOs.
- `application` handles use cases and transaction boundaries.
- `domain` contains business rules, domain models, and repository contracts.
- `infrastructure` contains technical implementations such as JPA, Redis, and external clients.
- `infrastructure/persistence` contains Spring Data JPA repositories and JPA entity mapping.
- `infrastructure/querydsl` contains Querydsl read/query implementations for dynamic or complex lookups.
- `global` contains cross-cutting concerns shared by multiple domains.
- Domain code should not depend on Spring MVC, JPA entities, Redis, or HTTP DTOs.

## Initial Domain Direction

- `user` owns user identity, profile, role, and account status.
- `auth` owns login, token issuing, refresh token storage, and logout/session policies.
- `todo` owns todo lifecycle, completion state, and user-scoped todo queries.
- `auth` may read user information through a user-facing application use case or domain contract, but it should not directly own user lifecycle rules.
