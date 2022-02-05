package contracts.balance

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Balance :: Return BadRequest when pin is invalid")
    request {
        method 'GET'
        url '/v1/accounts/123456789/balance'
    }
    response {
        status 400
        body([
                description: "Required request header 'pin' for method parameter type String is not present"
        ]
        )
        headers {
            contentType(applicationJson())
        }
    }
}