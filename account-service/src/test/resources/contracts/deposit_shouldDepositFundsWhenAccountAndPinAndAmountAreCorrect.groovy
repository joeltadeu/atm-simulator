package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Deposit Funds :: Carry out a deposit transaction in the account")
    request {
        method 'PUT'
        url '/v1/accounts/123456789/deposit'
        headers {
            header('pin','1234')
        }
        body(amount : 100)
    }
    response {
        status 200
    }
}