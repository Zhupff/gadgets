plugins {
    id("gadgets.library")
}

script {
    configuration("zhupff.gadgets.blur") {
        configure()
    }
    publication {
        publish()
    }
}