package test_project

import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class HibernateFilterTests {

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        Publication.disableHibernateFilter('parametrizedFilter')
        Publication.disableHibernateFilter('defaultFilter')
        Publication.disableHibernateFilter('countryFilter')
    }

    @Test
    void "Basic usage of hibernate filters with an example that filter publication using pages number"() {

        // Creating two different country
        def aCountry = new Country(name:"UK").save(failOnError: true)
        def anotherCountry = new Country(name:"SP").save(failOnError: true)

        // creating two publications with different number of pages
        def publicationWithOnePage = new Publication(country: aCountry, author: "juan", numberOfPages: 1, title: "1 page in a book", datePublished: new Date(), enable: true )
        publicationWithOnePage.save(failOnError: true,flush: true)
        def publicationWithTwoPages = new Publication(country: anotherCountry, author: "Peter", numberOfPages: 2, title: "1 page in a book", datePublished: new Date() , enable: false )
        publicationWithTwoPages.save(failOnError: true,flush: true)

        // testing the filters
        // Default behaviour in the class
        println "By default filter should be enabled ->" + Publication.list()
        assert  Publication.count == 2 //TODO FAIL
        println "Filter fails by default because it must be enable"

        // Testing filter with closures: Code inside filter run with the filter disabled
        Publication.withoutHibernateFilter('defaultFilter') {
            println "Using closure WITHOUT filter ->" + Publication.list() // returns all members
            assert  Publication.count == 2
        }

        // Testing filter with closures: Code inside filter run with the filter enabled
        Publication.withHibernateFilter('defaultFilter') {
            println "Using closure WITH filter ->" + Publication.list() // return filtered Publication
            assert "Only one Publication should be returned", Publication.count == 1
        }

        // USing filter on demand
        Publication.enableHibernateFilter('defaultFilter')
        println "Enabling defaultFilter ->" + Publication.list()
        assert Publication.count == 1

        Publication.disableHibernateFilter('defaultFilter')
        println "Disabling defaultFilter ->" + Publication.list()
        assert Publication.count == 2
    }


    @Test
    void "Using a parametrized filter that filter the publication by enable or disable"() {

        // creating two publication disabled
        def aCountry = new Country(name:"UK").save(failOnError: true)
        def publicationWithOnePage = new Publication(country: aCountry, author: "juan", numberOfPages: 1, title: "1 page in a book", datePublished: new Date(), enable: false )
        publicationWithOnePage.save(failOnError: true,flush: true)

        def anotherCountry = new Country(name:"SP").save(failOnError: true)
        def publicationWithTwoPages = new Publication(country: anotherCountry, author: "Peter", numberOfPages: 2, title: "1 page in a book", datePublished: new Date() , enable: false )
        publicationWithTwoPages.save(failOnError: true,flush: true)

        // testing the filters
        println "default result ->" + Publication.list() // returns all members
        assert Publication.count() == 2

        // enabling and initialization the filter to search only enabled Publications
        def parametrizedFilter =  Publication.enableHibernateFilter('parametrizedFilter')
        parametrizedFilter.setParameter('myParam',true)

        assert Publication.count == 0
    }

    @Test
    void "Filtering the Publications using a parametrized filter by country object"() {

        // creating publication assigned to one country
        def aCountry = new Country(name:"UK").save(failOnError: true)
        def publicationWithOnePage = new Publication(country: aCountry, author: "juan", numberOfPages: 1, title: "1 page in a book", datePublished: new Date(), enable: false )
        publicationWithOnePage.save(failOnError: true,flush: true)

        // creating publication assigned to another country
        def anotherCountry = new Country(name:"SP").save(failOnError: true)
        def publicationWithTwoPages = new Publication(country: anotherCountry, author: "Peter", numberOfPages: 2, title: "1 page in a book", datePublished: new Date() , enable: false )
        publicationWithTwoPages.save(failOnError: true,flush: true)

        // testing the filters
        println "default result ->" + Publication.list() // returns all members
        assert Publication.count() == 2

        //enabling the filter and setting the parameter
        def parametrizedFilter =  Publication.enableHibernateFilter('countryFilter')
        parametrizedFilter.setParameter('myParam',anotherCountry.id)

        assert "Only publication filtered should be returned", Publication.count == 1

        // disabling the filter
        Publication.disableHibernateFilter('countryFilter')
        println "Disabling defaultFilter ->" + Publication.list() // returns all members
        assert Publication.count == 2
    }

    @Test
    void "A filter could be defined in a father class and be used with inheritance"() {

        /** @see PublicationWithInheritance and @see CountryFilterable ,
           understand that country field and filters are provided by inheritance
         */

        // publication with one country
        def aCountry = new Country(name:"UK").save(failOnError: true)
        def publicationWithOnePage = new PublicationWithInheritance(country: aCountry, author: "juan", numberOfPages: 1, title: "1 page in a book", datePublished: new Date(), enable: false )
        publicationWithOnePage.save(failOnError: true,flush: true)

        // publication with another country
        def anotherCountry = new Country(name:"SP").save(failOnError: true)
        def publicationWithTwoPages = new PublicationWithInheritance(country: anotherCountry, author: "Peter", numberOfPages: 2, title: "1 page in a book", datePublished: new Date() , enable: false )
        publicationWithTwoPages.save(failOnError: true,flush: true)

        println "default result ->" + PublicationWithInheritance.list() // returns all members
        assert PublicationWithInheritance.count() == 2

        // enabling filter and filter by the object anotherCountry
        def parametrizedFilter =  PublicationWithInheritance.enableHibernateFilter('countryFilter')
        parametrizedFilter.setParameter('myParam',anotherCountry.id)

        println "Using countryFilter ->" + PublicationWithInheritance.list()
        assert PublicationWithInheritance.count == 1

        // disabling filter
        PublicationWithInheritance.disableHibernateFilter('countryFilter')
        println "Disabling countryFilter ->" + PublicationWithInheritance.list() // returns all members
        assert PublicationWithInheritance.count == 2

        // Testing if the parent class can activate the filter for childrens classes
        println "Can we use the abstractClass to Activate the filter ?"
        def parametrizedFilterUsingParentClass = CountryFilterable.enableHibernateFilter('countryFilter')
        parametrizedFilterUsingParentClass.setParameter('myParam',anotherCountry.id)

        assert PublicationWithInheritance.count == 1

        PublicationWithInheritance.list().each {
            assert it.country.id == anotherCountry.id
            println "------>"+it.inspect()
        }

        CountryFilterable.disableHibernateFilter('countryFilter')
        assert PublicationWithInheritance.count == 2
    }


    @Test
    void "Named queries and hibernate filter should work together"() {

        // publication with one country and book in the title
        def aCountry = new Country(name:"UK").save(failOnError: true)
        def publicationWithBookTitleAndOneCountry = new PublicationWithInheritance(country: aCountry, author: "juan", numberOfPages: 1, title: "title that contains book word", datePublished: new Date(), enable: false )
        publicationWithBookTitleAndOneCountry.save(failOnError: true)
        // publication with one country and not book in the title
        def publicationWithoutBookTitleAndOneCountry = new PublicationWithInheritance(country: aCountry, author: "juan", numberOfPages: 1, title: "magazine", datePublished: new Date(), enable: false )
        publicationWithoutBookTitleAndOneCountry.save(failOnError: true)

        // publication with another country  and book in title
        def anotherCountry = new Country(name:"SP").save(failOnError: true)
        def publicationWithBookTitleAndAnotherCountry = new PublicationWithInheritance(country: anotherCountry, author: "Peter", numberOfPages: 2, title: "title that contains book word", datePublished: new Date() , enable: false )
        publicationWithBookTitleAndAnotherCountry.save(failOnError: true,flush: true)

        assert "Total Publications created", PublicationWithInheritance.count() == 3

        // filtering using namedQuery
        assert "Must exists 2 publication with 'book' in title", PublicationWithInheritance.publicationsWithBookInTitle.count() == 2

        //Enablig filter in father class
        def countryFilter = CountryFilterable.enableHibernateFilter('countryFilter')
        countryFilter.setParameter("myParam", aCountry.id)

        assert "Only two publivations in aCountry", PublicationWithInheritance.count()

        // Using named query with filter by country we need to get only one
        assert "Only one publication with 'book' word in title and in aCountry" , PublicationWithInheritance.publicationsWithBookInTitle().count() == 1
    }


}
