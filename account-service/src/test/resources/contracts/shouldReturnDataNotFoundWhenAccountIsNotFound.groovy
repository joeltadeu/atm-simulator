package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Return DataNotFound when account is not found on the database")
    request {
        method 'GET'
        url '/v1/accounts/54644/balance'
        headers {
            contentType(applicationJson())
            header('pin','1234')
        }
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