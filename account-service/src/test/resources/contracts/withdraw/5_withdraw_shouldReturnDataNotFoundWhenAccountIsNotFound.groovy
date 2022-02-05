package contracts.withdraw

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Withdraw Money :: Return DataNotFound when account is not found on the database")
    request {
        method 'PUT'
        url '/v1/accounts/54644/withdraw'
        headers {
            header("Content-type", applicationJson())
            header('pin','1234')
        }
        body(amount : 500)
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