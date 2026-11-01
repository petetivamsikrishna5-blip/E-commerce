# ThinkMart Store

ThinkMart is a Spring Boot 3 storefront built with Java 17, Spring MVC, Thymeleaf, and responsive CSS. The product catalog is provided by the Java service layer, and cart state is held in the user's HTTP session.

## Run

Requirements: Java 17+ and Maven 3.8+.

```powershell
mvn spring-boot:run
```

Open http://localhost:8080.

The app starts at signup. Create a demo account, sign in, and continue to the storefront.

Public source repository: https://github.com/petetivamsikrishna5-blip/E-commerce

## Test

```powershell
mvn test
```

The sample coupon code is `THINK8`. Product, cart, and account data are demo-only; accounts are not persisted or authenticated. Checkout and contact submission do not persist orders or send email.