package test_project

class Author {
    static hasMany = [publications: Publication]



    static namedQueries = {
        authorsWithRecentPublications {
            publications {
                // invoking a named query defined in the Publication class…
                recentPublications()
            }
        }
        authorsWithRecentsPublicationsLargerThanTenPages {
            publications {
                recentPublicationsLargerThan(10)
            }
        }
    }
}
