package test_project

abstract class CountryFilterable {
    Country country

    static hibernateFilters = {
        countryFilter(condition: ':myParam = country_id', types:'long')
    }
}
