package contracts.withdraw

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Withdraw Funds :: Withdraw money from account")
    request {
        method 'PUT'
        url '/v1/accounts/123456789/withdraw'
        headers {
            header("Content-type", applicationJson())
            header('pin','1234')
        }
        body(amount : 500)
    }
    response {
        status 200
    }
}