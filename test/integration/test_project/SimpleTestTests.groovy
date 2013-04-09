package test_project

import static org.junit.Assert.*
import org.junit.*

class SimpleTestTests {

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void "Using a simple named query works"() {
        def aCountry = new Country(name: "UK").save(failOnError: true)
        def publication = new Publication(country: aCountry, author: "juan", numberOfPages: 10, title: "10 pages in a book", datePublished: new Date() )
        publication.save(failOnError: true,flush: true)

        def author = new Author().addToPublications(publication).save(flush: true)
        assert Publication.count == 1

        assert Author.authorsWithRecentPublications().count()  == 1

    }

    //  Testing Nested NamedQueries in integration because in unit test fail
    void  ValidatingThatProblemsWithNestedNamedQueriesAreOnlyInUnitTesting() {
        def aCountry = new Country(name:"UK").save(failOnError: true)
        def publication = new Publication(country: aCountry, author: "juan", numberOfPages: 11, title: "11 pages in a book", datePublished: new Date() )
        publication.save(failOnError: true,flush: true)

        assert Publication.count == 1

        def author = new Author().addToPublications(publication).save(flush: true)

        assert Author.authorsWithRecentsPublicationsLargerThanTenPages().count() == 1
    }
}
