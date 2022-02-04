package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Balance :: Return BadRequest when pin is invalid")
    request {
        method 'GET'
        url '/v1/accounts/123456789/balance'
        headers {
            header('pin','xxxx')
        }
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