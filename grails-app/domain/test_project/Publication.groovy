package test_project

import org.codehaus.groovy.grails.plugins.web.taglib.CountryTagLib

class Publication {
    String title
    String author
    Date datePublished
    Integer numberOfPages
    Boolean enable = true
    Country country

    static hibernateFilters = {
        defaultFilter(condition:'enable=1' , default: true)
        parametrizedFilter(condition: ':myParam = enable', types:'boolean')

        countryFilter(condition: ':myParam = country_id', types:'long')
    }

    static namedQueries = {
        recentPublications {
            def now = new Date()
            gt 'datePublished', now - 365
        }

        recentPublicationsLargerThan { pageCount ->
            def now = new Date()
            gt 'datePublished', now - 365
            gt 'numberOfPages', pageCount
        }

        oldPublicationsLargerThan { pageCount ->
            def now = new Date()
            lt 'datePublished', now - 365
            gt 'numberOfPages', pageCount
        }

        publicationsWithBookInTitle {
            like 'title', '%Book%'
        }

        recentPublicationsWithBookInTitle {
            // calls to other named queries…
            recentPublications()
            publicationsWithBookInTitle()
        }
    }
}
