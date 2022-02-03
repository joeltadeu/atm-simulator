package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Withdraw Money :: Return BadRequest when pin is missing")
    request {
        method 'PUT'
        url '/v1/accounts/123456789/withdraw'
        headers {
            contentType(applicationJson())
        }
        body(amount : 100)
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