package test_project



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Author)
@Mock(Publication)
class AuthorTests {

    @Test
    void "Calling a named query that wrapper another named query fails"() {
        def publication = new Publication(author: "juan", numberOfPages: 10, title: "10 pages in a book", datePublished: new Date() )
        publication.save(failOnError: true,flush: true)
        assert Publication.count == 1

        def author = new Author(publications: [publication]).save(failOnError: true,flush: true)

        assert Author.authorsWithRecentPublications().count() == 0

    }
}
