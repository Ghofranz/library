package contracts.book

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return available books"

    request {
        method GET()
        url "/books/disponibles"
        headers {
            header("Authorization", $(consumer(regex("Bearer .*")), producer("Bearer test-token")))
        }
    }

    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
                [
                        id: 1,
                        titre: "Le Petit Prince",
                        auteur: "Antoine de Saint-Exupéry",
                        isbn: "978-2-07-040850-4",
                        disponible: true
                ]
        ])
    }
}