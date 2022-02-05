package contracts.balance

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Balance :: Return balance from account")
    request {
        method 'GET'
        url '/v1/accounts/123456789/balance'
        headers {
            header('pin','1234')
        }
    }
    response {
        status 200
        body(
                balance: "800",
                overdraft: "200"
        )
        headers {
            contentType(applicationJson())
        }
    }
}