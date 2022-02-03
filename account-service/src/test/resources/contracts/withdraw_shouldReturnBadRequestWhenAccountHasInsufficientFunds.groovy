package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Withdraw Money :: Return BadRequest when account has insufficient funds")
    request {
        method 'PUT'
        url '/v1/accounts/123456789/withdraw'
        headers {
            contentType(applicationJson())
            header('pin','1234')
        }
        body(amount : 2000)
    }
    response {
        status 400
        body([
                description: "Your Account has insufficient funds to complete this request"
        ]
        )
        headers {
            contentType(applicationJson())
        }
    }
}