package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Deposit Funds :: Return BadRequest when pin ins invalid")
    request {
        method 'PUT'
        url '/v1/accounts/123456789/deposit'
        headers {
            header('pin','xxxx')
        }
        body(amount : 100)
    }
    response {
        status 400
        body([
                description: "Pin account is invalid!"
        ]
        )
        headers {
            contentType(applicationJson())
        }
    }
}