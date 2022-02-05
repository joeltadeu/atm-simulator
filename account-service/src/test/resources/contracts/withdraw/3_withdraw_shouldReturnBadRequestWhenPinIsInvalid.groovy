package contracts.withdraw

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Withdraw Money :: Return BadRequest when pin is invalid")
    request {
        method 'PUT'
        url '/v1/accounts/123456789/withdraw'
        headers {
            header("Content-type", applicationJson())
            header('pin','xxxx')
        }
        body(amount : 500)
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