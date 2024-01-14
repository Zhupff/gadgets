plugins {
    id("zhupf.gadget.android")
}

gadget {
    configuration("zhupf.gadget.media") {
        configure()
    }
    publication {
        publish()
    }
}