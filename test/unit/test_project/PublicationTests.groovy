package test_project



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Publication)
class PublicationTests {

    void testSomething() {
       def publication = new Publication(author: "juan", numberOfPages: 10, title: "10 pages in a book", datePublished: new Date() )

        publication.save(failOnError: true,flush: true)

        assert Publication.count == 1
        assert Publication.recentPublications().count() == 1
    }
}
