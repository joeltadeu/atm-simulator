package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Deposit Funds :: Return DataNotFound when account is not found on the database")
    request {
        method 'PUT'
        url '/v1/accounts/54644/deposit'
        headers {
            header('pin','1234')
        }
        body(amount : 100)
    }
    response {
        status 404
        body([
                description: "Account number '54644' was not found"
        ]
        )
        headers {
            contentType(applicationJson())
        }
    }
}